package com.ca.logs.profiler.util;

import java.io.File;
import java.io.FileInputStream;
import java.text.MessageFormat;
import java.util.Properties;

public class PropertiesUtil {
	
	private static Properties configProperties = null;
	
	static{
		try {
			loadProperties();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	private static void loadProperties() throws Exception {
		configProperties = new Properties();
		if(System.getProperty("config.properties.url")!=null){
			configProperties.load(new FileInputStream(new File(System.getProperty("config.properties.url"))));
		}else{
			configProperties.load(Thread.currentThread().getContextClassLoader().getResourceAsStream("config.properties"));
		}
	}
	
	public static String getProperty(String propName,String...strings ) {
		String propertyBaseValue = configProperties.getProperty(propName);
		MessageFormat format = new MessageFormat(propertyBaseValue);
		return format.format(strings);
	}
}
