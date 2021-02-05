package server;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;

public class Main {

    private static final String[] database = new String[1000];

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
                    String command = inputStream.readUTF();
                    //outputs the result after using the run method
                    outputStream.writeUTF(run(command));
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //Takes a command from the arguments and outputs a result
    public static String run(String command) {

        String[] commandArray = command.split(" ");
        switch (commandArray[0]) {
            case "set": {
                int index = Integer.parseInt(commandArray[1]);
                if (indexIsInvalid(index)) {
                    return "ERROR";
                } else {
                    index -= 1;
                    String textInput = "";
                    for (int i = 2; i < commandArray.length; i++) {
                        textInput = textInput.concat(commandArray[i] + " ");
                    }
                    database[index] = textInput;
                    return "OK";
                }
            }
            case "get": {
                int index = Integer.parseInt(commandArray[1]);
                if (indexIsInvalid(index)) {
                    return "ERROR";
                } else {
                    index -= 1;
                    if (database[index] == null || database[index].isEmpty()) {
                        return "ERROR";
                    } else {
                        return database[index];
                    }
                }
            }
            case "delete": {
                int index = Integer.parseInt(commandArray[1]);
                if (indexIsInvalid(index)) {
                    return "ERROR";
                } else {
                    index -= 1;
                    database[index] = "";
                    return "OK";
                }
            }
            case "exit":
                System.exit(0);
            default:
                return "ERROR";
        }

    }

    public static boolean indexIsInvalid(int index) {
        return index < 1 || index > 1000;
    }

}


