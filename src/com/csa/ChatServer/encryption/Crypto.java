package com.csa.ChatServer.encryption;

public interface Crypto {
	public byte[] encrypt(byte[] data);
	public byte[] decrypt(byte[] data);
}
