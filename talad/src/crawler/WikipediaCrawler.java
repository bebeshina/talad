package crawler;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
//import java.io.FileWriter;
import java.io.IOException;
//import java.io.PrintWriter;
import java.text.ParseException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Locale;
import java.util.Map;
//import java.util.Map.Entry;
import java.util.Set;
import org.jsoup.Jsoup;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.json.*;
import org.jsoup.Connection.Response;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

/**
 * 
 * @author bebeshina
 * class for crawling wikipedia parts according to some criteria using API Wikidata
 */
/**
 * TODO 
 * commandline interface with
 * - input : list of terms
 * - output : file with term-definition pairs
 *
 */
public class WikipediaCrawler 
{
	
	/**
	 * constructor
	 */
	
	public WikipediaCrawler() {}
	
	
	/**
	 * configuration des paramètres utilisés pour la gestion des requetes à partir d'une ligne de commandes
	 * @return
	 */
	private static Options configParameters() {

	    final Option termeFileOption = Option.builder("t") 
	            .longOpt("terme") //
	            .desc("Terme pour lequel on effectue la recherche.") 
	            .hasArg(true) 
	            .argName("algo")
	            .required(false) 
	            .build();

	    final Option inFileOption = Option.builder("infile") 
	            .longOpt("infile") 
	            .desc("Chemin du fichier qui contient la liste de termes fournie en entrée (infile).") 
	            .hasArg(true) 
	            .argName("infile") 
	            .required(true) 
	            .build();

	    final Option outFileOption = Option.builder("outfile") 
	            .longOpt("outfile") 
	            .desc("Chemin du fichier dans lequel doivent être écrites les résultats de la recherche (outfile).") 
	            .hasArg(true) 
	            .required(false) 
	            .build();

	    final Option langFileOption = Option.builder("lang") 
	            .longOpt("langue") 
	            .desc("Langue de recherche.") 
	            .hasArg(true) 
	            .required(false) 
	            .build();
	    final Options options = new Options();

	    options.addOption(termeFileOption);
	    options.addOption(inFileOption);
	    options.addOption(outFileOption);
	    options.addOption(langFileOption);

	    return options;
	}
	
	
	/**
	 * information google pour un terme
	 */
	
	
	
	/**
	 * information wikipedia pour un terme
	 */
	public static final String baseURL="https://fr.wikipedia.org/wiki/";
	public static final String wikiURL = "https://fr.wikipedia.org/w/api.php?"
			+ "action=query"
			+ "&prop=extracts"
			+ "&titles=?"
			+ "&exintro="
			+ "&exsentences=5"
			+ "&explaintext="
			+ "&redirects="
			+ "&format=json";
	
	public static final String wikiInfoURL = "https://fr.wikipedia.org/w/api.php?"
			+ "action=query"
			+ "&prop=extracts"
			+ "&titles=?"
			+ "&exintro="
			+ "&exsentences=5"
			+ "&explaintext="
			+ "&redirects="
			+ "&format=json";
	
	public static void getPage(String s) throws IOException
	{
		
		String rep=s.substring(0, 1);
		rep=rep.toUpperCase(Locale.FRANCE);
		terme=s.replaceFirst(s.substring(0, 1),rep);
		url=baseURL+terme;
		System.out.println(url);
		getDocument();
		System.out.println(doc.text());
		Elements definition=doc.getElementsByTag("div[id='mw-content-text'][lang=\"fr\"][dir=\"ltr\"]");
		for(Element def:definition)
		{
			String[]str=def.ownText().split("(.|!|?)\\s");
			System.out.println(str.length);
			for(String sent:str)
			{
				System.out.println(sent);
			}
			
		}
	}
	
	public static String getWikipediaInformation(String s) throws IOException, ParseException
	{
		String d=null;
		if(s.contains(" "))
		{
			s=s.replaceAll("\\s", "%20");
		}
		url=wikiURL.replaceAll("\\&titles\\=\\?", "&titles="+s);
		System.out.println(url);
		
		getDocument();
		
		String json = doc.text();
		   
	    JSONObject obj = new JSONObject(json);
	    try 
	    {
		    JSONObject test=obj.getJSONObject("query");
	//	    System.out.println(test.toString());
		    JSONObject pages=test.getJSONObject("pages");
	//	    System.out.println(pages.toString());
		    String[]names=JSONObject.getNames(pages);
	//	    System.out.println(names[0]);
		    String num=pages.get(names[0]).toString();
	//	    System.out.println(num);
		    String[]def=num.split("\":\"");
		    d=def[1];
		    
		    System.out.println(d);
	    }
	    catch(JSONException e)
	    {
	    	return d;
	    }
	    
	    return d;
	}
	
	public static String getDefinition(String s) throws IOException, ParseException
	{
		String d=null;
		if(s.contains(" "))
		{
			s=s.replaceAll("\\s", "%20");
		}
		url=wikiURL.replaceAll("\\&titles\\=\\?", "&titles="+s);
		System.out.println(url);
		getDocument();
		String json = doc.text();
		   
	    JSONObject obj = new JSONObject(json);
	    try 
	    {
		    JSONObject test=obj.getJSONObject("query");
	//	    System.out.println(test.toString());
		    JSONObject pages=test.getJSONObject("pages");
	//	    System.out.println(pages.toString());
		    String[]names=JSONObject.getNames(pages);
	//	    System.out.println(names[0]);
		    String num=pages.get(names[0]).toString();
	//	    System.out.println(num);
		    String[]def=num.split("\":\"");
		    d=def[1];
		    System.out.println(d);
	    }
	    catch(JSONException e)
	    {
	    	return d;
	    }
	    
	    return d;
	}
	
	/**
	 * information wiktionnary pour un terme
	 */

	/**
	 * information publicis
	 */
	
	/**
	 * information cnrtl pour un terme
	 */
	
	/**
	 information SIL pour un terme
	 */
	
	/**
	 * information 
	 */
	/**
	 * crawler pour un terme
	 */
	
	
	/**
	 * crawler pour une liste de termes
	 * 
	 */
	
	public static final String filePath="resources/termes.txt";
	
	
	//https://fr.wikipedia.org/w/api.php?action=query&prop=extracts"&titles=?"&exintro="&exsentences=2"&explaintext="&redirects="&format=json";
	
	/**
	 * members
	 */
	public static String terme;
	public static String url;
	public static Document doc;
	public static  Response response;
	public static String definition;
	
	/**
	 * collections
	 */
	
	public static Set<String> termset=new HashSet<String>();
	public static Map<String,String>termmap=new HashMap<String,String>();
	public static Set<String> nodef=new HashSet<String>();
	
	
	
	public static void getDocument() throws IOException
	{
		doc=Jsoup.connect(url).ignoreHttpErrors(true).ignoreContentType(true).timeout(50000).get();
	}
	
	public void getResponse(String url) throws IOException
	{
		response=Jsoup.connect(url).followRedirects(true).ignoreHttpErrors(true).timeout(100000).execute();
	}
	
	

	
	
	
	
	public static void getList() throws IOException
	{
		BufferedReader br=new BufferedReader(new FileReader(new File(filePath)));
		String l="";
		while((l=br.readLine())!=null)
		{
			termset.add(l);
		}
		br.close();
	}
	
	public static void definitions() throws IOException, ParseException
	{
		getList();
		for(String t:termset)
		{
			String d=null;
			d=getDefinition(t);
			if(d!=null)
			{
				if(!(d.equals("\",\"title")))
				{
					termmap.put(t,d);
				}
				else
				{
					nodef.add(t);
				}
				
			}
			else
			{
				nodef.add(t);
			}
		}
	}
	
	public static void main(final String[] args) 
	{
		final Options options = configParameters();
		final CommandLineParser parser = new DefaultParser();
		try 
		{
			@SuppressWarnings("unused")
			final CommandLine line = parser.parse(options, args);
		} 
		catch (org.apache.commons.cli.ParseException e) 
		{
			e.printStackTrace();
		}
		
	}
	
//	public static void main (String[] args) throws IOException, ParseException
//	{
//		
//		
//		
//		definitions();
//		
//		System.out.println("termes avec définition "+termmap.size());
//		System.out.println("termes sans définition "+nodef.size());
//		
////		PrintWriter pw=new PrintWriter(new FileWriter(new File("definitions/wikipedia.csv")));
//		for(Entry<String,String> e:termmap.entrySet())
//			
//		{
//			System.out.println(e.getKey()+";"+e.getValue());
////			pw.println(e.getKey()+";"+e.getValue());
////			pw.flush();
//		}
////		pw.close();
////		
//		PrintWriter pw_nodef=new PrintWriter(new FileWriter(new File("definitions/nodef.txt")));
//		for(String e:nodef)
//			
//		{
//			pw_nodef.println(e);
//			pw_nodef.flush();
//		}
//		pw_nodef.close();
//	}
}
