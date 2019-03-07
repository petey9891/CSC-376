import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Andrew Peterson
 * CSC 376
 */
public class ChatServer {
    private int port;
    private ServerSocket server_socket;
    private ArrayList<ClientConnection> clientConnections;

    private HashMap<Socket, PrintWriter> socketMap;
    private HashMap<String, Integer> portMap;

    private ChatServer(int port) {
        this.port = port;
        this.clientConnections = new ArrayList<>();

        this.socketMap = new HashMap<>();
        this.portMap = new HashMap<>();
    }

    private void startServer() throws Exception {
        if (port == -1) throw new Exception("Port is not defined");
        try {
            server_socket = new ServerSocket(port);
            while(true) {
                Socket client_socket = server_socket.accept();
                PrintWriter output = new PrintWriter(client_socket.getOutputStream(), true);
                socketMap.put(client_socket, output);

                Thread read = new Thread(() -> read(client_socket));
                read.start();
//                newClientConnection(client_socket).start();
            }
        } catch (IOException e) {
            System.err.println("[ChatServer:startServer()]: " + e.getMessage());
        }
    }

    private void read(Socket client_socket) {
        try {
            BufferedReader input = new BufferedReader(new InputStreamReader(client_socket.getInputStream()));
            String message;

            socketMap.get(client_socket).println("Please enter your name: ");
            String name = input.readLine();
            int port = Integer.parseInt(input.readLine());
            portMap.put(name, port);

            while ((message = input.readLine()) != null) {
                switch (message) {
                    case "m":
                        for (Map.Entry<Socket, PrintWriter> entry : socketMap.entrySet()) {
                            if (entry.getKey() != client_socket) {
                                entry.getValue().println(message);
                                entry.getValue().println(name + ": " + input.readLine());
                            }
                        }
                        break;
                    case "f":
                        String file_owner = input.readLine();
                        String file_name = input.readLine();
                        if (portMap.get(file_owner) != null) {
                            socketMap.get(client_socket).println(message);
                            socketMap.get(client_socket).println(file_name);
                            socketMap.get(client_socket).println(portMap.get(file_owner));
                        }
                        break;
                }
            }
            portMap.remove(name);
            socketMap.remove(client_socket);
        } catch (IOException e) {
            // ignore
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
