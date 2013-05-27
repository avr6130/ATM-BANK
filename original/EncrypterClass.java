package original;// Copied from http://www.java2s.com/Code/Java/Security/EncryptingaStringwithDES.htm
// Modified to use byte[] instead of strings.  

import java.security.Key;
import java.security.SecureRandom;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;

public class EncrypterClass {
	Cipher ecipher;
	Cipher dcipher;

	EncrypterClass(SecretKey key, String encryptionType) throws Exception {
		ecipher = Cipher.getInstance(encryptionType);
		dcipher = Cipher.getInstance(encryptionType);
		ecipher.init(Cipher.ENCRYPT_MODE, key);
		dcipher.init(Cipher.DECRYPT_MODE, key);
	}

	EncrypterClass(String encryptionType, String rsaArgs, String provider)
			throws Exception {

		ecipher = Cipher.getInstance(encryptionType + rsaArgs, provider);
		dcipher = Cipher.getInstance(encryptionType + rsaArgs, provider);

	}

	public byte[] encryptRsaWithPublicKey(byte[] bytesToEncrypt,
			SecureRandom random, Key pubKey) throws Exception {

		ecipher.init(Cipher.ENCRYPT_MODE, pubKey, random);

		// Encrypt the given byte array
		byte[] enc = ecipher.doFinal(bytesToEncrypt);

		return enc;
	} // end encryptRSAPublicKey()

	public byte[] decryptRsaWithPrivateKey(byte[] bytesToDecrypt, Key privateKey)
			throws Exception {

		dcipher.init(Cipher.DECRYPT_MODE, privateKey);

		// Encrypt the given byte array
		byte[] dec = dcipher.doFinal(bytesToDecrypt);

		return dec;
	} // end encryptRSAPublicKey()

	public byte[] encrypt(byte[] bytesToEncrypt) throws Exception {

		// Encrypt the given byte array
		byte[] enc = ecipher.doFinal(bytesToEncrypt);

		return enc;
	}

	public byte[] decrypt(byte[] encrypted) throws Exception {

		// Decipher the given byte array
		byte[] decipheredBytes = dcipher.doFinal(encrypted);

		return decipheredBytes;
	}
}
