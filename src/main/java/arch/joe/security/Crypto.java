package arch.joe.security;

import java.util.Base64;
import java.nio.charset.StandardCharsets;
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
import javax.crypto.spec.PBEKeySpec;

public class Crypto {

    private Crypto() {

    }

    public static String encoderHelper(byte[] bytes) {
        return Base64.getEncoder().encodeToString(bytes);
    }

    public static byte[] decoderHelper(String string) {
        return Base64.getDecoder().decode(string);
    }

    public static String makeSalt() {

        SecureRandom random = new SecureRandom();

        byte[] salt = new byte[16];
        random.nextBytes(salt);

        return encoderHelper(salt);
    }

    public static String stringToHash(String password, String salt) throws Exception {

        byte[] saltBytes = decoderHelper(salt);

        KeySpec spec = new PBEKeySpec(password.toCharArray(), saltBytes, 65536, 256);
        SecretKeyFactory factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256");

        byte[] hashBytes = factory.generateSecret(spec).getEncoded();
        return encoderHelper(hashBytes);
    }

    public static KeyPair makeKeyPair() throws Exception {

        KeyPairGenerator gen = KeyPairGenerator.getInstance("RSA");
        gen.initialize(2048);
        return gen.generateKeyPair();

    }

    public static String cipher(String msg, PublicKey key) throws Exception {

        Cipher cipher = Cipher.getInstance("RSA");
        cipher.init(Cipher.ENCRYPT_MODE, key);

        byte[] encryptedBytes = cipher.doFinal(msg.getBytes());

        return encoderHelper(encryptedBytes);
    }

    public static String decipher(String encryptedMsg, PrivateKey key) throws Exception {

        Cipher cipher = Cipher.getInstance("RSA");
        cipher.init(Cipher.DECRYPT_MODE, key);

        byte[] encryptedBytes = decoderHelper(encryptedMsg);
        byte[] decryptedBytes = cipher.doFinal(encryptedBytes);

        return new String(decryptedBytes, StandardCharsets.UTF_8);
    }

    public static PublicKey bytesToPublicKey(byte[] bytes) throws Exception {

        KeyFactory keyFac = KeyFactory.getInstance("RSA");
        EncodedKeySpec publickeyspec = new X509EncodedKeySpec(bytes);

        return keyFac.generatePublic(publickeyspec);
    }

    public static void main(String[] args) throws Exception {

        KeyPair pair = makeKeyPair();
        PrivateKey privkey = pair.getPrivate();
        PublicKey pubkey = pair.getPublic();

        // make into bytes for storage
        byte[] bytes = pubkey.getEncoded();

        // recreate instance
        PublicKey pubkey2 = bytesToPublicKey(bytes);

        String ciphered = cipher("blah blah blah", pubkey2);
        String deciphered = decipher(ciphered, privkey);
        System.out.println(deciphered);

    }
}
