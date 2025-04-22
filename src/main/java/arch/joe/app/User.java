package arch.joe.app;

public class User {

    private final String name;
    private final String password;
    private final String email;
    private final String salt;
    private final byte[] pubKey;

    public User(String name, String email, String password, String salt, byte[] pubKey) throws Exception {

        this.name = name;
        this.password = password;
        this.email = email;
        this.salt = salt;
        this.pubKey = pubKey;

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

    public byte[] getKey() {
        return pubKey;
    }

    public String getEmail() {
        return email;
    }

    public byte[] getPubKey() {
        return pubKey;
    }
}
