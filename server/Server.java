package server;

import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicBoolean;

public class Server {
    private final JsonDatabase jsonDatabase;
    private final ServerSocket socket;
    private final ExecutorService executor;
    private final AtomicBoolean[] stopServer = {new AtomicBoolean(true)};

    public Server(int PORT, String ADDRESS, String dbPath) throws IOException {
        jsonDatabase = new JsonDatabase(dbPath);
        socket = new ServerSocket(PORT, 50, InetAddress.getByName(ADDRESS));
        executor = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors() * 3);
    }

    //Runs continuously until an exit request gets sent by the client
    public void run() {
        System.out.println("Server started!");
        try {
            while (true) {
                executor.submit(new Session(socket.accept(), stopServer, jsonDatabase));
            }
        } catch (IOException exception) {
            exception.printStackTrace();
        }

    }

    public void stop() {
        try {
            socket.close();
        } catch (IOException exception) {
            exception.printStackTrace();
        }
        executor.shutdown();
    }
}
