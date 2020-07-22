package structure;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashSet;
import java.util.Locale;
import java.util.Scanner;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.w3c.dom.Document;

public class Structure 
{
	/**
	 * structuration en "tours de parole" 
	 * 
	 */
	public Structure(){}
	
	public static final String i_sep="\n(?=\\*{4})"; //séparation des interviews
	//(^|\\-{2,}\n)(?=(\\*{2,}.+\\*annee\\_\\d{4}\n)(\\w+)?(candidate\\_2017\\_12FlorianPhilippot)?)
	public static final String t_sep="\n(\\s)?\n";
	public String header="<?xml version=\"1.0\" encoding=\"utf-8\"?>";
	protected static final String i_ref="\\*{4}.+(?=\n)";
	//****  *annee_2016 *mois_2016_06 *cand_AlainJuppé *candidate_2016_06AlainJuppé
	//\\*{2,}.+\\*annee\\_\\d{4}(\\w+|\\*+)?(candidate\\_2017\\_12FlorianPhilippot)?(?=\n)
	protected Pattern p=Pattern.compile(i_ref);
	protected Document document;
	/**
	 * utility functions
	 */
	@SuppressWarnings("resource")
	public static String readFileAsString(String file) throws FileNotFoundException 
	{
			Locale loc=new Locale("fr","FR");
		   return new Scanner(new FileInputStream(file),"UTF-8").useDelimiter("\\Z").useLocale(loc).next();
	}
	/**
	 * TODO
	 * faire un set avec les nominations à traiter
	 * lister .xml les tours de parole du corpus principal avec 
	 * <tour id="123">
	 * <ref>référence iramuteq</ref>
	 * <s>texte du segment</s>
	 * </tour>
	 * 
	 */
	protected Set<String> nominations=new HashSet<String>();
	public void liste()
	{
		nominations.add("protectionnisme");
		nominations.add("isolationnisme");
		nominations.add("patriotisme");
		nominations.add("écologie intégrale");
		nominations.add("musulman modéré");
		nominations.add("musulmans modérés");
		nominations.add("islam modéré");
		nominations.add("réfugiés climatiques");
		nominations.add("appropriation culturelle");
		nominations.add("moralisation");
		nominations.add("mobilité douce");
		nominations.add("violences obstétricales");
		
	}
	/**
	 * 
	 * @param path
	 * @throws IOException 
	 */
	public void split(String path) throws IOException
	{
		String input=readFileAsString(path);
		this.liste();
		PrintWriter pw=new PrintWriter(new FileWriter(new File("output/test_tours.xml")));
		pw.println(header);
		pw.println("<interviews>");
		String[] interviews=input.split(i_sep);
		
		System.out.println(interviews.length);
		for(int i=0;i<interviews.length;i++)
		{
			/*identifier le locuteur*/
//			Matcher m=p.matcher(interviews[i]);	
			
			String tours[]=interviews[i].split(t_sep);
			Matcher m=p.matcher(tours[0]);
			String r=null;
			if(m.find())
			{
				r="<ref>"+m.group(0)+"</ref>";
				/**
				 * add element etc
				 */
			}
			boolean go=false;
			
			for(String n:nominations)
			{
				if(interviews[i].contains(n))
				{
					go=true;
					
				}
			}
			if(go)
			{
				if(r!=null)
				{
					pw.println("<interview id=\""+i+"\">");
					pw.println(r);
					go=false;
				
			for(int j=1;j<tours.length;j++)
			{
				boolean continuer=false;
				for(String n:nominations)
				{
					if(tours[j].contains(n))
					{
						continuer=true;
					}
				}
				
				if(continuer)
				{
					String t="";
					t=tours[j].replaceAll("<","");
					File af=new File("./output/annoter/"+i+"-"+j+".ann");
					if(this.pair(j))
					{
						
						pw.println("<tour id=\""+i+"-"+j+"\">");
						pw.println("<role>journaliste</role>");
						pw.println("<s>"+t+"</s>");
						pw.println("</tour>");
						
						PrintWriter lpw=new PrintWriter(new FileWriter(new File("./output/annoter/"+i+"-"+j+".txt")));
						lpw.print(t);
						lpw.flush();
						lpw.close();
						
						
					}
					else
					{
						
						pw.println("<tour id=\""+i+"-"+j+"\">");
						pw.println("<role>personnalite</role>");
						pw.println("<s>"+t+"</s>");
						pw.println("</tour>");
						
						PrintWriter lpw=new PrintWriter(new FileWriter(new File("output/annoter/"+i+"-"+j+".txt")));
						lpw.print(t);
						lpw.flush();
						lpw.close();
					}
					af.createNewFile();
				}	
			}
			pw.println("</interview>");//+"\n"
			pw.flush();
			i++;
				}
			}
			
		}
		pw.println("</interviews>");
		pw.flush();
		pw.close();
	}
	
	
	
	public boolean pair(int i)
	{
		boolean res=false;
		int reste = i % 2;
        if (reste == 0) 
        {
            res=true;
        } 
        return res;
		
	}
	public void execute() throws IOException
	{
		this.split("input/Export_IRaMuTeQ.txt");
	}
	
	public static void main(String[] args) throws IOException
	{
		Structure str=new Structure();
		str.execute();
	}
}
