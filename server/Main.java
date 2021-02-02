package server;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;

public class Main {

    public static void main(String[] args) {
        String address = "127.0.0.1";
        int port = 23456;
        try (ServerSocket serverSocket = new ServerSocket(port, 50, InetAddress.getByName(address))) {
            System.out.println("Server started!");
            try (Socket socket = serverSocket.accept();
                 DataInputStream inputStream = new DataInputStream(socket.getInputStream());
                 DataOutputStream outputStream = new DataOutputStream(socket.getOutputStream())
            ) {
                String incomingMessage = inputStream.readUTF();
                System.out.println("Received: " + incomingMessage);

                String outgoingMessage = String.format("A record # %s was sent!", incomingMessage.split("\\s+#\\s+")[1]);
                outputStream.writeUTF(outgoingMessage);
                System.out.println("Sent: " + outgoingMessage);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

        String[] text = new String[100];
        label:
        while (true) {
            String[] command = scanner.nextLine().split(" ");
            switch (command[0]) {
                case "set": {
                    int index = Integer.parseInt(command[1]);
                    if (indexIsInvalid(index)) {
                        System.out.println("ERROR");
                    } else {
                        index -= 1;
                        String textInput = "";
                        for (int i = 2; i < command.length; i++) {
                            textInput = textInput.concat(command[i] + " ");
                        }
                        text[index] = textInput;
                        System.out.println("OK");
                    }
                    break;
                }
                case "get": {
                    int index = Integer.parseInt(command[1]);
                    if (indexIsInvalid(index)) {
                        System.out.println("ERROR");
                    } else {
                        index -= 1;
                        if (text[index] == null || text[index].isEmpty()) {
                            System.out.println("ERROR");
                        } else {
                            System.out.println(text[index]);
                        }
                    }
                    break;
                }
                case "delete": {
                    int index = Integer.parseInt(command[1]);
                    if (indexIsInvalid(index)) {
                        System.out.println("ERROR");
                    } else {
                        index -= 1;
                        text[index] = "";
                        System.out.println("OK");
                    }
                    break;
                }
                case "exit":
                    break label;
                default:
                    System.out.println("ERROR");
            }
        }
    }

    public static boolean indexIsInvalid(int index) {
        return index < 1 || index > 100;
    }
}
