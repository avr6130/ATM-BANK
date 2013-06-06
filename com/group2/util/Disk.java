package com.group2.util;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;

public class Disk {

	public static Object load(String objFile) throws IOException {
		InputStream is = Disk.class.getResourceAsStream("/" + objFile);
		if (is == null) {
			if (PropertiesFile.isDebugMode()) {
				System.err.println("Resource does not exist.");
			}
			return null;
		}

		ObjectInputStream ois = new ObjectInputStream(is);
		Object obj = null;
		try {
			obj = ois.readObject();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
			System.exit(1);
		} catch (IOException e) {
			e.printStackTrace();
			System.exit(1);
		}
		ois.close();
		return obj;
	}

	public static Object loadExtFile(String objFile) throws IOException {
		FileInputStream fis = new FileInputStream(objFile);
		ObjectInputStream ois = new ObjectInputStream(fis);
		Object obj = null;
		try {
			obj = ois.readObject();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
			System.exit(1);
		} catch (IOException e) {
			e.printStackTrace();
			System.exit(1);
		}
		ois.close();
		fis.close();
		return obj;
	}


	public static void save(Serializable obj, String objFile) 
			throws IOException {
		FileOutputStream fos = new FileOutputStream(objFile);
		ObjectOutputStream oos = new ObjectOutputStream(fos);
		oos.writeObject(obj);
		oos.close();
		fos.close();
	}

}
