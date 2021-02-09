package server;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.lang.reflect.Type;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;

public class Main {
    //Stores and handles all keys and values in a HashMap (get, put, remove)
    private static final Map<Object, Object> database = new HashMap<>();
    //Create a TypeToken object in order to declare the map's type when it is parsed by Gson
    private static final Type mapType = new TypeToken<Map<String, String>>() {
    }.getType();
    private static Map<String, String> params;
    //Stores the elements of the server to response, resets everytime there is a new connection
    private static Map<String, String> response = new HashMap<>();

    public static void main(String[] args) {
        connect();
    }

    public static void connect() {
        String address = "127.0.0.1";
        int port = 23456;
        try (ServerSocket serverSocket = new ServerSocket(port, 50, InetAddress.getByName(address))) {
            System.out.println("Server started!");
            //Loops until the server is closed
            while (!serverSocket.isClosed()) {
                try (Socket socket = serverSocket.accept();
                     DataInputStream inputStream = new DataInputStream(socket.getInputStream());
                     DataOutputStream outputStream = new DataOutputStream(socket.getOutputStream())
                ) {
                    //reads the incoming message
                    String incomingJsonString = inputStream.readUTF();
                    Gson gson = new Gson();
                    //Parse the JSON message into our params Map
                    params = gson.fromJson(incomingJsonString, mapType);
                    //outputs the result after using the run method
                    response = new HashMap<>(); //clears the response hashmap to build a new one
                    run(params.get("type"));
                    String outgoingGsonString = gson.toJson(response);
                    outputStream.writeUTF(outgoingGsonString);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //Takes a command from the arguments and builds a response
    public static void run(Object command) {
        switch ((String) command) {
            case "set": {
                database.put(params.get("key"), params.get("value"));
                response.put("response", "OK");
                return;
            }
            case "get": {
                if (!database.containsKey(params.get("key"))) {
                    response.put("response", "ERROR");
                    response.put("reason", "No such key");
                } else {
                    response.put("response", "OK");
                    response.put("value", (String) database.get(params.get("key")));
                }
                return;
            }

            case "delete": {
                if (!database.containsKey(params.get("key"))) {
                    response.put("response", "ERROR");
                    response.put("reason", "No such key");
                } else {
                    database.remove(params.get("key"));
                    response.put("response", "OK");
                }
                return;
            }
            case "exit":
                System.exit(0);
            default:
                response.put("response", "ERROR");
        }

    }

//    public static boolean indexIsInvalid(int index) {
//        return index < 1 || index > 1000;
//    }

}


