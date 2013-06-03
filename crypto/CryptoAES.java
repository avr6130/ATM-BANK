package crypto;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.math.BigInteger;
import java.security.InvalidKeyException;
import java.security.Key;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.spec.RSAPublicKeySpec;

import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SealedObject;
import javax.crypto.spec.IvParameterSpec;

import messaging.Payload;
import messaging.SessionResponse;

public class CryptoAES {

	private static SecureRandom rng;

	static {
		try {
			rng = SecureRandom.getInstance("SHA1PRNG");
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
			rng = new SecureRandom();
		}
	}
	
	/**
	 * Used to test the generator
	 * @param args
	 */
	public static void main(String[] args) {
		Key myKey = null;
		
		BufferedReader rdr = new BufferedReader(new InputStreamReader(System.in));
		
		try {
			String algorithmName = "AES";
			
			System.out.print("Enter key size:");
			Integer keySize = Integer.decode(rdr.readLine());

			myKey = (Key) Keygen.generateKey(algorithmName, keySize);
			
			SessionResponse msg = new SessionResponse(200, true);
			
			SealedObject so = CryptoAES.encrypt(myKey, msg);
			
			ByteArrayOutputStream baos = new ByteArrayOutputStream(1000);
			ObjectOutputStream oos = new ObjectOutputStream(baos);
			oos.writeObject(so);
			
			byte[] ba = baos.toByteArray();
			
			ObjectInputStream ois = new ObjectInputStream(new ByteArrayInputStream(ba));
			Object obj = ois.readObject();
			SealedObject newso = (SealedObject) obj;
			
			Payload decryptedMsg = CryptoAES.decrypt(myKey, newso);
			
			System.out.println("Done");

		} catch (Exception e) {
			System.out.println("Bad input! (" + e.toString() + ") TRY AGAIN...");
		}
		
		if (myKey == null) {
			System.out.println("Keygen failed! Exiting...");
		}
		else {
			System.out.println("Keygen successful!  Exiting...");
		}
	}


	/**
	 * Takes the payload object, Serializes using AES into a SealedObject which
	 * it returns.  Returns null if any error.
	 * 
	 * @param encKey
	 * @param msg
	 * @return
	 */
	public static SealedObject encrypt(Key encKey, Payload msg) {
		SealedObject so = null;
		
		try {
			byte[] ivBytes = new byte[16];
			rng.nextBytes(ivBytes);
			IvParameterSpec iv = new IvParameterSpec(ivBytes);

			Cipher encCipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
			encCipher.init(Cipher.ENCRYPT_MODE,encKey,iv);
			so = new SealedObject(msg, encCipher);
		} catch (Exception e) {
			if (System.getProperty("DEBUG_MODE") != null) {
				e.printStackTrace();
			}
			return null;
		}
		
		return so;
	}

	/**
	 * Takes the SealedObject, extracts the encrypted object,
	 * verifies that the unencrypted object is a Payload and
	 * returns the Payload.  If the object is not a Payload 
	 * or anything goes wrong, it returns null.
	 * 
	 * @param encKey
	 * @param cipherBytes
	 * @return
	 */
	public static Payload decrypt(Key encKey, SealedObject so) {

		try {
			Object obj = so.getObject(encKey);
			
			if ( !(obj instanceof Payload)) {
				if (System.getProperty("DEBUG_MODE") != null) {
					(new Exception("Decrypted Object not Payload (" + (obj == null?"<null)>" : obj.getClass().getName() + ")"))).printStackTrace();
				}
				return null;
			}
			
			return (Payload) obj;
			
		} catch (Exception e) {
			if (System.getProperty("DEBUG_MODE") != null) {
				e.printStackTrace();
			}
			return null;
		}
	}

}
