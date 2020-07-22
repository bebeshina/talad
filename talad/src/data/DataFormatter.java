      package data;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

/**
 * 
 * @author bebeshina
 * Class for formatting the input data for the database to be written in a database by another program. 
 * Allows combining different sources of data, different lists and putting it all together ready to be imported.
 *
 */
public class DataFormatter 
{
	public final static String separator="";
	public final static Integer position=0;

	public static Map<String,Integer>termMap=new HashMap<String,Integer>();
	public static Set<String>relSet=new HashSet<String>();
	/** 31 July 2019
	 * 
	 * @param path
	 * Path to the file containing the list of terms 
	 * @throws IOException
	 */
	public static void prepareTermList(String path) throws IOException
	{
		BufferedReader br=new BufferedReader(new FileReader(new File(path)));
		String line="";
		int i=1;
		while((line=br.readLine())!=null)
		{
			if(!termMap.containsKey(line))
			{
				i++;
				termMap.put(line, i);
			}
		}
		br.close();
		
	}
	/**
	 * termes et relations à partir de relations en clair 
	 * @param path
	 * @throws IOException
	 */
	public static void prepareTerm2import(String path) throws IOException
	{
		BufferedReader br=new BufferedReader(new FileReader(new File(path)));
		
		String line="";
		int i=1;
		while((line=br.readLine())!=null)
		{
			if(!termMap.containsKey(line))
			{
				i++;
				termMap.put(line, i);
			}
		}
		br.close();
		
	}
	
	/**
	 * 
	 * @param path
	 * Path to the file containing 
	 * @throws IOException
	 */
	public static void prepareRelationList(String path) throws IOException
	{
		BufferedReader br=new BufferedReader(new FileReader(new File(path)));
//		PrintWriter pw=new PrintWriter(new FileWriter(new File(path)));
		String line="";
	
		while((line=br.readLine())!=null)
		{
			if(line.contains("|"))
			{
				String[]str=line.split("\\|");
				relSet.add(string2Integer(str));
			}
			else 
			{
				String[]str=line.split("\\,");
				relSet.add(string2Integer(str));
			}
			
		}
		br.close();
	}
	static int rid=0;
	public static String string2Integer(String[] str)
	{
		String result=null;
		
		int source=0;
		int type=0;
		int origin=0;
		int weight=0;
		int cible=0;
		try {
		if((termMap.containsKey(str[0]))&&(termMap.containsKey(str[4])))
		{
			rid++;
			source=termMap.get(str[0]);
			type=TNRRelType.valueOf(str[1]).id();
			weight=Integer.valueOf(str[2]);
			origin=TNROrigin.valueOf(str[3]).id();
			cible=termMap.get(str[4]);
			result=rid+","+source+","+type+","+weight+","+origin+","+cible;
			System.out.println(result);
		}
		}
		catch(ArrayIndexOutOfBoundsException e)
		{
			System.out.println(Arrays.asList(str));
		}
		return result;
	}
	public static void printMap(Map<?,?>map, String path) throws IOException
	{
		PrintWriter pw=new PrintWriter(new FileWriter(new File(path)));
		for(Entry<?,?> e:map.entrySet())
		{
			pw.println(e.getValue()+"|"+e.getKey());
		}
		pw.flush();
		pw.close();
	}
	
	public static void printSet(Set<?>set, String path) throws IOException
	{
		PrintWriter pw=new PrintWriter(new FileWriter(new File(path)));
		for(Object e:set)
		{
			pw.println(e);
		}
		pw.flush();
		pw.close();
	}
	
	public static void main(String[] args) throws IOException
	{
		
		prepareTermList("resources/terms2import.csv");
		printMap(termMap, "termes_emb.txt");
		prepareRelationList("resources/rels2import.csv");
		printSet(relSet,"relations_emb.csv");
	
		System.out.println(termMap.size()+" TERMS");
		System.out.println(relSet.size()+" RELATIONS");
		

	}
	
}
