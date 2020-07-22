package crawler;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;

import java.net.URLDecoder;
import java.net.URLEncoder;
import java.nio.charset.Charset;

import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

/**
 * 
 * @author bebeshina-clairet
 * type: seed based crawler
 * input: list of terms to be requested, "seed" (can be updated automatically), "basic" url of the resource (wordreference, glosbe)
 * output: file directory (one html page (file) per seed term)
 * 
 * The program is targeted on crawling WordReference and GlossBe pages
 * Algorithm: 
 * 1. acquisition of terms to be requested from a seed file (that can be automatically updated during the crawling process)
 * 2. acquisition of the .html page for each entry
 * 3. parsing the locally stored .html pages
 * 
 * the program waits for a random lapse of time before crawling the next page 
 * 
 * TODO : automatic update of the seed for "deeper" crawling strategy
 */

public class RequestWordreference 
{
	
	/**
	 * path(s) to the seed file/multiple seed files in case of simultaneous crawling of multiple web resources
	 */
	public static final String SEED_1 ="C:/Users/clairet/Documents/CORPORA/CORPUS_STATS/unigrams2_ru.csv";
	public static final String SEED_2 ="C:/Users/clairet/Documents/KNOWLEDGE/labels_MIAM.txt";
	/**
	 * path(s) to the output directory (directories)
	 */
	public static final String OUTPUT_DIR="C:/Users/clairet/Documents/CRAWLING/";
	/**
	 * basic urls of the resources to be crawled
	 */
	public static final String URL_1="https://glosbe.com/ru/fr/";
	public static final String URL_2="http://www.wordreference.com/fren/";
	/**
	 * Constructor
	 * @throws IOException
	 */
	public RequestWordreference () throws IOException{}
	/**
	 * acquisition of already processed items (in case of multiple crawling sessions)
	 */
	public Set<String> processed=new HashSet<String>();
	public void getProcessed(String dir)
	{
		Set<String> processed=this.processed;
		File[] outdir=new File(dir).listFiles();
		for(File f:outdir)
		{
			String s=f.getName().replaceAll("\\.html", "");
			processed.add(s);
		}
	}
	/**
	 * acquisition of seed terms
	 * @param filepath
	 * @return seed terms as a Set<String>
	 * @throws IOException
	 */
	public Set<String> getSeed(String filepath)throws IOException
	{
		Set<String> set=new HashSet<String>();
		BufferedReader br=new BufferedReader(new FileReader(new File(filepath)));
		String line="";
		while((line=br.readLine())!=null)
		{
			if(line.length()>0)
			set.add(line.toLowerCase());
		}
		this.getProcessed(OUTPUT_DIR);
		set.removeAll(this.processed);
		br.close();
		return set;
	}
	
	public static void main(String[] args)throws IOException, InterruptedException
	{
		GenericCrawler gc=new GenericCrawler();
		RequestWordreference rwr=new RequestWordreference();
		
		/**
		 * acquisition of seed terms from a text file
		 */
		Set<String>labelset1=rwr.getSeed(SEED_1);
		Set<String>labelset2=rwr.getSeed(SEED_2);;
		/**
		 * random wait implementation 
		 */
		int lower = 3;
		int higher = 59;
		int random = (int)(Math.random() * (higher-lower)) + lower;
		System.out.println("will wait "+random+" seconds");
		TimeUnit.SECONDS.sleep(random);
		/**
		 * crawling
		 */
		
		Thread t1=new Thread(rwr.new GetWholePage(gc,URL_1,labelset1));
		t1.start();
		
		Thread t2=new Thread(rwr.new GetWholePage(gc,URL_2,labelset2));
		t2.start();
		
//		Thread t3=new Thread(rwr.new GetTranslations(OUTPUT_DIR));
//		t3.start();
	}/** end of main class**/
	

/**
 * 
 * @author clairet
 * multi-threading compliant class 
 */
class RunRequest implements Runnable
{
	private String term;
	private String url;
	private GenericCrawler gc;

	public RunRequest (GenericCrawler gc,String url,String term) throws IOException
	{
		this.gc=gc;
		this.url=url;
		this.term=term;
	}

	public void run() 
	{
		try 
		{
			String wordref=this.url+this.term;
			System.out.println("processing "+wordref);
			Document doc = this.gc.go(wordref);
			System.out.println(doc.data());
			
			Element absent=doc.select("p[style]").first();
			if(absent!=null)
			{
				if(absent.text().contains("WordReference ne peut pas traduire cette expression"))
				{
					System.err.println("term is absent");
					System.out.println(term);
//					pi.flush();
				}
			}
			else
			{
				/**
				 * look for the main translations
				 */
				Elements couple=doc.select(".even[id]");
				for(Element c:couple)
				{
					String source=c.select(".FrWrd>strong").text();
					String cible=c.select(".ToWrd").text().replaceAll("nnoun\\: Refers to person, place, thing, quality, etc\\.", ",NOUN").replaceAll("adjadjective\\:.+",",ADJ");
					System.out.println(source+","+cible);
//					pt.flush();
				}
				//<tr class='even' id='fren:3084624'><td class='FrWrd' ><strong>huile d'olive</strong> <em class='tooltip POS2'>nf<span><i>nom féminin</i>: s'utilise avec les articles <b>"la", "l'" </b>(devant une voyelle ou un h muet), <b>"une"</b>. <i>Ex : fille - nf > On dira "<b>la</b> fille" ou "<b>une</b> fille".</i> Avec un nom féminin, l'adjectif s'accorde. En général, on ajoute un "e" à l'adjectif. Par exemple, on dira "une petit<b>e</b> fille".</span></em></td><td> (huile à base d'olives)</td><td class='ToWrd' >olive oil <em class='tooltip POS2'>n<span><i>noun</i>: Refers to person, place, thing, quality, etc. </span></em></td></tr>
			}	
		}
		catch (IOException e) 
		{
			e.printStackTrace();
		}		
	}
}
/**
 * 
 * @author clairet
 * multi-threading compliant class for crawling the whole page 
 */
class GetWholePage implements Runnable
{
	//private String term;
	private String url;
	private GenericCrawler gc;
	private Set<String> input;

	public GetWholePage (GenericCrawler gc,String url,Set<String> input) throws IOException
	{
		this.gc=gc;
		this.url=url;
		this.input=input;
	}
	public void randomWait() throws InterruptedException
	{
		int lower = 5;
		int higher = 59;
		int random = (int)(Math.random() * (higher-lower)) + lower;
		System.out.println("will wait "+random+" seconds");
		TimeUnit.SECONDS.sleep(random);
	}
	@Override
	/**
	 * separator is comma
	 */
	public void run() 
	{
		try
		{
			for(String term:this.input)
			{
				term=term.replaceAll("\\_"," ");
				String t="";
				if(term.contains(","))
				{
					String[] tab=term.split(",");
					t=URLEncoder.encode(tab[0],"UTF-8");
				}
				else
				{
					t=URLEncoder.encode(term,"UTF-8");
				}
				
				String query=this.url+t;
				Document doc = this.gc.go(query);
				OutputStreamWriter fow=new OutputStreamWriter(new FileOutputStream(OUTPUT_DIR+URLDecoder.decode(t, "UTF-8")+".html"));
				fow.write(doc.html());
				fow.close();
				Thread.sleep(40000);
				this.randomWait();
			}
		}
		catch(IOException io)
		{
			io.printStackTrace();
		} 
		catch (InterruptedException e)
		{
			e.printStackTrace();
		}
	}
}/** end of GetWholePage class **/
/**
 * 
 * @author clairet
 * multi-threading compliant class for retrieving translations from a web page (WordReference) 
 *
 */
class GetTranslations implements Runnable
{
	private String dir;
	public GetTranslations (String dir) throws IOException
	{
		this.dir=dir;
	}
	@Override
	public void run() 
	{
		try
		{
			File[] files=new File(dir).listFiles();
			
			File incorrect=new File("C:/Users/clairet/Documents/KNOWLEDGE/wr_wrong.txt");
			PrintWriter pi=new PrintWriter(new FileWriter(incorrect));
			//labels traduits
			File correct=new File("C:/Users/clairet/Documents/KNOWLEDGE/wr_ok.txt");
			PrintWriter pt=new PrintWriter(new FileWriter(correct));
			
			File translated=new File("C:/Users/clairet/Documents/KNOWLEDGE/glosbe_1.txt");
			PrintWriter ptr=new PrintWriter(new FileWriter(translated));
			//labels traduits
			File expanded=new File("C:/Users/clairet/Documents/KNOWLEDGE/glosbe_2.txt");
			PrintWriter pexp=new PrintWriter(new FileWriter(expanded));
			
			File termforms=new File("C:/Users/clairet/Documents/KNOWLEDGE/glosbe_3.txt");
			PrintWriter pf=new PrintWriter(new FileWriter(termforms));
			
			for(File f:files)
			{
				Pattern p =Pattern.compile("[a-zàéèêôîÏâ]+\\.html");
				String name=f.getName();
				Matcher m=p.matcher(name);
				if(m.find())
				{
					Document doc=Jsoup.parse(f, "UTF-8");
					Element absent=doc.select("p[style]").first();
					Elements couple=doc.select(".even[id]");
					Elements couple2=doc.select(".odd[id]");
					if(absent==null)
					{
						System.out.println("absent is null");
					}
					if(absent!=null)
					{
						if(absent.text().contains("WordReference ne peut pas traduire cette expression"))
						{
							System.err.println("term is absent "+f.getName());
							pi.println(f.getName());
							pi.flush();
						}
					}
					//chercher les traductions principales	
					if(couple.size()==0)
					{
						System.err.println("no couple");
					}
					if(couple.size()>=1)
					{
						for(Element c:couple)
						{
							String source=c.select(".FrWrd>strong").text();
							String cible=c.select(".ToWrd").text().replaceAll("nnoun\\: Refers to person, place, thing, quality, etc\\.", ",NOUN").replaceAll("adjadjective\\:.+",",ADJ");
							System.out.println(source+","+cible);
							pt.println(source+","+cible);
							pt.flush();
						}
					}
					if(couple2.size()>=1)
					{
						for(Element c:couple2)
						{
							String source=c.select(".FrWrd>strong").text();
							String cible=c.select(".ToWrd").text().replaceAll("nnoun\\: Refers to person, place, thing, quality, etc\\.", ",NOUN").replaceAll("adjadjective\\:.+",",ADJ");
							System.out.println(source+","+cible);
							pt.println(source+","+cible);
							pt.flush();
						}
						// the targeted line is as follows:
						//<tr class='even' id='fren:3084624'><td class='FrWrd' ><strong>huile d'olive</strong> <em class='tooltip POS2'>nf<span><i>nom féminin</i>: s'utilise avec les articles <b>"la", "l'" </b>(devant une voyelle ou un h muet), <b>"une"</b>. <i>Ex : fille - nf > On dira "<b>la</b> fille" ou "<b>une</b> fille".</i> Avec un nom féminin, l'adjectif s'accorde. En général, on ajoute un "e" à l'adjectif. Par exemple, on dira "une petit<b>e</b> fille".</span></em></td><td> (huile à base d'olives)</td><td class='ToWrd' >olive oil <em class='tooltip POS2'>n<span><i>noun</i>: Refers to person, place, thing, quality, etc. </span></em></td></tr>
					}	
				}
				else
				{
					Document doc=Jsoup.parse(f, "UTF-8");
					
					Elements translations = doc.select(".text-info");
					String term=doc.select("h1>span").text();
					for(Element tr:translations)
					{
						String translatedterm=tr.select(".phr").text();
						String currentform=tr.select(".gender-n-phrase").text();
						if(translatedterm.length()>0)
						{
							ptr.println(term+"|"+translatedterm+"|"+currentform);
							ptr.flush();
						}
					}
					
					Elements forms=doc.select("tr");
					for(Element form:forms)
					{
						String formtype=form.select("th").text();
						Elements fs=form.select("td");
						for(Element frm:fs)
						{
							pf.println(frm.text()+"|"+formtype);
							pf.flush();
						}
					}
					Elements similarPairs =doc.select(".tableRow");
					for(Element sim:similarPairs)
					{
						String simterm=sim.select("dt").text();
						String simtrans=sim.select("dd[dir][class][lang]").text();
						if(simtrans.length()>0)
						{
							pexp.println(term+"|"+simterm+"|"+simtrans);
							pexp.flush();
						}
					}
				}
			}
			pi.close();
			pt.close();
			pf.close();
			pexp.close();
			ptr.close();
			Thread.sleep(4000);

		}
		catch(IOException io)
		{
			
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
}/** end of GetTranslations **/

/**
 * utility function to "force" UTF-8 encoding of s string input
 */
private static final Charset UTF_8 = Charset.forName("UTF-8");

@SuppressWarnings("unused")
private String forceUtf8Coding(String input) 
{
	return new String(input.getBytes(UTF_8), UTF_8);
}
}




