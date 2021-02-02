package client;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.net.InetAddress;
import java.net.Socket;

public class Main {

    public static void main(String[] args) {
        String address = "127.0.0.1";
        int port = 23456;
        int record = 14;
        try (Socket socket = new Socket(InetAddress.getByName(address), port);
             DataInputStream inputStream = new DataInputStream(socket.getInputStream());
             DataOutputStream outputStream = new DataOutputStream(socket.getOutputStream())
        ) {
            System.out.println("Client started!");
            String outputMessage = "Give me a record of # " + record;
            outputStream.writeUTF(outputMessage);
            System.out.println("Sent: " + outputMessage);

            String receivedMessage = inputStream.readUTF();
            System.out.println("Received: " + receivedMessage);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
