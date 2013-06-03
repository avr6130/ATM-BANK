package crypto;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.spec.RSAPrivateKeySpec;
import java.security.spec.RSAPublicKeySpec;

import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;

public class Keygen {

	/**
	 * Used to test the generator
	 * @param args
	 */
	public static void main(String[] args) {
		boolean done = false;
		Object worked = null;
		
		BufferedReader rdr = new BufferedReader(new InputStreamReader(System.in));
		
		while (!done) {
			System.out.print("Enter Algorithm (AES/RSA) or 'Exit':");
			try {
				String algorithmName = rdr.readLine();
				
				//check for exit
				if ("Exit".equalsIgnoreCase(algorithmName)) {
					System.out.println("Exiting program.  Goodbye...");
					System.exit(0);
				}
				
				System.out.print("Enter key size:");
				Integer keySize = Integer.decode(rdr.readLine());

				//check for valid input name
				if (!algorithmName.equals("AES") &&
						!algorithmName.equals("RSA")) {
					//bad input so continue with the top of the loop
					System.out.println("Bad input! (must be algorithm name in caps or 'exit') TRY AGAIN...");
					continue;
				}
				
				worked = generateKey(algorithmName, keySize);
				
				done = true;

			} catch (Exception e) {
				System.out.println("Bad input! (" + e.toString() + ") TRY AGAIN...");
			}
		}
		
		if (worked == null) {
			System.out.println("Keygen failed! Exiting...");
		}
		else {
			System.out.println("Keygen successful!  Exiting...");
		}
	}

	/**
	 * Will generate a key for the specific algorithm.  Key Size can be 2048 or 4096 for RSA or 256 or 128 
	 * for AES.
	 * @param algorithmName  Name of the Algorithm (RSA or AES)
	 * @param keySize  Size of the key to generate (2048, 4096, 128 or 256 depending on algorithm)
	 * @return  SecretKey if algorithm is AES, RSAKeyPairInfo if RSA and null if anything else or an error occurs
	 */
	public static Object generateKey(String algorithmName, int keySize) {
		
		if (algorithmName.equals("RSA")) {
			if (keySize == 2048 || keySize == 4096) {
				return processRSA(keySize);
			}
		}
		
		if (algorithmName.equals("AES")) {
			if (keySize == 128 || keySize == 256) {
				return processAES(keySize);
			}
		}
		
		return null;
	}
	
	/**
	 * returns an AES SecretKey or null if an error occurs
	 * @param keySize
	 * @return
	 */
	private static SecretKey processAES(int keySize) {
		KeyGenerator keyGen;
		try {
			keyGen = KeyGenerator.getInstance("AES");
			
			keyGen.init(keySize);
			
			return keyGen.generateKey();
						
		} catch (Exception e) {
			if (System.getProperty("DEBUG_MODE") != null) {
				e.printStackTrace();
			}
			return null;
		}
	}

	/**
	 * returns an RSA key pair in an RSAKeyPairInfo or null if an error occurs
	 * @param keySize
	 * @return
	 */
	private static RSAKeyPairInfo processRSA(int keySize) {
		try {
			//generate the public/private key pair
			KeyPairGenerator kpg = KeyPairGenerator.getInstance("RSA");
			kpg.initialize(keySize);
			KeyPair kp = kpg.genKeyPair();
			
			//now generate the public/private KeySpecs to write to the key file
			KeyFactory fact = KeyFactory.getInstance("RSA");
			RSAPublicKeySpec pub = fact.getKeySpec(kp.getPublic(),RSAPublicKeySpec.class);
			RSAPrivateKeySpec priv = fact.getKeySpec(kp.getPrivate(),RSAPrivateKeySpec.class);
			
			return new RSAKeyPairInfo(pub.getModulus(),pub.getPublicExponent(), priv.getModulus(),priv.getPrivateExponent());
			
		} catch (Exception e) {
			if (System.getProperty("DEBUG_MODE") != null) {
				e.printStackTrace();
			}
			return null;
		}
	}
}
