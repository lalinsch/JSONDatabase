package client;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.net.InetAddress;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import com.beust.jcommander.JCommander;
import com.beust.jcommander.Parameter;

public class Main {
    private static final List<String> parameters = new ArrayList<>();

    @Parameter(names = "-t", description = "type of request")
    private static String type;

    @Parameter(names = "-i", description = "index of the cell")
    private static int index;

    @Parameter(names = "-m", description = "value to save")
    private static String value;

    public static void main(String[] args) {
        Main main = new Main();
        //parses args through JCommander
        JCommander.newBuilder()
                .addObject(main)
                .build()
                .parse(args);
        main.run();
    }

    //Connects to the server and sends the message using the program parameters
    public static void run() {
        String address = "127.0.0.1";
        int port = 23456;
        try (Socket socket = new Socket(InetAddress.getByName(address), port);
             DataInputStream inputStream = new DataInputStream(socket.getInputStream());
             DataOutputStream outputStream = new DataOutputStream(socket.getOutputStream())
        ) {
            System.out.println("Client started!");
            String outputMessage;
            if (value != null) {
                outputMessage = type + " " + index + " " + value;
            } else {
                outputMessage = type + " "  + index;
            }
            outputStream.writeUTF(outputMessage);
            System.out.println("Sent: " + outputMessage);

            String receivedMessage = inputStream.readUTF();
            System.out.println("Received: " + receivedMessage);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}

