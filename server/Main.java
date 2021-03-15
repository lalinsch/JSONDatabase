package server;

import java.io.IOException;

public class Main {
    protected static Server server;
    private static String ADDRESS = "127.0.0.1";
    private static int PORT = 23456;

    public static void main(String[] args) throws IOException {
        server = new Server(PORT, ADDRESS, "./JSON Database/task/src/server/data/db.json");
        server.run();
    }

}


