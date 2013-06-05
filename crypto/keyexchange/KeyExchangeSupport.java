package crypto.keyexchange;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.Serializable;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.Security;
import java.security.Signature;
import java.security.SignedObject;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SealedObject;

import original.PropertiesFile;
import authority.G2Constants;
import crypto.Certificate;
import crypto.RSAKeyInfo;
import crypto.RSAKeyPairInfo;

public class KeyExchangeSupport {

	public static enum AppMode {
		ATM(), BANK();
	}

	private static final String pkAlgorithm = "RSA/ECB/PKCS1Padding";

	private final AppMode mode;

	/**
	 * only the ATM has this
	 */
	private final PublicKey root;

	/**
	 * only the bank has this 
	 */
	private final PrivateKey bankPrivate;

	/**
	 * only the bank has this 
	 */
	private final SignedObject bankCertificate;

	public KeyExchangeSupport(AppMode mode) {
		if (PropertiesFile.isDebugMode()) {
			System.out.println("Security Providers=" + Security.getProviders());
		}

		this.mode = mode;

		if (mode == AppMode.ATM) {
			//must be in ATM Mode
			this.root = (PublicKey) KeyExchangeSupport.readKey(G2Constants.CA_NAME + G2Constants.CA_PUBLIC_KEY_SER_FILE_SUFFIX);
			this.bankPrivate = null;
			this.bankCertificate = null;
		}
		else {
			//must be in Bank Mode
			this.root = null;
			this.bankPrivate = (PrivateKey) KeyExchangeSupport.readKey(G2Constants.BANK_NAME + G2Constants.PRIVATE_KEY_SER_FILE_SUFFIX);
			this.bankCertificate = (SignedObject) KeyExchangeSupport.readKey(G2Constants.BANK_NAME + G2Constants.CERTIFICATE_SER_FILE_SUFFIX);
		}
	}

	/**
	 * Used by BANK
	 * @return the bank's certificate
	 */
	public SignedObject getBankCertificate() {
		if (this.mode == AppMode.BANK) {
			if (PropertiesFile.isDebugMode()) {
				System.out.println("getBankCertificate=" + this.bankCertificate);
			}
			return this.bankCertificate;
		}

		return null;
	}

	/**
	 * Used by ATM
	 * @param bankSignedObject
	 * @return the public key of the bank or null if the object is invalid
	 */
	public PublicKey validateCertificate(SignedObject bankSignedObject, String bankName) {
		if (this.mode != AppMode.ATM) {
			return null;
		}

		try {
			//first validate the signed object using the CA's root key
			Signature sig = Signature.getInstance(G2Constants.SIGNATURE_ALGORITHM);
			boolean verified = bankSignedObject.verify(this.root, sig);
			if (!verified) {
				return null;
			}
			//next compare the public key in the certificate with the root certificate
			Certificate cert = (Certificate) bankSignedObject.getObject();
			if (!KeyExchangeSupport.pubKeysEqual(this.root, cert.getCaKey())) {
				return null;
			}

			//next compare the CA name with the one in the certificate
			if (!G2Constants.CA_NAME.equals(cert.getCaName())) {
				return null;
			}

			//finally compare the bank name
			if (!G2Constants.BANK_NAME.equals(bankName)) {
				return null;
			}

			//if we get here then all is well so return the bank's public key
			return cert.getBankKey();

		} catch (Exception e) {
			//the signed object probably didn't contain a certificate
			return null;
		}
	}

	/**
	 * Used by ATM - takes the serializable secret information and encrypts it using the 
	 * PublicKey of the bank which must have been extracted from the certificate the bank 
	 * sent previously.
	 * @param key
	 * @param bankPublicKey
	 * @return the encrypted bytes of the key parameter or an empty array if any error occurs
	 */
	public SealedObject encryptSecret(Serializable secret, PublicKey bankPublicKey) {
		if (this.mode != AppMode.ATM) {
			return null;
		}

		try {
			Cipher rsaCipher = Cipher.getInstance(KeyExchangeSupport.pkAlgorithm);
			rsaCipher.init(Cipher.ENCRYPT_MODE, bankPublicKey);
			SealedObject so = new SealedObject(secret, rsaCipher);
			return so;
		} catch (Exception e) {
			if (PropertiesFile.isDebugMode()) {
				e.printStackTrace();
			}
		}
		return null;
	}

	/**
	 * Used by BANK
	 *- takes the byte array which must be an encrypted serialized object and recovers
	 * and returns the object.  The decryption uses the bank's private key.
	 * @param secretBytes
	 * @return the decrypted secret object or null if an error occurs
	 */
	public Object decryptSecret(SealedObject so) {
		if (this.mode != AppMode.BANK) {
			return null;
		}

		try {
			Cipher rsaCipher = Cipher.getInstance(KeyExchangeSupport.pkAlgorithm);
			rsaCipher.init(Cipher.DECRYPT_MODE, this.bankPrivate);
			return so.getObject(rsaCipher);
		} catch (InvalidKeyException | NoSuchAlgorithmException | NoSuchPaddingException | ClassNotFoundException | IllegalBlockSizeException | BadPaddingException | IOException e) {
			if (PropertiesFile.isDebugMode()) {
				e.printStackTrace();
			}
		}
		return null;
	}

	private static boolean pubKeysEqual(PublicKey key1, PublicKey key2) {
		byte[] rootBytes = key1.getEncoded();
		byte[] caBytes = key2.getEncoded();

		if (caBytes.length != rootBytes.length) {
			return false;
		}

		for (int i = 0; i < caBytes.length; i++) {
			if (caBytes[i] != rootBytes[i]) {
				return false;
			}
		}

		return true;
	}

	private static Serializable readKey(String inKeyFileName) {
		String keyFileName = inKeyFileName.replace(' ', '_');
		try {
			ObjectInputStream keyInputStream = new ObjectInputStream(new FileInputStream(keyFileName));
			Object key = keyInputStream.readObject();
			keyInputStream.close();

			//validate the file return
			if (key == null) {
				return null;
			}
			else if (!(key instanceof RSAKeyInfo) &&
					!(key instanceof RSAKeyPairInfo) &&
					!(key instanceof PublicKey) &&
					!(key instanceof PrivateKey) &&
					!(key instanceof SignedObject)) {
				return null;
			}

			return (Serializable) key;

		} catch (Exception e) {
			if (PropertiesFile.isDebugMode()) {
				e.printStackTrace();
			}
			return null;
		}
	}
}
