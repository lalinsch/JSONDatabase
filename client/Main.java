package client;

import com.beust.jcommander.JCommander;
import com.beust.jcommander.Parameter;
import com.google.gson.Gson;

import java.io.*;
import java.net.InetAddress;
import java.net.Socket;
import java.util.LinkedHashMap;
import java.util.Map;

public class Main {
    //Using JCommander to declare and parse arguments
    @Parameter(names = "-t", description = "type of request")
    private static String type;

    @Parameter(names = "-in", description = "JSON file name")
    private static String fileName;

    @Parameter(names = "-k", description = "key of data")
    private static String key;

    @Parameter(names = "-v", description = "value to save")
    private static String value = null;

    private static final String ADDRESS = "127.0.0.1";
    private static final int PORT = 23456;
    private static Map<String, String> params = new LinkedHashMap<>();


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

        try (Socket socket = new Socket(InetAddress.getByName(ADDRESS), PORT);
             DataInputStream inputStream = new DataInputStream(socket.getInputStream());
             DataOutputStream outputStream = new DataOutputStream(socket.getOutputStream())
        ) {

            System.out.println("Client started!");
            String outgoingMessage = null;
            //If there's a file specified in args it reads it to parse the JSON arguments
            if (fileName != null) {
                String filePath = "src/client/data/" + fileName;
                try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
                    String line;
                    StringBuilder jsonParam = new StringBuilder();
                    while ((line = br.readLine()) != null) {
                        jsonParam.append(line);
                        jsonParam.append("\n");
                    }
                    outgoingMessage = jsonParam.toString();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            } else {
                params.put("type", type);
                params.put("key", key);
                if (value != null) {
                    params.put("value", value);
                }
                //Use Gson object to send parameters as Json
                Gson gson = new Gson();
                outgoingMessage = gson.toJson(params);
            }
            //Sends the message to server in JSON format
            assert outgoingMessage != null;
            outputStream.writeUTF(outgoingMessage);
            System.out.println("Sent: " + outgoingMessage);
            //Receives the server message in JSON format
            String receivedMessage = inputStream.readUTF();
            System.out.println("Received: " + receivedMessage);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}

