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
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.Vector;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AnnotationStatistics 
{
	public double score_inter;
	public Integer count_1;
	public Integer count_2;
	public Integer[] coord;
	public AnnotationStatistics() 
	{
		
	}
	
	public static final String[] fields={"corpus", "annotateur", "date","nid", "nomination", "type nomination", "cotexte", "Pl", "mPr", "aPr","mAt", "aAt", "repère", "relation"}; //"coord cotexte"
	public static String document;
	public static String[][] annotation;
	
	final Logger logger = LoggerFactory.getLogger(AnnotationStatistics.class);
	

	
	public void countAnnotations(String type)
	{
		
	}
	
	
	public void getAnnotation(String[] line)
	{
		annotation= new String[][]{fields,line};
	}
	
	public void countSentences(String dir)
	{
		
	}
	
	public void cotexte()
	{
		int start=0;
		int end=0;
		logger.info("processing annotated line {}",Arrays.deepToString(annotation));
		logger.info("cotexte {}",annotation[1][6].replaceAll("\"",""));
		Pattern p=Pattern.compile(annotation[1][6].replaceAll("\"",""));
		Matcher m=p.matcher(document);
		if(m.find())
		{
			start=document.indexOf(m.group(0));
			end=start+m.group(0).length();
			coord= new Integer[]{start,end};
			
			logger.info("coord ({},{}), cotexte {}",start,end,annotation[1][6]);
			logger.info("T1 Cotexte {}\t{}\t{}",start,end,annotation[1][6]);
//			t_map.replace(String.valueOf(counter),)
		}

	}
	
	/**
	 * c1 - string corresponding to the first cotext
	 * c2 - string corresponding to the second cotext
	 * @return the match between the two contexts to check whether they overlap
	 */
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
	/**
	 * cotexte pair is a pair of very similar cotexts, we get the reference of the annotations containing similar cotexts 
	 * (thus, the other 
	 * 
	 * @param path1
	 * @param path2
	 * @throws IOException
	 */
	@SuppressWarnings("unused")
	private Integer po_po,pl_pl,ind_ind,po_pl,pl_po,ind_po,po_ind,ind_pl,pl_ind;
	@SuppressWarnings("unused")
	private Integer pri_pri,pra_pra,prr_prr,pra_pri,pra_prr, pri_pra,prr_pra,pri_prr,prr_pri, ai_ai, ac_ac, apc_apc, ai_apc,ai_ac, apc_ai, apc_ac,ac_apc,ac_ai;
	
	
	
	public void getCotextePairs(String path1, String path2) throws IOException
	{
//		Map<String,Double[]> accord=new HashMap<String,Double[]>();
		
		BufferedReader br1=new BufferedReader(new FileReader(new File(path1)));
		BufferedReader br2=new BufferedReader(new FileReader(new File(path2)));
		String line="";
		Set<String> aset=new HashSet<String>();
		Set<String> bset=new HashSet<String>();
		Set<String> pset=new HashSet<String>();
		Set<String> prset=new HashSet<String>();
		Map<String,Set<String>>amap=new HashMap<String,Set<String>>();
		Set<String> lset;
		String cot="";
		po_po=0;pl_pl=0;ind_ind=0;po_pl=0;pl_po=0;ind_po=0;po_ind=0;pl_ind=0;ind_pl=0;
		pri_pri=0;pra_pra=0;prr_prr=0;pra_pri=0;pra_prr=0;pri_pra=0;prr_pra=0;pri_prr=0;prr_pri=0;
		ai_ai=0;ac_ac=0; apc_apc=0; ai_apc=0;ai_ac=0; apc_ai=0; apc_ac=0;ac_apc=0;ac_ai=0;
		
		br1.readLine();
		while((line=br1.readLine())!=null)
		{
//			System.out.println(line);
			cot=line.split("(\")?\\;(\")?")[6].trim();
			aset.add(cot);
			if(amap.containsKey(cot))
			{
				lset=amap.get(cot);
			}
			else 
			{
				lset=new HashSet<String>();
				lset.add(line);
				amap.put(cot,lset);
			}
		}
		
		br1.close();
		this.logger.info("cotextes dans le set "+aset.size());
		
		int count=0;
		
		br2.readLine();
		if(!aset.isEmpty())
		{
			while((line=br2.readLine())!=null)
			{
				String[] str=line.split("\\;");
				cot=str[6].replaceAll("\"","");;
				bset.add(cot);
				int p=0;
				for(String s:aset)
				{
					double score=this.cotexteMatch(s,cot);
					
//					if(0.3<score&&score<0.45)
					if(score>=0.3)
					{
						this.logger.info("cot {}", cot);
						this.logger.info("processing pair, score {}", score);
//						this.logger.info("cotexte 1 {}", s);
//						this.logger.info("cotexte 2 {}", line.split("\\;")[6]);
						cot.replaceAll("\"","");
						
						if(amap.containsKey(cot))
						{
							p++;
							System.out.println("p="+p);
							for(String s1:amap.get(cot))
							{
								
								
								String[] lstr=s1.split("(\")?\\;(\")?");
//								System.out.println("lstr len "+lstr.length);
								for(int i=0;i<lstr.length;i++)
								{
									String t1=null;
									String t2=null;
									try 
									{
										t1=str[i].replaceAll("\"", "");
//										this.logger.info("i={} case1 ={}",i,t1);
										
									}
									catch(ArrayIndexOutOfBoundsException e)
									{
										this.logger.info("see annot A {}",str[0]+str[1]+str[2]+str[3]);
										count++;
									}
									try
									{
										t2=lstr[i].replaceAll("\"", "");
//										this.logger.info("i={} case2={}",i,t2);
									}
									catch(ArrayIndexOutOfBoundsException e)
									{
										this.logger.info("see annot B {}",lstr[0]+lstr[1]+lstr[2]+lstr[3]);
										count++;
									}
									
//									if(i==4)
//									{
//										if(t1.toLowerCase().equals(t2.toLowerCase()))
//										{
//											this.logger.info("nomination {} == {}",t1,t2);
//											/**
//											 * processing nomination
//											 */
//										}
//									}
//									if(i==5)
//									{
//										if(t1.toLowerCase().equals(t2.toLowerCase()))
//										{
//											this.logger.info("type d'entité {} == {}",t1,t2);
//											/**
//											 * processing type
//											 */
//										}
//									}
									
									if(i==7)
									{
										t1=t1.replaceAll("\"","");
										t2=t2.replaceAll("\"","");
										
//										if(t1.replaceAll("\"","").equals(t2.replaceAll("\"","")))
//										{
											this.logger.info("processing plan annotation {} == {}",t1,t2);
											/**
											 * processing plan
											 */
											switch(t1)
											{
											case "Po":
												if(t2.contains("Po"))
												{
													po_po++;
													break;
												}
												if(t2.contains("Pl"))
												{
													po_pl++;
													break;
												}
												if(t2.contains("Ind"))
												{
													po_ind++;
													break;
												}
											
											case "Pl":
												if(t2.contains("Pl"))
												{
													pl_pl++;
													break;
												}
												if(t2.contains("Po"))
												{
													pl_po++;
													break;
												}
												if(t2.contains("Ind"))
												{
													pl_ind++;
													break;
												}
											case "Ind":
												if(t2.contains("Ind"))
												{
													ind_ind++;
													break;
												}
												if(t2.contains("Po"))
												{
													ind_po++;
													break;
												}
												if(t2.contains("Pl"))
												{
													ind_pl++;
													break;
												}
											}
										}
//										else 
//										{
//											switch(t1)
//											{
//											case "Po":
//												if(t2.contentEquals("Po"))
//												{
//													po_po++;
//													break;
//												}
//												if(t2.contentEquals("Pl"))
//												{
//													po_pl++;
//													break;
//												}
//												if(t2.contentEquals("Ind"))
//												{
//													po_ind++;
//													break;
//												}
//											
//											case "Pl":
//												if(t2.contentEquals("Pl"))
//												{
//													pl_pl++;
//													break;
//												}
//												if(t2.contentEquals("Po"))
//												{
//													pl_po++;
//													break;
//												}
//												if(t2.contentEquals("Ind"))
//												{
//													pl_ind++;
//													break;
//												}
//											case "Ind":
//												if(t2.contentEquals("Ind"))
//												{
//													ind_ind++;
//													break;
//												}
//												if(t2.contentEquals("Po"))
//												{
//													ind_po++;
//													break;
//												}
//												if(t2.contentEquals("Pl"))
//												{
//													ind_pl++;
//													break;
//												}
//											}
////											switch(t2)
////											{
////											case "Po":
////												if(t1.contentEquals("Po"))
////												{
////													po_po++;
////													break;
////												}
////												if(t1.contentEquals("Pl"))
////												{
////													pl_po++;
////													break;
////												}
////												if(t1.contentEquals("Ind"))
////												{
////													ind_po++;
////													break;
////												}
////											
////											case "Pl":
////												if(t1.contentEquals("Pl"))
////												{
////													pl_pl++;
////													break;
////												}
////												if(t1.contentEquals("Po"))
////												{
////													po_pl++;
////													break;
////												}
////												if(t1.contentEquals("Ind"))
////												{
////													ind_pl++;
////													break;
////												}
////											case "Ind":
////												if(t1.contentEquals("Ind"))
////												{
////													ind_ind++;msg
////													break;
////												}
////												if(t1.contentEquals("Po"))
////												{
////													po_ind++;
////													break;
////												}
////												if(t1.contentEquals("Pl"))
////												{
////													pl_ind++;
////													break;
////												}
////											}
//										}
//									}
//									if(i==8)
//									{
////										prset.add(t1);
////										prset.add(t2);
//										t1.replaceAll("\"", "");
//										t2.replaceAll("\"", "");
//										double mscore=this.cotexteMatch(t1, t2);
//										if(mscore>0.1)
//										{
//											this.logger.info("marqueurs procédé {} < == > {}",t1,t2);
//											prset.add(t1);
//											prset.add(t2);
//											
//										}
//										
//									}
									if(i==9)
									{
										t1.toString().replaceAll("\"", "");
										t2.toString().replaceAll("\"", "");
										this.logger.info("t1 = {} ; t2 = {}", t1, t2);
										this.logger.info("annotation procédé {} < == > {}",t1,t2);
										if(t1.contains("PRa"))
										{
											if(t2.contains("PRa"))
											{
												pra_pra++;
											}
											if(t2.contains("PRi"))
											{
												pra_pri++;
											}
											if(t2.contains("PRr"))
											{
												pra_prr++;
											}
										}
										if(t1.contains("PRi"))
										{
											if(t2.contains("PRi"))
											{
												pri_pri++;
											}
											if(t2.contains("PRi"))
											{
												pri_pra++;
											}
											if(t2.contains("PRr"))
											{
												pri_prr++;
											}
										}
										if(t1.contains("PRr"))
										{
											if(t2.contains("PRr"))
											{
												prr_prr++;
											}
											if(t2.contains("PRa"))
											{
												prr_pra++;
											}
											if(t2.contains("PRi"))
											{
												prr_pri++;
											}
										}
										
//											if(t2.contentEquals("PRi"))
//											{
//												pri_pri++;
//												break;
//											}
//											if(t2.contentEquals("PRa"))
//											{
//												pra_pra++;
//												break;
//											}
//											if(t2.contentEquals("PRr"))
//											{
//												prr_prr++;
//												break;
//											}
////											if(t2.contentEquals("PRi|PRr"))
////											{
////												priprr_priprr++;
////												break;
////											}
////											if(t2.contentEquals("PRr|PRa"))
////											{
////												prr_pra++;
////												break;
////											}
////											if(t2.contentEquals("PRr"))
////											{
////												prr_prr++;
////												break;
////											}
//										}
////										
									}
//									if(i==10)
//									{
//										this.logger.info("marqueurs attitude {} < == > {}",t1,t2);
//									}
//									if(i==11)
//									{
//										t1.toString().replaceAll("\"", "");
//										t2.toString().replaceAll("\"", "");
//										this.logger.info("t1 = {} ; t2 = {}", t1, t2);
//										this.logger.info("annotation attitude {}< == > {}",t1,t2);
//										if(t1.contains("Ai"))
//										{
//											if(t2.contains("Apc"))
//											{
//												apc_apc++;
//											}
//											if(t2.contains("Ai"))
//											{
//												apc_ai++;
//											}
//											if(t2.contains("Ac"))
//											{
//												apc_ac++;
//											}
//										}
//										if(t1.contains("Ai"))
//										{
//											if(t2.contains("Apc"))
//											{
//												ai_apc++;
//											}
//											if(t2.contains("Ai"))
//											{
//												ai_ai++;
//											}
//											if(t2.contains("Ac"))
//											{
//												ai_ac++;
//											}
//										}
//										if(t1.contains("Ac"))
//										{
//											if(t2.contains("Apc"))
//											{
//												ac_apc++;
//											}
//											if(t2.contains("Ai"))
//											{
//												ac_ai++;
//											}
//											if(t2.contains("Ac"))
//											{
//												ac_ac++;
//											}
//										}
//									}
//									if(i==12)
//									{
//										this.logger.info("repère {} < == > {}",t1,t2);	
//									}
//									if(i==13)
//									{
//										this.logger.info("relation {} < == > {}",t1,t2);
//									}
								}
							}
							
						}
					
						pset.add(cot);
					}
					else
					{
						if(score>0.3&&score<0.45)
						{
							if(cot.contains(line.split("\\;")[6].trim()))
							{
//								score=1.0;
								pset.add(cot);
//								this.logger.info("processing pair, score {}", score);
//								this.logger.info("cotexte 1 {}", s);
//								this.logger.info("cotexte 2 {}", line.split("\\;")[6]);
//								
							}
							if(line.split("\\;")[6].trim().contains(cot))
							{
//								score=1.0;
//								this.logger.info("processing pair, score {}", score);
//								this.logger.info("cotexte 2 {}", line.split("\\;")[6]);
//								this.logger.info("cotexte 1 {}", s);
								pset.add(line.split("\\;")[6].trim());
							}
						}
					}
				}
			}
			this.logger.info("exceptions count {}",count);
			br2.close();
			/**
			 * quel est l'ensemble de paires attendues ?
			 */
		}
//		this.logger.info("aset size {}, bset size {}, pairs {}",aset.size(),bset.size(),pset.size());
//		this.logger.info("prset size={}", prset.size());
		
		
		this.logger.info("pl_pl={}, pl_po={},pl_ind={}",pl_pl,pl_po,pl_ind);
		this.logger.info("po_pl={}, po_po={}, po_ind={}", po_pl,po_po,po_ind);
		this.logger.info("ind_pl={}, ind_po={}, ind_ind={}",ind_pl, ind_po, ind_ind);
		this.logger.info("pri_pri={},pri_pra={}, pri_prr={}", pri_pri,pri_pra,pri_prr);
		this.logger.info("pra_pri={},pra_pra={}, pra_prr={}", pra_pri,pra_pra,pra_prr);
		this.logger.info("prr_pri={},prr_pra={}, prr_prr={}", prr_pri,prr_pra,prr_prr);
//		
//		this.logger.info("apc_apc={},apc_ai={}, apc_ac={}", apc_apc,apc_ai,apc_ac);
//		this.logger.info("ai_apc={},ai_ai={}, ai_ac={}", ai_apc,ai_ai,ai_ac);
//		this.logger.info("ac_apc={},ac_ai={}, ac_ac={}", ac_apc,ac_ai,ac_ac);
	}
	
	
	public void printSequences(String d) throws IOException
	{
		File[]dir=new File(d).listFiles();
		PrintWriter pw=null;
		pw=new PrintWriter(new FileWriter(new File("/home/bebeshina/Documents/talad/marqueurs/d221/sequences_freq.tsv"),true));
		
		Map<String,Integer> map=new HashMap<String,Integer>();
		
		for(File f:dir)
		{
			if(f.getName().endsWith(".ann"))
			{
				BufferedReader br=new BufferedReader(new FileReader(f));
				String line;
				
				LinkedList<String> seq=new LinkedList<String>();
				LinkedList<Vector<Object>> veq=new LinkedList<Vector<Object>>();
				while((line=br.readLine())!=null)
				{
					if(line.startsWith("T"))
					{
						String split_1="(?<=T\\d{1,4})\\t";
						try 
						{
							String[]temp=line.split(split_1);
							Pattern pt=Pattern.compile("(?<label>[A-Za-z_]+)\\s(?<coord>\\d+\\s\\d+(\\;\\d+\\s+\\d+)?)\t(?<term>.+)");
							Matcher m=pt.matcher(temp[1]);
							if(m.find())
							{
								String label=m.group("label");
									label.replaceAll("PROCEDE_AJUSTEMENT", "PRa")
									.replaceAll("ATTITUDE_INTERACTION", "Ai")
									.replaceAll("ATTITUDE_CADRAGE", "Ac")
									.replaceAll("ATTITUDE_JUSTIFICATION", "Aj")
									.replaceAll("PROCEDE_REJET", "PRr")
									.replaceAll("ENT_ABSTRAITE", "N_ent_abstraite");
								System.out.println(line+" "+label);
								/**
								 * ordonnancement 
								 */
								String tm=m.group("coord");
								/**
								 * chercher le min et le max
								 */
								int start, end, min, max;
								if(tm.contains(";"))
								{
									String[] sub=tm.split("\\;");
									for(int n=0;n<sub.length;n++)
									{
										start=Integer.valueOf(sub[n].split("\\s")[0]);
										end=Integer.valueOf(sub[n].split("\\s")[1]);
										Vector<Object> v=new Vector<Object>();
										v.add(0, start);
										v.add(1, end);
										v.add(2, label);
										if(veq.isEmpty())
										{
											veq.addFirst(v);
										}
										else
										{
											min= (Integer)veq.getFirst().get(0);
											max= (Integer)veq.getLast().get(1);
											if(min>start)
											{
												veq.addFirst(v);
											}
											else
											{
												if(max<start)
												veq.addLast(v);
											}
										}
									}
								}
								else
								{
									start=Integer.valueOf(tm.split("\\s")[0]);
									end=Integer.valueOf(tm.split("\\s")[1]);
									Vector<Object> v=new Vector<Object>();
									v.add(0, start);
									v.add(1, end);
									v.add(2, m.group("label"));
									v.add(3, m.group("term"));
									if(veq.isEmpty())
									{
										veq.addFirst(v);
									}
									else
									{
										min= (Integer)veq.getFirst().get(0);
										max= (Integer)veq.getLast().get(1);
										if(min>start)
										{
											veq.addFirst(v);
										}
										else
										{
											if(max<start)
											veq.addLast(v);
										}
									}
								}
							}
							
						}
						catch(ArrayIndexOutOfBoundsException e)
						{
//							System.out.println("processing "+f.getName()+">>>"+line);
						}
					}
				}
				br.close();
				
				/**
				 * ajout dans map
				 */
				for(int i=0;i<veq.size();i++)
				{
					String l=(String)veq.get(i).get(2);
					seq.add(i,l);
				}
				/**
				 * récup string
				 */
				StringBuilder sb=new StringBuilder();
				sb.append(seq.get(0));
				for(int j=1;j<seq.size();j++)
				{
					sb.append(" ");
					sb.append(seq.get(j));
				}
				/**
				 * ajout map
				 */
				String lstr=sb.toString();
				if(map.containsKey(lstr))
				{
					Integer old=map.get(lstr);
					int nouveau=0;
					nouveau=old+1;
					map.replace(lstr, nouveau);
				}
				else
				{
					map.put(lstr,1);
				}	
			}
		}
		/**
		 * impression
		 */
		for(Entry<String,Integer>e:map.entrySet())
		{
			pw.println(e.getValue()+"\t"+e.getKey());
		}
		pw.close();
	}
	

	public void printLGraphs(String f) throws IOException
	{
		BufferedReader br=new BufferedReader(new FileReader(f));
		String line;
		Map<String, Set<String>> map=new HashMap<String,Set<String>>();
		br.readLine();
		Set<String> set=new HashSet<String>();
		String ref="";
	
		while((line=br.readLine())!=null)
		{
			String[]str=line.split("\t");
			
			if(str[6].equals(ref))
			{
				set.add(str[3]+","+str[4]);
				if(map.containsKey(str[1]+ref))
				{
					set=new HashSet<String>();
					set=map.get(str[1]+ref);
				}
			}
			else
			{
				
				ref=str[6];
				set=new HashSet<String>();
				set.add(str[3]+","+str[4]);
				map.put(str[1]+ref, set);
			}
		}
		br.close();
		
		/**
		 * calcul similarité et impression
		 */
		PrintWriter pw=new PrintWriter(new FileWriter(new File("/home/bebeshina/Documents/talad/marqueurs/d221/graphes_loc_sim.tsv")));
		for(Entry<String,Set<String>>e1:map.entrySet())
		{
			if(e1.getValue().size()>=2)
			{
				pw.println(e1.getKey());
				for(String s:e1.getValue())
				{
					pw.print(s+"|");
				}
				pw.println("\n");
				for(Entry<String,Set<String>>e2:map.entrySet())
				{
					if(!(e1.getKey().equals(e2.getKey())))
					{
						
						if(e2.getValue().size()>=2)
						{
							
							double score=0.0;
							score=this.sim(e1.getValue(), e2.getValue());
						
							if((score>=0.5)&&(score<1))
							{
								pw.printf("%f%n",score);
						
								pw.println(e2.getKey().replaceAll("\\w+(?= \\d+\\-\\d+)",""));
								for(String s:e2.getValue())
								{
									pw.print(s+"|");
								}
								pw.println("\n");
								pw.flush();
							}
							if(score>=0.3&&score<=0.5)
							{
								pw.printf("%f%n",score);
								
								pw.println(e2.getKey().replaceAll("\\w+(?= \\d+\\-\\d+)",""));
								for(String s:e2.getValue())
								{
									pw.print(s+"|");
								}
								pw.println("\n");
								pw.flush();
							}
							if(score<0.3)
							{
								String test=e1.getKey().replaceAll("[\\d\\-]", "");
								System.out.println(test);
								if(e2.getValue().contains(test))
								{
									pw.printf("%f%n",score);
							
									pw.println(e2.getKey().replaceAll("\\w+(?= \\d+\\-\\d+)",""));
									for(String s:e2.getValue())
									{
										pw.print(s+"|");
									}
									pw.println("\n\n");
									pw.flush();
								}
							}
						}
					}
					else
					{
						
					}
				}
				pw.println("----------------------------------------");
			}
			
		}
		
		pw.close();
	}
	/**
	 * passer les donnés de l'annotation dans neo4j pour visualiser et explorer
	 * @param f
	 * @throws IOException
	 */
	public void annot2Neo4j(String f) throws IOException
	{
		
		/**
		 * 1ere lecture
		 * récupérer les relations existantes en passant de "nomin" à "nomin-nom_fichier"
		 * pour moralisation, patriotisme, protectionnisme, isolationnisme, réfugié, violences obstétricales
		 * "id"	"source"	"marque_source"	"type_relation"	"cible"	"marque_cible"	"ref_document"
			1	"antipatriotisme"	"N_ent_abstraite"	"r_association"	"une voie sans issue"	"REPERE"	"1264-31"
			2	"moralisation"	"NOMINATION"	"r_association"	"transparence"	"REPERE"	"2137-47"
			3	"une frange de notre électorat"	"Ac"	"rs_identite"	"le partisan du"	"PRa_cadre"	"595-9"
			>> dans une arraylist
		 */
		Set<String> nl=new HashSet<String>();
		nl.add("moralisation");
		nl.add("protectionnisme");
		nl.add("patriotisme");
		nl.add("isolationnisme");
		nl.add("Europe");
		nl.add("violences obstétricales");
		nl.add("réfugiés climatiques");
		
		
		BufferedReader br=new BufferedReader(new FileReader(f));
		String line;
//		ArrayList<String> rel2import=new ArrayList<>();
		
		Map<String, Set<String>> map=new HashMap<String,Set<String>>();
		Set<String> set=new HashSet<String>();
		String ref="";
//		String[]rels=new String[6];
		
		System.out.println("start...");
		
		br.readLine();
		while((line=br.readLine())!=null)
		{
			String[]str=line.split("\t");
			String ns=str[1];
			String nc=str[4];
			
			if(ns.endsWith(","))
			{
				ns=ns.replaceAll("\\,", "");
			}
			if(nc.endsWith(","))
			{
				nc=ns.replaceAll("\\,", "");
			}
			if(nl.contains(str[1]))
			{
				ns=str[1].replaceAll("\\,", "")+"-"+str[6];
			}
			if(nl.contains(str[4]))
			{
				nc=str[4].replaceAll("\\,", "")+"-"+str[6];
			}
			
			if(str[6].equals(ref))
			{
				if(map.containsKey(ns))
				{
					set=new HashSet<String>();
					set=map.get(ns);
					set.add(str[3]+";"+nc);
					set.add("r_categorie"+";"+str[2]);
					map.replace(ns, set);
				}
				if(map.containsKey(nc))
				{
					set=new HashSet<String>();
					set=map.get(nc);
					set.add("r_categorie"+";"+str[5]);
					map.replace(nc, set);
				}
			}
			else
			{
				/**
				 * passage nouveau document
				 */
				ref=str[6];
				set=new HashSet<String>();
				set.add(str[3]+";"+nc);
				set.add("r_categorie"+";"+str[2]);
				map.put(ns, set);
				
				set.clear();
				set.add("r_categorie"+";"+str[5]);
				map.put(nc, set);
				
			}
		}
		br.close();
		/**
		 * impression map de départ
		 */
		Map<String,Integer>tmap=new HashMap<String,Integer>();
		int counter=1;
		
		PrintWriter pw=new PrintWriter(new FileWriter(new File("/home/bebeshina/Documents/talad/marqueurs/d221/graphes_initiaux.csv")));
		
		String header="tid;source;relation;cible";
		
		pw.println(header);
		
		int n=1;
		for(Entry<String,Set<String>>e:map.entrySet())
		{
			if(!tmap.containsKey(e.getKey()))
			{
				tmap.put(e.getKey(),counter);
				counter++;
			}
			for(String s:e.getValue())
			{
				String ent=n+";"+e.getKey()+";"+s;
				n++;
				pw.println(ent);
				pw.flush();
				System.out.println(s);
				String t=s.split("\\;")[1];
				
				if(!tmap.containsKey(t))
				{
					tmap.put(t,counter);
					counter++;
				}
			}
		}
		pw.flush();
		pw.close();
		
		
		
		
		/**
		 * fusion dans un nouveau map
		 */
		int i=1;
		Map<String, Set<String>> cmap1=new HashMap<String,Set<String>>();
		Map<String, Set<String>> decote=new HashMap<String,Set<String>>();
		
		Map<String,String> conversion=new HashMap<String,String>();
		
		System.out.println("taille map initiale = "+map.size());
		
		/**
		 * premier passage fusion dans cmap1
		 */
		
		for(Entry<String,Set<String>>e1:map.entrySet())
		{
			for(Entry<String,Set<String>>e2:map.entrySet())
			{
					if(!(e1.getKey().equals(e2.getKey())))
					{
						double score=0.0;
						score=this.sim(e1.getValue(), e2.getValue());
	
						String key1=e1.getKey().replaceAll("[\\d-]+", "");
						String key2=e2.getKey().replaceAll("[\\d-]+", "");
		
						if(key1.equals(key2))
						{
							
							if(score>=0.5)
							{
								/**
								 * fusionner
								 */
								String key="";
								if(conversion.containsKey(e1.getKey()))
								{
									key=conversion.get(e1.getKey());
								}
								else
								{
									key=key1+"-"+i;
									conversion.put(e1.getKey(),key);
									i++;
								}
								
								if(conversion.containsKey(e2.getKey()))
								{
									key=conversion.get(e2.getKey());
								}
								else
								{
									key=key1+"-"+i;
									conversion.put(e2.getKey(),key);
									i++;
								}
								
								
								Set<String>loc=new HashSet<String>();
								
								loc=e1.getValue();
								loc.addAll(e2.getValue());
								loc.add("r_proche"+";"+e2.getKey());
								loc.add("r_proche"+";"+e1.getKey());
								
								cmap1.put(key, loc);
								
							}
						}
						
					}
				}
			}
		System.out.println("taille cmap1  = "+cmap1.size());
		/**
		 * autres paires que les paires équivalentes
		 * on récupère seulement ce qui n'est pas fusionné
		 */
		for(Entry<String,Set<String>>e1:map.entrySet())
		{
			if(!conversion.containsKey(e1.getKey()))
			{
				decote.put(e1.getKey(), e1.getValue());
			}
		}
		for(Entry<String,Set<String>>e2:map.entrySet())
		{
			
			if(!conversion.containsKey(e2.getKey()))
			{
				decote.put(e2.getKey(), e2.getValue());
			}		
		}

		System.out.println("taille decote = "+decote.size());		
		/**
		 * deuxième passage (fusion entre les entrée de cmap) 
		 */
		Map<String, Set<String>> cmap2=new HashMap<String,Set<String>>();
		for(Entry<String,Set<String>>e1:cmap1.entrySet())
		{
			for(Entry<String,Set<String>>e2:cmap1.entrySet())
			{
				if(!(e1.getKey().equals(e2.getKey())))
				{
					String key1=e1.getKey().replaceAll("[\\d-]+", "");
					String key2=e2.getKey().replaceAll("[\\d-]+", "");
	
					if(key1.equals(key2))
					{
						double score=0.0;
						score=this.sim(e1.getValue(), e2.getValue());
						if(score>=0.5)
						{
							Set<String>loc=new HashSet<String>();
							
							loc=e1.getValue();
							loc.addAll(e2.getValue());
							loc.add("r_proche"+";"+e2.getKey());
							loc.add("r_proche"+";"+e1.getKey());
							
							String key="";
							if(conversion.containsKey(e1.getKey()))
							{
								key=conversion.get(e1.getKey());
							}
							else
							{
								key=key1+"-"+i;
								conversion.put(e1.getKey(),key);
								i++;
							}
							
							if(conversion.containsKey(e2.getKey()))
							{
								key=conversion.get(e2.getKey());
							}
							else
							{
								key=key1+"-"+i;
								conversion.put(e2.getKey(),key);
								i++;
							}
							cmap2.put(key, loc);
						}
					}
				}
			}
		}
		
		System.out.println("taille cmap2  = "+cmap2.size());
		
		/**
		 * troisième passage 
		 */
		Map<String, Set<String>> cmap3=new HashMap<String,Set<String>>();
		for(Entry<String,Set<String>>e1:cmap2.entrySet())
		{
			for(Entry<String,Set<String>>e2:cmap2.entrySet())
			{
				if(!(e1.getKey().equals(e2.getKey())))
				{
					String key1=e1.getKey().replaceAll("[\\d-]+", "");
					String key2=e2.getKey().replaceAll("[\\d-]+", "");
	
					if(key1.equals(key2))
					{
						double score=0.0;
						score=this.sim(e1.getValue(), e2.getValue());
						if(score>=0.5)
						{
							Set<String>loc=new HashSet<String>();
							
							loc=e1.getValue();
							loc.addAll(e2.getValue());
							loc.add("r_proche"+";"+e2.getKey());
							loc.add("r_proche"+";"+e1.getKey());
							
							String key="";
							if(conversion.containsKey(e1.getKey()))
							{
								key=conversion.get(e1.getKey());
							}
							else
							{
								key=key1+"-"+i;
								conversion.put(e1.getKey(),key);
								i++;
							}
							
							if(conversion.containsKey(e2.getKey()))
							{
								key=conversion.get(e2.getKey());
							}
							else
							{
								key=key1+"-"+i;
								conversion.put(e2.getKey(),key);
								i++;
							}
							cmap3.put(key, loc);
						}
					}
				}
			}
		}
		System.out.println("taille cmap3  = "+cmap3.size());
		
		
		/**
		 * on met tout ensemble
		 */
		Map<String, Set<String>> fmap=new HashMap<String,Set<String>>();
		
		fmap.putAll(cmap3);
		fmap.putAll(decote);
		
		System.out.println("taille fmap  = "+fmap.size());
		/**
		 * dans fmap les entrées fusionnées et les relations similarité , les autres laissés tels quels
		 * on crée le map final
		 */
		
		int j=1;
		Map<String, Set<String>> final_map=new HashMap<String,Set<String>>();
		Map<String, Set<String>> done=new HashMap<String,Set<String>>();
		for(Entry<String,Set<String>>e1:fmap.entrySet())
		{
			for(Entry<String,Set<String>>e2:fmap.entrySet())
			{
				if(!(e1.getKey().equals(e2.getKey())))
				{
					double score=0.0;
					score=this.sim(e1.getValue(), e2.getValue());
					
					String key1=e1.getKey().replaceAll("[\\d-]+", "");
					String key2=e2.getKey().replaceAll("[\\d-]+", "");
					if(score==0.0)
					{
						if(key1.equals(key2))
						{
							/**
							 * raffiner
							 */
							Set<String>rs=new HashSet<String>();
							String key=key1;
							/**
							 * créer le générique avec raffinements
							 */
							
							if(conversion.containsKey(e1.getKey()))
							{
								rs.add("r_raffinement"+";"+conversion.get(e1.getKey()));
								done.put(e1.getKey(),e1.getValue());
							}
//							else
//							{
//								rs.add("r_raffinement"+";"+e1.getKey());
//							}
							if(conversion.containsKey(e2.getKey()))
							{
								rs.add("r_raffinement"+";"+conversion.get(e2.getKey()));
								done.put(e2.getKey(),e2.getValue());
							}
//							else
//							{
//								rs.add("r_raffinement"+";"+e2.getKey());
//							}
							
							if(final_map.containsKey(key))
							{
								Set<String> lr=new HashSet<String>();
								lr=final_map.get(key);
								lr.addAll(rs);
								final_map.replace(key, lr);
							}
							else
							{
								final_map.put(key, rs);
							}
						}
						
					}
					else
					{
						/**
						 * reporter tel quel
						 */
						if(!done.containsKey(e1.getKey()))
						{
							final_map.put(e1.getKey(), e1.getValue());
						}
						
						if(!done.containsKey(e2.getKey()))
						{
							final_map.put(e2.getKey(), e2.getValue());
						}
					}
				}
			}
		}
		
		System.out.println("taille final_map  = "+final_map.size());
		/**
		 * impression
		 */
		
		PrintWriter pw1=new PrintWriter(new FileWriter(new File("/home/bebeshina/Documents/talad/marqueurs/d221/graphes_fusion.tsv")));
		
		pw.println(header);
		int m=1;
		for(Entry<String,Set<String>>e:final_map.entrySet())
		{
			if(!tmap.containsKey(e.getKey()))
			{
				tmap.put(e.getKey(), counter);
				counter++;
			}
			for(String s:e.getValue())
			{
				String ent=m+";"+e.getKey()+";"+s;
				m++;
				pw1.println(ent);
				pw1.flush();
				String t=s.split("\\;")[1];
				if(!tmap.containsKey(t))
				{
					tmap.put(t,counter);
					counter++;
				}
			}
		}
		pw1.close();
		PrintWriter pw2=new PrintWriter(new FileWriter(new File("/home/bebeshina/Documents/talad/marqueurs/d221/termes.csv")));
		
		for(Entry<String,Integer>e:tmap.entrySet())
		{
			pw2.println(e.getValue()+","+e.getKey());
		}
		pw2.flush();
		pw2.close();
		
		PrintWriter pw3=new PrintWriter(new FileWriter(new File("/home/bebeshina/Documents/talad/marqueurs/d221/relations_init.csv")));
		counter=1;
		for(Entry<String,Set<String>>e:map.entrySet())
		{
			for(String s:e.getValue())
			{
				String t=s.split("\\;")[1];
				pw3.println(counter+";"+tmap.get(e.getKey())+";"+s.split("\\;")[0]+";"+tmap.get(t));
				counter++;
			}
		}
		pw3.flush();
		pw3.close();
		
		PrintWriter pw4=new PrintWriter(new FileWriter(new File("/home/bebeshina/Documents/talad/marqueurs/d221/relations_fus.csv")));
		counter=1;
		for(Entry<String,Set<String>>e:final_map.entrySet())
		{
			for(String s:e.getValue())
			{
				String t=s.split("\\;")[1];
				pw4.println(counter+";"+tmap.get(e.getKey())+";"+s.split("\\;")[0]+";"+tmap.get(t));
				counter++;
			}
		}
		pw4.flush();
		pw4.close();
	}
	
	
	
	public double sim(Set<String>s1, Set<String>s2)
	{
		double score=0.0;
		Set<String> u=new HashSet<String>();
		Set<String>i=new HashSet<String>();
		u.addAll(s1);
		u.addAll(s2);
		i.addAll(s1);
		i.retainAll(s2);
		double z=Double.valueOf(i.size());
		score=(Double)z/u.size();
		
		
		return score;
	}
	
	
	
	/**
	 * TODO:couvrir les aspects suivants:
	 * cf. Arstein et Peosio, 2008
	 * - validity ( les catégories sont-elles correctes?), validité du schéma d'annotation
	 * - reliability (accord intra, inter annotateur plus gold standard)
	 * 		#stability (intra)
	 * 		#reproductibility (inter)
	 * 		#accuracy (gs)
	 * alignement : choix de l'annotation finale
	 * accord entre -1 et 1
	 */
	public Map<String,String> tmap=new HashMap<String,String>();
	public void countBratAnnotations(String d) throws IOException
	{
		File[]dir=new File(d).listFiles();
		PrintWriter pw=null;
		
		OutputStream os = null;
		OutputStreamWriter osw = null;
		
		OutputStream or = null;
		OutputStreamWriter orw = null;
		
		OutputStream orc = null;
		OutputStreamWriter orcw = null;
		
		File out=new File("/home/bebeshina/Documents/talad/marqueurs/structures_vf/segments.tsv");
		File outr=new File("/home/bebeshina/Documents/talad/marqueurs/structures_vf/relations.tsv");
		File outrc=new File("/home/bebeshina/Documents/talad/marqueurs/structures_vf/relations_en_clair.tsv");
		
//		List<String> tlist=new ArrayList<String>();
//		Set<String>tset=new HashSet<String>();
//		List<String> rlist=new ArrayList<String>();
		int i=0;
		int j=0;
		Map<String,Integer> map=new HashMap<String,Integer>();
		for(File f:dir)
		{
			if(f.getName().endsWith(".ann"))
			{
				os = new FileOutputStream(out,true);
				osw=new OutputStreamWriter(os,"UTF-8");
				
				orc = new FileOutputStream(outrc,true);
				orcw=new OutputStreamWriter(orc,"UTF-8");
				
				pw=new PrintWriter(new FileWriter(new File("/home/bebeshina/Documents/talad/marqueurs/structures_vf/sequences.tsv"),true));
				
				String fname="\n"+f.getName().replaceAll(".ann", "");
				pw.println(fname);
				
				BufferedReader br=new BufferedReader(new FileReader(f));
				String line;
				Map<String,String> tmap=new HashMap<String,String>();
				LinkedList<Vector<Object>> seq=new LinkedList<Vector<Object>>();
				while((line=br.readLine())!=null)
				{
					if(line.startsWith("T"))
					{
						String split_1="(?<=T\\d{1,4})\\t";
//						String sub_split_1="(?<=(\\d){1,2})\\t";
						try 
						{
							String[]temp=line.split(split_1);
//							System.out.println(temp[0]+":"+temp[1]);
							
							tmap.put(temp[0],temp[1]);
							
							Pattern pt=Pattern.compile("(?<label>[A-Za-z_]+)\\s(?<coord>\\d+\\s\\d+(\\;\\d+\\s+\\d+)?)\t(?<term>.+)");
							Matcher m=pt.matcher(temp[1]);
							if(m.find())
							{
								j++;
								String w=j+"\t"+m.group("term")+"\t"+m.group("label")
								.replaceAll("PROCEDE_AJUSTEMENT", "PRa")
								.replaceAll("ATTITUDE_INTERACTION", "Ai")
								.replaceAll("ATTITUDE_CADRAGE", "Ac")
								.replaceAll("ATTITUDE_JUSTIFICATION", "Aj")
								.replaceAll("PROCEDE_REJET", "PRr")
								.replaceAll("ENT_ABSTRAITE", "N_ent_abstraite")
								+"\t"+m.group("coord")+"\t"+f.getName().replaceAll(".ann", "");
								
								map.put(temp[0], j);
								
								osw.write(w,0,w.length());
								osw.write("\n");
								osw.flush();
							
								
								/**
								 * ordonnancement 
								 */
								
								String tm=m.group("coord");
								/**
								 * chercher le min et le max
								 */
								int start,end,min,max;
								if(tm.contains(";"))
								{
									String[] sub=tm.split("\\;");
									for(int n=0;n<sub.length;n++)
									{
										start=Integer.valueOf(sub[n].split("\\s")[0]);
										end=Integer.valueOf(sub[n].split("\\s")[1]);
										Vector<Object> v=new Vector<Object>();
										v.add(0, start);
										v.add(1, end);
										v.add(2, m.group("label"));
										v.add(3, m.group("term"));
										if(seq.isEmpty())
										{
											seq.addFirst(v);
										}
										else
										{
											min= (Integer)seq.getFirst().get(0);
											max= (Integer)seq.getLast().get(1);
//											if(min<start)
//											{
//												seq.addLast(v);
//											}
//											else
//											{
//												seq.addFirst(v);
//											}
											if(min>start)
											{
												seq.addFirst(v);
											}
											else
											{
												if(max<start)
												seq.addLast(v);
											}
										}
									}
									
								}
								else
								{
									start=Integer.valueOf(tm.split("\\s")[0]);
									end=Integer.valueOf(tm.split("\\s")[1]);
									Vector<Object> v=new Vector<Object>();
									v.add(0, start);
									v.add(1, end);
									v.add(2, m.group("label"));
									v.add(3, m.group("term"));
									if(seq.isEmpty())
									{
										seq.addFirst(v);
									}
									else
									{
										min= (Integer)seq.getFirst().get(0);
										max= (Integer)seq.getLast().get(1);
										if(min>start)
										{
											seq.addFirst(v);
										}
										else
										{
											if(max<start)
											seq.addLast(v);
										}
									}
								}
							}
						}
						catch(ArrayIndexOutOfBoundsException e)
						{
//							System.out.println("processing "+f.getName()+">>>"+line);
						}
						
					}
					/****/
				}
				br.close();
				osw.close();
				
				
				/**
				 * impression séquence
				 */
				
				for(int p=0;p<seq.size();p++)
				{
					pw.println(seq.get(p));
					
				}
				pw.flush();
				System.out.println();

				
				
				/**
				 * fin impression sequence
				 */
				
				br=new BufferedReader(new FileReader(f));
				or = new FileOutputStream(outr,true);
				orw=new OutputStreamWriter(or,"UTF-8");
				while((line=br.readLine())!=null)
				{
					if(line.startsWith("R"))
					{
						try
						{
//						System.out.println("line starts with R "+line);
						Pattern p=Pattern.compile("(?<type>r(s)?\\_[a-z_]+)\\sArg1\\:(?<source>T\\d+)\\sArg2\\:(?<cible>T\\d+)");
						Matcher m=p.matcher(line);
						if(m.find())
						{
//							System.out.println(m.group("source")+"--"+m.group("type")+"-->"+m.group("cible"));
							Pattern pt=Pattern.compile("(?<label>[A-Za-z_]+)\\s(?<coord>\\d+\\s\\d+(\\;\\d+\\s+\\d+)?)\t(?<term>.+)");
							String s="";
							String t="";
							String l1="";
							String l2="";
						
							Matcher ms=pt.matcher(tmap.get(m.group("source")));
							Matcher mt=pt.matcher(tmap.get(m.group("cible")));
							if(ms.find())
							{
								s =ms.group("term");	
								l1=ms.group("label");
							}
							
							if(mt.find())
							{
								t=mt.group("term");	
								l2=mt.group("label");
							}
							i++;
							orcw.write(i+"\t"+s+"\t"+l1+"\t"+m.group("type")+"\t"+t+"\t"+l2+"\t"+f.getName().replaceAll(".ann", ""));
							orcw.write("\n");
							orcw.flush();
							
							
							orw.write(i+"\t"+map.get(m.group("source"))+"\t"+m.group("type")+"\t"+map.get(m.group("cible"))+"\t"+f.getName().replaceAll(".ann", ""));
							orw.write("\n");
							orw.flush();
						}
						}
						
						catch(NullPointerException e)
						{
							System.out.println("PROBLEM WITH "+fname);
						}
					}
				}
				br.close();
				orw.close();
				orcw.close();
				pw.close();
			}
			
			
			
		
					
//						
//						
//						
////							if(temp[0].contains("PRr"))
////							{
////								System.out.println(temp[0]+" /// "+temp[1]);
////								tlist.add(temp[1]);
////								tset.add(temp[1]);
////							}
////							if(temp[0].contains("PROCEDE_REJET"))
////							{
////								System.out.println(temp[0]+" /// "+temp[1]);
////								tlist.add(temp[1]);
////								tset.add(temp[1]);
////							}
//							
//							
//						}
						
//						String split_2="\\s\\d{1,4}\\t";
//						try 
//						{
//							String[]temp=line.split(split_2);
//							if(temp[0].contains("PRr"))
//							{
//								System.out.println(temp[0]+" /// "+temp[1]);
//								tlist.add(temp[1]);
//								tset.add(temp[1]);
//							}
//							if(temp[0].contains("PROCEDE_REJET"))
//							{
//								System.out.println(temp[0]+" /// "+temp[1]);
//								tlist.add(temp[1]);
//								tset.add(temp[1]);
//							}
//							
//							
//						}
						
				
					}
//					if(line.startsWith("R"))
//					{
//						Pattern sp=Pattern.compile("Arg1\\:.+");
//						Pattern tp=Pattern.compile("Arg2\\:.+");
//						Pattern typ=Pattern.compile("(?<=\t)r\\_[a-z]+(?=\\s)");
//						Matcher msp=sp.matcher(line);
//						Matcher mtyp=typ.matcher(line);
//						Matcher mtp=tp.matcher(line);
//						
//						StringBuilder res=new StringBuilder();
//						if(msp.find())
//						{
//							tmap.get(msp.group(0));
//							
////							StringBuilder res=new StringBuilder();
//							if(msp.find())
//							{
//								res.append(msp.group(0));
//							}
//							res.append("--");
//							
//							if(mtyp.find())
//							{
//								res.append(mtyp.group(0));
//							}
//							res.append("-->");
//							if(mtp.find())
//							{
//								res.append(mtp.group(0));
//							}
//							
////						}
//						
////						rlist.add(res.toString());
//					}
			
				
				
				
//			}
//		}
//		System.out.println("RELS "+rlist.size());
//		System.out.println("TERMS "+tlist.size());
//		Set<String> tset=
//		for(String s:tset)
//		{
//			System.out.println(s);
//		}
	}
	/**
	 * TODO accord observé
	 * accord : qd un item est assigné à la même catégorie
	 * diviser par le nombre d'items pour obtenir un pourcentage
	 */
	private BufferedReader br;
	private String line;
	
	@SuppressWarnings("unused")
	private String[]str;
	
//	private static final File fa1=new File("");
//	private static final File fa2=new File("");
	private final String separator="\\;";
	public void units() throws IOException
	{
		br=new BufferedReader(new FileReader(new File("")));
		line="";
		br.readLine();
		while((line=br.readLine())!=null)
		{
			str=line.split(separator);
			
		}
		br.close();
	}
	/**
	 * 	
	 * @throws IOException
	 */
	public void test() throws IOException
	{
		this.getCotextePairs("corpus/annote/annot_1bis.csv", "corpus/annote/annot_2bis.csv");
	}
	
	public static void main(String[] args)
	{
		AnnotationStatistics as=new AnnotationStatistics();
		try
		{
			as.test();
//			as.annot2Neo4j("/home/bebeshina/Documents/talad/marqueurs/structures_vf/humain/relations_input.tsv");
//			as.printSequences("/home/bebeshina/Documents/talad/annotations_reticular_vf_f/");//"/home/bebeshina/Documents/talad/annotations_reticular/"
		}
		catch(IOException io)
		{
			
		}
	}
}
