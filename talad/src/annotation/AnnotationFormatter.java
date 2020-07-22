package annotation;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * 
 * @author bebeshina
 *
 *nous cherchons à produire une sortie au format brat standoff avec les types de données suivants 
 *
 *	T: text-bound annotation --- représentation des co-textes, marquers, nominations, repères
	R: relation --- représentation des relations ( relations sémantiques, statiques etc)
	E: event
	A: attribute --- pour encoder les annotations des marqueurs (les étiquettes)
	M: modification (alias for attribute, for backward compatibility) --- ne sera pas utilisé
	N: normalization [new in v1.3]
	#: note
 */
public class AnnotationFormatter 
{
	/**
	 * membres
	 */
	public static final String token="T";
	public Integer count;
	
	public String dirpath="corpus/brut/campagne/processed";
	
	public static final String[] fields={"corpus", "annotateur", "date","nid", "nomination", "type nomination", "cotexte", "Pl", "mPr", "aPr","mAt", "aAt", "repère", "relation"}; //"coord cotexte"
	
	public String document;
	public static String[][] annotation;
	
	
	
	/**
	 * constructor
	 */
	public AnnotationFormatter() {}
	/*
	 * logger
	 */
	final Logger logger = LoggerFactory.getLogger(AnnotationFormatter.class);
	
	public Map<Integer,String> terms=new HashMap<Integer,String>();
	public Map<Integer,String> rels=new HashMap<Integer,String>();
	
	public int it=0;	
	public int ir=0;
	
	/**
	 * Récupération du texte depuis un fichier sous forme de chaîne de caractères. 
	 * @param filename
	 * @throws IOException
	 */
	public void getText(String filename) throws IOException
	{
		String dir="./corpus/brut/campagne/";
		
		document=null;
		
		Locale locale = Locale.FRANCE;
		Locale.setDefault(locale);
		
		filename=filename.replaceAll("\"", "");
		logger.info("traitement du fichier "+dir+filename);
		
		File current=null;
		current=new File(dir+filename);
		byte[] encoded = Files.readAllBytes(Paths.get(current.getCanonicalPath())); 
		document=new String(encoded, Charset.forName("UTF-8"));
		this.onePerLine(document);
	}
	
	/**
	 * Une phrase par ligne.
	 * @param doc
	 * @return
	 */
	public String onePerLine(String doc)
	{
		//[.?!][]\"')} »]*($\\|     \\|  \\)*(\n)?
		String res=null;
		Pattern p=Pattern.compile("[.?!][]\"')} »]*($)?(\n)?");
		Matcher m=p.matcher(doc);
		while(m.find())
		{
			doc.replace(m.group(0), m.group(0)+System.getProperty("line.separator"));
		}
		res=doc;
		return res;
	}
	
	public void getAnnotation(String[] line)
	{
		annotation= new String[][]{fields,line};
	}
	String test="AGA1	A	08_10_2019	5	écofascisme	entité abstraite	\"Même si les réseaux sociaux lui donnent désormais une résonance inédite, même si certains, sur les sites de la « fachosphère », prônent ouvertement des solutions génocidaires aux problèmes environnementaux, « l'écofascisme, d'un point de vue doctrinal, est insignifiant par rapport à la littérature fasciste traditionnelle », affirme Jérôme Jamin.	Po	d'un point de vue doctrinal, est insignifiant par rapport à	PRa		Ac	d'un point de vue doctrinal	r_association";
	public Integer[] coord;
	
	//T1	Organization 0 43	International Business Machines Corporation
	public Map<String,String> t_map=new HashMap<String,String>();
	public Integer counter=0;
	public String cot;
	public Integer n;
	public Integer[] coord_cotexte;
	public void cotexte()
	{
		int start=0;
		int end=0;
//		logger.info("processing annotated line {}",Arrays.deepToString(annotation));
		Pattern p=Pattern.compile(annotation[1][6].replaceAll("\"",""));//.trim()
		logger.info("cotexte {}",annotation[1][6].replaceAll("\"",""));//
//		System.out.println(document);
		Matcher m=p.matcher(document);
		try 
		{
			if(m.find())
			{
				start=document.indexOf(m.group(0));
				end=start+m.group(0).length();
				coord= new Integer[]{start,end};
				coord_cotexte= new Integer[]{start,end};
				
				logger.info("coord ({},{}), cotexte {}",start,end,annotation[1][6]);
				logger.info("T1 COTEXT {}\t{}\t{}",start,end,annotation[1][6]);
				this.cot=start+" "+end+"\t"+annotation[1][6].replaceAll("\"", "");
				if(!terms.values().contains("COTEXTE "+cot))
				{
					it++;
					terms.put(it, "COTEXTE "+cot);
				}
//				t_map.replace(String.valueOf(counter),)
			}
		}
		catch(NullPointerException e)
		{
			System.out.println("pas trouvé : "+annotation[1][6]);
		}
		System.out.println("\n");
	}
	
	/**
	 * 1. sortir les fragments textuels : cotexte, marqueurs, repère
	 * 2. sortir les annotation au format  T1	MAttitude	0 5; 7 10	string 
	 * 3. monter à l'échelle
	 */
	String nomin;
	
	public void nomination()
	{
		int start=0;
		int end=0;
		int index=0;
		logger.info("{}",annotation[1][4].replaceAll("\"","").trim());
		this.nomin=annotation[1][4].replaceAll("\"","").trim();
		Pattern p=Pattern.compile(nomin);
		Matcher m=p.matcher(document);
		String ind="";
		Set<String> cset=new HashSet<String>();
		if(m.find())
		{
			start=document.indexOf(m.group(0), index);
			end=start+m.group(0).length();
			coord= new Integer[]{start,end};
			index=end;
			if(end<coord_cotexte[1])
			{
				cset.add(String.valueOf(start+" "+end));
				if(ind.isEmpty())
				{
					ind=String.valueOf(start+" "+end);
				}
				else
				{
					ind=ind+";"+String.valueOf(start+" "+end);
				}	
			}
		}	
	for(String cind:cset)
	{
			if(!annotation[1][5].isEmpty())
			{
				if(annotation[1][5].contains("entité abstraite"))
				{
	//				logger.info("T2 ENT_ABSTRAITE\\s{}\\s{}\t{}",start,end,nomin);
					if(!terms.values().contains("N_ent_abstraite "+cind+"\t"+nomin))
					{
						it++;
						terms.put(it, "N_ent_abstraite "+cind+"\t"+nomin);
						this.n=it;
					}
				}
				if(annotation[1][5].contains("processus"))
				{
					if(!terms.values().contains("N_ent_processus "+cind+"\t"+nomin))
					{
	//					logger.info("T2 ENT_PROCESSUS {}\\s{}\\s{}",start,end,nomin);
						it++;
						terms.put(it, "N_ent_processus "+cind+"\t"+nomin);
						this.n=it;
					}
				}
				if(annotation[1][5].contains("entité humaine"))
				{
					if(!terms.values().contains("N_ent_humaine "+cind+"\t"+nomin))
					{
	//					logger.info("T2 ENT_HUMAINE {}\\s{}\\s{}",start,end,nomin);
						it++;
						terms.put(it, "N_ent_humaine "+cind+"\t"+nomin);
						this.n=it;
					}
				}
				if(annotation[1][5].contains("objet physique (naturel ou artefact)"))
				{
					if(!terms.values().contains("N_ent_artefact "+cind+"\t"+nomin))
					{
	//					logger.info("T2 ENT_ARTEFACT {} {}\t{}",start,end,nomin);
						it++;
						terms.put(it, "N_ent_artefact "+cind+"\t"+nomin);
						this.n=it;
					}
				}
				if(annotation[1][5].contains("événement"))
				{
					if(!terms.values().contains("N_event "+cind+"\t"+nomin))
					{
	//					logger.info("T2 ENT_EVENT {} {}\t{}",start,end,nomin);
						it++;
						terms.put(it, "N_event "+cind+"\t"+nomin);
						this.n=it;
					}
				}
				if(annotation[1][5].contains("lieu"))
				{
					if(!terms.values().contains("N_ent_lieu "+cind+"\t"+nomin))
					{
	//					logger.info("T2 ENT_LIEU {} {}\t{}",start,end,nomin);
						it++;
						terms.put(it, "N_ent_lieu "+cind+"\t"+nomin);
						this.n=it;
					}
				}
			}
			else 
			{
				if(!terms.values().contains("NOMINATION "+ind+"\t"+nomin))
				{
					it++;
					terms.put(it, "NOMINATION "+cind+"\t"+nomin);
					this.n=it;
				}
			}
		}
	}
	
	public void plan()
	{
		int start=0;
		int end=0;
		int index=0;
		logger.info("{}",annotation[1][6].replaceAll("\"","").trim());
		Pattern p=Pattern.compile(annotation[1][6].replaceAll("\"","").trim());
		Matcher m=p.matcher(document);
		while(m.find())
		{
			start=document.indexOf(m.group(0), index);
			end=start+m.group(0).length();
			coord= new Integer[]{start,end};
			index=end;
			if(!(annotation[1][7].replaceAll("\"","").trim().isEmpty()))
			{
				if(annotation[1][7].replaceAll("\"","").trim().contains("Po"))
				{
					logger.info("T2 PLAN_ONT {}\t{}\t{}",start,end,annotation[1][6].replaceAll("\"",""));
					
					if(!terms.values().contains("PLAN_ONT "+start+" "+end+"\t"+annotation[1][6].replaceAll("\"","")))
					{
						it++;
						terms.put(it, "PLAN_ONT "+start+" "+end+"\t"+annotation[1][6].replaceAll("\"",""));
					}
				}
				if(annotation[1][7].replaceAll("\"","").trim().contains("Pl"))
				{
					if(!terms.values().contains("PLAN_LING	"+start+" "+end+"\t"+annotation[1][6].replaceAll("\"","")))
					{
						logger.info("T2 PLAN_LING {} {}\t{}",start,end,annotation[1][6].replaceAll("\"",""));
						it++;
						terms.put(it, "PLAN_LING "+start+" "+end+"\t"+annotation[1][6].replaceAll("\"",""));
					}
				}
				if(annotation[1][7].replaceAll("\"","").trim().contains("Ind"))
				{
					if(!terms.values().contains("PLAN_IND "+start+" "+end+"\t"+annotation[1][6].replaceAll("\"","")))
					{
						logger.info("T2 PLAN_IND {} {}\t{}",start,end,annotation[1][6].replaceAll("\"",""));
						it++;
						terms.put(it, "PLAN_IND "+start+" "+end+"\t"+annotation[1][6].replaceAll("\"",""));
					}
				}
				
			}
			else
			{
				logger.info("T2 PLAN_IND {}\t{}\t{}",start,end,annotation[1][4].replaceAll("\"",""));
				it++;
				terms.put(it, "PLAN_IND	"+start+"\t"+end+"\t"+annotation[1][6].replaceAll("\"",""));
			}
				
		}
	}
	
	public void procede()
	{
		int start=0;
		int end=0;
		int index=0;
		System.out.println(Arrays.deepToString(annotation));
		String mpr=annotation[1][8].replaceAll("\"","").trim();
		logger.info("{}",annotation[1][8].replaceAll("\"","").trim());
		if(mpr.contains("|"))
		{
			String[] mstr=annotation[1][8].replaceAll("\"","").trim().split("\\|");
			for(String ms:mstr)
			{
				System.out.println("processing "+ms);
				try
				{
					Pattern p=Pattern.compile(ms);
					
					Matcher m=p.matcher(document);
					
					start=document.indexOf(m.group(0), index);
					end=start+m.group(0).length();
					coord=new Integer[]{start,end};
					index=end;
					logger.info("T2 MPROCEDE {}\t{}\t{}",start,end,ms);
					logger.info("annotation procédé {}",annotation[1][9]);
					if(!annotation[1][9].isEmpty())
					{
						if(annotation[1][9].contains("PRi"))
						{
							if(end<coord_cotexte[1])
							{
								logger.info("T2 PRi {} {}\t{}",start,end,ms);
								it++;
								terms.put(it, "PRi "+start+" "+end+"\t"+ms);
							}
						}
						if(annotation[1][9].contains("PRa"))
						{
							if(end<coord_cotexte[1])
							{
								logger.info("T2 PRa {} {}\t{}",start,end,ms);
								it++;
								terms.put(it, "PRa "+start+" "+end+"\t"+ms);
							}
						}
						if(annotation[1][9].contains("PRr"))
						{
							if(end<coord_cotexte[1])
							{
								logger.info("T2 PRr {} {}\t{}",start,end,ms);
								it++;
								terms.put(it, "PRr "+start+" "+end+"\t"+ms);
							}
						}
					}
				}
				catch(IllegalStateException e)
				{
					
				}
			}
		}
		else
		{
			if(mpr.length()!=0)
			{
				
				Pattern p=Pattern.compile(annotation[1][8]
						.replaceAll("\"","")
						.replaceAll("\\(","\\(")
						.replaceAll("\\)","\\)")
						.replaceAll("«","\\«")
						.replaceAll("»","\\»")
						.replaceAll("\\.","\\.").trim());
				
				System.out.println("trying to compile : "+annotation[1][8]
						.replaceAll("\"","")
						.replaceAll("\\(","\\(")
						.replaceAll("\\)","\\)")
						.replaceAll("«","\\«")
						.replaceAll("»","\\»").trim());
				Matcher m=p.matcher(document);
				if(m.find())
				{
					start=document.indexOf(m.group(0), index);
					end=start+m.group(0).length();
					coord=new Integer[]{start,end};
					index=end;
//					logger.info("T2 MPROCEDE {}\t{}\t{}",start,end,annotation[1][8]);
//					logger.info("annotation procédé {}",annotation[1][9]);
					if(!annotation[1][9].isEmpty())
						
					{
						if(annotation[1][9].contains("PRi"))
						{
							if(end<coord_cotexte[1])
							{
								logger.info("T2	PRi {} {}\t{}",start,end,mpr);
								it++;
								terms.put(it, "PRi "+start+" "+end+"\t"+mpr);
							}
						}
						if(annotation[1][9].contains("PRa"))
						{
							if(end<coord_cotexte[1])
							{
								logger.info("T2 PRa {} {}\t{}",start,end,mpr);
								it++;
								terms.put(it, "PRa "+start+" "+end+"\t"+mpr);
							}
						}
						if(annotation[1][9].contains("PRr"))
						{
							if(end<coord_cotexte[1])
							{
								logger.info("T2 PRr {} {}\t{}",start,end,mpr);
								it++;
								terms.put(it, "PRr "+start+" "+end+"\t"+mpr);
							}
						}
					}
				}
			}
		}
	}
	
	public void attitude()
	{
		int start=0;
		int end=0;
		int index=0;
		String ma=annotation[1][10].replaceAll("\"","").trim();
//		logger.info("0-marqueurs attitude {}",ma);
//		logger.info("0-annotation attitude {}",annotation[1][11].replaceAll("\"","").trim());
		if(ma.length()>0)
		{
//			logger.info("marqueurs attitude {}",annotation[1][10].replaceAll("\"","").trim());
//			logger.info("1 - annotation attitude {}",annotation[1][11].replaceAll("\"","").trim());
			if(ma.contains("|"))
			{
				String[] mstr=ma.split("\\|");
				for(String ms:mstr)
				{
					Pattern p=Pattern.compile(ms);
					Matcher m=p.matcher(document);
					if(m.find())
					{
						start=document.indexOf(m.group(0), index);
						end=start+m.group(0).length();
						coord=new Integer[]{start,end};
						index=end;
//						logger.info("T2 MATTITUDE {}\t{}\t{}",start,end,ms);
//						logger.info("2 - annotation attitude {}",annotation[1][11]);
						if(end<coord_cotexte[1])
						{
							if(!annotation[1][11].isEmpty())
							{
								if(annotation[1][11].contains("Ac"))
								{
									if(!terms.values().contains("Ac "+start+" "+end+"\t"+ms))
									{
										if(end<coord_cotexte[1])
										{
											logger.info("T2 Ac {} {}\t{}",start,end,ms);
											it++;
											terms.put(it, "Ac "+start+" "+end+"\t"+ms);
										}
									}
								}
								if(annotation[1][11].contains("Ai"))
								{
									if(!terms.values().contains("Ai "+start+" "+end+"\t"+ms))
									{
										if(end<coord_cotexte[1])
										{
											logger.info("T2 Ai {} {}\t{}",start,end,ms);
											it++;
											terms.put(it, "Ai "+start+" "+end+"\t"+ms);
										}
									}
								}
								if(annotation[1][11].contains("Apc"))
								{
									if(!terms.values().contains("Apc "+start+" "+end+"\t"+ms))
									{
										if(end<coord_cotexte[1])
										{
											logger.info("T2 Apc {} {}\t{}",start,end,ms);
											it++;
											terms.put(it, "Apc "+start+" "+end+"\t"+ms);
										}
									}
								}
							}
						}
					}
				}
			}
			else
			{	
				if(ma.length()!=0)
				{
					System.out.println("tentative de matching "+ma);
					Pattern p=Pattern.compile(ma);
					Matcher m=p.matcher(document);
				/*while?*/
					if(m.find())
					{
						start=document.indexOf(m.group(0), index);
						end=start+m.group(0).length();
						coord=new Integer[]{start,end};
						index=end;
						if(end>0)
						{
							if(end<coord_cotexte[1])
							{
								logger.info("T2 MATTITUDE {} {}\t{}",start,end,ma);
								if(annotation[1][11].length()>1)	
								{
									if(annotation[1][11].contains("Ac"))
									{
										if(!terms.values().contains("Ac "+start+" "+end+"\t"+ma))
										{
											
											logger.info("T2 Ac {} {}\t{}",start,end,ma);
											it++;
											terms.put(it, "Ac "+start+" "+end+"\t"+ma);
											
										}
									}
								}
								if(annotation[1][11].contains("Ai"))
								{
									if(!terms.values().contains("Ai "+start+"\t"+end+"\t"+ma))
									{
										logger.info("T2 Ai {} {}\t{}",start,end,ma);
										it++;
										terms.put(it, "Ai "+start+" "+end+"\t"+ma);
									}
								}
								if(annotation[1][11].contains("Apc"))
								{
									if(!terms.values().contains("Apc "+start+" "+end+"\t"+ma))
									{
										logger.info("T2 Apc {} {}\t{}",start,end,ma);
										it++;
										terms.put(it, "Apc "+start+" "+end+"\t"+ma);
									}
								}
							}
						}
						else
						{
							logger.info("else *** T2 MATTITUDE {} {}\t{}",start,end,ma);
							if(!terms.values().contains("Ac "+start+" "+end+"\t"+ma))
							{
								if(end<coord_cotexte[1])
								{
									logger.info("T2 Ac {} {}\t{}",start,end,ma);
									it++;
									terms.put(it, "Ac "+start+" "+end+"\t"+ma);
								}
							}
						}
							}
						}
						else
						{
							logger.info("3 - annotation attitude {}",annotation[1][11]);
							if(end<coord_cotexte[1])
							{
								if(annotation[1][11].length()>1)	
								{
									if(annotation[1][11].contains("Ac"))
									{
										logger.info("T2 Ac {} {}\t{}",start,end,ma);
										it++;
										terms.put(it, "Ac "+start+" "+end+"\t"+ma);
									}
									if(annotation[1][11].contains("Ai"))
									{
										logger.info("T2 Ai {} {}\t{}",start,end,ma);
										it++;
										terms.put(it, "Ai "+start+" "+end+"\t"+ma);
									}
									if(annotation[1][11].contains("Apc"))
									{
										logger.info("T2 Apc {} {}\t{}",start,end,ma);
										it++;
										terms.put(it, "Apc "+start+" "+end+"\t"+ma);
									}
								}
							}
						}
					}	
				}
			else
			{
				if(!(annotation[1][11].isEmpty()))
				{
					if(end<coord_cotexte[1])
					{
						if(annotation[1][11].contains("Ac"))
						{
							logger.info("T2 Ac {} {}\t{}",cot);
							it++;
							terms.put(it, "Ac "+start+" "+end+"\t"+cot);
						}
						if(annotation[1][11].contains("Ai"))
						{
							logger.info("T2 Ai {} {}\t{}",cot);
							it++;
							terms.put(it, "Ai "+start+" "+end+"\t"+cot);
						}
						if(annotation[1][11].contains("Apc"))
						{
							logger.info("T2 Apc {} {}\t{}",cot);
							it++;
							terms.put(it, "Apc "+start+" "+end+"\t"+cot);
						}
					}
				}
			}
	}
	
	public void check()
	{
		logger.info("annotation : {} : ",Arrays.deepToString(annotation));
		logger.info("0 : {} : {}",annotation[0][0],annotation[1][0].replaceAll("\"","").trim());
		logger.info("1 : {} : {}",annotation[0][1],annotation[1][1].replaceAll("\"","").trim());
		logger.info("2 : {} : {}",annotation[0][2],annotation[1][2].replaceAll("\"","").trim());
		logger.info("3 : {} : {}",annotation[0][3],annotation[1][3].replaceAll("\"","").trim());
		logger.info("4 : {} : {}",annotation[0][4],annotation[1][4].replaceAll("\"","").trim());
		logger.info("5 : {} : {}",annotation[0][5],annotation[1][5].replaceAll("\"","").trim());
		logger.info("6 : {} : {}}",annotation[0][6],annotation[1][6].replaceAll("\"","").trim());
		logger.info("7 : {} : {}",annotation[0][7],annotation[1][7].replaceAll("\"","").trim());
		logger.info("8 : {} : {}",annotation[0][8],annotation[1][8].replaceAll("\"","").trim());
		logger.info("9 : {} : {}",annotation[0][9],annotation[1][9].replaceAll("\"","").trim());
		logger.info("10 : {} : {}",annotation[0][10],annotation[1][10].replaceAll("\"","").trim());
		logger.info("11 : {} : {}",annotation[0][11],annotation[1][11].replaceAll("\"","").trim());
		logger.info("annotation : {} : ",Arrays.deepToString(annotation));
		logger.info("12 : {} : {}",annotation[0][12],annotation[1][12].replaceAll("\"","").trim());
		logger.info("13 : {} : {}",annotation[0][13],annotation[1][13].replaceAll("\"","").trim());

		
	}

	
	//R1	Origin Arg1:T3 Arg2:T4
	public void relations()
	{
		int start=0;
		int end=0;
		int index=0;
		/**
		 * récupérer repère
		 */
		String r=annotation[1][12].replaceAll("\"","").trim();
		String t=annotation[1][13].replaceAll("\"","").trim();
		String rep=annotation[1][12].replaceAll("\"","").trim();
		Pattern p=Pattern.compile(rep);
		Matcher m=p.matcher(document);
		
		if(m.find())
		{
			start=document.indexOf(m.group(0), index);
			end=start+m.group(0).length();
			coord=new Integer[]{start,end};
			index=end;
			if(end!=0)
			{
				if(end<coord_cotexte[1])//			if(end>0)
				{
					logger.info("T1 REPERE {} {}\t{}",start,end,rep);
					/**********************/
					if(!(terms.values().contains("REPERE "+start+" "+end+"\t"+rep)))
					{
						it++;
						terms.put(it, "REPERE "+start+" "+end+"\t"+rep);
					}
					r=terms.get(it);
					logger.info("R1 {} Arg 1:{} Arg 2:{}",t,nomin,r);
		//			String s=terms.get(n);
					
					
					//Origin Arg1:T3 Arg2:T4
					if(!(rels.values().contains(t+" "+"Arg 1:T"+n+" Arg 2:T"+it)))
					{
						ir++;
						rels.put(ir, t+" "+"Arg1:T"+n+" Arg2:T"+it);
					}
				}
			}
		}
//		logger.info("processing relation {}",annotation[1][13]);
	}
	
	public void start()
	{
		terms.clear();
		rels.clear();
		it=0;
		ir=0;
	}
	/**
	 * Ensemble des fichiers texte qui ont été annotés par un annotateur
	 * permet de lister ces noms de fichiers
	 */
	public Set<String> tfiles=new HashSet<String>();
	/**
	 * fichiers manquants 
	 */
//	public Set<String> manquants=new HashSet<String>();
	public void getFileNames(String annotated) throws IOException
	{
		String line="";
		BufferedReader br=new BufferedReader(new FileReader(new File(annotated)));
		/**
		 * on passe la première ligne
		 */
		br.readLine();
		
		while((line=br.readLine())!=null)
		{
			String[]a=line.split("\\;");//(\")?
			String fname=a[0].replaceAll("\"","");
			tfiles.add(fname);
		}
		br.close();
	}
	public String header="<?xml version=\"1.0\" encoding=\"utf-8\"?>";
	
	public double cotexteMatch(String c1,String c2) 
	{
		double m=0.0;
		if(c1.equals(c2))
		{
			m=1.0;
		}
		else
		{
			Set<String> cs1=new HashSet<String>();
			Set<String> cs2=new HashSet<String>();
			Set<String>union=new HashSet<String>();
			String[] spl;
			spl=c1.split(" ");
			for(String mot:spl)
			{
				cs1.add(mot);
			}
			spl=c2.split(" ");
			for(String mot:spl)
			{
				cs2.add(mot);
			}
			
			if(!(cs1.isEmpty()&&cs2.isEmpty()))
			{
				union.addAll(cs1);
				union.addAll(cs2);
				cs1.retainAll(cs2);
				m=cs1.size()/Double.valueOf(union.size());
			}
		}
		
		return m;
	}
	
	@SuppressWarnings("unused")
	private Set<String> aset,bset,pset;
	private Map<String,Set<String>> amap,bmap,gmap;
	private final String separator="\\;";
	
	public void getCotexts(String path1, String path2) throws IOException
	{
		/**
		 * ensemble texte-cotextes
		 */
	
		gmap=new HashMap<String, Set<String>>();
	
		BufferedReader br1=new BufferedReader(new FileReader(new File(path1)));
		BufferedReader br2=new BufferedReader(new FileReader(new File(path2)));
		String line="";
		aset=new HashSet<String>();
		bset=new HashSet<String>();
		pset=new HashSet<String>();
		
		/**
		 * ensembles cotexte-annotations
		 */
		amap=new HashMap<String,Set<String>>();
		bmap=new HashMap<String,Set<String>>();
		
		Set<String> lset;
		String cot="";
		String tref="";
		
		/**
		 * début traitement annotateur A
		 */
		
		br1.readLine();
		String[] loc;
		while((line=br1.readLine())!=null)
		{
			loc=line.split("(\")?\\;(\")?");
			tref=loc[0].replaceAll("\"","").trim();
			cot=loc[6].replaceAll("\"","").trim();
			
			
			aset.add(cot);
			if(amap.containsKey(cot))
			{
				lset=amap.get(cot);
				lset.add(line);
				amap.replace(cot, lset);
			}
			else 
			{
				lset=new HashSet<String>();
				lset.add(line);
				amap.put(cot,lset);
			}
			
			if(gmap.containsKey(tref))
			{
				lset=gmap.get(tref);
				lset.add(cot);
				gmap.replace(tref, lset);
			}
			else
			{
				lset=new HashSet<String>();
				lset.add(cot);
				gmap.put(tref,lset);
			}
		}
		
		br1.close();
		this.logger.info("cotextes dans le set A : {}",aset.size());
		this.logger.info("textes annotés par A : {}",gmap.size());
		
		/**
		 * fin traitement annotateur A
		 */
		
		/**
		 * début traitement annotateur B
		 */
		
		br2.readLine();
		
		while((line=br2.readLine())!=null)
		{
			loc=line.split("(\")?\\;(\")?");
			tref=loc[0].replaceAll("\"","").trim();
			cot=loc[6].replaceAll("\"","").trim();
			
			bset.add(cot);
			
			if(bmap.containsKey(cot))
			{
				lset=bmap.get(cot);
			}
			else
			{
				lset=new HashSet<String>();
				lset.add(line);
				bmap.put(cot,lset);
			}
			if(gmap.containsKey(tref))
			{
				lset=gmap.get(tref);
				lset.add(cot);
				gmap.replace(tref, lset);
			}
			else
			{
				lset=new HashSet<String>();
				lset.add(cot);
				gmap.put(tref,lset);
			}
		}
		br2.close();
		this.logger.info("cotextes dans le set B : {}",bset.size());
		this.logger.info("textes annotés par A et B : {}",gmap.size());
		for(Entry<String,Set<String>>ge:gmap.entrySet())
		{
			System.out.println(ge.getKey()+" >>> "+ge.getValue().size());
			
		}
		/**
		 *fin traitement annotateur B, fin de récupération des informations
		 */
	}
	
	public void printCotexts() throws IOException
	{
		/**
		 * mise en place de l'écriture
		 * 1. fichier .xml pivot
		 * 2. fichier .txt contenant le corpus 
		 */
		
		PrintWriter pw=new PrintWriter(new FileWriter(new File("output/nomination_cotexts2.xml")));
		
		OutputStream os = null;
		OutputStreamWriter osw = null;
		
		File corpus_sup=new File("output/corpus_sup2.txt");
		os = new FileOutputStream(corpus_sup,true);
		osw=new OutputStreamWriter(os,"UTF-8");
		
		Set<String> vu=new HashSet<String>();
		
		/**
		 * début écriture .xml
		 */
		
		pw.println(header);
		pw.println("<texts>");
		for(Entry<String,Set<String>>ge:gmap.entrySet())
		{
			pw.println("<text>");
			
			String t=ge.getKey();
			String bf=t.toLowerCase();
			String rd="**** *"+t+" *année_2019";
			
			this.start();
			this.getText(t+".txt");
			
			pw.println("<ref>"+rd+"</ref>");
					
			PrintWriter pwra=new PrintWriter(new FileWriter(new File("/home/bebeshina/Downloads/brat-v1.3_Crunchy_Frog/data/ab2/"+"a-"+bf+".ann")));
			PrintWriter pwta=new PrintWriter(new FileWriter(new File("/home/bebeshina/Downloads/brat-v1.3_Crunchy_Frog/data/ab2/"+"a-"+bf+".txt")));
			
			
					/**
			 * ajout du texte au corpus au format iramuteq
			 */
			
			String dr=rd+"\n";
			
			osw.write(dr, 0, dr.length());
			osw.write(document, 0, document.length());
			osw.write("\n"+"\n");
			osw.flush();
			
			vu.add(rd);
			/**
			 * traitement des cotextes
			 */
			pw.println("<cotexts>");
			
			int i=1;
			for(String gs:ge.getValue())
			{
				pw.println("<cotext>");
				pw.println("<cref>"+t+"-"+i+"</cref>");
				pw.println("<s>"+gs+"</s>");
				
				
				int j=1;
				if(amap.containsKey(gs))
				{
					for(String s:amap.get(gs))
					{
						String[] a = s.split(separator);
//						nomination="<nomin>"+a[4].replaceAll("\"","").trim()+"</nomin>";
				
						String aref="<ann id=\""+j+"\">"+"a-"+bf+"</ann>";
						pw.println(aref);
						pw.println("<plan-a>"+a[7].replaceAll("\"","")+"</plan-a>");
						j++;
						
						/**
						 * génération des annotations A
						 */
					
						this.getAnnotation(a);
						this.cotexte();
						this.nomination();
						this.procede();
						this.attitude();
						this.relations();
					}
					
					
				}
				if(bmap.containsKey(gs))
				{
					for(String s:bmap.get(gs))
					{
						String[] b = s.split(separator);
						String bref="<ann id=\""+j+"\">"+"b-"+bf+"</ann>";
						pw.println(bref);
						pw.println("<plan-b>"+b[7].replaceAll("\"","")+"</plan-b>");
						j++;
					}
				}
				pw.println("</cotext>");
				i++;
			}
			for(Entry<Integer,String>e:this.terms.entrySet())
			{
				String str=e.getValue().trim().replaceAll("(?<=\\d+)(\t|(\\s){2,})(?=\\d+)", "\\s").replaceAll("(?<=\t)[A-Z_]+(?=\\s\\d+)","");
				pwra.println("T"+e.getKey()+"\t"+str);
				pwra.flush();
			}
			for(Entry<Integer,String>r:this.rels.entrySet())
			{
				String str=r.getValue().trim();
				pwra.println("R"+r.getKey()+"\t"+str);
				pwra.flush();
			}
			pwta.print(document);
			pwta.flush();
			
			pw.println("</cotexts>");
			pwra.close();
			pwta.close();
			pw.println("</text>");
		}
		pw.println("</texts>");
		/**
		 * deuxième passe pour récupérer les annotations B
		 */
		for(Entry<String,Set<String>>ge:gmap.entrySet())
		{
			String t=ge.getKey();
			String bf=t.toLowerCase();
			String rd="**** *"+t+" *année_2019";
	
			PrintWriter pwrb=new PrintWriter(new FileWriter(new File("/home/bebeshina/Downloads/brat-v1.3_Crunchy_Frog/data/ab2/"+"b-"+bf+".ann")));
			PrintWriter pwtb=new PrintWriter(new FileWriter(new File("/home/bebeshina/Downloads/brat-v1.3_Crunchy_Frog/data/ab2/"+"b-"+bf+".txt")));
			
			
			this.start();
			this.getText(t+".txt");
			
			/********/
			if(!vu.contains(rd))
			{
				String dr=rd+"\n";
				osw.write(dr, 0, dr.length());
				osw.write(document, 0, document.length());
				osw.write("\n"+"\n");
				osw.flush();
			}
			
			for(String gs:ge.getValue())
			{
				if(bmap.containsKey(gs))
				{
					for(String s:bmap.get(gs))
					{
						String[] b = s.split(separator);
						
						/**
						 * génération des annotations B
						 */
						
						this.getAnnotation(b);
						this.cotexte();
						this.nomination();
						this.procede();
						this.attitude();
						this.relations();
					}
				}
			}
			logger.info("taille terms {}",terms.size());
			
			for(Entry<Integer,String>e:this.terms.entrySet())
			{
				String str=e.getValue().trim().replaceAll("(?<=\\d+)(\t|(\\s){2,})(?=\\d+)", "\\s").replaceAll("(?<=\t)[A-Z_]+(?=\\s\\d+)","");
				pwrb.println("T"+e.getKey()+"\t"+str);
				pwrb.flush();
			}
			for(Entry<Integer,String>r:this.rels.entrySet())
			{
				String str=r.getValue().trim();
				pwrb.println("R"+r.getKey()+"\t"+str);
				pwrb.flush();
			}
			pwtb.print(document);
			pwtb.flush();
			pwtb.close();
			pwrb.close();
		}
		pw.close();
		osw.close();
				
//				
//					
//				
//					
//					
//					
//					
//					
//					
//					/*génération du fichier d'annotation A*/
//					this.start();
//					this.getText(a[0].replaceAll("\"","")+".txt");//".txt"
//					this.getAnnotation(a);
//					this.cotexte();
//					this.nomination();
//					this.procede();
//					this.attitude();
//					this.relations();
//					
//					PrintWriter pwr=new PrintWriter(new FileWriter(new File("/home/bebeshina/Downloads/brat-v1.3_Crunchy_Frog/data/ab/"+"a-"+a[0].replaceAll("\"","").toLowerCase()+".ann")));
//					for(Entry<Integer,String>e:this.terms.entrySet())
//					{
//						String str=e.getValue().trim().replaceAll("(?<=\\d+)(\t|(\\s){2,})(?=\\d+)", "\\s").replaceAll("(?<=\t)[A-Z_]+(?=\\s\\d+)","");
//						pwr.println("T"+e.getKey()+"\t"+str);
//						pwr.flush();
//					}
//					for(Entry<Integer,String>r:this.rels.entrySet())
//					{
//						String str=r.getValue().trim();
//						pwr.println("R"+r.getKey()+"\t"+str);
//						pwr.flush();
//						
//					}
//					
//					pwr.close();
//					
//					/*génération de fichier texte, ajout*/
//					PrintWriter pwt=new PrintWriter(new FileWriter(new File("/home/bebeshina/Downloads/brat-v1.3_Crunchy_Frog/data/ab/"+"a-"+a[0].replaceAll("\"","").toLowerCase()+".txt")));
//					pwt.print(document);
//					pwt.flush();
//					pwt.close();
//					
//					
//				}
//				pw.println("</cotext>");
//			}
//			pw.println("</cotexts>");
//			pw.println("</text>");
//			
//		}
//		pw.println("</texts>");
//		pw.flush();
//		/**
//		 * fermeture des flux
//		 */
//		pw.close();
//		osw.close();
//		/**
//		 * fin
//		 */
//		String reference=null;
//		String nomination=null;
//		
//		
//		
//				
//		
//		Set<String> vu=new HashSet<String>();
//		
//		
//		
//		pw.println("<cotexts>");
//		
//		/*impression des cotextes et des fichiers annotés*/
//		
//		if(!aset.isEmpty())
//		{
//			for(String c:aset)
//			{
//				pw.println("<cotext>");
//				pw.println("<s>"+c+"</s>");
//				
////				annotateur="<coder ref=\"A\">";
////				pw.println(annotateur);
//				
//				int counter=0;
//				
//				for(String s:amap.get(c))
//				{
//					counter++;
//					String[] a = s.split(separator);
//					String rd="**** *"+a[0].replaceAll("\"","")+" "+"*année_2019";
//					reference="<ref>"+rd+"</ref>";
//					nomination="<nomin>"+a[4].replaceAll("\"","").trim()+"</nomin>";
//					String aref="<ann>"+"a-"+a[0].replaceAll("\"","").toLowerCase()+"</ann>";
//					
//					pw.println(aref);
//					pw.println("<plan-a>"+a[7].replaceAll("\"","")+"</plan-a>");
//					
//					/*génération du fichier d'annotation A*/
//					this.start();
//					this.getText(a[0].replaceAll("\"","")+".txt");//".txt"
//					this.getAnnotation(a);
//					this.cotexte();
//					this.nomination();
//					this.procede();
//					this.attitude();
//					this.relations();
//					
//					PrintWriter pwr=new PrintWriter(new FileWriter(new File("/home/bebeshina/Downloads/brat-v1.3_Crunchy_Frog/data/ab/"+"a-"+a[0].replaceAll("\"","").toLowerCase()+".ann")));
//					for(Entry<Integer,String>e:this.terms.entrySet())
//					{
//						String str=e.getValue().trim().replaceAll("(?<=\\d+)(\t|(\\s){2,})(?=\\d+)", "\\s").replaceAll("(?<=\t)[A-Z_]+(?=\\s\\d+)","");
//						pwr.println("T"+e.getKey()+"\t"+str);
//						pwr.flush();
//					}
//					for(Entry<Integer,String>r:this.rels.entrySet())
//					{
//						String str=r.getValue().trim();
//						pwr.println("R"+r.getKey()+"\t"+str);
//						pwr.flush();
//						
//					}
//					pwr.close();
//					
//					/*génération de fichier texte, ajout*/
//					PrintWriter pwt=new PrintWriter(new FileWriter(new File("/home/bebeshina/Downloads/brat-v1.3_Crunchy_Frog/data/ab/"+"a-"+a[0].replaceAll("\"","").toLowerCase()+".txt")));
//					pwt.print(document);
//					pwt.flush();
//					pwt.close();
//					
//					
//				}
//				if(bset.contains(c))
//				{
////					annotateur="<coder ref=\"B\">";
////					pw.println(annotateur);
//					
//					counter=0;
//					for(String s:bmap.get(c))
//					{
//						counter++;
//						String[] b = s.split(separator);
////						reference="<ref>"+b[0]+"</ref>";						
////						nomination="<nomin>"+b[4]+"</nomin>";
//						String bref="<ann>"+"b-"+b[0].replaceAll("\"","").toLowerCase()+"</ann>";
//						pw.println(bref);
//						pw.println("<plan-b>"+b[7].replaceAll("\"","")+"</plan-b>");
//						
//						/*génération du fichier d'annotation A*/
//						this.start();
//						this.getText(b[0].replaceAll("\"","")+".txt");//".txt"
//						this.getAnnotation(b);
//						this.cotexte();
//						this.nomination();
//						this.procede();
//						this.attitude();
//						this.relations();
//						
//						PrintWriter pwr=new PrintWriter(new FileWriter(new File("/home/bebeshina/Downloads/brat-v1.3_Crunchy_Frog/data/ab/"+"b-"+b[0].replaceAll("\"","").toLowerCase()+".ann")));
//						for(Entry<Integer,String>e:this.terms.entrySet())
//						{
//							String str=e.getValue().trim().replaceAll("(?<=\\d+)(\t|(\\s){2,})(?=\\d+)", "\\s").replaceAll("(?<=\t)[A-Z_]+(?=\\s\\d+)","");
//							pwr.println("T"+e.getKey()+"\t"+str);
//							pwr.flush();
//						}
//						for(Entry<Integer,String>r:this.rels.entrySet())
//						{
//							String str=r.getValue().trim();
//							pwr.println("R"+r.getKey()+"\t"+str);
//							pwr.flush();
//							
//						}
//						pwr.close();
//						
//						/*génération de fichier texte, ajout*/
//						PrintWriter pwt=new PrintWriter(new FileWriter(new File("/home/bebeshina/Downloads/brat-v1.3_Crunchy_Frog/data/ab/"+"b-"+b[0].replaceAll("\"","").toLowerCase()+".txt")));
//						pwt.print(document);
//						pwt.flush();
//						pwt.close();
//					}
////					pw.println("</coder>");
//				}
//				pw.println(reference);
//				pw.println(nomination);
//				
//				pw.println("</cotext>");
//				vu.add(c);
//			}
//		}
//		
//		aset.removeAll(vu);
//		bset.removeAll(vu);
//		
//		/***********************/
//		if(!bset.isEmpty())	
//		{
//			for(String c:bset)
//			{
//				counter=0;
//				pw.println("<cotext>");
//				pw.println("<s>"+c+"</s>");
////				annotateur="<coder ref=\"B\">";
////				pw.println(annotateur);
//				
//				for(String s:bmap.get(c))
//				{
//					counter++;
//					String[] b = s.split(separator);
//					reference="<ref>"+b[0].replaceAll("\"","")+"</ref>";
//					nomination="<nomin>"+b[4].replaceAll("\"","")+"</nomin>";
//					String bref="<ann>"+"b-"+b[0].replaceAll("\"","").toLowerCase()+"</ann>";
//					pw.println(bref);
//					pw.println("<plan-b>"+b[7].replaceAll("\"","")+"</plan-b>");
//					
//				}
////				pw.println("</coder>");
//				pw.println(reference);
//				pw.println(nomination);
//				pw.println("</cotext>");
//			}
//		}
//		if(!aset.isEmpty())	
//		{
//			for(String c:aset)
//			{
//				counter=0;
//				pw.println("<cotext>");
//				pw.println("<s>"+c+"</s>");
////				annotateur="<coder ref=\"A\">";
////				pw.println(annotateur);
//				
//				for(String s:bmap.get(c))
//				{
//					counter++;
//					String[] a = s.split(separator);
//					reference="<ref>"+a[0].replaceAll("\"","")+"</ref>";
//					String aref="<ann>"+"a-"+a[0].replaceAll("\"","").toLowerCase()+"</ann>";
//					pw.println(aref);
//					pw.println("<plan-a>"+a[7].replaceAll("\"","")+"</plan-a>");
//				}
//			}
////			pw.println("</coder>");
//			pw.println(reference);
//			pw.println(nomination);
//			pw.println("</cotext>");
//		}
//		pw.println("</cotexts>");
//		pw.flush();
//		pw.close();
//		osw.close();
	}

	
	public void printData() throws IOException
	{
		this.getCotexts("./corpus/annote/annot_2bis.csv","./corpus/annote/annot_1bis.csv");
		this.printCotexts();
	}
	public void execute(String annotated) throws IOException
	{
		this.getFileNames(annotated);
		for(String fn:tfiles)
		{
			try
			{
				this.logger.info("processing file {}",fn+".txt");
				this.getText(fn+".txt");
				if(this.document!=null)
			{
				String af="";
				String tf="";
				this.start();
				BufferedReader br=new BufferedReader(new FileReader(new File(annotated)));
				String line="";
				br.readLine();
				while((line=br.readLine())!=null)
				{
					String[]a=line.split("\\;");//(\")?
					String fname=a[0].replaceAll("\"","");
					
					if(fname.equals(fn))//************************
					{
						af=a[1].toLowerCase().replaceAll("\"","")+fname.toLowerCase()+".ann";
						tf=a[1].toLowerCase().replaceAll("\"","")+fname.toLowerCase()+".txt";
						this.getAnnotation(a);
//						this.check();
						this.cotexte();
						this.nomination();
						this.plan();
						this.procede();
						this.attitude();
//						this.relations();
					}
				}
				br.close();
				if(!this.terms.isEmpty())
				{
					this.logger.info("taille de l'ensemble {}",terms.size());
					this.logger.info("impressions des annotations {}","/home/bebeshina/Downloads/brat-v1.3_Crunchy_Frog/data/examples/ab2/"+af);
					PrintWriter pw=new PrintWriter(new FileWriter(new File("/home/bebeshina/Downloads/brat-v1.3_Crunchy_Frog/data/ab2/"+af)));
					for(Entry<Integer,String>e:this.terms.entrySet())
					{
						String str=e.getValue().trim().replaceAll("(?<=\\d+)(\t|(\\s){2,})(?=\\d+)", "\\s").replaceAll("(?<=\t)[A-Z_]+(?=\\s\\d+)","");
						pw.println("T"+e.getKey()+"\t"+str);
						pw.flush();
					}
					pw.close();

					pw=new PrintWriter(new FileWriter(new File("/home/bebeshina/Downloads/brat-v1.3_Crunchy_Frog/data/ab2/"+tf)));
					pw.print(document);
					pw.flush();
					pw.close();
				}
				else 
				{
					this.logger.info("pas d'annotations, voir problèmes d'encodage");
				}
				/**
				 * traitement pour les relations à venir
				 * 
				 */
//				for(Entry<Integer,String>e:this.rels.entrySet())
//				{
//					System.out.println("R"+e.getKey()+"\t"+e.getValue());
//				}
				this.logger.info("Done ({})\n",fn);
			}
			else
			{
				this.logger.info("fichier non trouvé {}\n",fn);
			}
			}
			catch(NoSuchFileException e)
			{
				
			}
		}
	}
	

	public static void main(String[] args) throws IOException
	{
		AnnotationFormatter af=new AnnotationFormatter();
//		af.execute("./corpus/annote/annot_1bis.csv");
		af.printData();
		
		
	}
}
