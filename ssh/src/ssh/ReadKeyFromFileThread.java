package ssh;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.security.Key;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;

public class ReadKeyFromFileThread extends Thread {

	private String filePath = "";
	private Key key;
	boolean isPublicKey;

	public ReadKeyFromFileThread(String path,boolean isPublicKey) {
		this.filePath = path;
		this.isPublicKey = isPublicKey;
	}

	@Override
	public void run() {
		try {
			if (isPublicKey) {
				key = readPubKeyFromFile(filePath);
			}else {
				key = readPrivateKeyFromFile(filePath);
			}	
		} catch (InvalidKeySpecException e) {
			e.printStackTrace();
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		} catch (NoSuchProviderException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private RSAPublicKey readPubKeyFromFile(String keyPath) throws IOException,
			InvalidKeySpecException, NoSuchAlgorithmException,
			NoSuchProviderException {
		File pubKeyFile = new File(keyPath);
		if (!pubKeyFile.exists()) {
			return null;
		}
		String keyString = readFileIntoSingleLineString(pubKeyFile);

		// converts the String to a PublicKey instance
		KeyFactory keyFactory = KeyFactory.getInstance("RSA");
		byte[] keyBytes = Base64.getDecoder().decode(
				keyString.getBytes("utf-8"));
		X509EncodedKeySpec pubSpec = new X509EncodedKeySpec(keyBytes);
		RSAPublicKey pubKey = (RSAPublicKey) keyFactory.generatePublic(pubSpec);
		return pubKey;
	}

	private RSAPrivateKey readPrivateKeyFromFile(String keyPath)
			throws IOException, InvalidKeySpecException,
			NoSuchAlgorithmException, NoSuchProviderException {
		File privKeyFile = new File(keyPath);
		if (!privKeyFile.exists()) {
			return null;
		}
		String keyString = readFileIntoSingleLineString(privKeyFile);

		// converts the String to a PublicKey instance
		KeyFactory keyFactory = KeyFactory.getInstance("RSA");
		byte[] keyBytes = Base64.getDecoder().decode(
				keyString.getBytes("utf-8"));
		PKCS8EncodedKeySpec privSpec = new PKCS8EncodedKeySpec(keyBytes);
		RSAPrivateKey privKey = (RSAPrivateKey) keyFactory
				.generatePrivate(privSpec);
		return privKey;
	}

	private String readFileIntoSingleLineString(File file) throws IOException {
		// read the key stored in a file
		BufferedReader br = new BufferedReader(new InputStreamReader(
				new FileInputStream(file)));
		List<String> lines = new ArrayList<String>();
		String line = null;
		while ((line = br.readLine()) != null)
			lines.add(line);

		// concats the remaining lines to a single String
		StringBuilder sb = new StringBuilder();
		for (String aLine : lines)
			sb.append(aLine);
		String keyString = sb.toString();
		return keyString;
	}

	public Key getKey() {
		return this.key;
	}

}
