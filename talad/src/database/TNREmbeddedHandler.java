package database;

import java.sql.Connection;
import java.sql.DriverManager;
//import java.sql.ResultSet;
import java.sql.SQLException;
//import java.sql.Statement;


public class TNREmbeddedHandler 
{
	/**
	 * driver information
	 */
	  static final String JDBC_DRIVER = "org.h2.Driver";   
//	  static final String DB_URL = "jdbc:h2:~/tnr;DB_CLOSE_ON_EXIT=FALSE";  
	  public static final String DB_URL = "jdbc:h2:./tnr";  
	
	  /**
	 * credentials
	 */
	public static final String USER="root";
	public static final String PASS="praxi";

	public Connection connection;
	
	public TNREmbeddedHandler() throws ClassNotFoundException, SQLException, InstantiationException, IllegalAccessException 
	{
		Class.forName(JDBC_DRIVER).newInstance(); 
		
//		System.out.println("Connecting to database..."); 
		this.connection=DriverManager.getConnection(DB_URL,USER,PASS); 
	}
	
	public static void main(String[] args) throws SQLException, ClassNotFoundException, InstantiationException, IllegalAccessException
	{
		/**
		 * initialization
		 */
		TNREmbeddedHandler eh=new TNREmbeddedHandler();
////		String sql="CREATE TABLE relations ("
//				+ "rid int(11) NOT NULL, "
//				+ "source int(11) NOT NULL, "
//				+ "type_id int(11) NOT NULL, "
//				+ "poids int(11) NOT NULL, "
//				+ "origine int(11) NOT NULL, "
//				+ "cible int(11) NOT NULL)";
		
//		+ "rid int(11) NOT NULL, "
//		+ "source int(11) NOT NULL, "
//		+ "type_id int(11) NOT NULL, "
//		+ "poids int(11) NOT NULL, "
//		+ "origine int(11) NOT NULL, "
//		+ "cible int(11) NOT NULL";
		
		String r_index="CREATE INDEX rid ON relations(rid)";
		String t_index="CREATE INDEX tid ON termes(tid)";
	
//		eh.connection.createStatement().executeUpdate(sql);
		eh.connection.createStatement().executeUpdate(r_index);
		eh.connection.createStatement().executeUpdate(t_index);
		eh.connection.commit();
		eh.connection.close();
		System.out.println("Created table in the tnr database..."); 
		
	}
}
