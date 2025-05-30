package gov.nist.toolkit.simcommon.server;

import java.util.logging.Level;
import java.util.logging.Logger;

import java.io.File;
import java.io.FileInputStream;
import java.util.Properties;

public class ExtendedPropertyManager {

	static Logger logger = Logger.getLogger(ExtendedPropertyManager.class.getName());
	static Properties properties = null;
	
	
	static public void load(File warHome) {
		if (properties != null) 
			return;

		File propFile = new File(warHome + File.separator + "WEB-INF" + File.separator + "extended.properties");
		properties = new Properties();
		try {
			properties.load(new FileInputStream(propFile));
		} catch (Exception e) {
//			logger.info("Cannot load extended.properties");
		}
	}
	
	static public String getProperty(String propName)  {
		if (properties == null) {
			RuntimeException e = new RuntimeException("Extended properties not loaded");
			logger.log(Level.SEVERE, "Extended Properties queried before they are loaded", e);
			throw e;
		}
		return properties.getProperty(propName);
	}
	
	static public String getProperty(@SuppressWarnings("rawtypes") Class clas, String propShortName)  {
		String val = getProperty(clas.getName() + "." + propShortName);
		return val;
	}
}
