package authority;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.math.BigInteger;
import java.security.KeyFactory;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.Signature;
import java.security.SignedObject;
import java.security.spec.RSAPrivateKeySpec;
import java.security.spec.RSAPublicKeySpec;
import java.util.Properties;

import crypto.Certificate;
import crypto.Keygen;
import crypto.RSAKeyInfo;
import crypto.RSAKeyPairInfo;

public class CA {

	private static final BufferedReader rdr = new BufferedReader(new InputStreamReader(System.in));


	private CA() {
		// TODO Auto-generated constructor stub
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		
		System.out.println("0 => generate CA Keys");
		System.out.println("1 => Generate Bank Certificate and Private Key");
		System.out.print("Choose Operation (0,1): ");
		try {
			int choice = Integer.parseInt(rdr.readLine());
			
			switch (choice) {
			case 0:
				
				CA.generateCAKeyPair();
				
				break;

			default:
				
				CA.createCertificate();
				
				break;
			}
		} catch (Exception e) {
			e.printStackTrace();
			System.exit(-1);
		}

	}
	
	private static void createCertificate() {
		System.out.println();
		System.out.print("Enter Bank Name (hit <cr> for default): ");
		try {
			String bankName = rdr.readLine();
			if (bankName.trim().equals("")) {
				bankName = G2Constants.BANK_NAME;
			}
			
			//generate a key pair for the bank
			int keySize = getBankKeyPairSize();
			Object obj = Keygen.generateKey("RSA", keySize);
			if (obj == null) {
				System.out.println("Bank key pair Keygen returned null");
				System.exit(-1);
			}
			
			if (!(obj instanceof RSAKeyPairInfo)) {
				System.out.println("Bank key pair Keygen did not return a RSAKeyPairInfo (" + 
						obj.getClass().getSimpleName() + ")");
				System.exit(-1);
			}
			
			RSAKeyPairInfo bankKeyPairInfo = (RSAKeyPairInfo) obj;
			
			//write the Bank private key to a serialized file
			BigInteger modPriv = bankKeyPairInfo.getPrivModulus();
			BigInteger expPriv = bankKeyPairInfo.getPrivExponent();
			RSAPrivateKeySpec bankPrivKeySpec = new RSAPrivateKeySpec(modPriv, expPriv);
			try {
				KeyFactory fact = KeyFactory.getInstance("RSA");
				PrivateKey bankPrivateKey = fact.generatePrivate(bankPrivKeySpec);
				String bankPrivateKeyFileName = bankName.replace(' ', '_') + G2Constants.PRIVATE_KEY_SER_FILE_SUFFIX;
				CA.writeKey(bankPrivateKeyFileName, bankPrivateKey);

				System.out.println("CA Key Generation Complete");
			} catch (Exception e) {
				e.printStackTrace();
				System.exit(-1);
			}
			
			//next reconstitute the Bank's Public Key
			BigInteger modPub = bankKeyPairInfo.getPubModulus();
			BigInteger expPub = bankKeyPairInfo.getPubExponent();
			RSAPublicKeySpec bankPubKeySpec = new RSAPublicKeySpec(modPub, expPub);
			KeyFactory fact = KeyFactory.getInstance("RSA");
			PublicKey bankPublicKey = fact.generatePublic(bankPubKeySpec);
			
			//we need the CA's keys so read in the CA key pair file
			Serializable sobj = readKey(G2Constants.CA_NAME.replace(' ', '_') + G2Constants.CA_KEY_PAIR_INFO_SER_FILE_SUFFIX);
			if (sobj == null) {
				System.exit(-1);
			}
			//get the CA's private key
			BigInteger caModPriv = ((RSAKeyPairInfo) sobj).getPrivModulus();
			BigInteger caExpPriv = ((RSAKeyPairInfo) sobj).getPrivExponent();
			RSAPrivateKeySpec caPrivKeySpec = new RSAPrivateKeySpec(caModPriv, caExpPriv);
			PrivateKey caPrivateKey = fact.generatePrivate(caPrivKeySpec);
			
			//get the CA's public key
			BigInteger caModPub = ((RSAKeyPairInfo) sobj).getPubModulus();
			BigInteger caExpPub = ((RSAKeyPairInfo) sobj).getPubExponent();
			RSAPublicKeySpec caPubKeySpec = new RSAPublicKeySpec(caModPub, caExpPub);
			PublicKey caPublicKey = fact.generatePublic(caPubKeySpec);
			
			//now create the certificate for the bank
			Certificate bankCertificate = new Certificate(G2Constants.CA_NAME, caPublicKey, bankName, bankPublicKey);
			
			//now put the certificate into a signed object using the CA private key
			Signature signature = Signature.getInstance(G2Constants.SIGNATURE_ALGORITHM);
			SignedObject signedBankCertificate = new SignedObject(bankCertificate, caPrivateKey, signature);
			
			
			//now write the signed object to a serialized file
			String bankCertificateFileName = bankName.replace(' ', '_') + G2Constants.CERTIFICATE_SER_FILE_SUFFIX;
			CA.writeKey(bankCertificateFileName, signedBankCertificate);
			
			System.out.println("Certificate generation complete");
			
		} catch (Exception e) {
			e.printStackTrace();
			System.exit(-1);
		}

	}

	private static void generateCAKeyPair() {
		System.out.println("CA Key Generation Started");
		int keySize = getCAKeyPairSize();
		Object obj = Keygen.generateKey("RSA", keySize);
		if (obj == null) {
			System.out.println("CA Keygen returned null");
			System.exit(-1);
		}
		
		if (obj instanceof RSAKeyPairInfo) {
			RSAKeyPairInfo keyPairInfo = (RSAKeyPairInfo) obj;
			
			//write the RSAKeyPairInfo to a serialized file
			CA.writeKey(G2Constants.CA_NAME.replace(' ', '_') + G2Constants.CA_KEY_PAIR_INFO_SER_FILE_SUFFIX, keyPairInfo);
			
			//now write the CA public key to a serialized file
			BigInteger modPub = keyPairInfo.getPubModulus();
			BigInteger expPub = keyPairInfo.getPubExponent();
			RSAPublicKeySpec keySpec = new RSAPublicKeySpec(modPub, expPub);
			try {
				KeyFactory fact = KeyFactory.getInstance("RSA");
				PublicKey caPublicKey = fact.generatePublic(keySpec);
				CA.writeKey(G2Constants.CA_NAME.replace(' ', '_') + G2Constants.CA_PUBLIC_KEY_SER_FILE_SUFFIX, caPublicKey);

				System.out.println("CA Key Generation Complete");
			} catch (Exception e) {
				e.printStackTrace();
				System.exit(-1);
			}			
		}
		else {
			System.out.println("CA Keygen returned class other than RSAKeyPairInfo");
			System.exit(-1);
		}
	}

	
	/**
	 * Writes the serialized objects that make up the key or keys to the key file
	 * @param inKeyFileName
	 * @param keyInfo
	 * @return
	 */
	private static boolean writeKey(String inKeyFileName, Serializable keyInfo) {
		String keyFileName = inKeyFileName.replace(' ', '_');
		System.out.println("Writing serialized file " + keyFileName);
		if (keyInfo == null) {
			System.out.println("Error writing serialized file - No Key information to write");
			return false;
		}
			
		try {
			ObjectOutputStream keyOutputStream = new ObjectOutputStream(new BufferedOutputStream(new FileOutputStream(keyFileName)));
			keyOutputStream.writeObject(keyInfo);
			keyOutputStream.close();
			
			System.out.println("File written");
		} catch (Exception e) {
			System.out.println("Error writing serialized file (" + e.toString() + ")");
			return false;
		}
		
		return true;
	}

	private static Serializable readKey(String inKeyFileName) {
		String keyFileName = inKeyFileName.replace(' ', '_');
		System.out.println("Reading serialized file " + keyFileName);
			
		try {
			ObjectInputStream keyInputStream = new ObjectInputStream(new FileInputStream(keyFileName));
			Object key = keyInputStream.readObject();
			keyInputStream.close();
			
			//validate the file return
			if (key == null) {
				System.out.println("file read returned null.");
				return null;
			}
			else if (!(key instanceof RSAKeyInfo) &&
					!(key instanceof RSAKeyPairInfo) &&
					!(key instanceof PublicKey) &&
					!(key instanceof PrivateKey)) {
				System.out.println("File did not contain a valid type (" + 
					key.getClass().getSimpleName() + ")");
				return null;
			}
			
			return (Serializable) key;
			
		} catch (Exception e) {
			System.out.println("Error writing serialized file (" + e.toString() + ")");
			return null;
		}
	}

	private static int getCAKeyPairSize() {
		Properties prop = new Properties();
	
		try {
			//load a properties file
			prop.load(new FileInputStream("keysizeconfig.properties"));
	
			//get the property value
			return Integer.decode(prop.getProperty("ca.rsa.keysize"));
	
		} catch (IOException ex) {
			ex.printStackTrace();
		}
		return 4096;
	}

	private static int getBankKeyPairSize() {
		Properties prop = new Properties();
	
		try {
			//load a properties file
			prop.load(new FileInputStream("keysizeconfig.properties"));
	
			//get the property value
			return Integer.decode(prop.getProperty("bank.rsa.keysize"));
	
		} catch (IOException ex) {
			ex.printStackTrace();
		}
		return 2048;
	}
	
	private static int getAesKeySize() {
		Properties prop = new Properties();
	
		try {
			//load a properties file
			prop.load(new FileInputStream("keysizeconfig.properties"));
	
			//get the property value
			return Integer.decode(prop.getProperty("aes.keysize"));
	
		} catch (IOException ex) {
			ex.printStackTrace();
		}
		return 128;
	}

}
