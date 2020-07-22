package annotation;

import java.io.File;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Map;
import java.util.Queue;
import java.util.Set;
import java.util.Map.Entry;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.configuration2.ex.ConfigurationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import data.TNRDataHandler;


public class Annotator extends TNRDataHandler
{

	public Annotator() 
			throws ConfigurationException, ClassNotFoundException, SQLException, InstantiationException,IllegalAccessException 
	{
		super();
	}
	final Logger logger = LoggerFactory.getLogger(TNRDataHandler.class);
	public static final File stopfile=new File("resources/stopwords.txt");
	
	public PreparedStatement psGetRelations=this.connection.prepareStatement("SELECT * FROM relations WHERE source=?");
	public PreparedStatement psShowRelations=this.connection.prepareStatement("SELECT tname FROM termes WHERE tid=?");

	
	public boolean isInPattern(String s) throws SQLException
	{
		boolean b=false;
		if(this.termeExiste(s)>0)
		{
			ResultSet rs=this.connection.createStatement().executeQuery("SELECT * FROM relations WHERE source="+this.termeExiste(s));
			this.connection.commit();
			while(rs.next())
			{
				if(rs.getInt(3)==34)
				{
					b=true;
				}
			}
		}
		return b;
	}
	public Set<String> getPatterns(String s) throws SQLException
	{
		Set<String> pats = new HashSet<String>();
		ResultSet rs=this.connection.createStatement().executeQuery("SELECT * FROM relations WHERE source="+this.termeExiste(s));
		this.connection.commit();
		while(rs.next())
		{
			if(rs.getInt(3)==34)
			{
				int tid=rs.getInt(6);
				ResultSet r=this.connection.createStatement().executeQuery("SELECT * FROM termes WHERE tid="+tid);
				if(r.next())
				{
					pats.add(r.getString(2));
				}
			}
		}
		return pats;
	}

	public boolean isInFrame(String s) throws SQLException
	{
		boolean b=false;
		if(this.termeExiste(s)>0)
		{
			ResultSet rs=this.connection.createStatement().executeQuery("SELECT * FROM relations WHERE source="+this.termeExiste(s));
			this.connection.commit();
			while(rs.next())
			{
				if(rs.getInt(3)==35)
				{
					b=true;
				}
			}
		}
		return b;
	}
	public Set<String> getFrames(String s) throws SQLException
	{
		Set<String> frames = new HashSet<String>();
		ResultSet rs=this.connection.createStatement().executeQuery("SELECT * FROM relations WHERE source="+this.termeExiste(s));
		this.connection.commit();
		while(rs.next())
		{
			if(rs.getInt(3)==35)
			{
				int tid=rs.getInt(6);
				ResultSet r=this.connection.createStatement().executeQuery("SELECT * FROM termes WHERE tid="+tid);
				if(r.next())
				{
					frames.add(r.getString(2));
				}
			}
		}
		return frames;
	}
	public boolean isTerm(String s)
	{
		boolean yes=false;
		if(!this.isPunctuation(s))
		{
			if(!this.isVariable(s))
			{
				yes=true;
			}
		}
		return yes;
	}
	public boolean isVariable(String s)
	{
		boolean yes=false;
		Pattern p=Pattern.compile("\\$");
		Matcher m=p.matcher(s);
		if(m.find())
		{
			yes=true;
		}
		return yes;
	}
	public boolean containsVariable(String pattern)
	{
		boolean yes=false;
		if(pattern.contains("$"))
		{
			yes=true;
		}
		return yes;
	}
	public boolean isPunctuation(String s)
	{
		boolean yes=false;
		Pattern p=Pattern.compile("[.!\";,:-]{1,3}");
		Matcher m=p.matcher(s);
		if(m.matches())
		{
			yes=true;
		}
		return yes;
	}
	
	/**
 * 
 * @param s - ngramme qui correspond 
 * @return
 * @throws SQLException 
 */
public Map<Integer,String> segmentText(String s) throws SQLException
{
	/**retrouver le mot dans la BC. 
Si le terme TNR correspondant à ce mot a une relation typée r_pattern, alors il appartient au patron
vérifier si les autres termes matchent un des patrons auxquels appartient le terme*/
	Map<Integer,String> tmap=new HashMap<Integer,String>();
	Queue<Integer> q=new LinkedList<Integer>();
	Pattern p=Pattern.compile("[^\\s\\wéâàîôèüçê\']{1,3}(\\s)?");/**(« )( »)**/
	Matcher m=p.matcher(s);
	int from=0;
	q.add(0);
	while(m.find())//*****find
	{
		int t=0;
		t=s.indexOf(m.group(0),from);
		System.out.println("s="+t+" "+m.group(0));	
		q.add(s.indexOf(m.group(0),from));
		from=t;
	}
	System.out.println(Arrays.asList(q));
	Queue<String> qs=new LinkedList<String>();

	int start=0;
	int end=0;
	int j=0;

	while(!q.isEmpty())
	{
		System.out.println(Arrays.asList(q));
		if(q.size()>1)//while
		{
			start=q.peek();
			q.remove();
			end=q.peek();
			//	System.out.println(start+" "+end+"-->"+s.substring(start, end));
			qs.add(s.substring(start, end));
			System.out.println(Arrays.asList(qs));
			String sub=s.substring(start, end).trim();
			System.out.println("processing substring == "+sub);
			if(sub.length()>0)
			{
				if(sub.length()==1)
				{
					tmap.put(j, sub.trim());
					j++;
				}
				else
				{
					String[]substr=sub
							.replaceAll("qu\'","que ")
							.replaceAll("d\'", "de ")
							.replaceAll("l\'", "le ")
							.split("( |\u00a0)");
					for(int n=0;n<=substr.length-1;n++)
					{
						if(substr[n].length()>0)
						{
							System.out.println("sub sub string "+substr[n]);
							tmap.put(j, substr[n].trim());
							j++;
						}
					}
				}	
			}
		}
		//taille 0 indique qu'on n'a pas de signe de ponctuation dans le segment à traiter
		if(q.size()==1)
		{
			//	System.out.println("q.size()==1");
			String temp=s.substring(q.poll());
			if(temp.contains(" "))
			{
				//	System.out.println("contient espace");

				String[]str=temp.split("\\s");
				//	System.out.println("str len"+str.length);
				for(int k=j;k<str.length+j;k++)//for(int k=0;k<str.length-1;k++)
				{
					//	System.out.println("k=="+k+" "+"str len"+str.length);
					tmap.put(k,str[k]);
				}
			}
			else
			{
				//	System.out.println("ne contient pas d'espace");
				tmap.put(j,temp);
			}

		}

		for(Entry<Integer,String>e:tmap.entrySet())
		{
			System.out.println(e.getKey()+" "+e.getValue());
		}
	}

	return tmap;
}
public Map<Integer,String> segmentPattern(String s) throws SQLException
{
	/**retrouver le mot dans la BC. 
	Si le terme TNR correspondant à ce mot a une relation typée r_pattern, alors il appartient au patron
	vérifier si les autres termes matchent un des patrons auxquels appartient le terme*/
	
	Map<Integer,String> tmap=new HashMap<Integer,String>();
	Queue<Integer> q=new LinkedList<Integer>();
	Pattern p=Pattern.compile("[^\\s\\wéâàîôèüçê$:\']{1,3}");
	Matcher m=p.matcher(s);
	s=s+"|";
	int from=0;
	q.add(0);
	while(m.find())//*****find
	{
		int t=0;
		t=s.indexOf(m.group(0),from);
		System.out.println("s="+t+" "+m.group(0));	
		q.add(s.indexOf(m.group(0),from));
		from=t;
	}
	q.add(s.indexOf("|"));
	System.out.println(Arrays.asList(q));

	Queue<String> qs=new LinkedList<String>();

	int start=0;
	int end=0;
	int j=0;

	while(!q.isEmpty())
	{
		System.out.println(Arrays.asList(q));
		if(q.size()>1)//while
		{
			start=q.peek();
			q.remove();
			end=q.peek();
			System.out.println(start+" "+end+"-->"+s.substring(start, end));
			qs.add(s.substring(start, end));
			System.out.println(Arrays.asList(qs));
			String sub=s.substring(start, end).trim();
			System.out.println("processing substring == "+sub);
			if(sub.length()>0)
			{
				if(sub.length()==1)
				{
					tmap.put(j, sub.trim());
					j++;
				}
				else
				{
					String[]substr=sub
							.replaceAll("qu\'","que ")
							.replaceAll("qu(\u00a0)?(\\’|’\0xAB|\00B4|\u02BC|\u0027|\u2019|\u2018||\u0060|\u2032|\u2035|\u02B9)","que ")
							.replaceAll("d\'", "de ")
							.replaceAll("l\'", "le ")
							.split("( |\u00a0)");
					for(int n=0;n<=substr.length-1;n++)
					{
						if(substr[n].length()>0)
						{
							System.out.println("sub sub string "+substr[n]);
							tmap.put(j, substr[n].trim());
							j++;
						}
					}
				}	
			}
		}
		if(q.size()==1)
		{
			break;
		}

	}
	for(Entry<Integer,String>e:tmap.entrySet())
	{

		System.out.println(e.getKey()+" "+e.getValue());
	}
	return tmap;
}
public boolean checkPos(String s,String pos) throws SQLException
{
	boolean match=false;
	this.connection.createStatement().execute("USE tnr");
	Integer pos_id=this.termeExiste(pos);
	ResultSet rs=this.connection.createStatement().executeQuery("SELECT * FROM relations WHERE source="+this.termeExiste(s)+" AND type_id=26");
	int c=0;
	while(rs.next())
	{
		c=rs.getInt(6);
		if(c==pos_id)
		{
			match=true;
		}
	}
	return match;
}
public Integer getVariablePos(String var) throws SQLException
{
	int pos_id=0;
	String pos=var.replaceAll("\\$(X|Y|Z)(\\:)?", "");
	if(pos.length()>0)
	{
		pos_id=this.termeExiste(pos);
	}
	return pos_id;
}
public Set<Integer> getTermPos(String term) throws SQLException
{
	Set<Integer>pos_set=new HashSet<Integer>();
	this.connection.createStatement().execute("USE tnr");
	ResultSet rs=this.connection.createStatement().executeQuery("SELECT * FROM relations WHERE source="+this.termeExiste(term)+" AND type_id=26");
	int c=0;
	while(rs.next())
	{
		c=rs.getInt(6);
		pos_set.add(c);
	}
	return pos_set;
}
public boolean applyPattern(Map<Integer,String> text,Map<Integer,String> pattern) throws SQLException
{
	this.connection.createStatement().execute("USE tnr");
	boolean match=false;
	//	if(text.size()==pattern.size())
	//	{
	for(int i=0;i<=pattern.size()-1;i++)
	{
		String ps=pattern.get(i);
		String ts=text.get(i);
		if(ts!=null)
		{
			System.out.println("matching "+ps+ " against "+ts);
			String m=null;
			if(this.isTerm(ps))
			{
				m="term";

			}
			if(this.isVariable(ps))
			{
				m="var";
			}
			if(this.isPunctuation(ps))
			{
				m="punct";

			}
			System.err.println("m="+m);
			//	
			switch(m)
			{
			case "term":
				//check equality
				if(ps.equals(ts))
				{
					System.out.println("processing term = "+ts);
				}
				break;

			case "var":

				//check pos
				System.out.println("processing variable = "+ps);
				int pid=this.getVariablePos(ps);
				System.out.println("pid = "+pid);
				for(Integer pos:this.getTermPos(ts))
				{
					System.out.println("pos for "+ts+" "+pos);
				}
				if(this.getTermPos(ts).contains(pid))
				{
					System.out.println("contains pid");
					break;
				}
			case "punct":
				//check equality
				if(ps.equals(ts))
				{
					break;
				}
			}
		}
	}
	return match;
}
}
