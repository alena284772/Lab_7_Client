package ClientSide;

import java.io.IOException;
import java.security.NoSuchAlgorithmException;

public class Client {
    public static void main(String[] args) throws IOException, NoSuchAlgorithmException {
        ClientConnection clientConnection = new ClientConnection();
        clientConnection.start();

    }
}