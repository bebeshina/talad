package crawler;

import org.jsoup.select.Elements;

import java.io.File;
import java.io.FileOutputStream;
//import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Properties;
import java.util.Queue;
import java.util.Set;
import java.util.TreeSet;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.jsoup.Connection.Response;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

public class GenericCrawler
{
	
//private final static String URL="http://www.quericavida.com/recetas/";
private final static String URL="https://en.wiktionary.org/wiki/Index:Russian/%D0%B0";
//private final static String LINK_SELECTOR="a[href*=recepts]";

//private final static String LES_SELECTOR="a[href*=recetas]";
private final static String LES_SELECTOR="a[href]";
//:not(a[href*='.pdf']):not(a[href*='.gif']):not(a[href*='.jpg']:not(a[href*='news']
//a[href]:not(a[href*='.pdf']):not(a[href*='.gif']):not(a[href*='.jpg']:not(a[href*='news']
//private final static String TITLE_SELECTOR="h1";//RU
private final static String TITLE_SELECTOR="title";//ES
//private final static String ING_SELECTOR=".recipeList>li";//ok
//private final static String QUANTITY_SELECTOR=".recipe-ingredients__list[itemprop='ingredients']";
private final static String ING_SELECTOR=".recipe-ingredients__list>.recipe-ingredients__list-item";
//>.recipe-ingredients__list-item>a
//ul class="recipe-ingredients__list"
//quericavida ---> dl>dd>span
//private final static String INS_SELECTOR=".details_desc>p";
private final static String INS_SELECTOR=".recipe-method__list>.recipe-method__list-item>p";
//quericavida --->.recipePartStepDescription
//private final static String NUTRI_KEY="dt[class='nutrition-fact-title']";
//private final static String NUTRI_VALUE="dd[class='nutrition-fact-units']";

//members
String title;
String link;
Elements ings;
Elements inst;

//constructor
GenericCrawler()throws IOException{}
	
public Document doc;
public Response response;
	
	public void connect() throws IOException
	{
		this.doc=Jsoup.connect(URL).timeout(50000).get();
	}
	//inaccessible pour un thread si déjà utilisé par un autre thread
	public Document go(String url) throws IOException
	{
		Document d=Jsoup.connect(url).ignoreHttpErrors(true).ignoreContentType(true).timeout(50000).get();
		return d;
	}
	
	public void getResponse(String url) throws IOException
	{
		this.response=Jsoup.connect(url).followRedirects(true).ignoreHttpErrors(true).timeout(100000).execute();
	}
	
	public void setProperties(String starturl,String recipeSelector,String titleSelector,String ingredientSelector,String instructionSelector)
	{
		Properties prop = new Properties();
		OutputStream output = null;
		try 
		{
			output = new FileOutputStream("crawler.properties");

			// set the properties value
			prop.setProperty("startUrl", starturl);
			prop.setProperty("recipeSelector", recipeSelector);
			prop.setProperty("titleSelector", titleSelector);
			prop.setProperty("ingredientSelector", ingredientSelector);
			prop.setProperty("instructionSelector", instructionSelector);

			// save properties to project root folder
			prop.store(output, null);

		} catch (IOException io) {
			io.printStackTrace();
		} finally {
			if (output != null) {
				try {
					output.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}
	public void await()
	{
		try
		{
			wait(10000);
		}
		catch(InterruptedException e)
		{
			System.out.println(e.getMessage()); 
		}
	}
	//obtenir la liste de tous les termes référencés dans Wiktionnary avec leur pos.
	//borrowings
	/*
	 * @param String startUrl is the most generic page of the resource to be crawled
	 * this function generates a list of borrowings to be checked and charged into a database and return the lines of the list as a Set<String> for further processing
	 */
	public Set<String> getBorrowings(String startUrl) throws IOException
	{
		Set<String> set=new HashSet<String>();
		String tgt_lang=">Russian";
		//String lookup="";
		//utility collections
		Set<String> vu=new HashSet<String>();
		Queue<String> qu=new LinkedList<String>();
		PrintWriter pw=new PrintWriter(new FileOutputStream(new File("borrowings.txt")));
		//start
		//add al the other links
		
		Document doc1=this.go(startUrl);
		//get further links
		Elements links=doc1.select("a[href][title]");
		for(Element l:links)
		{
			String link=l.attr("abs:href").toString();
			System.out.println("link "+link);
			if(!(vu.contains(link)))
			{
				qu.add(link);
				vu.add(link);
			}
		}
		
		System.out.println("qu size="+qu.size());;
		String borrowing=null;
		String origin=null;
		String lang=null;
		while(!(qu.isEmpty()))
		{
			String url=qu.poll().trim();
			System.out.println("processing "+url);
			//parse pages
			Document doc2=this.go(url);
			borrowing=doc2.select("strong[class=Cyrl headword][lang=ru]").text();
			origin=doc2.select("i[class=Latn mention][lang]").text();
			lang=doc2.select(".etyl").text();
		
			if((borrowing.length()!=0)&&(lang.equals("French")))
			{
				pw.println(lang+tgt_lang+","+origin+","+borrowing);
				pw.flush();
				set.add(lang+tgt_lang+","+origin+","+borrowing);
			}
		}
		pw.close();
		return set;
		
		/*
		 * <h2><span class="mw-headline" id="Russian">Russian</span><span class="mw-editsection"><span class="mw-editsection-bracket">[</span><a href="/w/index.php?title=%D0%B0%D0%BF%D0%BE%D1%81%D1%82%D1%80%D0%BE%D1%84&amp;action=edit&amp;section=9" title="Edit section: Russian">edit</a><span class="mw-editsection-bracket">]</span></span></h2>
<h3><span class="mw-headline" id="Etymology">Etymology</span><span class="mw-editsection"><span class="mw-editsection-bracket">[</span><a href="/w/index.php?title=%D0%B0%D0%BF%D0%BE%D1%81%D1%82%D1%80%D0%BE%D1%84&amp;action=edit&amp;section=10" title="Edit section: Etymology">edit</a><span class="mw-editsection-bracket">]</span></span></h3>
<p>Borrowed from <span class="etyl"><a href="https://en.wikipedia.org/wiki/French_language" class="extiw" title="w:French language">French</a></span> <i class="Latn mention" lang="fr"><a href="/wiki/apostrophe#French" title="apostrophe">apostrophe</a></i>.</p>
		 */
	}
	//derivations
	
	public Set<String> getTerms(String startUrl) throws IOException
	{
		//initialisation listes
		Set<String> result=new HashSet<String>();
		Set<String> vu=new HashSet<String>();
		Queue<String> qu=new LinkedList<String>();
		//départ
		qu.add(startUrl);
		while(!(qu.isEmpty()))
		{
			String lookup=qu.poll();
			System.out.println("processing "+lookup);
			//get elements
			Document doc1=this.go(lookup);
			//<li><a href="/wiki/%D0%B0%D0%B1%D0%B0%D0%B4%D0%B0%D0%BD%D0%BA%D0%B0" title="абаданка">абаданка</a> <i>n</i></li>
			Elements terms=doc1.select("li");
			for(Element t:terms)
			{
				String str=t.select("a[href][title]").text();
				String info=t.select("i").text();
				if(!(info.isEmpty()))
				{
					if((!(info.equals("proper")))&&(!(info.equals("prefix")))&&(!(info.equals("suffix")))&&(!(info.equals("affix")))&&(!(info.equals("particle"))))
					{
						if((!(str.endsWith("-")))|(!(str.endsWith(" * "))))
						{
							result.add(str+"|"+info);
						}	
					}
				}
			}
			//get more links
			Elements links= doc1.select(LES_SELECTOR);
			for(Element l:links)
			{
				String link=l.attr("abs:href").toString();
				if(this.isCategory(link))
				{
					
					if(!(vu.contains(link)))
					{
						qu.add(link);
						vu.add(link);
						System.out.println("added "+link);
					}	
				}
			}
			//get translated terms	
		}
		return result;
	}
	
	//built for Russian Wiktionnary
	public void getForms(String startUrl) throws IOException
	{
		PrintWriter pw1=new PrintWriter(new FileOutputStream(new File("russian_lemmas2.txt")));
		PrintWriter pw2=new PrintWriter(new FileOutputStream(new File("russian_forms2.txt")));
		PrintWriter pw3=new PrintWriter(new FileOutputStream(new File("russian_desc2.txt")));
		//Set<String> forms=new HashSet<String>();
		Set<String> vu=new HashSet<String>();
		Queue<String> qu=new LinkedList<String>();
		//départ
		//System.out.println(startUrl);
		qu.add(startUrl);
		while(!(qu.isEmpty()))
		{
			String lookup=qu.poll();
			Document doc1=this.go(lookup);
			vu.add(lookup);
			Elements links=doc1.select("a[href]");
			for(Element l:links)
			{
				String link=l.attr("abs:href").toString();
				if(!(vu.contains(link)))
				{
					qu.add(link);
				}
			}
			Elements terms=doc1.select("a[href^=/wiki/%][title]");
			int i=1;
			for(Element t:terms)
			{
				String termlink=t.attr("abs:href").toString();
				if(!(vu.contains(termlink)))
				{
					Document doc2=this.go(termlink);
					//terme
					String term =doc2.select("h1[id=firstHeading][class=firstHeading][lang=ru]").text().replaceAll(" • ", "|");
					//pos
					String pos=doc2.select("p>a[href][title]").text().replaceAll("(склонение )?классификации А. А. Зализняка", "");
					pw1.println(i+","+term+","+pos);
					pw1.flush();
					i++;
					//déclinaisons et conjugaisons
					Elements formgroup=doc2.select("tr");
					for(Element fg:formgroup)
					{
						String ftype=fg.select("td[bgcolor=#EEF9FF]").text();
						Elements fs=fg.select("td[bgcolor=#FFFFFF]");
						Elements fs2=fg.select("td[align=left]");
						if(!(fs.isEmpty()))
						{
							for(Element f:fs)
							{
								String fr=f.text();
								if(fr.length()!=0)
								{
									if(fr.length()!=0)
									{
										pw2.println(i+","+fr+","+ftype+","+term+",ru");
										pw2.flush();
										i++;
									}
								}
							}
						}
						if(!(fs2.isEmpty()))
						{
							for(Element f:fs2)
							{
								String fr=f.text();
								if(fr.length()!=0)
								{
									pw2.println(i+","+fr+","+ftype+","+term+",ru");
									pw2.flush();
									i++;
								}
							}
						}
					}
					Elements sensegroup=doc2.select("ol>li");
					int j=1;
					for(Element s:sensegroup)
					{
						String desc=s.text().replaceAll("(\\s)?◆ (.+)(\\.)", "").replaceAll("Список литературы(\\))", "");
						if(desc.length()!=0)
						{
							if((!(desc.equals("-")))&&(!(desc.equals(" ?"))))
							{
								pw3.println(term+","+desc+","+j);
								pw3.flush();
								j++;
							}
						}
					}
					vu.add(termlink);
				}
			}
		}
	pw1.close();
	pw2.close();
	pw3.close();
	}
	
	public Set<String> getLinks(String url) throws IOException
	{
		//initialisation listes
		Set<String> result=new HashSet<String>();
		Set<String> vu=new HashSet<String>();
		Queue<String> qu=new LinkedList<String>();
		
		//départ
		qu.add(url);
		
		while(!(qu.isEmpty()))
		{
			String lookup=qu.poll();
			System.err.println("processing link "+lookup);
			
			if(this.isRecipe(lookup))
			{
				System.err.println("1");
				//System.out.println("is recipe "+lookup);
				result.add(lookup);
				vu.add(lookup);
				//qu.add(lookup);
			}
			
			else
			{
				System.err.println("2");
				Document doc1=this.go(lookup);
				Elements links= doc1.select(LES_SELECTOR);
				System.out.println("size links ="+links.size());
//				String str=this.getEverything(doc1);
//				Set<String>items=this.getUrlFromText(str);
//				if(!(items.isEmpty()))
//				{
//					qu.addAll(items);
//				}
				for(Element sl:links)
				{
					String lin=sl.attr("abs:href").toString().trim();
					//System.out.println("lin="+lin);
					if(this.isCategory(lin))//si catégorie, on continue
					{
						System.err.println("3");
						System.out.println("is category "+lin);
						if(!(vu.contains(lin)))
						{
							vu.add(lin);
							qu.add(lin);
						}
						if(this.isRecipe(lin))
						{
							System.err.println("4");
							System.out.println("is recipe "+lin);
							result.add(lin);
							vu.add(lin);
							qu.add(lin);
						}
						else
						{
							if(!(vu.contains(lin)))
							{
								vu.add(lin);
							}
						}
						System.out.println("result size="+result.size());
					}
				}	
			}
		
			System.out.println("qu size = "+qu.size());
		}//end while
		System.out.println("vu size "+vu.size());
		System.out.println("final result size "+result.size());
		return result;
	}
	public boolean isCategory(String url)
	{
		boolean is_cat=false;
		//Pattern rec=Pattern.compile("\\/recepts\\/[a-z]+\\/[a-z]+\\/");
		///recetas/tamales-de-dulce-mexicanos/b5135baa-b154-4e7d-87b3-5411d53fce66"
		//category Wiktionnary
		//view-source:https://en.wiktionary.org/wiki/Index:Russian/%D0%B0
		Pattern rec=Pattern.compile("food\\/(collections\\/)?.+$");	
		//gotovim ---> Index\\:Russian\\/\\%
		//Pattern rec=Pattern.compile("\\/recetas\\/([a-z]+(\\-)[a-z]+)+(\\/)?([a-z]+(\\-)[a-z]+)?+");
		Matcher mec=rec.matcher(url);
		if(mec.find())
		{
			if(!((url.contains("programmes"))|(url.contains("search?"))|(url.contains("blogs/food"))))
			{
				is_cat=true;
			}
		}
		return is_cat;
	}
	
	public boolean isRecipe(String url)
	{
		boolean is_recipe=false;
		//Pattern rec=Pattern.compile("\\/recetas\\/([a-z]+(\\-)[a-z]+(\\-)?)+(\\/)(\\-)?([0-9a-z]+(\\-)[0-9a-z]+(\\-)?)+([0-9a-z]+)?");
		Pattern rec=Pattern.compile("food\\/recipes\\/.+(\\d+)");
		//wiktionnary "\\/\\/en\\.\\wiktionary\\.org\\/wiki\\/[\\p{IsCyrilic}]"
		//en.wiktionary.org/wiki/Абадан
		Matcher mec=rec.matcher(url);
		while(mec.find())
		{
			is_recipe=true;
		}
		return is_recipe;
	}

	public String getJavascript(Document doc) throws IOException
	{
		String js="script";
		//[type='text/javascript']
		String jscontent=doc.select(js).text();
		System.out.println(jscontent);
		return jscontent;
	}
	public Set<String> getContentFromScriptText(String url) throws IOException
	{
		Set<String> pairs=new HashSet<String>();
		Document doc=this.go(url);
		@SuppressWarnings("unused")
		String toparse=this.getJavascript(doc);
		@SuppressWarnings("unused")
		Pattern p=Pattern.compile("(?<key>(\")Url(\"))(\\:)(\")(?<value>\\/recetas\\/([a-z]+(\\-)[a-z]+(\\-)?)+(\\/)(\\-)?([0-9a-z]+(\\-)[0-9a-z]+(\\-)?)+([0-9a-z]+)?)(\")(\\,)(\"Target\"\\:)");
		
		return pairs;
	}
	public String getEverything(Document doc) throws IOException
	{
		
		String everything=doc.body().data();
		return everything;
	}
	public Set<String> getUrlFromText(String str)
	{
		Set<String> set=new TreeSet<String>();
		
		Pattern p=Pattern.compile("(?<key>(\")Url(\"))(\\:)(\")(?<value>\\/recetas\\/([a-z]+(\\-)[a-z]+(\\-)?)+(\\/)(\\-)?([0-9a-z]+(\\-)[0-9a-z]+(\\-)?)+([0-9a-z]+)?)(\")(\\,)(\"Target\"\\:)");
		Matcher m=p.matcher(str);
		while(m.find())
		{
			String itemurl="http://www.quericavida.com"+String.valueOf(m.group("value"));
			System.out.println("itemurl --> "+itemurl);
			set.add(itemurl);
		}
		return set;
	}
 	public String getUrls(){String urls=null;return urls;}
	public String getTitle(Document doc) throws IOException
	{
		String title=doc.select(TITLE_SELECTOR).text();
		return title;
	}

	
	public Set<String> ingredients(Document doc) throws IOException
	{
		Set<String> ings=new TreeSet<String>();
		Elements ingrs=doc.select(ING_SELECTOR);
		for(Element ingr:ingrs)
		{
			String ing=ingr.select("a").text();
			String quant=ingr.text().replaceAll(ing, "");
			String ingredient="<quantity>"+quant.replaceAll(",", "").trim()+"</quantity>"+ing;
			ings.add(ingredient);
		}
		return ings;	
	}
	public Set<String> instructions(Document doc) throws IOException
	{
		Set<String> inst=new TreeSet<String>();
		Elements instr=doc.select(INS_SELECTOR);
		for(Element ins:instr)
		{
			String instruction=ins.text();
			inst.add(instruction);
		}
		return inst;
	}
	public int getId(String url)
	{
		int ident=0;
		//appledateandgingerch_67595
		//(?<=\\/)[0-9]+(?=\\.shtml)
		Pattern pid=Pattern.compile("(?<=(\\_))d+");
		Matcher mid=pid.matcher(url);
		while(mid.find())
		{
			ident=Integer.valueOf(mid.group(0));
		}
		return ident;
	}
	public String getStringId(String url)
	{
		String ident="";
		//Pattern pid=Pattern.compile("(?<=\\/)[0-9]+(?=\\.shtml)");
		//b5135baa-b154-4e7d-87b3-5411d53fce66
		//"(?<=(\\_))d+"
		//quericavida (?<=\\/)(\\-)?([0-9a-z]+(\\-)[0-9a-z]+(\\-)?)+([0-9a-z]+)?
		Pattern pid=Pattern.compile("(?<=\\_)\\d+(?=$)");
		Matcher mid=pid.matcher(url);
		while(mid.find())
		{
			ident=String.valueOf(mid.group(0));
		}
		return ident;
	}
	
	public String url;	
}

