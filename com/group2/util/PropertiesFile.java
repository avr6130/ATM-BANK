package com.group2.util;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class PropertiesFile {

	public static final String DEBUG_MODE = "DebugMode";
	public static final String SESSION_TIMEOUT = "SessionTimeout";
	public static final String LOGIN_ATTEMPTS = "LoginAttempts";
	public static final String LOCKOUT_DURATION = "LockoutDuration";

	public static final String CIPHER_TRANSFORMATION = "cipher.transformation";

	public static final String CA_ALGORITHM_NAME = "ca.algorithm.name";
	public static final String CA_ALGORITHM_KEYSIZE = "ca.algorithm.keysize";

	public static final String BANK_ALGORITHM_NAME = "bank.algorithm.name";
	public static final String BANK_ALGORITHM_KEYSIZE = "bank.algorithm.keysize";

	public static final String SESSION_ALGORITHM_KEYSIZE = "session.algorithm.keysize";
	public static final String SESSION_ALGORITHM_NAME = "session.algorithm.name";

	public static final String VM_PROP_BUILD_EXT_DATA = "buildExtData";

	public static final String PORT_ATM = "port.atm";
	public static final String PORT_BANK = "port.bank";

	private static Properties props;

	private static final String PROP_FILE_NAME_PLAINTEXT = "Group2.properties";
	private static final String PROP_FILE_NAME_SERIALIZED = "Properties.ser";

	static {
		// If the system is configured to build external data
		if (Boolean.getBoolean(VM_PROP_BUILD_EXT_DATA)) {
			props = new Properties();
			InputStream is = null;
			try {
				// Load the plain-text properties file
				is = PropertiesFile.class.getResourceAsStream("/" + PROP_FILE_NAME_PLAINTEXT);
				props.load(is);
				is.close();
			} catch (IOException e) {
				if (PropertiesFile.isDebugMode()) {
					e.printStackTrace();
				}
				System.exit(-1);
			}
			try {
				// Save the properties to Disk as a serialized object
				Disk.save(props, PROP_FILE_NAME_SERIALIZED);
			} catch (IOException e) {
				if (PropertiesFile.isDebugMode()) {
					e.printStackTrace();
				}
				System.exit(-1);
			}
		} 
		// External data should already be available
		else {
			try {
				props = (Properties) Disk.load(PROP_FILE_NAME_SERIALIZED);
			} catch (IOException e) {
				if (PropertiesFile.isDebugMode()) {
					e.printStackTrace();
				}
				System.exit(-1);
			}
		}
	}

	/**
	 * Private constructor to avoid instantiation.
	 */
	private PropertiesFile(){};


	public static String getProperty(String key) {
		return props.getProperty(key);
	}

	public static String getProperty(String key, String defaultValue) {
		return props.getProperty(key, defaultValue);
	}

	/**
	 * Returns whether the system is in debug mode.  Use to inhibit debug comments
	 * and errors that could otherwise provide critical information to exploit the system.
	 * @return true if debug mode is enabled; false otherwise
	 */
	public static boolean isDebugMode() {
		return Boolean.parseBoolean(props.getProperty(DEBUG_MODE, "false"));
	}
}
