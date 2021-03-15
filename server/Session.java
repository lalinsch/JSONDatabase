package server;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.concurrent.atomic.AtomicBoolean;

public class Session implements Runnable {
    private final Socket socket;
    private final AtomicBoolean[] stopServer;
    private final JsonDatabase database;

    public Session(Socket socket, AtomicBoolean[] stopServer, JsonDatabase database) {
        this.socket = socket;
        this.stopServer = stopServer;
        this.database = database;
    }

    //Allows for multithreading requests
    @Override
    public synchronized void run() {
        try (var inputStream = new DataInputStream(socket.getInputStream());
             DataOutputStream outputStream = new DataOutputStream(socket.getOutputStream())) {

            String clientIncomingJSON = inputStream.readUTF();
            String outgoingJsonString;
            System.out.println("Received: " + clientIncomingJSON);
            if (clientIncomingJSON.contains("\"type\":\"exit\"")) {
                stopServer[0] = new AtomicBoolean(false);
                outgoingJsonString = "{\"response\":\"OK\"}";
                Main.server.stop();
            } else {
                outgoingJsonString = database.executeJson(clientIncomingJSON);
            }
            outputStream.writeUTF(outgoingJsonString);
            System.out.println("Sent: " + outgoingJsonString);
        } catch (IOException exception) {
            exception.printStackTrace();
        } finally {
            try {
                socket.close();
            } catch (IOException exception) {
                exception.printStackTrace();
            }
        }
    }
}
