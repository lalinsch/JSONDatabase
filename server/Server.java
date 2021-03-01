package server;

import java.io.IOException;
import java.net.ServerSocket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Server {
    private int PORT;
    private JsonDatabase jsonDatabase;
    private ServerSocket socket;
    private ExecutorService executor;

    public Server(int PORT, String dbPath) throws IOException {
        this.PORT = PORT;
        jsonDatabase = new JsonDatabase(dbPath);
        socket = new ServerSocket(PORT);
        executor = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors() * 3);
    }

    public void run() {
        System.out.println("Server started!");
        try {
            while (true) {
                executor.submit(new Session(socket.accept(), jsonDatabase));
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
    }
}
