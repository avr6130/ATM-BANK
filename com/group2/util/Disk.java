package com.group2.util;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;

public class Disk {

	public static Object load(String objFile) throws IOException {
		ObjectInputStream ois = new ObjectInputStream(Disk.class.getResourceAsStream("/" + objFile));
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


	public static void save(Serializable obj, String objFile) 
			throws IOException
			{
		FileOutputStream fos = new FileOutputStream(objFile);
		ObjectOutputStream oos = new ObjectOutputStream(fos);
		oos.writeObject(obj);
		oos.close();
		fos.close();
			}

}
