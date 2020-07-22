package database;

import org.apache.commons.configuration2.Configuration;
import org.apache.commons.configuration2.builder.fluent.Configurations;
import org.apache.commons.configuration2.ex.ConfigurationException;

public class TNRConfiguration 
{
	/**
     * The default path for the properties configuration file.
     */
	//"./resources/tnr.properties"
    static private final String CONFIG_PATH = "resources/tnr.properties";
    /**
     * The configuration.
     */
    static private Configuration configuration;
    
    static public Configuration getConfiguration() throws ConfigurationException
    {
        return TNRConfiguration.getConfiguration(TNRConfiguration.CONFIG_PATH);
    }
    static public Configuration getConfiguration(String path) throws ConfigurationException
    {
        if (path == null)
        {
            path = TNRConfiguration.CONFIG_PATH;
        }

        if (TNRConfiguration.configuration == null)
        {
           Configurations configurations = new Configurations();
           TNRConfiguration.configuration = configurations.properties(path);
        }

        return TNRConfiguration.configuration;
    }
    
    public static void main(String[] args) throws ConfigurationException
    {
    	
    	Configuration c=TNRConfiguration.getConfiguration("resources/tnr.properties");
    	System.out.println(c.containsKey("tnr.serverTimezone"));
    	System.out.println(c.getClass().getProtectionDomain().getCodeSource().getLocation().getPath());
    	}

}
