package view;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.commons.configuration2.ex.ConfigurationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import data.TNRDataHandler;

public class OntoLexView 
{
	final Logger logger = LoggerFactory.getLogger(OntoLexView.class);
	public TNRDataHandler dh;
	public String nom;
	public OntoLexView(TNRDataHandler dh) throws ClassNotFoundException, InstantiationException, IllegalAccessException, ConfigurationException, SQLException 
	{
		this.logger.info("Started");
		this.dh=dh;
	}
	
	public static final String prolog="@prefix lemon:http://www.w3.org/ns/lemon/all .\n"
			+"@prefix skos: <http://www.w3.org/2004/02/skos/core#> .\n";

	public StringBuilder sb;
	public Map<String,String> map=new HashMap<String,String>();
	public String lexicalEntry(String nom) throws SQLException
	{
		String le=null;
		this.nom=nom;
		map=dh.EntryData(nom, dh.psGetRelations, dh.psShowRelations);
		
		for(Entry<String,String>e:map.entrySet())
		{
			
		}
		
		return le;
		
	}
	
	public void run()
	{
		System.out.println("Printing for ");
		
	}
	
	
}
