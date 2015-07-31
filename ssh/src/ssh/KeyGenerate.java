package ssh;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.security.Key;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;

public class KeyGenerate {
	private KeyPairGenerator keyGen = null;
    private KeyPair keyPair = null;
    private Key privateKey = null;
    private Key publicKey = null;
    private String privFileName = "";
    private String pubFileName = "";
    private String savePath = "";
    
    ByteArrayOutputStream out = new ByteArrayOutputStream();
    
    public KeyGenerate(String ownerName, String savePath){
    	privFileName = "priv"+ownerName;
    	pubFileName = "pub"+ownerName;
    	this.savePath = savePath;
    }
    
    public void createKey(){
    	try {
            keyGen = KeyPairGenerator.getInstance("RSA");//, "AndroidKeyStore"
            keyGen.initialize(2048);
            keyPair = keyGen.generateKeyPair();
            
            //get 2 keys
            privateKey = keyPair.getPrivate();
            publicKey = keyPair.getPublic();
            
            //write private key's file
            File privFile = new File(savePath+privFileName);
            if (!privFile.exists()) {
				privFile.createNewFile();
			}
            FileOutputStream privFileOut = new FileOutputStream(privFile);
            //encode by base64
            privFileOut.write(Base64.getEncoder().encode(privateKey.getEncoded()));
            privFileOut.close();
            
            //write public key's file
            File pubFile = new File(savePath+pubFileName);
            if (!pubFile.exists()) {
            	pubFile.createNewFile();
			}
            FileOutputStream pubFileOut = new FileOutputStream(pubFile);
            //encode by base64
            pubFileOut.write(Base64.getEncoder().encode(publicKey.getEncoded()));
            pubFileOut.close();
            
            
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    }
    
    public Key getPrivateKey(){
    	return this.privateKey;
    }
    
//    public static void main(String[] args) {
//		KeyGenerate generate = new KeyGenerate(true);
//		generate.createKey();
//	}
    
}
