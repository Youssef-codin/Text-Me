package arch.joe.security;

import java.security.SecureRandom;

import arch.joe.app.User;

public class Auth {

    private Auth() {

    }

    public static boolean userCheck(User usr, String checkPass) throws Exception {

        String password = usr.getPassword();
        String salt = usr.getSalt();

        // System.out.println("Password and check password below");
        // System.out.println(password);
        // System.out.println(Crypto.stringToHash(checkPass, salt));

        if (!password.equals(Crypto.stringToHash(checkPass, salt))) {
            return false;
        } else {
            return true;
        }
    }

    public static byte[] makeSecret() { // To be used later with Java JWT

        SecureRandom random = new SecureRandom();

        byte[] secret = new byte[32];
        random.nextBytes(secret);

        return secret;

    }
}
