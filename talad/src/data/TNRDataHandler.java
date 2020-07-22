package data;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.Duration;
import java.time.Instant;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Queue;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.Set;
import java.util.TreeSet;

import org.apache.commons.configuration2.Configuration;
import org.apache.commons.configuration2.ex.ConfigurationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import database.TNRDatabaseHandler;

public class TNRDataHandler extends TNRDatabaseHandler
{
	public TNRDataHandler() 
			throws ConfigurationException, ClassNotFoundException, SQLException, InstantiationException,
			IllegalAccessException 
	{
		super();
		// TODO Auto-generated constructor stub
	}
	public TNRDataHandler(Configuration configuration) 
			throws ClassNotFoundException, IllegalAccessException, InstantiationException, SQLException
	{
		super(configuration);
	}
	
	final Logger logger = LoggerFactory.getLogger(TNRDataHandler.class);
	public static final File stopfile=new File("resources/stopwords.txt");
	//	public TNRParser tp=new TNRParser();

	@SuppressWarnings("unused")
	private PreparedStatement psInsertTerms = this.connection.prepareStatement(
			"INSERT INGORE INTO termes (`tid`, `tname`) "
					+ "VALUES (?, ?)");
	@SuppressWarnings("unused")
	private PreparedStatement psInsertTerm=this.connection.prepareStatement("INSERT IGNORE INTO termes(`tname`) VALUES ?");
	//définition
	@SuppressWarnings("unused")
	private PreparedStatement psInsertDefinition=this.connection.prepareStatement("INSERT IGNORE INTO relations(`source`, `type_id`, `poids`, `origine`, `cible`) VALUES (?, 7, 200, 7, ?)");
	@SuppressWarnings("unused")
	private PreparedStatement psInsertShortDefinition=this.connection.prepareStatement("INSERT IGNORE INTO relations(`source`, `type_id`, `poids`, `origine`, `cible`) VALUES (?, 34, 200, 7, ?)");
	@SuppressWarnings("unused")
	private PreparedStatement psModifyDefinition=this.connection.prepareStatement("UPDATE relations SET tname = ? WHERE tname=?");
	@SuppressWarnings("unused")
	private PreparedStatement psModifyShortDefinition=this.connection.prepareStatement("UPDATE relations SET tname = ? WHERE tname=?");

	@SuppressWarnings("unused")
	private PreparedStatement psInsertHyperonym=this.connection.prepareStatement("INSERT IGNORE INTO relations(`source`, `type_id`, `poids`, `origine`, `cible`) VALUES (?, 1, 200, 7, ?)");
	@SuppressWarnings("unused")
	private PreparedStatement psInsertVariant=this.connection.prepareStatement("INSERT IGNORE INTO relations(`source`, `type_id`, `poids`, `origine`, `cible`) VALUES (?, 3, 200, 7, ?)");

	//compléter les autres cas de figure!!

	public PreparedStatement psGetRelations=this.connection.prepareStatement("SELECT * FROM relations WHERE source=?");
	public PreparedStatement psShowRelations=this.connection.prepareStatement("SELECT tname FROM termes WHERE tid=?");



	@SuppressWarnings("unused")
	private PreparedStatement psInsertRelations = this.connection.prepareStatement(
			"INSERT INTO relations (`rid`, `source`, `type_id`, `poids`, `origine`, `cible`) "
					+ "VALUES (?, ?, ?, ?, ?, ?)");
	private PreparedStatement psInsertTypes = this.connection.prepareStatement(
			"INSERT INTO types (`type_id`, `type`, `contrainte`) "
					+ "VALUES (?, ?, ?)");
	/**
	 * insertion des termes dans la base à partir d'un ensemble clé-valeur 
	 * avec la clé (chaîne de catactères) et sa valeur soit id du terme à insérer (integer)
	 * @param ps
	 * @param map
	 * @return
	 * @throws SQLException
	 */
	@SuppressWarnings("unused")
	private boolean insertTerms(PreparedStatement ps,Map<String,Integer>map)throws SQLException
	{
		for(Entry<String, Integer> e:map.entrySet())
		{
			try
			{
				ps.setInt(1,e.getValue());
				ps.setString(2, e.getKey());
				ps.executeUpdate();
			}
			catch(Exception cv)
			{
				cv.printStackTrace();
				this.logger.warn("error while processing key={} value={}",e.getKey(),e.getValue());
			}
		}
		return true;	
	}
	
	
	
	/**
	 * insertion des relations à partir d'un tableau de Integers 
	 * @param ps
	 * @param relset
	 * @return
	 * @throws SQLException
	 */
	@SuppressWarnings("unused")
	private boolean insertRelations(PreparedStatement ps,Set<Integer[]>relset)throws SQLException
	{

		for(Integer[] e:relset)
		{
			try
			{
				ps.setInt(1,e[0]);
				ps.setInt(2,e[1]);
				ps.setInt(3,e[2]);
				ps.setInt(4,e[3]);
				ps.setInt(5,e[4]);
				ps.setInt(6,e[5]);
				ps.executeUpdate();
			}
			catch(Exception cv)
			{
				cv.printStackTrace();
				this.logger.warn("error while processing {}",Arrays.asList(e));
			}
		}
		return true;	
	}
	private boolean insertTypes(PreparedStatement ps,Set<String[]>set)throws SQLException
	{

		for(String[] e:set)
		{
			try
			{
				ps.setInt(1,Integer.valueOf(e[0]));
				ps.setString(2, e[1]);
				ps.setString(3, e[2]);
				ps.executeUpdate();
			}
			catch(Exception cv)
			{
				cv.printStackTrace();
				this.logger.warn("error while processing {}",Arrays.asList(e));
			}
		}
		return true;	
	}
	//	private PreparedStatement psInsertConnectors = this.connection.prepareStatement(
	//	        "INSERT IGNORE INTO termes (`tid`,`tname`) "
	//     + "VALUES (?)");

	public boolean insertConnectors(PreparedStatement ps, String path) throws SQLException, FileNotFoundException
	{
		this.connection.createStatement().execute("USE tnr");
		BufferedReader br=new BufferedReader(new FileReader(new File(path)));
		String line="";
		try
		{
			while((line=br.readLine())!=null)
			{
				ps.setString(1, line);
				ps.executeUpdate();
				String[]str=line.split(" ");
				for(String s:str)
				{
					String part=s.toString();
					this.createRelation(part, TNRRelType.r_pattern, line, 50, 4);
				}
			}
			br.close();
			this.connection.commit();
		}
		catch (IOException io)
		{
			stop("erreur");
			this.logger.warn("error while processing line {} ",line);
		}
		return true;	
	}
	public boolean termExists(String s)
	{
		boolean r=false;
		try 
		{
			r = this.connection.createStatement().execute("SELECT * FROM termes WHERE tname='"+s+"'");
		} 
		catch (SQLException e) 
		{
			e.printStackTrace();
		}
		return r;
	}
	/**
	 * mise à jour du poids des relatiosns pour une liste de relations en clair
	 * 
	 * @param path
	 * @throws SQLException 
	 */
	public void updateWeigth(String path)throws IOException, SQLException
	{
		BufferedReader br=new BufferedReader(new FileReader(new File(path)));
		String line="";
		while((line=br.readLine())!=null)
		{
			String[]l=line.split(",");
			int s=0;
			int c=0;
			s=this.termeExiste(l[0]);
			c=this.termeExiste(l[4]);
			String q="UPDATE relations SET poids=25 WHERE source="+s+" AND cible="+c+" AND type_id=9";
			this.connection.createStatement().execute("USE tnr");
			this.connection.createStatement().execute(q);
			this.logger.info("updated {}",line);
			this.connection.commit();
		}
		br.close();
	}
	public Map<String,Set<String>>stree_map=new HashMap<String,Set<String>>(); 
	public Set<String> categories()
	{
		Set<String>set=new HashSet<String>();
		try 
		{
			this.logger.info("initialisation...");
			
			this.connection.createStatement().execute("USE tnr");
			ResultSet r = this.connection.createStatement().executeQuery("SELECT tname FROM termes WHERE tid IN(SELECT source FROM relations WHERE cible=1862 AND type_id=1)");
			while(r.next())
			{
				String categorie=r.getString(1);
//				this.logger.info("processing {}",categorie);
				set.add(categorie);
				ResultSet res = this.connection.createStatement().executeQuery("SELECT tname FROM termes WHERE tid IN (SELECT source FROM relations WHERE type_id=9 AND poids>30 AND cible IN (SELECT tid FROM termes WHERE tname=\""+categorie+"\"))");

				while(res.next())
				{
//					this.logger.info("{} est une sous-catégorie de {}",res.getString(1),categorie);
					this.tree_map.put(res.getString(1), categorie);
				}
				String concept="";
				for(Entry<String,String>en:this.tree_map.entrySet())
				{
					concept=en.getKey();
//					this.logger.info("processing {}",concept);
					Set<String>local_set=new HashSet<String>();
					int s=0;
					s=this.termeExiste(concept);
					ResultSet lr = this.connection.createStatement().executeQuery("SELECT tname FROM termes WHERE tid IN (SELECT source FROM relations WHERE type_id=1 AND poids>30 AND cible="+s+")");
					while(lr.next())
					{
//						this.logger.info("{} est un hypo de {}",lr.getString(1),en.getKey());
						local_set.add(lr.getString(1));

					}
					this.stree_map.put(concept, local_set);
				}
			}
		} 
		catch (SQLException e) 
		{
			e.printStackTrace();
		}
		return set;
	}


	public Map<String,String>tree_map=new HashMap<String,String>();

	public Integer termeExiste(String s) throws SQLException
	{
		int i=0;

		try 
		{
			this.connection.createStatement().execute("USE tnr");
			
			if(s!=null)
			{
				ResultSet r = this.connection.createStatement().executeQuery("SELECT * FROM termes WHERE tname='"+s.replaceAll("\'", "''")+"'");
				if(r.next())
				{
					i=r.getInt(1);
				}
			}
		} 
		catch (SQLException e) 
		{
			e.printStackTrace();
		}
		return i;
	}
	public Integer relationExiste(int s, int type, int c)
	{
		int i=0;
		try 
		{
			ResultSet r = this.connection
					.createStatement()
					.executeQuery("SELECT * FROM relations WHERE "
							+ "source="+s
							+" AND cible="+c
							+" AND type_id="+type);
			if(r.next())
			{
				i=r.getInt(1);
			}
		} 
		catch (SQLException e) 
		{
			e.printStackTrace();
		}
		return i;

	}

	public Integer createTerm(String s) throws SQLException
	{
		this.connection.createStatement().execute("USE tnr");
		boolean termExists=false;
		Integer tid=0;
		ResultSet r=this.connection.createStatement().executeQuery("SELECT * FROM termes WHERE tname='"+s.replaceAll("\'", "''")+"'" );
		if(r.next())
		{
			tid=r.getInt(1);
			System.out.println("tid exists="+tid);
			termExists=true;
		}
		if(!termExists)
		{
			System.out.println("inserting "+s.replaceAll("\'", "''"));
			this.connection.createStatement().executeUpdate(""
					+ "INSERT IGNORE INTO termes (tname) "
					+ "VALUES (\'"+s.replaceAll("\'", "''")+"\');");
			this.connection.commit();
		}

		ResultSet r2=this.connection.createStatement().executeQuery("SELECT * FROM termes WHERE tname='"+s.replaceAll("\'", "''")+"'" );
		if(r2.next())
		{
			tid=r2.getInt(1);
			System.out.println("tid="+tid);
		}
		return tid;
	}

	public boolean createRelation(String source,TNRRelType type,String cible,int poids,int origine) throws SQLException
	{
		this.connection.createStatement().execute("USE tnr");
		int s=0;
		int c=0;
		s=this.termeExiste(source);
		c=this.termeExiste(cible);
		//	poids=50;
		//	origine=3;
		if(s==0)
		{
			s=this.createTerm(source);
		}
		if(c==0)
		{
			c=this.createTerm(cible);
		}
		this.connection.commit();
		if(this.relationExiste(s, type.id(), c)==0)
		{
			this.connection
			.createStatement()
			.executeUpdate("INSERT IGNORE INTO relations "
					+ "(source,type_id,poids,origine,cible) "
					+ " VALUES ("+s+","+type.id()+","+poids+","+origine+","+c+");");
			System.out.println("inserting relation "+s+"--"+type.id()+"-->"+c);
			this.connection.commit();
		}
		return true;
	}
	
	public boolean newRelation(String source,Integer type,String cible,int poids,int origine) throws SQLException
	{
		this.connection.createStatement().execute("USE tnr");
		int s=0;
		int c=0;
		s=this.termeExiste(source);
		c=this.termeExiste(cible);
		//	poids=50;
		//	origine=3;
		if(s==0)
		{
			s=this.createTerm(source);
		}
		if(c==0)
		{
			c=this.createTerm(cible);
		}
		this.connection.commit();
		if(this.relationExiste(s, type, c)==0)
		{
			this.connection
			.createStatement()
			.executeUpdate("INSERT IGNORE INTO relations "
					+ "(source,type_id,poids,origine,cible) "
					+ " VALUES ("+s+","+type+","+poids+","+origine+","+c+");");
			System.out.println("inserting relation "+s+"--"+type+"-->"+c);
			this.connection.commit();
		}
		return true;
	}
	
	
	public Map<Integer,Set<String>>Relations(String tname, PreparedStatement rels,PreparedStatement names)throws SQLException
	{
		Map<Integer,Set<String>> relmap=new HashMap<Integer,Set<String>>();
		this.connection.createStatement().execute("USE tnr");
		int tid=0;
		int key=0;
		int cible=0;
		tid=this.termeExiste(tname);
//		System.out.println("tid "+tid);
		try
		{
			rels.setInt(1,tid);
			ResultSet r=rels.executeQuery();
			
			while(r.next())
			{
				key=r.getInt(3);
				cible=r.getInt(6);
				
				String value="";
				
				names.setInt(1, cible);
//				System.out.println(names);
				
				ResultSet r2=names.executeQuery();
				
				Set<String> set=new HashSet<String>();
				
				while(r2.next())
				{
					value=r2.getString(1);
					set.add(value);
					
//					this.logger.info("key : {} value : {}",key, value);
					
				}
//				if(set)
				relmap.put(key,set);
			}
		}
		catch(Exception cv)
		{
			cv.printStackTrace();
			this.logger.warn("error while processing {}",tname);
		}
		this.connection.commit();
//			for(Entry<Integer,Set<String>>e:relmap.entrySet())
//			{
//				System.out.println(e.getKey()+" "+e.getValue());
//			}
		return relmap;
		
	}
	
	public Map<String,String> EntryData(String tname, PreparedStatement rels,PreparedStatement names)throws SQLException
	{
		Map<String,String> map=new HashMap<String,String>();
		this.connection.createStatement().execute("USE tnr");
		int tid=0;
		int type=0;
		int cible=0;

		tid=this.termeExiste(tname);
		System.out.println("tid "+tid);
		try
		{
			rels.setInt(1,tid);
			//	System.out.println(rels);

			ResultSet r=rels.executeQuery();
			while(r.next())
			{
				//	System.out.println("résultat non vide");
				String key="";
				String value="";
				type=r.getInt(3);
				//	System.out.println("type "+type);
				cible=r.getInt(6);
				//	System.out.println("cible "+cible);
				if(type==1)
				{
					key="hyperonyme";
				}
				if(type==7)
				{
					key="définition";
				}
				if(type==34)
				{
					key="définition courte";
				}
				if(type==3)
				{
					key="variante";
				}
				if(type==4)
				{
					key="synonyme";
				}
				if(type==9)
				{
					key="domaine";
				}
				if(type==8)
				{
					key="exemple";
				}
				names.setInt(1, cible);
				ResultSet r2=names.executeQuery();
				StringBuilder sb=new StringBuilder();
				while(r2.next())
				{
					value=r2.getString(1);
					sb.append(value+" ; ");
					
				}
				map.put(key, sb.toString());
			}

		}
		catch(Exception cv)
		{
			cv.printStackTrace();
			this.logger.warn("error while processing {}",tname);
		}
		this.connection.commit();
			for(Entry<String,String>e:map.entrySet())
			{
				System.out.println(e.getKey()+" "+e.getValue());
			}
		return map;
	}

	public void deleteDuplicatedEntries()
	{
		try 
		{
			ResultSet r=this.connection.createStatement().executeQuery("SELECT * FROM termes" );
			//	"select * from (select *, row_number() OVER ( partition by tname order by tname) as rn from #programming) dups WHERE rn > 1"
			if(r.next())  
			{

			}
		} 
		catch (SQLException e) 
		{
			e.printStackTrace();
		}
	}

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
	public void processVariable()
	{

	}
	public String getStringType()
	{
		String s="";
		return s;
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
			//	if(i==pattern.size()-1)
			//	{
			//	match=true;
			//	}
			//	}
		}
		return match;
	}
	public void loadData() throws SQLException, IOException
	{
		Instant start = Instant.now();
		//	tp.getDataSet(false);
		this.connection.createStatement().execute("USE tnr");
		//	this.insertTerms(this.psInsertTerms, tp.termes);
		//	this.insertRelations(this.psInsertRelations, tp.rels);
		this.insertTypes(this.psInsertTypes, TNRRelType.getAllTypes());
		this.connection.commit();
		this.logger.info("Done ({})", Duration.between(start, Instant.now()));
	}
	public void loadDataFromFile(String path,String datatype) throws IOException, SQLException
	{
		File f=new File(path);
		BufferedReader br=new BufferedReader(new FileReader(f));
		String line="";
		int i=1;
		this.connection.createStatement().execute("USE tnr");
		while((line=br.readLine())!=null)
		{
			if(datatype.equals("termes"))
			{
				System.out.println("processing line "+i);
				int exists=this.termeExiste(line);
				if(exists==0)
				{
					this.createTerm(line);
				}
			}
			if(datatype.equals("relations"))
			{
				String[] str=line.split(";");
				this.createRelation(str[0], TNRRelType.valueOf(str[1]), str[4], Integer.valueOf(str[2]), Integer.valueOf(str[3]));
			}
			i++;
		}
		br.close();

	}

	public Set<String> getStopwords() throws IOException
	{
		Set<String> words=new TreeSet<String>();
		BufferedReader br=new BufferedReader(new FileReader(stopfile));
		String line="";
		while((line=br.readLine())!=null)
		{
			words.add("\\b"+line+"\\b");
			words.add(line);
		}
		br.close();
		return words;
	}

	public void annotate(String segment) throws SQLException, IOException
	{
		Set<String> pats=new HashSet<String>();
		Set<String> frs=new HashSet<String>();
		Set<String> stop=new HashSet<String>();
		stop=this.getStopwords();
		this.logger.info("traitement des patrons répertoriés...");
		for(Entry<Integer,String>e:this.segmentText(segment).entrySet())
		{
			if(this.isInPattern(e.getValue()))
			{
				if(!stop.contains(e.getValue()))
				{
					if(pats.isEmpty())
					{
						pats.addAll(this.getPatterns(e.getValue()));
						this.logger.info("premier patron obtenu à partir de ..."+e.getValue());
					}
					else
					{
						pats.addAll(this.getPatterns(e.getValue()));
						//	pats.retainAll(this.getPatterns(e.getValue()));
						this.logger.info("patron obtenu à partir de ..."+e.getValue());
					}
				}
			}
			if(this.isInFrame(e.getValue()))
			{
				if(frs.isEmpty())
				{
					frs.addAll(this.getFrames(e.getValue()));
					this.logger.info("frame obtenu à partir de ..."+e.getValue());
				}
				else
				{
					frs.retainAll(this.getFrames(e.getValue()));
					this.logger.info("frame obtenu à partir de ..."+e.getValue());
				}
			}
		}
		for(String p:pats)
		{
			System.out.println("patron "+p);
		}
		for(String f:frs)
		{
			System.out.println("frame : "+f);
		}
	}
	/**context categories 
	 * - comprehension >>relations taxonomiques
	 * - variation >> syn, variantes
	 * - prise en charge >> présence du frame +rels
	 * @throws SQLException 
	 * */
	public Integer comprehension(String context) throws SQLException
	{
		int c=0;
		for(Entry<Integer,String>e:this.segmentText(context).entrySet())
		{
			if(this.termeExiste(e.getValue())>0)
			{

			}
		}

		return c;
	}
	public static void main(String[]args) 
			throws ClassNotFoundException, InstantiationException, IllegalAccessException, ConfigurationException, SQLException
	{
		TNRDataHandler dh=new TNRDataHandler();
		dh.connection.createStatement().execute("USE tnr");

		//	System.out.println(dh.isPunctuation("abcd"));
		//	dh.segmentPattern("la supercherie qu’il pouvait y avoir à désigner certaines personnes comme $X:Nom:");
		//	dh.applyPattern(dh.segmentText("le phénomène, improprement nommé discours"),dh.segmentPattern("le phénomène, improprement nommé $X:Nom:"));
		try
		{
			System.out.println("started...");
			Instant start=Instant.now();
//			ResultSet res=dh.connection.createStatement().executeQuery("SELECT COUNT(*) FROM termes");
//			while(res.next())
//			{
//				dh.logger.info("nombre de termes {}", res.getInt(1));
//			}
			//dh.updateWeigth("output/tnr/rels/arbre_rels_mod.csv");
			//	dh.EntryData("liage", dh.psGetRelations, dh.psShowRelations);
			System.out.println(dh.termeExiste("sur-détermination"));
//			dh.Relations("querelle de catégorisation", dh.psGetRelations, dh.psShowRelations);
//				dh.annotate("La méthode, c'est de mettre en place un patriotisme économique et un protectionnisme intelligent.");
			//	dh.loadDataFromFile("C:/Users/clairet/Documents/TALAD/Articles_2015_JDMlemma.csv","relations");
			dh.logger.info("Done ({})", Duration.between(start, Instant.now()));
		}
		catch(Exception io)
		{
			io.printStackTrace();
		}
	}
}
