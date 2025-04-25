package arch.joe.security;

import java.security.SecureRandom;
import java.util.Date;
import java.util.Properties;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTCreationException;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.bastiaanjansen.otp.HMACAlgorithm;
import com.bastiaanjansen.otp.HOTPGenerator;
import com.bastiaanjansen.otp.SecretGenerator;

public class Auth {

    private static final Algorithm algo = Algorithm.HMAC256(makeSecret());

    private Auth() {

    }

    // JWT
    private static byte[] makeSecret() {

        SecureRandom random = new SecureRandom();

        byte[] secret = new byte[32];
        random.nextBytes(secret);

        return secret;

    }

    public static String makeToken(String username) {

        try {
            String token = JWT.create()
                    .withClaim("user_name", username)
                    .withIssuer("auth0")
                    .withExpiresAt(new Date(System.currentTimeMillis() + 60000)) // debug: only 60,000 here so show it
                                                                                 // works
                    .sign(algo);
            return token;

        } catch (JWTCreationException e) {
            // e.printStackTrace();
            return null;
        }
    }

    public static String verifyToken(String token) {
        DecodedJWT decodedJWT;

        try {
            JWTVerifier verifier = JWT.require(algo).withIssuer("auth0").build();

            decodedJWT = verifier.verify(token);
            return decodedJWT.getClaim("user_name").asString();

        } catch (JWTVerificationException e) {
            e.printStackTrace();
            return null;
        }
    }

    public byte[] generateSecret() {
        return SecretGenerator.generate();
    }

    public String generateOTP(String secret, int counter) {

        HOTPGenerator hotp = new HOTPGenerator.Builder(secret).withAlgorithm(HMACAlgorithm.SHA256).build();
        String otp = hotp.generate(counter);

        return otp;
    }

    public void sendOTP() {
        Properties prop = new Properties();
        prop.put("", true);

    }
}
