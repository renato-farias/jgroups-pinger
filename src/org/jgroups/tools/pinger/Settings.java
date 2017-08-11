package org.jgroups.tools.pinger;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

public class Settings {
	
	private static Properties properties = new Properties();
	private static boolean propertiesOK = false;

	private static final String JGROUPS_PROTO = "jGroupsProtocolFile";
	private static final String JGROUPS_CHANNEL = "jGroupsChannelName";
	
	private static final String JETTY_ADDR = "jettyAddr";
	private static final String JETTY_PORT = "jettyPort";
	
	private static final String COMETD_INIT = "cometdInitParam.";

	private static final MainLogger _logger = new MainLogger(Settings.class.getName());
	
	static {
		String[] paths = new String[] { Main.getParamConfig(), Main.getParamEnv() + ".properties", "config.properties" };
		
		for (String string : paths) {
			propertiesOK = tryLoadProperties(string);
			if (propertiesOK) {
							
				_logger.info("Properties file loaded: " + string);
		
				break;
			}
		}
		if (propertiesOK == false) {
			_logger.error("No properties file found.");
		}
	}
	
	private static boolean tryLoadProperties(String path) {
		try {
			//load a properties file
			properties.load(new FileInputStream(path));			
			return true;
		} catch (FileNotFoundException e) {
			_logger.warn("The " + path + " file was not found!");
		} catch (IOException e) {
			_logger.warn("The " + path + " file was not accessible! Check if it have read permissions.");
		}
		
		return false;
	}
	
	private static String getProperty(String property) {
		return properties.getProperty(property);
	}
	
	private static String getProperty(String property, String defaultVal) {
		return properties.getProperty(property, defaultVal);
	}

	public static synchronized String getJgroupsProto() {
		return getProperty(JGROUPS_PROTO);
	}

	public static synchronized String getJgroupsChannel() {
		return getProperty(JGROUPS_CHANNEL);
	}
	
	public static synchronized String getJettyAddr() {
		return getProperty(JETTY_ADDR, "localhost");
	}
	
	public static synchronized int getJettyPort() {
		return Integer.parseInt(getProperty(JETTY_PORT, "8080"));
	}
	
	public static String getBroadCasterClassName() {
		return "org.jgroups.tools.pinger.modules.JGroupsBroadcaster";
	}
	
	public static synchronized Map<String,String> getCometdInitParams() {
		Map<String, String> map = new HashMap<String, String>();

		Enumeration<Object> keys = properties.keys();
		while (keys.hasMoreElements()) {
			String theKey = (String) keys.nextElement();
			if (theKey.startsWith(COMETD_INIT)) {
				map.put(theKey.replaceFirst(COMETD_INIT, ""), getProperty(theKey));
				_logger.debug("added to map {" + theKey.replaceFirst(COMETD_INIT, "") + ","+ getProperty(theKey) + "}");
			}
		}
				
		return map;
	}
	

}