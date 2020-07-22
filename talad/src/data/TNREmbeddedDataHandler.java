package data;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map.Entry;

import database.TNREmbeddedHandler;

public class TNREmbeddedDataHandler extends TNREmbeddedHandler
{
	final Logger logger = LoggerFactory.getLogger(TNREmbeddedHandler.class);
	
	public TNREmbeddedDataHandler() throws ClassNotFoundException, SQLException, InstantiationException, IllegalAccessException 
	{
		super();
		connection.setAutoCommit(true);
		// TODO Auto-generated constructor stub
	}
	public Integer termeExiste(String s) throws SQLException
	{
		int i=0;

		try 
		{
			/*********/
//			System.out.println("Connecting to database..."); 
//			this.connection=DriverManager.getConnection(DB_URL,USER,PASS); 
			/************/
			ResultSet r = this.connection.createStatement().executeQuery("SELECT * FROM termes WHERE tname='"+s.replaceAll("'", "''")+"'");
			if(r.next())
			{
				i=r.getInt(1);
			}
		} 
		catch (SQLException e) 
		{
			e.printStackTrace();
		}
//		this.connection.close();
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
	
	public Integer createTerm(String tname) throws SQLException
	{
		this.logger.info("creating a new term");
		/**
		 * last tid 
		 */
		int last= 0;
		int tid;
		if(this.termeExiste(tname)==0)
		{
			
			ResultSet r=this.connection.createStatement().executeQuery("SELECT MAX(tid) FROM termes");
			if(r.next())
			{
				
				last=r.getInt(1);	
//				this.logger.info("last TID ="+last);
			}
			tid=last+1;
			this.insertTidTname(tid,tname);
			this.logger.info("term created with tid={}",tid);
		}
		else 
		{
			tid=this.termeExiste(tname);
			this.logger.info("term already exists with tid={}",tid);
		} 
		
		
		return tid;
	}
	
	/**
	 * 
	 * @param s 
	 * Chaine de caractères qui contient tid précalculé et tname. 
	 * @return
	 * Création du terme.
	 * @throws SQLException
	 */
	public Integer createTidTname(String s) throws SQLException
	{
		String[]str=s.split("\\|");
		int tid=Integer.valueOf(str[0]);//.replaceAll("\'", "''")
		String tname=str[1];
		System.out.println(str[0]+" "+str[1]);
		
		if(this.termeExiste(tname)==0)
		{
			System.out.println("inserting "+tname);
			String sql="INSERT INTO termes "
					+ "VALUES ("
					+ 	tid
					+	",\'"
					+	tname+"\')";
			System.out.println(sql);
			this.connection.createStatement().executeUpdate(sql);
			this.connection.commit();
		}
		return tid;
	}
	
	public Integer createRidRel(String s) throws SQLException
	{
		String[]str=s.split("\\,");
		
		int rid=Integer.valueOf(str[0]);
		int source=Integer.valueOf(str[1]);
		int type=Integer.valueOf(str[2]);
		int weight=Integer.valueOf(str[3]);
		int origin=Integer.valueOf(str[4]);
		int cible=Integer.valueOf(str[5]);
		
		if(this.relationExiste(source, type, cible)==0)
		{
			this.insertRidRel(rid, source, type, weight, origin, cible);
		}
		
		return rid;
	}
	public Integer lastRid;
	/**
	 * 
	 * @param s
	 * @param type
	 * @param w
	 * @param o
	 * @param t target term name
	 * @param def if true default weight and origin are applied
	 * @return
	 * @throws SQLException
	 */
	public Integer createRelation(String s, int type, int wei, int or, String t, boolean def) throws SQLException
	{
		this.logger.info("creating a new relation");
		int rid=0;
		int source=this.createTerm(s);
		int cible=this.createTerm(t);
		int w=50;
		int o=5;
		if(!def)
		{
			w=wei;
			o=or;
		}
		if(relationExiste(source,type,cible)==0)
		{
			
			rid=this.getLastRid()+1;
			this.insertRidRel(rid, source, type, w, o, cible);
			this.logger.info("relation created with rid={}",rid);
			this.connection.commit();
		}
		else 
		{
			/**
			 * check weight
			 */
			rid=relationExiste(source,type,cible);
			String q="SELECT poids FROM relations WHERE rid="+rid;
			ResultSet r=this.connection.createStatement().executeQuery(q);
			if(r.next())
			{
				this.logger.info("relation already exists, weight ={}",r.getInt(1));
			}
			
		}
//		super.connection.close();
		return rid;
	}
	
	
	public Integer getLastRid() throws SQLException
	{
		int last=0;
		ResultSet r=this.connection.createStatement().executeQuery("SELECT MAX(rid) FROM relations");
		if(r.next())
		{
			
			last=r.getInt(1);	
//			System.out.println("last ="+last);
		}
		return last;
		
	}
	public Integer insertTidTname(Integer tid,String tname) throws SQLException
	{
		this.logger.info("inserting term...");
		if(this.termeExiste(tname)==0)
		{
			System.out.println("inserting "+tname);
			String sql="INSERT INTO termes "
					+ "VALUES ("
					+ 	tid
					+	",\'"
					+	tname+"\')";
			System.out.println(sql);
			this.connection.createStatement().executeUpdate(sql);
			this.connection.commit();
		}
		return tid;
	}
	
	public Integer insertRidRel(Integer rid,Integer source,Integer type,Integer poids,Integer origine,Integer cible) throws SQLException
	{
		
		if(this.relationExiste(source,type,cible)==0)
		{
			this.logger.info("inserting relation ...");
			String sql="INSERT INTO relations "
					+ "VALUES ("
					+ 	rid
					+	","
					+	source
					+	","
					+	type
					+	","
					+	poids
					+	","
					+	origine
					+	","
					+	cible
					+")";
			System.out.println(sql);
			this.connection.createStatement().execute(sql);
			this.connection.commit();
		}
		return rid;
	}
	public void insertTermsFromFile(String path) throws IOException, SQLException
	{
		BufferedReader br=new BufferedReader(new FileReader(new File(path)));
		String line="";
		int i=0;
		while((line=br.readLine())!=null)
		{
			System.out.println(line);
			this.createTidTname(line);
			i++;
		}
		br.close();
		System.out.println("created "+i+" terms");
	}
	
	public void insertRelsFromFile(String path) throws IOException, SQLException
	{
		lastRid=this.getLastRid();
		BufferedReader br=new BufferedReader(new FileReader(new File(path)));
		String line="";
		int i=0;
		while((line=br.readLine())!=null)
		{
			System.out.println("line " +line);
			String []l=line.split("\\,");
			int type=TNRRelType.valueOf(l[1]).id();
			this.createRelation(l[0].replaceAll("'", "''"),type,Integer.valueOf(l[2]),TNROrigin.valueOf(l[3]).id(),l[4].replaceAll("'", "''"), false);
			i++;
//			connection.commit();
		}
		br.close();
		System.out.println("created "+i+" relations");
//		connection.commit();
		connection.close();
	}
	
	public Map<String,String>tree_map=new HashMap<String,String>();
	public Map<String,Set<String>>stree_map=new HashMap<String,Set<String>>();
	
	public Set<String> categories()
	{
		Set<String>set=new HashSet<String>();
		try 
		{
			this.logger.info("initializing...");
			
			ResultSet r = super.connection.createStatement().executeQuery("SELECT tname FROM termes WHERE tid IN(SELECT source FROM relations WHERE cible=955 AND type_id=1)");
			while(r.next())
			{
				String categorie=r.getString(1);
//				System.out.println("processing category "+categorie);
				set.add(categorie);
			}
			for(String categorie:set)
			{
//				System.out.println("processing category "+categorie);
				ResultSet res = super.connection.createStatement().executeQuery("SELECT tname FROM termes WHERE tid IN (SELECT source FROM relations WHERE type_id=9 AND poids>30 AND cible = (SELECT tid FROM termes WHERE tname='"+categorie.replaceAll("'", "''")+"'))");
//				System.err.println("!!!! "+res);
				while(res.next())
				{
//					System.out.println(res.getString(1)+" est une sous-catégorie de "+categorie);
					this.tree_map.put(res.getString(1), categorie);
				}
			}
//			for(Entry<String,String>en:this.tree_map.entrySet())
//			{
////				System.out.println(en.getKey()+" --> "+en.getValue());
//			}
			
				String concept="";
				for(Entry<String,String>en:this.tree_map.entrySet())
				{
					concept=en.getKey();
//					System.out.println("processing "+concept);
					Set<String>local_set=new HashSet<String>();
					int s=0;
					s=this.termeExiste(concept);
					ResultSet lr = super.connection.createStatement().executeQuery("SELECT tname FROM termes WHERE tid IN (SELECT source FROM relations WHERE type_id=1 AND poids>30 AND cible="+s+")");
					while(lr.next())
					{
//						this.logger.info("{} est un hypo de {}",lr.getString(1),en.getKey());
						local_set.add(lr.getString(1));
					}
					this.stree_map.put(concept, local_set);
				}
		} 
		catch (SQLException e) 
		{
			e.printStackTrace();
		}
//		System.out.println("categories size = "+set.size());
		return set;
	}
	
	public PreparedStatement psGetRelations=this.connection.prepareStatement("SELECT * FROM relations WHERE source=?");
	public PreparedStatement psShowRelations=this.connection.prepareStatement("SELECT * FROM termes WHERE tid=? ORDER BY tid DESC");
	
	public Map<String,String> EntryData(String tname, PreparedStatement rels,PreparedStatement names)throws SQLException
	{
		this.logger.info("building entry");
		Map<String,String> map=new HashMap<String,String>();
		int tid=0;
		int type=0;
		int cible=0;
		int origin=0;
		tid=this.termeExiste(tname);
//		System.out.println("tid "+tid);
//		this.connection=DriverManager.getConnection(DB_URL,USER,PASS); 
		try
		{
			rels.setInt(1,tid);
			//	System.out.println(rels);

			ResultSet r=rels.executeQuery();
			this.logger.info("available relations (type, origin)...");
			while(r.next())
			{
				//	System.out.println("résultat non vide");
				String key="";
				String value="";
				type=r.getInt(3);
				origin=r.getInt(5);
				
				this.logger.info("type {},  origin {}",type,origin);
				
				cible=r.getInt(6);
				//	System.out.println("cible "+cible);
				if(type==1)
				{
					key="hyperonyme";
				}
				if((type==7)&&(origin==4))
				{
					key="définition Wikipedia";
				}
				if((type==7)&&(origin!=4))
				{
					key="définition";
				}
					
				if(type==36)
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
//				StringBuilder sb=new StringBuilder();
				if(r2.next())
				{
					r2.absolute(1);
					value=r2.getString(2);
						
//						value=r2.getString(1);
//						System.out.println("value for "+key+" ---> "+value);
//						sb.append(value+" ; ");
//						
				}
//				map.put(key, sb.toString());
				map.put(key, value);
			}

		}
		catch(Exception cv)
		{
			cv.printStackTrace();
			System.err.println("error while processing "+tname);
		}
		this.connection.commit();
		/**************************************/
//		this.connection.close();
		this.logger.info("ENTRY DATA FOR {}",tname);
			for(Entry<String,String>e:map.entrySet())
			{
				this.logger.info("{}: {}",e.getKey(),e.getValue());
			}
		return map;
	}
	
	
	public void exportTermes(File f) throws IOException, SQLException
	{
		PrintWriter pw=new PrintWriter(new FileWriter(f));
		ResultSet rter=this.connection.createStatement().executeQuery("SELECT * FROM termes");
		while(rter.next())
		{
			String s=rter.getInt(1)+";"+rter.getString(2);
			pw.println(s);
			pw.flush();
		}
		pw.close();
	}
	
	public void exportRelations(File f)throws IOException,SQLException
	{
		PrintWriter pw=new PrintWriter(new FileWriter(f));
		ResultSet rrel=this.connection.prepareStatement("SELECT * FROM relations").executeQuery();
		while(rrel.next())
		{
			pw.println(rrel.getInt(1)+";"+rrel.getInt(2)+";"+rrel.getInt(3)+";"+rrel.getInt(4)+";"+rrel.getInt(5)+";"+rrel.getInt(6));
			pw.flush();
		}
		pw.close();
	}
	public static void main(String[] args) 
	{
		TNREmbeddedDataHandler edh;
		try 
		{
			edh = new TNREmbeddedDataHandler();
			System.out.println(edh.termeExiste("nom de cours d'eau"));
//			File f=new File("./testexport.csv");
//			edh.exportTermes(f);
			//edh.connection.createStatement("INSERT INTO types ")
//			ResultSet res=edh.connection.prepareStatement("SELECT tid FROM termes WHERE tname='odonyme'").executeQuery();
//			//AND source IN(SELECT tid FROM termes WHERE tname='oronyme')
//			if(res.next())
//			{
//				System.out.println(res.getInt(1));
//			}
//			System.out.println(edh.relationExiste(287, 36, 28758));
//			System.out.println(edh.lastRid);
//			edh.insertRidRel(edh.getLastRid()+1, edh.termeExiste("référence"), 9, 50, 5, 955);
////			edh.insertRelsFromFile("arbre_rels.csv");
//			System.out.println("lastRid="+edh.getLastRid());
		} 
		catch (ClassNotFoundException | InstantiationException | IllegalAccessException | SQLException e1) 
		{
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		
	}
}
