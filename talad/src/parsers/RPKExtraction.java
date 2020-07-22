package parsers;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Scanner;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.json.JSONException;

public class RPKExtraction
{
	/**
	 * relevant piece of knowledge extraction tools
	 * input - stream
	 */
	/**
	 * members
	 * 
	 */
	public InputStream is;
	public RPKExtraction(InputStream is)
	{
		this.is=is;
	}
	public RPKExtraction()
	{
		
	}
	
	static class annodisSeg extends RPKExtraction
	{
		public annodisSeg() {
			super();
			// TODO Auto-generated constructor stub
		}
		public InputStream as;
		public Pattern seg_pattern =Pattern.compile("(\\[)(?<value>.*?)(\\])\\_(?<key>\\d+)");
		public Pattern rel_pattern =Pattern.compile("(.*?)\\((.*?)\\)");
		/**
		 * appairer
		 */
		
		public Set<String> pairs(String path)
		{
			Set<String> annodis_set=new HashSet<String> ();
			File[]dir=new File(path).listFiles();
			String ref="";
			for(File f:dir)
			{
				ref=f.getName().replaceAll("\\.(seg|rel)","");
				annodis_set.add(ref);
			}
			System.out.println("annodis_set size = "+annodis_set.size());
			return annodis_set;
		}
		
		public void parseSeg(String ref) throws IOException 
		{
			try
			{
				is=new FileInputStream(new File("/home/bebeshina/Documents/ressources_structurées/annodis/annotations_expert/texte/A/"+ref+".seg"));
				BufferedReader br=new BufferedReader(new InputStreamReader(is));
				String s="";
				while((s=br.readLine())!=null)
				{
					Matcher m=seg_pattern.matcher(s);
					while(m.find())
					{
//						System.out.println(s);
						System.out.println("segment "+m.group(0));
					}
				}
				
				
//				
//				Scanner sc=new Scanner(is);
//				while(sc.hasNext())
//				{
//					String s=sc.next();
//					
//				}
//				sc.close();
			}
			catch(FileNotFoundException e)
			{
				e.printStackTrace();
			}
			
		}
		
		public Map<Integer,String> seg2Map(String ref) throws IOException 
		{
			Map<Integer,String>map=new HashMap<Integer,String>();
			try
			{
				is=new FileInputStream(new File("/home/bebeshina/Documents/ressources_structurées/annodis/annotations_expert/texte/A/"+ref+".seg"));
				BufferedReader br=new BufferedReader(new InputStreamReader(is));
				String s="";
				while((s=br.readLine())!=null)
				{
					Matcher m=seg_pattern.matcher(s);
					while(m.find())
					{
//						System.out.println(s);
						map.put(Integer.valueOf(m.group("key")), m.group("value"));
						System.out.println("key "+m.group("key")+" "+"value "+m.group("value"));
					}
				}
			}
			catch(FileNotFoundException e)
			{
				e.printStackTrace();
			}
			catch(JSONException je)
			{
				je.printStackTrace();
			}
			return map;
		}
		
		public void parseRel(String ref) throws FileNotFoundException
		{
			//elaboration(1/[30,2-10])
			//attribution(3/[4,5,6,7])
			
			is=new FileInputStream(new File("/home/bebeshina/Documents/ressources_structurées/annodis/annotations_expert/texte/A/"+ref+".rel"));
			Scanner sc=new Scanner(is);
			while(sc.hasNext())
			{
				String s=sc.next();
				Matcher m=rel_pattern.matcher(s);
				
				Set<Integer> iset=new HashSet<Integer>();
				Pattern lp=Pattern.compile("(?<start>\\d+)\\-(?<end>\\d+)");
				Pattern mp=Pattern.compile("(?<simple>\\d+ (?=(\\,|\\]|\\/)))");
				
				while(m.find())
				{
					System.out.println("classe "+m.group());
					String str=m.group(0);
					Matcher lm=lp.matcher(str);
					while(lm.find())
					{
						System.out.println("start "+lm.group("start")+" end "+lm.group("end"));
						Integer o=Integer.valueOf(lm.group("start"));
						Integer p=Integer.valueOf(lm.group("end"));
						while(o<=p)
						{
							iset.add(o);
							o++;
						}
						
					}
					Matcher lmp=mp.matcher(str);
					while(lmp.find())
					{
						iset.add(Integer.valueOf(lmp.group("simple")));
					}
				}
				
				System.out.println(Arrays.asList(iset));
				
				
			}
			sc.close();
		}
		
		public Map<Integer,String> rel2Map(String ref)
		{
			Map<Integer,String>map=new HashMap<Integer,String>();
			return map;
		}
		
		public void extractAnnodisSegments()throws IOException
		{
			
			for(String s:this.pairs("/home/bebeshina/Documents/ressources_structurées/annodis/annotations_expert/texte/A"))
			{
				System.out.println("------------------------------------");
				this.rel2Map(s);
//				this.seg2Map(s);
			}
		}
	}
	public static void main(String[] args) throws IOException
	{
		

		
		RPKExtraction.annodisSeg rpk=new RPKExtraction.annodisSeg();
		rpk.extractAnnodisSegments();
	}
}
