package ssh;

import java.io.BufferedInputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.net.UnknownHostException;
import java.security.InvalidKeyException;
import java.security.Key;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.interfaces.RSAPrivateKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.util.Base64;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;

/**
 * Trivial client for the date server.
 */
public class Client {

	private KeyGenerate keyGenerate;
	private String serverAddress;
	private Socket socket;
	private int port;
	DataOutputStream dos;
	DataInputStream dis;

	public Client(String serverAddress, int port) {
		this.serverAddress = serverAddress;
		this.port = port;
		try {
			keyGenerate = new KeyGenerate("Client",System.getProperty("user.dir")
					+ System.getProperty("file.separator"));
			startSocket();
			responseChallenge(encipherByClientPrivateKey(readChallenge()));
		} catch (UnknownHostException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (InvalidKeyException e) {
			e.printStackTrace();
		} catch (IllegalBlockSizeException e) {
			e.printStackTrace();
		} catch (BadPaddingException e) {
			e.printStackTrace();
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		} catch (NoSuchPaddingException e) {
			e.printStackTrace();
		} catch (InvalidKeySpecException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	private void startSocket() throws UnknownHostException, IOException {
		socket = new Socket(serverAddress, port);
		// prepare out/in stream
		OutputStream o = socket.getOutputStream();
		dos = new DataOutputStream(o);
		InputStream in = socket.getInputStream();
		dis = new DataInputStream(in);
	}

	private byte[] readChallenge() throws IOException {
		int len = dis.readInt();
		byte[] challenge = new byte[len];
		System.out.println("read challenge:" + len);
		if (len > 0) {
			dis.readFully(challenge);
		}
		System.out.println("challenge:" + new String(challenge));
		return challenge;
	}

	private byte[] encipherByClientPrivateKey(byte[] challenge)
			throws InvalidKeyException, IllegalBlockSizeException,
			BadPaddingException, NoSuchAlgorithmException,
			NoSuchPaddingException, InvalidKeySpecException, IOException {
		Cipher cipher = Cipher.getInstance("RSA");
		cipher.init(Cipher.ENCRYPT_MODE,readPrivateKeyFromFile());
		challenge = cipher.doFinal(challenge);
		System.out.println("new Challenge:" + new String(challenge));
		return challenge;
	}

	private void responseChallenge(byte[] challenge) throws IOException {
		dos.writeInt(challenge.length);
		if (challenge.length > 0) {
			dos.write(challenge, 0, challenge.length);
		}
	}

	private Key readPrivateKeyFromFile() throws InvalidKeySpecException, IOException, NoSuchAlgorithmException {
		File privKeyFile = new File("privClient");
		BufferedInputStream bis = null;
		try {
			bis = new BufferedInputStream(new FileInputStream(privKeyFile));
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		byte[] privateKeyBytes = new byte[(int) privKeyFile.length()];
		bis.read(privateKeyBytes);
		bis.close();
		// decoder from base64 string
		privateKeyBytes = Base64.getDecoder().decode(privateKeyBytes);
		KeyFactory keyFactory = KeyFactory.getInstance("RSA");
		PKCS8EncodedKeySpec privSpec = new PKCS8EncodedKeySpec(privateKeyBytes);
		RSAPrivateKey privKey = (RSAPrivateKey) keyFactory
				.generatePrivate(privSpec);
		return privKey;
	}

	/**
	 * Runs the client as an application. First it displays a dialog box asking
	 * for the IP address or hostname of a host running the date server, then
	 * connects to it and displays the date that it serves.
	 * 
	 * @throws Exception
	 */
	public static void main(String[] args) throws Exception {
		new Client("192.168.1.87", 9090);
	}
}