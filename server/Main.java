package server;

import java.io.IOException;

public class Main {
    protected static Server server;

    public static void main(String[] args) throws IOException {
        server = new Server(23456, "./JSON Database/task/src/server/data/db.json");
        server.run();
    }

}


