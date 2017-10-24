package com.matchandtrade.doc.config;

import com.matchandtrade.config.AppConfigurationProperties;

public class PropertiesLoader {
	
	public enum Key {
		CONFIG_FILE(AppConfigurationProperties.Keys.CONFIG_FILE.getKey(), "src/config/matchandtrade.properties"),
		DESTINATION_FOLDER("matchandtrade.doc.destination.folder","target/doc-maker"),
		STOP_WEBSERVER("matchandtrade.doc.stop.webserver","true");

		private final String defaultValue;
		private final String key;

		Key(final String key, final String defaultValue) {
	        this.key = key;
	        this.defaultValue = defaultValue;
	    }
		public String getKey() {
			return key;
		}
		public String getDefaultValue() {
			return defaultValue;
		}
		public static Key getKey(String s) {
			Key[] k = Key.values();
			for (int i = 0; i < k.length; i++) {
				Key key = k[i];
				if (key.getKey().equals(s)) {
					return key;
				}
			}
			return null;
		}
		@Override
		public String toString() {
			return key + "=" + defaultValue;
		}
	}
	
	public static void loadConfigurationProperties() {
		for (int i = 0; i < Key.values().length; i++) {
			if (System.getProperty(Key.values()[i].getKey()) == null) {
				System.setProperty(Key.values()[i].getKey(), Key.values()[i].getDefaultValue());
			}
		}
	}
	
	public static String serverUrl() {
		return "http://localhost:" + System.getProperty("server.port");
	}
	
	public static String getProperty(Key k) {
		return System.getProperty(k.getKey());
	}
	
	public static void setProperty(Key k, String value) {
		System.setProperty(k.getKey(), value);
	}
	
	public static boolean stopWebService() {
		String p = System.getProperty(Key.STOP_WEBSERVER.getKey());
		return Boolean.valueOf(p);
	}
	public static String configFile() {
		return System.getProperty(Key.CONFIG_FILE.getKey());
	}

	public static String destinationFolder() {
		return System.getProperty(Key.DESTINATION_FOLDER.getKey());
	}
	
}