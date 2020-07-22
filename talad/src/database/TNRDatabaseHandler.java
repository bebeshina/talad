package database;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Properties;

import org.apache.commons.configuration2.Configuration;
import org.apache.commons.configuration2.ex.ConfigurationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


	public class TNRDatabaseHandler 
	{
		/**
		 * The configuration.
	     */
	    protected Configuration configuration;

	    /**
	     * The database connection.
	     */
	    protected Connection connection;

	    /**
	     * The logger.
	     */
	    protected Logger logger;
	    static final String JDBC_DRIVER="com.mysql.cj.jdbc.Driver";
	    static final String DB_URL="jdbc:mysql://localhost/";
	    static final String USER="root";
	    static final String PASS="praxi";
	    public TNRDatabaseHandler(Configuration configuration)throws ClassNotFoundException, SQLException, IllegalAccessException, InstantiationException
	    {
	        this.logger = LoggerFactory.getLogger(this.getClass().getSimpleName());
	        this.configuration = configuration;
	        
	        try
	        {
		        Class.forName(this.configuration.getString("tnr.driverClass")).newInstance();
		        this.connection = this.getNewConnection();
		       
//		        System.out.println(this.connection.getClientInfo());
	        }
	        catch(ClassNotFoundException e)
	        {
	        	stop("Impossible de charger le pilote "+this.configuration.getString("tnr.driverClass"));
	        }
	        
	    }
	    public TNRDatabaseHandler()
	            throws ConfigurationException, ClassNotFoundException, SQLException, InstantiationException,
	                   IllegalAccessException
	        {
	            this(TNRConfiguration.getConfiguration("resources/tnr.properties"));
	        }
	    /**
	    * Creates and returns a new connection.
	    *
	    * Connection properties are loaded from the configuration, and autoCommit is set to false.
	    *
	    * @return a new connection
	    * @throws SQLException if a database access error occurs or the url is null
	    * @throws ClassNotFoundException 
	    * @throws IllegalAccessException 
	    * @throws InstantiationException 
	    */
	   protected Connection getNewConnection() 
			   throws SQLException, InstantiationException, IllegalAccessException, ClassNotFoundException
	   {
	   	/////////////////////////////////
//	   	 Class.forName("com.mysql.cj.jdbc.Driver").newInstance();
	   	 //////////////////////////////////////
	       Properties connectionProperties = new Properties();
	       connectionProperties.put(
	           "user",
	           this.configuration.getString("tnr.username"));
	       connectionProperties.put(
	           "password",
	           this.configuration.getString("tnr.password"));
	       connectionProperties.put(
	           "setLegacyDatetimeCode",
	           this.configuration.getString("tnr.useLegacyDatetimeCode"));
	       connectionProperties.put(
	           "serverTimezone",
	           this.configuration.getString("tnr.serverTimezone"));
	       connectionProperties.put("autocommit", false);

	       Connection c = DriverManager.getConnection(
	           this.configuration.getString("tnr.url"),
	           connectionProperties);
	       c.setAutoCommit(false);

	       return c;
	   }
	   protected static void stop(String msg)
	   {
		   System.err.println(msg);
		   System.exit(99);
	   }
	   public void createCatalog(String catalog, String charset, String collation) throws SQLException
	   {
	       this.logger.info("Creating catalog {}", catalog);
			if(!catalogExists(catalog))
			{
		        this.connection.createStatement().execute(String.format(
		            "CREATE DATABASE `%s` DEFAULT CHARACTER SET %s COLLATE %s",
		            catalog, charset, collation));
		
		        this.connection.commit();
			}
	   }
	   
	   public void dropCatalog(String catalog) throws SQLException
	   {
	       this.logger.info("Dropping database {}", catalog);

	       this.connection.createStatement().executeUpdate(String.format(
	           "DROP DATABASE `%s`",
	           catalog));

	       this.connection.commit();
	   }
	   
	   public boolean catalogExists(String catalog) throws SQLException
	   {
	       boolean found = false;
	       DatabaseMetaData md = this.connection.getMetaData();

	       try (ResultSet rs = md.getCatalogs())
	       {
	           while (rs.next() && !found)
	           {
	               if (rs.getString("TABLE_CAT").equals(catalog))
	               {
	                   found = true;
	               }
	           }
	       }

	       return found;
	   }
	   
	   public boolean tableExists(String catalog, String table) throws SQLException
	   {
	       boolean found = false;
	       DatabaseMetaData md = this.connection.getMetaData();

	       try (ResultSet rs = md.getTables(catalog, null, table, null))
	       {
	           while (rs.next() && !found)
	           {
	               if (rs.getString("TABLE_NAME").equals(table))
	               {
	                   found = true;
	               }
	           }
	       }

	       return found;
	   }
	   
	   /*****************************GD database management ***********************/
	   
	   
	   protected void createGDTables()throws SQLException
	   {
		   this.logger.info("Creating GD tables");
		   
		   this.connection.createStatement().execute("USE gd");
	       
//		   if(!tableExists("gd","questions"))
//	       {
//	    	   this.logger.info("Création de la table questions");
//		       this.connection.createStatement().execute(
//		            "CREATE TABLE `questions` ("
//		            + "	`qid`	VARCHAR(255)	 NOT NULL," //COMMENT='id de la question'
//		            + " `question`	TEXT	NOT NULL," //COMMENT='contenu de la question',
//		            + " PRIMARY KEY(qid) "
//		            + ") ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_bin    COMMENT='table des questions'");
//		        this.connection.commit();
//	    	   
//	       }
		   if(!tableExists("gd","ecologie"))
	       {
	    	   this.logger.info("Création de la table écologie");
	    	   this.connection.createStatement().execute(
	   	            "CREATE TABLE `ecologie` ("
	   	            + "  `id`           VARCHAR(255)  NOT NULL,"
	   	            + "  `reference`    VARCHAR(255),"
	   	            + "  `title`        VARCHAR(255),"
	   	            + "  `createdAt`    DATETIME,"
	   	            + "  `publishedAt`  DATETIME,"
	   	            + "  `updatedAt`    DATETIME,"
	   	            + "  `trashed`     BOOLEAN,"
	   	            + "  `trashedAt`   DATETIME,"
	   	            + "  `authorId`     VARCHAR(255),"
	   	            + "  `authorType`   VARCHAR(255),"
	   	            + "  `authorZipCode`INT(11),"
	   	            + "  `id_q1`           VARCHAR(255),"
	   	            + "  `id_q2`           VARCHAR(255),"
	   	            + "  `id_q3`           VARCHAR(255),"
	   	            + "  `id_q4`           VARCHAR(255),"
	   	            + "  `id_q5`           VARCHAR(255),"
	   	            + "  `id_q6`           VARCHAR(255),"
	   	            + "  `id_q7`           VARCHAR(255),"
	   	            + "  `id_q8`           VARCHAR(255),"
	   	            + "  `id_q9`           VARCHAR(255),"
	   	            + "  `id_q10`           VARCHAR(255),"
	   	            + "  `id_q11`           VARCHAR(255),"
	   	            + "  `id_q12`           VARCHAR(255),"
	   	            + "  `id_q13`           VARCHAR(255),"
	   	            + "  `id_q14`           VARCHAR(255),"
	   	            + "  `id_q15`           VARCHAR(255),"
	   	        	+ "  `id_q16`           VARCHAR(255),"
	   	        	+ "PRIMARY KEY(id)"
	   	            + ") ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_bin    COMMENT 'table qui contient le sous-corpus transition écologique'");
	   	        this.connection.commit();
	       }
	       if(!tableExists("gd","democratie"))
	       {
	    	   this.logger.info("Création de la table démocratie");
	    	   this.connection.createStatement().execute(
	      	            "CREATE TABLE `democratie` ("
	      	            + "  `id`           VARCHAR(255)      PRIMARY KEY	NOT NULL COMMENT 'id de la contibution',"
	      	            + "  `reference`    VARCHAR(255)      NOT NULL COMMENT 'référence',"
	      	            + "  `title`        VARCHAR(255)	COMMENT 'intitulé',"
	      	            + "  `createdAt`    DATETIME	COMMENT 'création',"
	      	            + "  `publishedAt`  DATETIME	COMMENT 'publication',"
	      	            + "  `updatedAt`    DATETIME	COMMENT 'mise à jour',"
	      	            + "  `trashed`     BOOLEAN,"
	     	            + "  `trashedAt`   DATETIME,"
	      	            + "  `authorId`     VARCHAR(255)	COMMENT 'id du contributeur',"
	      	            + "  `authorType`   VARCHAR(255)	COMMENT 'type de contributeur',"
	      	            + "  `authorZipCode`INT(11) 	COMMENT 'code postal',"
	      	            + "  `id_q1`        VARCHAR(255)      COMMENT 'id question',"
	      	            + "  `id_q2`        VARCHAR(255)      COMMENT 'id question',"
	      	            + "  `id_q3`        VARCHAR(255)      COMMENT 'id question',"
	      	            + "  `id_q4`        VARCHAR(255)      COMMENT 'id question',"
	      	            + "  `id_q5`        VARCHAR(255)      COMMENT 'id question',"
	      	            + "  `id_q6`        VARCHAR(255)      COMMENT 'id question',"
	      	            + "  `id_q7`        VARCHAR(255)      COMMENT 'id question',"
	      	            + "  `id_q8`        VARCHAR(255)      COMMENT 'id question',"
	      	            + "  `id_q9`        VARCHAR(255)      COMMENT 'id question',"
	      	            + "  `id_q10`       VARCHAR(255)      COMMENT 'id question',"
	      	            + "  `id_q11`       VARCHAR(255)      COMMENT 'id question',"
	      	            + "  `id_q12`       VARCHAR(255)      COMMENT 'id question',"
	      	            + "  `id_q13`       VARCHAR(255)      COMMENT 'id question',"
	      	            + "  `id_q14`       VARCHAR(255)      COMMENT 'id question',"
	      	            + "  `id_q15`       VARCHAR(255)      COMMENT 'id question',"
	      	        	+ "  `id_q16`       VARCHAR(255)      COMMENT 'id question',"
	      	        	+ "  `id_q17`       VARCHAR(255)      COMMENT 'id question',"
	    	            + "  `id_q18`       VARCHAR(255)      COMMENT 'id question',"
	    	            + "  `id_q19`       VARCHAR(255)      COMMENT 'id question',"
	    	            + "  `id_q20`       VARCHAR(255)      COMMENT 'id question',"
	    	            + "  `id_q21`       VARCHAR(255)      COMMENT 'id question',"
	    	            + "  `id_q22`       VARCHAR(255)      COMMENT 'id question',"
	    	            + "  `id_q23`       VARCHAR(255)      COMMENT 'id question',"
	    	            + "  `id_q24`       VARCHAR(255)      COMMENT 'id question',"
	    	            + "  `id_q25`      	VARCHAR(255)      COMMENT 'id question',"
	    	            + "  `id_q27`       VARCHAR(255)      COMMENT 'id question',"
	    	            + "  `id_q28`       VARCHAR(255)      COMMENT 'id question',"
	    	            + "  `id_q29`       VARCHAR(255)      COMMENT 'id question',"
	    	            + "  `id_q30`       VARCHAR(255)      COMMENT 'id question',"
	    	            + "  `id_q31`       VARCHAR(255)      COMMENT 'id question',"
	    	            + "  `id_q32`       VARCHAR(255)      COMMENT 'id question',"
	    	            + "  `id_q33`       VARCHAR(255)      COMMENT 'id question',"
	    	            + "  `id_q34`       VARCHAR(255)      COMMENT 'id question',"
	    	            + "  `id_q35`       VARCHAR(255)      COMMENT 'id question',"
	    	            + "  `id_q36`       VARCHAR(255)      COMMENT 'id question',"
	    	        	+ "  `id_q37`       VARCHAR(255)      COMMENT 'id question'"
	      	            + ") ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_bin    COMMENT 'table qui contient le sous-corpus démocratie'");
	      	        this.connection.commit();
	       }
	       if(!tableExists("gd","services"))
	       {
	    	   this.logger.info("Création de la table services");
	    	   
	    	   this.connection.createStatement().execute(
	     	            "CREATE TABLE `services` ("
	     	            + "  `id`           VARCHAR(255)      PRIMARY KEY	NOT NULL	COMMENT 'id de la contibution',"
	     	            + "  `reference`    VARCHAR(255)      NOT NULL COMMENT 'référence',"
	     	            + "  `title`        VARCHAR(255)	COMMENT 'intitulé',"
	     	            + "  `createdAt`    DATETIME	COMMENT 'création',"
	     	            + "  `publishedAt`  DATETIME	COMMENT 'publication',"
	     	            + "  `updatedAt`    DATETIME	COMMENT 'mise à jour',"
	     	            + "  `trashed`     BOOLEAN,"
	      	            + "  `trashedAt`   DATETIME,"
	     	            + "  `authorId`     VARCHAR(255)	COMMENT 'id du contributeur',"
	     	            + "  `authorType`   VARCHAR(255)	COMMENT 'type de contributeur',"
	     	            + "  `authorZipCode`INT(11) 	COMMENT 'code postal',"
	     	            + "  `id_q1`        VARCHAR(255)      COMMENT 'id question',"
	     	            + "  `id_q2`        VARCHAR(255)      COMMENT 'id question',"
	     	            + "  `id_q3`        VARCHAR(255)      COMMENT 'id question',"
	     	            + "  `id_q4`        VARCHAR(255)      COMMENT 'id question',"
	     	            + "  `id_q5`        VARCHAR(255)      COMMENT 'id question',"
	     	            + "  `id_q6`        VARCHAR(255)      COMMENT 'id question',"
	     	            + "  `id_q7`        VARCHAR(255)      COMMENT 'id question',"
	     	            + "  `id_q8`        VARCHAR(255)      COMMENT 'id question',"
	     	            + "  `id_q9`        VARCHAR(255)      COMMENT 'id question',"
	     	            + "  `id_q10`       VARCHAR(255)      COMMENT 'id question',"
	     	            + "  `id_q11`       VARCHAR(255)      COMMENT 'id question',"
	     	            + "  `id_q12`       VARCHAR(255)      COMMENT 'id question',"
	     	            + "  `id_q13`       VARCHAR(255)      COMMENT 'id question',"
	     	            + "  `id_q14`       VARCHAR(255)      COMMENT 'id question',"
	     	            + "  `id_q15`       VARCHAR(255)      COMMENT 'id question',"
	     	        	+ "  `id_q16`       VARCHAR(255)      COMMENT 'id question',"
	     	        	+ "  `id_q17`       VARCHAR(255)      COMMENT 'id question',"
		   	            + "  `id_q18`       VARCHAR(255)      COMMENT 'id question',"
		   	            + "  `id_q19`       VARCHAR(255)      COMMENT 'id question',"
		   	            + "  `id_q20`       VARCHAR(255)      COMMENT 'id question',"
		   	            + "  `id_q21`       VARCHAR(255)      COMMENT 'id question',"
		   	            + "  `id_q22`       VARCHAR(255)      COMMENT 'id question',"
		   	            + "  `id_q23`       VARCHAR(255)      COMMENT 'id question',"
		   	            + "  `id_q24`       VARCHAR(255)      COMMENT 'id question',"
		   	            + "  `id_q25`      	VARCHAR(255)      COMMENT 'id question',"
		   	            + "  `id_q27`       VARCHAR(255)      COMMENT 'id question',"
		   	            + "  `id_q28`       VARCHAR(255)      COMMENT 'id question',"
		   	            + "  `id_q29`       VARCHAR(255)      COMMENT 'id question',"
		   	            + "  `id_q30`       VARCHAR(255)      COMMENT 'id question',"
		   	            + "  `id_q31`       VARCHAR(255)      COMMENT 'id question',"
		   	            + "  `id_q32`       VARCHAR(255)      COMMENT 'id question',"
		   	        	+ "  `id_q33`       VARCHAR(255)      COMMENT 'id question'"
	     	            + ") ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_bin    COMMENT 'table qui contient le sous-corpus services'");
	     	        this.connection.commit();	   
	       }
	       if(!tableExists("gd","fiscalite"))
	       {
	    	   this.logger.info("Création de la table fiscalité");
	    	   
	    	   this.connection.createStatement().execute(
	      	            "CREATE TABLE `fiscalite` ("
	      	            + "  `id`           VARCHAR(255)      PRIMARY KEY NOT NULL COMMENT 'id de la contibution',"
	      	            + "  `reference`    VARCHAR(255)      NOT NULL COMMENT 'référence',"
	      	            + "  `title`        VARCHAR(255)	COMMENT 'intitulé',"
	      	            + "  `createdAt`    DATETIME	COMMENT 'création',"
	      	            + "  `publishedAt`  DATETIME	COMMENT 'publication',"
	      	            + "  `updatedAt`    DATETIME	COMMENT 'mise à jour',"
	      	            + "  `trashed`     BOOLEAN,"
	     	            + "  `trashedAt`   DATETIME,"
	      	            + "  `authorId`     VARCHAR(255)	COMMENT 'id du contributeur',"
	      	            + "  `authorType`   VARCHAR(255)	COMMENT 'type de contributeur',"
	      	            + "  `authorZipCode`INT(11) 	COMMENT 'code postal',"
	      	            + "  `id_q1`           VARCHAR(255)      COMMENT 'id question',"
	      	            + "  `id_q2`           VARCHAR(255)      COMMENT 'id question',"
	      	            + "  `id_q3`           VARCHAR(255)      COMMENT 'id question',"
	      	            + "  `id_q4`           VARCHAR(255)      COMMENT 'id question',"
	      	            + "  `id_q5`           VARCHAR(255)      COMMENT 'id question',"
	      	            + "  `id_q6`           VARCHAR(255)      COMMENT 'id question',"
	      	            + "  `id_q7`           VARCHAR(255)      COMMENT 'id question',"
	      	            + "  `id_q8`           VARCHAR(255)      COMMENT 'id question'"
	      	            + ") ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_bin    COMMENT 'table qui contient le sous-corpus fiscalité'");
	      	        this.connection.commit();
	    	   
	       }
	       if(!tableExists("gd","evenements"))
	       {
	    	   this.logger.info("Création de la table événements");
	    	   this.connection.createStatement().execute(
	     	            "CREATE TABLE `evenements` ("
	     	            + "  `id`           VARCHAR(255)      PRIMARY KEY	NOT NULL COMMENT 'id de la contibution',"
	     	            + "  `title`        VARCHAR(255)	COMMENT 'intitulé',"
	     	            + "  `createdAt`    DATETIME	COMMENT 'création',"
	     	            + "  `updateddAt`  DATETIME	COMMENT 'publication',"
	     	            + "  `startAt`    DATETIME	COMMENT 'début',"
	     	            + "  `endAt`    DATETIME	COMMENT 'fin',"
	     	            + "  `enabled`     BOOLEAN,"
	     	            + "  `lat`     DOUBLE,"
	     	            + "  `lng`     DOUBLE,"
	     	            + "  `fullAddress`     TEXT	COMMENT 'adresse',"
	     	            + "  `link`     VARCHAR(255)	COMMENT 'lien',"
	     	            + "  `url`     TEXT	COMMENT 'page web',"
	     	            + "  `body`     TEXT	COMMENT 'descriptif',"
	     	            + "  `authorId`   VARCHAR(255)	COMMENT 'identifiant',"
	     	            + "  `authorType`   VARCHAR(255)	COMMENT 'type de contributeur',"
	     	            + "  `authorZipCode`INT(11) COMMENT 'code postal'"
	     	            + ") ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_bin    COMMENT 'table qui contient le sous-corpus événements'");
	     	        this.connection.commit();
	       }
	       
	   }
	   /****************************end GD database management *****************/
	   
	   protected void createTables() throws SQLException
	   {
	       this.logger.info("Creating tables");
	       this.connection.createStatement().execute("USE tnr");
	       if(!tableExists("tnr","termes"))
	       {
		        this.logger.info("Création de la table des termes");
		        this.connection.createStatement().execute(
		            "CREATE TABLE `termes` ("
		            + "  `tid`           INT(11)      PRIMARY KEY AUTO_INCREMENT COMMENT 'id unique',"
		            + "  `tname`         TEXT 	NOT NULL               COMMENT 'chaîne de caractères qui correspond à un terme'"
		            + ") ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_bin    COMMENT 'table qui contient les id et le chaînes de caractères qui correspondent aux termes'");
		        this.connection.commit();
	       }
	       if(!tableExists("tnr","relations"))
	       {
		        this.logger.info("Création de la table des relations");
		        this.connection.createStatement().execute(
		            "CREATE TABLE `relations` ("
		            + "  `rid`       INT(11)      PRIMARY KEY AUTO_INCREMENT  COMMENT 'id de la relation',"
		            + "  `source`       INT(11)      NOT NULL  COMMENT 'id du terme source de la relation',"
		            + "  `type_id`     INT(11)      NOT NULL                    COMMENT 'id du type de la relation',"
		            + "  `poids`     INT(11)      NOT NULL                    COMMENT 'poids de la relation (poids fréquentiel ou associatif)',"
		            + "  `origine`     INT(11)      NOT NULL                    COMMENT 'ressource ou processus qui a fourni la relation',"
		            + "  `cible`   INT(11)      NOT NULL                    COMMENT 'id du terme cible de la relation'"
//		            + " FOREIGN KEY fk_type(type_id) "
//		            + "REFERENCES types(type_id) "
//		            + "ON DELETE SET NULL "
//		            + "ON UPDATE CASCADE "
		            + ") "
		            + "ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_bin COMMENT 'table qui contient le relations de la base : leur terme source, terme cible, type, poids et origine'");
		        this.connection.commit();
	       }
	       if(!tableExists("tnr","types"))
	       {
		        this.logger.info("Creating table 'types'...");
		        this.connection.createStatement().execute(
		            "CREATE TABLE `types` ("
		            + "  `type_id`   INT(11)     PRIMARY KEY AUTO_INCREMENT       COMMENT 'id du type de la relation', "
		            + "  `type`	VARCHAR(64) 	NOT NULL               COMMENT 'nom du type',"
		            + "  `contrainte`	VARCHAR(64) 	NOT NULL               COMMENT 'type de contrainte'"
		            + ") ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_bin COMMENT 'types des relations'");
		        this.connection.commit();
	       }
	       this.connection.commit();
	   }
	       
	   public void dropTable(String catalog, String table) throws SQLException
	   {
	       String qualifiedName = String.format("`%s`.`%s`", catalog, table);

	       this.logger.info("Dropping table {}", qualifiedName);

	       this.connection.createStatement().execute(String.format(
	           "DROP TABLE %s",
	           qualifiedName));
	   }
	       
	   static public void main(String[] args)throws ConfigurationException, ClassNotFoundException, SQLException, InstantiationException,
	   IllegalAccessException
	{
		Configuration c = TNRConfiguration.getConfiguration("resources/tnr.properties");
		TNRDatabaseHandler h = new TNRDatabaseHandler(c);
//		h.createCatalog("tnr", "UTF8", "utf8_bin");
//		h.createTables();
		h.logger.info("DONE");
	}


}
