package client;

import com.beust.jcommander.JCommander;
import com.beust.jcommander.Parameter;
import com.google.gson.Gson;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.net.InetAddress;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;

public class Main {
    //Using JCommander to declare and parse arguments
    @Parameter(names = "-t", description = "type of request")
    private static String type;

    @Parameter(names = "-k", description = "key of data")
    private static String key;

    @Parameter(names = "-v", description = "value to save")
    private static String value = null;

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
            //Creates a map with the parameters parsed by JCommander
            Map<String, String> params = new HashMap<>();
            params.put("type", type);
            params.put("key", key);
            if (value != null) {
                params.put("value", value);
            }
            //Create Gson object to send parameters as Json
            Gson gson = new Gson();
            String gsonString = gson.toJson(params);
            //Sends the message to server in JSON format
            outputStream.writeUTF(gsonString);
            System.out.println("Sent: " + gsonString);
            //Receives the server message in JSON format
            String receivedMessage = inputStream.readUTF();
            System.out.println("Received: " + receivedMessage);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}

