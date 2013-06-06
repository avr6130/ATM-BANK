package util;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
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
	
	private static Properties props;
	
	private static final String PROP_FILE_NAME_PLAINTEXT = "Group2.properties";
	private static final String PROP_FILE_NAME_SERIALIZED = "Properties.ser";
	
	static {
		if (Boolean.getBoolean(VM_PROP_BUILD_EXT_DATA)) {
			props = new Properties();
			File propFile = new File(PROP_FILE_NAME_PLAINTEXT);
			FileInputStream fis = null;
			try {
				fis = new FileInputStream(propFile);
				props.load(fis);
			} catch (IOException e) {
				e.printStackTrace();
			} finally {
				if (fis != null) {
					try {
						fis.close();
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			}
			try {
				Disk.save(props, PROP_FILE_NAME_SERIALIZED);
			} catch (IOException e) {
				e.printStackTrace();
			}
		} else {
			try {
				props = (Properties) Disk.load(PROP_FILE_NAME_SERIALIZED);
			} catch (IOException e) {
				e.printStackTrace();
				System.exit(-1);
			}
		}
	}
	
	private PropertiesFile(){};
	
	public static Properties getProperties() {
		return props;
	}
	
	public static String getProperty(String key) {
		return props.getProperty(key);
	}
	
	public static String getProperty(String key, String defaultValue) {
		return props.getProperty(key, defaultValue);
	}
	
	public static boolean isDebugMode() {
		return Boolean.parseBoolean(props.getProperty(DEBUG_MODE, "false"));
	}
}
