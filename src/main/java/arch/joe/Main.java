package arch.joe;

import arch.joe.client.GUI.Utils;
import arch.joe.client.GUI.Login.Login;

public class Main {

    public static void main(String[] args) throws Exception {
        Utils.createFolder();
        Login.main(args);

    }
}
