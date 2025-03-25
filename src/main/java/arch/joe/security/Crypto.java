package arch.joe.security;

import java.util.Base64;
import java.security.SecureRandom;

public abstract class Crypto {

    public static String makeSalt() {

        SecureRandom random = new SecureRandom();

        byte[] salt = new byte[16];
        random.nextBytes(salt);

        return Base64.getEncoder().encodeToString(salt);
    }
}
