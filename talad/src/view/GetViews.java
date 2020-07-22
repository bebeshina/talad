package view;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.PrintStream;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.apache.commons.configuration2.ex.ConfigurationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import data.TNRDataHandler;

public class GetViews
{

	public GetViews() throws ConfigurationException, ClassNotFoundException, SQLException, InstantiationException,IllegalAccessException 
	{
		
	}
	
	protected OntoLexView ov = new OntoLexView(new TNRDataHandler());
//	protected SKOSView sv = new SKOSView(new TNRDataHandler());
	
	final Logger logger = LoggerFactory.getLogger(GetViews.class);
	
	protected PrintStream stream;
	protected FileOutputStream file;
	
	public void getViews()
	{
		/**
		 * récupération de la date du jour
		 */
		
		SimpleDateFormat df = new SimpleDateFormat("yyyyMMdd_HHmmss");  
		Date date = new Date(System.currentTimeMillis());
		String d=df.format(date);
		
		
		logger.info("écriture fichier {}","views/skos_output_"+d+".ttl");
		
		this.ov.run();
		
//		try 
//		{
//			file = new FileOutputStream("./views/skos_output_"+d+".ttl");
//			stream = new PrintStream(file);
//			System.setOut(stream);
//		} 
//		catch (FileNotFoundException e1) 
//		{
//			// TODO Auto-generated catch block
//			e1.printStackTrace();
//		}  
//		
//		logger.info("écriture fichier {}","views/lemon_output_"+d+".ttl");
//		try 
//		{
//			file = new FileOutputStream("./views/lemon_output_"+d+".ttl");
//			stream = new PrintStream(file);
//			System.setOut(stream);
//		} 
//		catch (FileNotFoundException e1) 
//		{
//			// TODO Auto-generated catch block
//			e1.printStackTrace();
//		}  
	}
	
	public static void main (String [] args) throws ClassNotFoundException, InstantiationException, IllegalAccessException, ConfigurationException, SQLException
	{
		GetViews gw=new GetViews();
		gw.getViews();
	}
}
