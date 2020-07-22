package view;

import java.io.BufferedReader;
//import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
//import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintStream;
//import java.io.PrintWriter;
//import java.io.Writer;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
//import java.util.HashSet;
import java.util.Map;
//import java.util.HashMap;
//import java.util.Map;
import java.util.Map.Entry;
import java.util.NoSuchElementException;
import java.util.Set;

import org.apache.commons.configuration2.ex.ConfigurationException;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import data.TNRDataHandler;

public class SKOSView extends TNRDataHandler
{
	final Logger logger = LoggerFactory.getLogger(SKOSView.class);
	
	protected PrintStream stream;
	protected FileOutputStream file;
	public SKOSView() throws ClassNotFoundException, InstantiationException, IllegalAccessException, ConfigurationException, SQLException
	{
		super();
//		System.out.println(super.connection.getClientInfo());
		/**
		 * récupération de la date du jour
		 */
		
		SimpleDateFormat df = new SimpleDateFormat("yyyyMMdd_HHmmss");  
		Date date = new Date(System.currentTimeMillis());
		String d=df.format(date);
		
		/**
		 * préparation du StringBuilder
		 */
		this.skos_sb=new StringBuilder();
		this.ontolex_sb=new StringBuilder();
		
		/**
		 * écriture dans un fichier
		 */
		
		System.err.println("écriture fichier "+"views/skos_output_"+d+".ttl");
		try 
		{
			file = new FileOutputStream("./views/skos_output_"+d+".ttl");
			stream = new PrintStream(file);
			System.setOut(stream);
		} 
		catch (FileNotFoundException e1) 
		{
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}  
	}
	public final String prefix="tnr:";
	public final String period=";\n";
	public final String stop=".\n";
	public String scheme;
	//"rdf:type skos:ConceptScheme;\n" 
//	+ "dc:title
	public static final String skos_prolog="@prefix skos: <http://www.w3.org/2004/02/skos/core#> .\n" 
	+ "@prefix rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#> .\n"
	+ "@prefix rdfs: <http://www.w3.org/2000/01/rdf-schema#> .\n"
	+ "@prefix owl: <http://www.w3.org/2002/07/owl#> .\n"
	+ "@prefix dct: <http://purl.org/dc/terms/> .\n"
	+ "@prefix foaf: <http://xmlns.com/foaf/0.1/> .\n"  
	+ "@prefix tnr: <https://opentheso.huma-num.fr/opentheso/webresources/theso/TNR> ."+"\n";
	
	public final String skos_properties=
			"tnr:exemple rdf:type owl:ObjectProperty "
			+period
			+ "rdfs:subPropertyOf skos:related"
			+stop;
	
//			"<effet> rdf:type owl:ObjectProperty ;\n" + 
//			" rdfs:subPropertyOf skos:related ;\n" + 
//			"  owl:inverseOf <cause> .";

	public static final String ontolex_prolog="@prefix lemon:http://www.w3.org/ns/lemon/all .\n"
			+"@prefix skos: <http://www.w3.org/2004/02/skos/core#> .\n";
	
	public StringBuilder skos_sb;
	public StringBuilder ontolex_sb;
	
	
	/**
	 * 
	 * @param s
	 * généraion du nom de classe ou schéma approprié
	 * @return
	 * @throws SQLException 
	 */
	public String cname(String s) throws SQLException
	{
		String res=null;
		nom=s;
		entrymap=super.Relations(nom, super.psGetRelations, super.psShowRelations);
		
		s=StringUtils.stripAccents(s);
		s.replaceAll("(\\(|\\))", " ")
		.replaceAll("\\'", "")
		.replaceAll("\\)", "");
		
		if((s.contains(" "))|(s.contains("-")))
		{
			this.logger.info("processing {}", s);
			res=prefix;
			String [] str=s.split("(\\s|(l|d)(’|')|-|\\()+");
			for(String st:str)
			{
				st=st.substring(0, 1).toUpperCase() + st.substring(1);
				res=res+st.replaceAll("\\)", "")
				.replaceAll("\\'","");
			}
		}
		else
		{
			res=prefix;
			res=res+s.substring(0, 1).toUpperCase() + s.substring(1);
		}
		
		return res;
	}
	
	String nom;
	public Map<Integer,Set<String>> entrymap=new HashMap<Integer,Set<String>>();
	
	public String terme()
	{
		String r=null;
		if(this.nom.contains(" "))
		{
			String []str=this.nom.split(" ");
			r="<#\""
					+ this.nom
					+"\">"
					+ "a ontolex:MultiWordExpression"
					+ period
					+ "lexinfo:partOfSpeech lexinfo:noun\""
					+ "\n" 
					+ "decomp:constituent "
					+"\n";
			
			//autre option serait d'utiliser decomp:subterm <#summer>, <#school> .
			
			StringBuilder sb=new StringBuilder();
			for(int i=0;i<str.length;i++)
			{
				sb.append(this.nom+"/"+str[i]+"> ,\"\n");	
			}
			for(int i=0;i<str.length;i++)
			{
				sb.append("rdf:_"+i+"<#"+this.nom+"/"+str[i]+"> ,\"\n");	
			}
			r=sb.toString();
			
		}
		else
		{
			r="<#"+this.nom+"> a ontolex:Word ;"
					+ "lexinfo:partOfSpeech lexinfo:noun"
					+period;
		}
		
		return r;
	}
	/**
	 * Obtention de la définition
	 * @return
	 */
	public boolean hasDefinition()
	{
		boolean b=false;
		if(this.entrymap.containsKey(7))
		{
			b=true;
		}
		return b;	
	}
	
	public String definition()
	{
		String s=null;
		s=this.entrymap.get(7).stream().findFirst().get();
		String r= "skos:definition \""
				+ s.replaceAll("\"", "''").trim()
				+"\"@fr"
				+period;
		this.logger.info(r);
		return r;
	}
	
	public boolean hasExample()
	{
		boolean b=false;
		if(this.entrymap.containsKey(8))
		{
			b=true;
		}
		return b;	
	}
	
	public String example()
	{
		String s=null;
		s=this.entrymap.get(8).stream().findFirst().get();
		String r= "skos:example \""
				+ s
				+"\"@fr"
				+period;
		this.logger.info(r);
		return r;
	}
	
	public boolean hasVariant()
	{
		boolean b=false;
		if(this.entrymap.containsKey(3))
		{
			b=true;
		}
		return b;	
	}
	
	public String variante()
	{
		String s=null;
		String r=null;
		try
		{
			s=this.entrymap.get(3).stream().findFirst().get();
			r= "skos:altLabel \""
					+ s
					+"\"@fr"
					+period;
			this.logger.info(r);
		}
		catch(NoSuchElementException e)
		{
			
		}
		return r;
	}

	StringBuilder sb;
	public void candidats(String path) throws IOException, SQLException
	{
//		Set<String> set=new HashSet<String>();
		BufferedReader br=new BufferedReader(new FileReader(path));
		String line="";
		sb=new StringBuilder();
		sb.append(skos_prolog);
		sb.append("\n");
		
		while((line=br.readLine())!=null)
		{
			if(!line.startsWith("-"))
			{
				String c=this.cname(line);
				sb.append(c+" rdf:type skos:Concept"+period);
				sb.append("skos:prefLabel	\""+line.replaceAll("(\\(|\\))", " ")+"\"@fr"+stop+"\n");
			}
		}
		br.close();
		OutputStreamWriter osw=new OutputStreamWriter(file);
		osw.write(sb.toString());
		osw.close();
	}
	
	public String candidates(String path) throws IOException, SQLException
	{
		String result="";
		BufferedReader br=new BufferedReader(new FileReader(path));
		String line="";
		sb=new StringBuilder();
		sb.append("\n");
		
		// déclarer collection des candidats
		
		String coll=prefix+"Candidats";
		sb.append(coll+" rdf:type skos:Collection"+period);
		sb.append("skos:prefLabel \"candidats\"@fr"+period);
		StringBuilder lsb=new StringBuilder();
		while((line=br.readLine())!=null)
		{
			if(!(line.startsWith("-")))
			{
				String c=this.cname(line.replaceAll("(\\*|\\?)",""));
				sb.append("skos:member "+c+period);
				
				lsb.append(c+" rdf:type skos:Concept"+period);
				lsb.append("skos:memberOf "+coll+period);
				lsb.append("skos:inScheme tnr:TNR;"+period);
				lsb.append("skos:note \"terme candidat\"@fr"+period);
				lsb.append("skos:prefLabel	\""+line.replaceAll("(\\(|\\))", " ")+"\"@fr"+stop+"\n");
			}
		}
		br.close();
		result=sb.toString();
		
		int len=result.length();
		result.replace(result.substring(len-3,len-2),stop);
		lsb.insert(0,result);
		result=lsb.toString();
		
		return result;
	}
	
	public String scheme()
	{
		String result="tnr:TNR rdf:type skos:ConceptScheme;\n" + 
				"dc:title \"Thesaurus de la nomination et de la référence\"@fr;\n" + 
				"skos:hasTopConcept tnr:Reference;\n" + 
				"skos:hasTopConcept tnr:Discours;\n" + 
				"skos:hasTopConcept tnr:ExpressionReferentielle;\n" + 
				"skos:hasTopConcept tnr:Lexicologie;\n" + 
				"skos:hasTopConcept tnr:RelationReferentielle;\n" + 
				"skos:hasTopConcept tnr:InteractionEngagementDialogisme;\n" + 
				"skos:hasTopConcept tnr:ModeDeReferenciation;\n" + 
				"skos:hasTopConcept tnr:Representations;\n" + 
				"skos:hasTopConcept tnr:RelationDeDiscours;\n" + 
				"dc:creator tnr:ajackiewicz."+
				"\n";
		return result;
	}
	
	public String collections() throws SQLException
	{
		String result="";
		sb=new StringBuilder();
		for(String s:super.categories())
		{
			String coll=this.cname("Collection "+s);
			sb.append(coll+" rdf:type skos:Collection"+period);
			sb.append("skos:prefLabel \""+"Collection "+s+"\"@fr"+period);
			
			
			for(Entry<String,String> e:super.tree_map.entrySet())
			{
				if(e.getValue().equals(s))
				{
					String member=this.cname(e.getKey());
					sb.append("skos:member "+member+period);
				}
			}
			
			String res=sb.toString();
			int coord = res.lastIndexOf(";");
			sb.delete(coord, sb.length());
			sb.append(stop+"\n");
//			res.replace(res.substring(coord), "");
//			this.logger.info(res);
//			this.logger.info("substring -2  : {}--> {}", res.substring(len-2), stop);
			
		}
		result=sb.toString();
		return result;
	}
	
	
	public String concepts() throws SQLException
	{
		String result="";
		sb=new StringBuilder();
		for(String s:super.categories())
		{

			String sn=this.cname(s);
			String cn=this.cname("Collection "+s);
			sb.append(sn+" rdf:type skos:Concept"+period);
			sb.append("skos:topConceptOf tnr:TNR"+period);
			sb.append("skos:memberOf "+cn+stop);
//			this.nom=s;
			
			for(Entry<String,String> e:super.tree_map.entrySet())
			{
				if(e.getValue().equals(s))
				{
					/**
					 * il s'agit d'un concept
					 */
					String n=this.cname(e.getKey());
					
					sb.append(n+" rdf:type skos:Concept"+period);
					sb.append("skos:inScheme tnr:TNR"+period);
					sb.append("skos:broader "+sn+period);
//					sb.append("skos:memberOf "+cn+period);
					
					if(this.hasDefinition())
					{
						this.logger.info("has definition ");
						sb.append(this.definition());
					}
					if(this.hasExample())
					{
						this.logger.info("has example ");
						sb.append(this.example());
					}
					if(this.hasVariant())
					{
						sb.append(this.variante());
					}
					this.logger.info("!!! {}",n+" "+this.nom);
					sb.append("skos:prefLabel	\""+e.getKey().replaceAll("(\\(|\\))", " ")+"\"@fr"+stop+"\n");
					
					for(String str:super.stree_map.get(e.getKey()))
					{
						String m=this.cname(str);
						this.nom=str;
						sb.append(m+" rdf:type skos:Concept"+period);
						sb.append("skos:inScheme tnr:TNR"+period);
						sb.append("skos:broader	"+n+period);
//						skos_sb.append("skos:memberOf "+cn+period);
						
						if(this.hasDefinition())
						{
							sb.append(this.definition());
						}
						if(this.hasExample())
						{
							sb.append(this.example());
						}
						if(this.hasVariant())
						{
							sb.append(this.variante());
						}
						sb.append("skos:prefLabel	\""+str.replaceAll("(\\(|\\))", " ")+"\"@fr"+stop+"\n");
					}
				}
			}
		}
		result=sb.toString();
		return result;
	}
	public StringBuilder stb=new StringBuilder();
	public void doSKOS() throws SQLException, IOException
	{
		stb.append(skos_prolog+"\n");
		stb.append(this.scheme());
		stb.append(this.collections());
		stb.append(this.concepts());
		stb.append(this.candidates("input/candidats_terminologie.csv"));
		
		System.out.println(stb.toString());
	}
	
	public void buildSKOS() throws SQLException
	{
		this.logger.info("started");
		
		skos_sb.append(skos_prolog);
		skos_sb.append("\n");
//		skos_sb.append(skos_properties);
		
		skos_sb.append(prefix+"TNR"+" rdf:type skos:ConceptScheme"+period);
		skos_sb.append("dc:title \""+"Thesaurus de la nomination et de la référence\"@fr"+period);
		
		for(String s:super.categories())
		{
			String sn=this.cname(s);
			skos_sb.append("skos:hasTopConcept "+sn+period);
		}
		skos_sb.append("dc:creator tnr:ajackiewicz"+stop+"\n");
		
		for(String s:super.categories())
		{
			String sn=this.cname(s);
			scheme="TNR";
//			scheme="S_"+sn.replaceAll(prefix, "");
//			this.logger.info("nom {}",nom);
			this.nom=s;
			
			skos_sb.append(prefix+scheme+" rdf:type skos:ConceptScheme"+period);
			skos_sb.append("dc:title \""+s+"\"@fr"+period);
			skos_sb.append("skos:hasTopConcept "+sn+period);
			skos_sb.append("dc:creator tnr:ajackiewicz"+stop+"\n");
			
			skos_sb.append(sn+" rdf:type skos:Concept"+period);
			skos_sb.append("skos:prefLabel	\""+nom.replaceAll("(\\(|\\))"," ").substring(0, 1).toUpperCase() + nom.substring(1)+"\"@fr"+period);
			skos_sb.append("skos:topConceptOf "+prefix+"TNR"+stop+"\n");
			
			
			for(Entry<String,String> e:super.tree_map.entrySet())
			{
				if(e.getValue().equals(s))
				{
					/**
					 * il s'agit d'un concept
					 */
//					System.err.println("processing "+e.getKey());
					String n=this.cname(e.getKey());
					
					skos_sb.append(n+" rdf:type skos:Concept"+period);
					skos_sb.append("skos:broader "+sn+period);
					skos_sb.append("skos:inScheme "+prefix+scheme+period);
					if(this.hasDefinition())
					{
						skos_sb.append(this.definition());
					}
					if(this.hasExample())
					{
						skos_sb.append(this.example());
					}
					if(this.hasVariant())
					{
						skos_sb.append(this.variante());
					}
					skos_sb.append("skos:prefLabel	\""+nom.replaceAll("(\\(|\\))", " ")+"\"@fr"+stop+"\n");

//					System.out.println(skos_sb.toString());
					ontolex_sb.append(terme());
					
					for(String str:super.stree_map.get(e.getKey()))
					{
						String m=this.cname(str);
						skos_sb.append(m+" rdf:type skos:Concept"+period);
						skos_sb.append("skos:inScheme	"+prefix+scheme+period);
						skos_sb.append("skos:broader	"+n+period);
						
						if(this.hasDefinition())
						{
							skos_sb.append(this.definition());
						}
						if(this.hasExample())
						{
							skos_sb.append(this.example());
						}
						if(this.hasVariant())
						{
							skos_sb.append(this.variante());
						}
						skos_sb.append("skos:prefLabel	\""+nom.replaceAll("(\\(|\\))", " ")+"\"@fr"+stop+"\n");
						
						
//						System.out.println(skos_sb.toString());
						/**
						 * génération de la partie lexicale (OntoLex)
						 */
						ontolex_sb.append(terme());
					}
				}
//				System.out.print("\n");
			}
		}
		System.out.println(skos_sb.toString());
//		System.out.println(ontolex_sb.toString());
	}
	
	public static void main (String[] args) throws ClassNotFoundException, InstantiationException, IllegalAccessException, ConfigurationException, SQLException
	{
		SKOSView sv=new SKOSView();
		try 
		{
//			sv.candidats("input/candidats_terminologie.csv");
			sv.doSKOS();
		} 
		catch (IOException e) 
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
//		sv.buildSKOS();
	}
}
