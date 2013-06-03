package crypto;

import java.io.Serializable;
import java.security.PublicKey;

public final class Certificate implements Serializable {
	
	private String caName;
	private PublicKey caKey;
	private String bankName;
	private PublicKey bankKey;
	
	public Certificate(String caName, PublicKey caKey, String bankName, PublicKey bankKey) {
		this.caName=caName;
		this.caKey=caKey;
		this.bankName=bankName;
		this.bankKey=bankKey;
	}

	public String getCaName() {
		return caName;
	}

	public PublicKey getCaKey() {
		return caKey;
	}

	public String getBankName() {
		return bankName;
	}

	public PublicKey getBankKey() {
		return bankKey;
	}

}
