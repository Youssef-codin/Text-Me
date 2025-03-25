package arch.joe.app;

import arch.joe.security.Crypto;

public class User {

    private String name;
    private String password;
    private final String salt;

    public User(String name, String password, String salt) {

        this.name = name;
        this.password = password;
        this.salt = Crypto.makeSalt();

    }

    public String getName() {
        return name;
    }

    public String getPassword() {
        return password;
    }

    public String getSalt() {
        return salt;
    }
}
