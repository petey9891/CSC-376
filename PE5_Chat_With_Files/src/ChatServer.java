import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map.Entry;

/**
 * Andrew Peterson
 * CSC 376
 */

public class ChatServer {
    private int port;

    private HashMap<Socket, PrintWriter> connectedSockets;
    private HashMap<String, Integer> portMap;

    private ChatServer(int port) {
        this.port = port;
        this.connectedSockets = new HashMap<>();
        this.portMap = new HashMap<>();
    }

    private void startServer() throws Exception {
        if (port == -1) throw new Exception("Port is not defined");
        try {
            ServerSocket server_socket = new ServerSocket(port);
            while(true) {
                Socket client_socket = server_socket.accept();
                PrintWriter output = new PrintWriter(client_socket.getOutputStream(), true);
                connectedSockets.put(client_socket, output);
                Thread read = new Thread(() -> read(client_socket));
                read.start();
            }
        } catch (IOException e) {
            System.err.println("[ChatServer:startServer()]: " + e.getMessage());
        }
    }

    private void read(Socket client_socket) {
        try {
            BufferedReader input = new BufferedReader(new InputStreamReader(client_socket.getInputStream()));

            connectedSockets.get(client_socket).println("Please enter your name: ");
            String name = input.readLine();
            int port = Integer.parseInt(input.readLine());
            portMap.put(name, port);

            String message;
            while ((message = input.readLine()) != null) {
                if (message.equals("f")) {
                    String file_owner = input.readLine();
                    String file_name = input.readLine();
                    connectedSockets.get(client_socket).println(message);
                    connectedSockets.get(client_socket).println(file_name);
                    connectedSockets.get(client_socket).println(portMap.get(file_owner));
                } else {
                    sendToAll(client_socket, name, message);
                }
            }
            portMap.remove(name);
            connectedSockets.remove(client_socket);
        } catch (IOException e) {
            // ignore
        } catch (Exception e) {
            // ignore
        }
    }

    private void sendToAll(Socket client_socket, String name, String message) {
        for (Entry<Socket, PrintWriter> entry : connectedSockets.entrySet()) {
            if (!(entry.getKey() == client_socket)) {
                entry.getValue().println(name + ": " + message);
            }
        }
    }

    public static void main(String[] args) {
        if (args.length != 1) {
            System.out.println("Usage:");
            System.out.println("\tChatServer <port number>");
        } else {
            try {
                ChatServer s = new ChatServer(Integer.parseInt(args[0]));
                s.startServer();
            } catch (Exception e) {
                System.err.println("[ChatServer:main()]: " + e.getMessage());
                System.exit(1);
            }
        }
    }
}
