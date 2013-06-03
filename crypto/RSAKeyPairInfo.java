package crypto;

import java.io.Serializable;
import java.math.BigInteger;

public class RSAKeyPairInfo implements Serializable {

	private RSAKeyInfo pubKey = new RSAKeyInfo();
	private RSAKeyInfo privKey = new RSAKeyInfo();

	public RSAKeyPairInfo(BigInteger pubModulus, BigInteger publicExponent,
			BigInteger privModulus, BigInteger privExponent) {
		this.pubKey.setModulus(pubModulus);
		this.pubKey.setExponent(publicExponent);
		this.privKey.setModulus(privModulus);
		this.privKey.setExponent(privExponent);
	}

	public BigInteger getPubModulus() {
		return this.pubKey.getModulus();
	}

	public BigInteger getPubExponent() {
		return this.pubKey.getExponent();
	}

	public BigInteger getPrivModulus() {
		return this.privKey.getModulus();
	}

	public BigInteger getPrivExponent() {
		return this.privKey.getExponent();
	}

}
