package arch.joe.security;

import arch.joe.app.User;

public class Auth {

    private Auth() {

    }

    public static boolean userCheck(User usr, String checkPass) throws Exception {

        String password = usr.getPassword();
        String salt = usr.getSalt();

        System.out.println("Password and check password below");
        System.out.println(password);
        System.out.println(Crypto.stringToHash(checkPass, salt));

        if (!password.equals(Crypto.stringToHash(checkPass, salt))) {
            return false;
        } else {
            return true;
        }
    }
}
