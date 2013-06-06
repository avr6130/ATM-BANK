package com.group2.crypto;

import java.io.Serializable;
import java.math.BigInteger;

public class RSAKeyInfo implements Serializable {
	private BigInteger modulus;
	private BigInteger exponent;

	public RSAKeyInfo() {
	}

	public BigInteger getModulus() {
		return modulus;
	}

	public void setModulus(BigInteger modulus) {
		this.modulus = modulus;
	}

	public BigInteger getExponent() {
		return exponent;
	}

	public void setExponent(BigInteger exponent) {
		this.exponent = exponent;
	}
}