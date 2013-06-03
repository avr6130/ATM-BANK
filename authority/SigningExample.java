package authority;

import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.Signature;
import java.security.SignatureException;
import java.security.SignedObject;

public class SigningExample {
 
  public static void main(String[] args) {

    try {
	    
	// Generate a 1024-bit Digital Signature Algorithm (DSA) key pair
	KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance("DSA");
	keyPairGenerator.initialize(1024);
	KeyPair keyPair = keyPairGenerator.genKeyPair();
	PrivateKey privateKey = keyPair.getPrivate();
	PublicKey publicKey = keyPair.getPublic();
	    
	// We can sign Serializable objects only
	String unsignedObject = new String("A Test Object");
	Signature signature = Signature.getInstance(privateKey.getAlgorithm());
	SignedObject signedObject = new SignedObject(unsignedObject, privateKey, signature);
	
	// Verify the signed object
	Signature sig = Signature.getInstance(publicKey.getAlgorithm());
	boolean verified = signedObject.verify(publicKey, sig);

	System.out.println("Is signed Object verified ? " + verified);
	
	// Retrieve the object
	unsignedObject = (String) signedObject.getObject();
	
	System.out.println("Unsigned Object : " + unsignedObject);
	
    } catch (SignatureException e) {
    } catch (InvalidKeyException e) {
    } catch (NoSuchAlgorithmException e) {
    } catch (ClassNotFoundException e) {
    } catch (IOException e) {
    }

 }

}
