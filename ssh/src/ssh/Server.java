package ssh;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.security.InvalidKeyException;
import java.security.Key;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.Security;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.X509EncodedKeySpec;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import java.util.Random;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;

import com.sun.org.apache.bcel.internal.generic.NEW;

/**
 * A TCP server that runs on port 9090. When a client connects, it sends the
 * client the current date and time, then closes the connection with that
 * client. Arguably just about the simplest server you can write.
 */
public class Server {
	Socket socket = null;
	// String challenge = "h";
	String pubFileName;
	String clientPublicKeysFolderpath = System.getProperty("user.dir")
			+ System.getProperty("file.separator") + "keybase"
			+ System.getProperty("file.separator");
	String serverKeysFolderpath = System.getProperty("user.dir")
			+ System.getProperty("file.separator");
	DataOutputStream dos;
	DataInputStream dis;
	Cipher cipher;
	String serverName = "Server";
	int challengeLen = 10;
	private Key privateKey;

	public Server(int port) throws NoSuchProviderException {
		ServerSocket listener = null;
		// load provider BC
		Security.addProvider(new org.bouncycastle.jce.provider.BouncyCastleProvider());
		// generate pair of keys for server
		KeyGenerate generate = new KeyGenerate(serverName, serverKeysFolderpath);
		generate.createKey();
		this.privateKey = generate.getPrivateKey();
		try {
			// get instance here to save time
			cipher = Cipher.getInstance("RSA/None/NoPadding", "BC");
			listener = new ServerSocket(port);
			System.out.println("wifi server ready");
			while (true) {
				socket = listener.accept();
				if (socket != null) {
					System.out.println("client connect");
					// preparer dos dis
					OutputStream out = socket.getOutputStream();
					dos = new DataOutputStream(out);
					InputStream in = socket.getInputStream();
					dis = new DataInputStream(in);
					// read client's name
					// pub key file's name is based on client's name
					pubFileName = readClientName();
					// get client's pub key
					File pubClientFile = new File(clientPublicKeysFolderpath
							+ pubFileName);
					if (pubClientFile.exists()) {
						// read client pub key from file
						ReadKeyFromFileThread readPubKeyThread = new ReadKeyFromFileThread(
								pubClientFile.getAbsolutePath(), true);
						readPubKeyThread.start();
						// send server's public key to client
						sendServerPublicKey();
						// random challenge
						String challenge = randomString(challengeLen);
						// send a challenge encrypt
						sendChallenge(challenge);
						byte[] response = waitForResponse();
						readPubKeyThread.join();
						response = decryptByClientPublicKey(response,
								readPubKeyThread.getKey());
						if (checkResponse(response, challenge)) {
							System.out.println("Authentification success");
							sendAuthenResult(true);
							ActionController actionController = new ActionController();
							actionController.controllerON_OFF();
						} else {
							System.out.println("Authentification failed");
							sendAuthenResult(false);
							socket.close();
						}
					} else {
						System.out
								.println("cannot find key:Authentification failed");
						sendAuthenResult(false);
						socket.close();
					}
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		} catch (InvalidKeyException e) {
			e.printStackTrace();
		} catch (IllegalBlockSizeException e) {
			e.printStackTrace();
		} catch (BadPaddingException e) {
			e.printStackTrace();
		} catch (NoSuchPaddingException e) {
			e.printStackTrace();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	private void sendServerPublicKey() throws IOException {
		System.out.println("start sending server public key");
		File publicKeyFile = new File(serverKeysFolderpath + "pub"
				+ serverName);
		if (publicKeyFile.exists()) {
			try {
				BufferedInputStream bis = new BufferedInputStream(
						new FileInputStream(publicKeyFile));
				byte[] bytes = new byte[(int) publicKeyFile.length()];
				bis.read(bytes);
				int len = (int)publicKeyFile.length();
				dos.writeInt(len);
				dos.writeUTF("pub" + serverName);
				dos.write(bytes, 0, len);
			} catch (FileNotFoundException e) {
				System.err.println("File not found. ");
			}
		} else {
			System.err.println("cannot find server pub key");
		}
	}

	private String readClientName() throws IOException {
		int len = dis.readInt();
		byte[] name = new byte[len];
		if (len > 0) {
			dis.read(name, 0, len);
		}
		String nameString = new String(name);
		System.out.println("get Name:" + nameString);
		return nameString;
	}

	private void sendChallenge(String challenge) throws IOException,
			InvalidKeyException, IllegalBlockSizeException, BadPaddingException {
		// encrypt challenge
		byte[] encryptChallenge = encryptByServerPrivateKey(
				challenge.getBytes(), this.privateKey);
		// send challenge
		dos.writeInt(encryptChallenge.length);
		dos.write(encryptChallenge, 0, encryptChallenge.length);
	}

	private byte[] waitForResponse() throws IOException {
		// read new challenge
		int len = dis.readInt();
		byte[] codeChallenge = new byte[len];
		if (len > 0) {
			dis.readFully(codeChallenge);
		}
		return codeChallenge;
	}

	private byte[] decryptByClientPublicKey(byte[] message, Key publicClientKey)
			throws IllegalBlockSizeException, BadPaddingException,
			InvalidKeyException, NoSuchAlgorithmException,
			NoSuchPaddingException, NoSuchProviderException {
		cipher.init(Cipher.DECRYPT_MODE, publicClientKey);
		message = cipher.doFinal(message);
		return message;
	}

	private byte[] encryptByServerPrivateKey(byte[] message,
			Key privateServerKey) throws InvalidKeyException,
			IllegalBlockSizeException, BadPaddingException {
		cipher.init(Cipher.ENCRYPT_MODE, privateServerKey);
		message = cipher.doFinal(message);
		return message;
	}

	private boolean checkResponse(byte[] response, String challenge) {
		System.out.println(new String(response));
		return challenge.equals(new String(response));
	}
	
	private void sendAuthenResult(boolean isAuthen) throws IOException{
		dos.writeBoolean(isAuthen);
	}

	public String randomString(int len) {
		String str = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
		Random random = new Random();
		StringBuffer buf = new StringBuffer();
		for (int i = 0; i < len; i++) {
			int num = random.nextInt(62);
			buf.append(str.charAt(num));
		}
		return buf.toString();
	}
	

	/**
	 * Runs the server.
	 * 
	 * @throws Exception
	 */
	public static void main(String[] args) throws Exception {
		new Server(9090);
	}
}