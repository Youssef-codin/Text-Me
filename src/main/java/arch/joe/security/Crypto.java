package arch.joe.security;

import java.util.Base64;
import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.SecureRandom;
import java.security.spec.EncodedKeySpec;
import java.security.spec.KeySpec;
import java.security.spec.X509EncodedKeySpec;

import javax.crypto.*;
import javax.crypto.spec.GCMParameterSpec;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;

public class Crypto {

    private Crypto() {

    }

    private static final String algoAES = "AES/GCM/NoPadding";

    public static String encoder(byte[] bytes) {
        return Base64.getEncoder().encodeToString(bytes);
    }

    public static byte[] decoder(String string) {
        return Base64.getDecoder().decode(string);
    }

    // -------- hashing -----------
    public static String makeSalt() {

        byte[] salt = new byte[16];
        new SecureRandom().nextBytes(salt);

        return encoder(salt);
    }

    public static String stringToHash(String password, String salt) throws Exception {

        byte[] saltBytes = decoder(salt);

        KeySpec spec = new PBEKeySpec(password.toCharArray(), saltBytes, 65536, 256);
        SecretKeyFactory factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256");

        byte[] hashBytes = factory.generateSecret(spec).getEncoded();
        return encoder(hashBytes);
    }

    // -------- RSA -----------

    public static KeyPair makeKeyPair() throws Exception {

        KeyPairGenerator gen = KeyPairGenerator.getInstance("RSA");
        gen.initialize(2048);
        return gen.generateKeyPair();

    }

    public static String cipherRSA(byte[] msg, PublicKey key) throws Exception {

        Cipher cipher = Cipher.getInstance("RSA");
        cipher.init(Cipher.ENCRYPT_MODE, key);

        byte[] encryptedBytes = cipher.doFinal(msg);

        return encoder(encryptedBytes);
    }

    public static byte[] decipherRSA(String encryptedMsg, PrivateKey key) throws Exception {

        Cipher cipher = Cipher.getInstance("RSA");
        cipher.init(Cipher.DECRYPT_MODE, key);

        byte[] encryptedBytes = decoder(encryptedMsg);
        byte[] decryptedBytes = cipher.doFinal(encryptedBytes);

        return decryptedBytes;
    }

    public static PublicKey bytesToPublicKey(byte[] bytes) throws Exception {

        KeyFactory keyFac = KeyFactory.getInstance("RSA");
        EncodedKeySpec publickeyspec = new X509EncodedKeySpec(bytes);

        return keyFac.generatePublic(publickeyspec);
    }

    // -------- AES -----------

    public static SecretKey makeAESKey() throws Exception {

        KeyGenerator gen = KeyGenerator.getInstance("AES");
        gen.init(256);
        return gen.generateKey();
    }

    public static String generateIVBytes() {
        byte[] iv = new byte[12];
        new SecureRandom().nextBytes(iv);

        return encoder(iv);
    }

    public static String cipherAES(String string, SecretKey key, String ivBytes) throws Exception {

        GCMParameterSpec iv = new GCMParameterSpec(128, decoder(ivBytes));
        Cipher cipher = Cipher.getInstance(algoAES);
        cipher.init(Cipher.ENCRYPT_MODE, key, iv);
        byte[] encryptedString = cipher.doFinal(string.getBytes());

        return encoder(encryptedString);

    }

    public static String decipherAES(String string, SecretKey key, String ivBytes) throws Exception {

        GCMParameterSpec iv = new GCMParameterSpec(128, decoder(ivBytes));
        Cipher cipher = Cipher.getInstance(algoAES);
        cipher.init(Cipher.DECRYPT_MODE, key, iv);
        byte[] decryptedString = cipher.doFinal(decoder(string));

        return new String(decryptedString);

    }

    public static String getAlgo() {
        return algoAES;
    }

    // public static void main(String[] args) throws Exception {
    // String string = "hi mom";
    // SecretKey aesKey = makeAESKey();
    // String ivBytes = generateIVBytes();
    //
    // String aesEncryptedMsg = cipherAES(string, aesKey, ivBytes);
    //
    // KeyPair pair = makeKeyPair();
    //
    // String encryptedAESKey = cipherRSA(aesKey.getEncoded(), pair.getPublic());
    // byte[] aesKeyBytes = decipherRSA(encryptedAESKey, pair.getPrivate());
    //
    // SecretKey decryptedAESKey = new SecretKeySpec(aesKeyBytes, "AES");
    //
    // String decrypted = decipherAES(aesEncryptedMsg, decryptedAESKey, ivBytes);
    //
    // System.out.println("encrypted = " + aesEncryptedMsg);
    // System.out.println("decrypted = " + decrypted);
    //
    // }
}
