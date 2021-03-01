package server;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

public class Session implements Runnable {
    private final Socket socket;
    private final JsonDatabase database;

    public Session(Socket socket, JsonDatabase database) {
        this.socket = socket;
        this.database = database;
    }

    @Override
    public void run() {
        try (DataInputStream inputStream = new DataInputStream(socket.getInputStream());
             DataOutputStream outputStream = new DataOutputStream(socket.getOutputStream())) {

            String clientIncomingJSON = inputStream.readUTF();
            System.out.println("Received: " + clientIncomingJSON);
            if (clientIncomingJSON.contains("\"type\":\"exit\"")) {
                outputStream.writeUTF("{\"response\":\"OK\"}");
                Main.server.stop();
                return;
            }
            String responseFromDatabase = database.executeJson(clientIncomingJSON);
            outputStream.writeUTF(responseFromDatabase);
            System.out.println("Sent: " + responseFromDatabase);
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
