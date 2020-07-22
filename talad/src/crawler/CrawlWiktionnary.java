package crawler;

//import java.io.File;
//import java.io.FileOutputStream;
//import java.io.PrintStream;
//import java.io.FileWriter;
//import java.io.PrintWriter;
import java.io.IOException;



public class CrawlWiktionnary 
{
	static final String DB_DRIVER = "com.mysql.jdbc.Driver";  
	static final String DB_CONNECTION = "jdbc:mysql://localhost/Data";
	public static void main(String[] args) throws IOException
	{
//		FileOutputStream f=new FileOutputStream(new File("/Users/clairet/Documents/CORPORA/LEMMA/RUSSIAN/WIKTIONNARY_forms.txt"));
//		System.setOut(new PrintStream(f));
		//PrintWriter pw=new PrintWriter(new FileWriter(new File("/Users/clairet/Documents/CORPORA/LEMMA/RUSSIAN/WIKTIONNARY_forms.txt")));
		GenericCrawler gn=new GenericCrawler();
		gn.getForms("https://ru.wiktionary.org/wiki/%D0%98%D0%BD%D0%B4%D0%B5%D0%BA%D1%81:%D0%A0%D1%83%D1%81%D1%81%D0%BA%D0%B8%D0%B9_%D1%8F%D0%B7%D1%8B%D0%BA/%D0%90");
		//gn.getBorrowings("https://en.wiktionary.org/wiki/Category:Russian_terms_borrowed_from_French");
		//https://ru.wiktionary.org/wiki/%D0%9A%D0%B0%D1%82%D0%B5%D0%B3%D0%BE%D1%80%D0%B8%D1%8F:%D0%A1%D0%BB%D0%BE%D0%B2%D0%B0_%D1%84%D1%80%D0%B0%D0%BD%D1%86%D1%83%D0%B7%D1%81%D0%BA%D0%BE%D0%B3%D0%BE_%D0%BF%D1%80%D0%BE%D0%B8%D1%81%D1%85%D0%BE%D0%B6%D0%B4%D0%B5%D0%BD%D0%B8%D1%8F/ru
//		//https://en.wiktionary.org/wiki/Category:Russian_terms_borrowed_from_French
		//for(String s:gn.getForms("https://en.wiktionary.org/wiki/Category:Russian_non-lemma_forms"))
//		{
//			//parser s
//			//borrowings
//			//faire une liste de formes id,form lemma,formdesc
//			//faire une liste de lemmas id,lemma, pos
//			//charger dans une bd
//		}
		//charger dans une bd
		 
		
//		for(String s:gn.getTerms("https://en.wiktionary.org/wiki/Index:Russian/%D0%B0"))
//			{
//					pw.println(s.replaceAll("(\\s)?(\\*|\\~)(\\s)?","").replaceAll("(\\s)?(\\►)",""));
//					pw.flush();
//			}
//		pw.close();
		
		//forms start url
		//https://en.wiktionary.org/wiki/Category:Russian_non-lemma_forms
//		for(String f:gn.getForms("https://en.wiktionary.org/wiki/Category:Russian_non-lemma_forms"))
//		{
//			System.out.println("f="+f);
//			pw.println(f);
//			pw.flush();
//		}
//		pw.close();
	}
}
