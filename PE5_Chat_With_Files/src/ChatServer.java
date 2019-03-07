import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;

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
                newClientConnection(client_socket).start();
//                PrintWriter output = new PrintWriter(client_socket.getOutputStream(), true);
//                socketMap.put(client_socket, output);

//                Thread read = new Thread(() -> read(client_socket));
//                read.start();
//                newClientConnection(client_socket).start();
            }
        } catch (IOException e) {
            System.err.println("[ChatServer:startServer()]: " + e.getMessage());
        }
    }

    private Thread newClientConnection(Socket client_socket) {
        return new Thread(() -> {
           ClientConnection cc = new ClientConnection(client_socket);
           clientConnections.add(cc);
           Thread read = new Thread(() -> read(cc));
           read.start();
        });
    }

//    private void read(Socket client_socket) {
    private void read(ClientConnection client) {
        try {
//            BufferedReader input = new BufferedReader(new InputStreamReader(client_socket.getInputStream()));
            BufferedReader input = client.getInputStream();
            String message;

//            socketMap.get(client_socket).println("Please enter your name: ");
            client.write("Please enter your name: ");
            String name = input.readLine();
            int port = Integer.parseInt(input.readLine());

            client.setName(name);
            portMap.put(name, port);

            while ((message = input.readLine()) != null) {
                switch (message) {
                    case "m":
                        sendToAll(client.getName(), input.readLine(), message);
                        break;
                    case "f":
                        String file_owner = input.readLine();
                        String file_name = input.readLine();
                        if (portMap.get(file_owner) != null) {
                            client.write(message);
                            client.write(file_name);
                            client.write(portMap.get(file_owner).toString());
//                            socketMap.get(client).println(message);
//                            socketMap.get(client).println(file_name);
//                            socketMap.get(client).println(portMap.get(file_owner));
                        }
                        break;
                }
            }
//            portMap.remove(name);
//            socketMap.remove(client_socket);
        } catch (IOException e) {
            // ignore
        } catch (Exception e) {
            // ignore
        }
//        try {
//            BufferedReader input = client.getInputStream();
//            String message;
//            while ((message = input.readLine()) != null) {
//                switch (message) {
//                    case "m":
//                        sendToAll(client.getName(), input.readLine(), message);
//                        break;
//                    case "f":
//                        String fileOwner = input.readLine();
//                        String fileName = input.readLine();
//                        for (ClientConnection c : clientConnections) {
//                            if (c.getName().equals(fileOwner)) {
//                                c.write(message);
//                                c.write(fileName + ":" + fileOwner + ":" + client.getListeningPort());
//                            }
//                        }
//                        break;
//                }
//            }
//        } catch (IOException e) {};
    }

//    private Thread newClientConnection(Socket client_socket) {
//        return new Thread(() -> {
//            try {
//                BufferedReader input = new BufferedReader(new InputStreamReader(client_socket.getInputStream()));
//                String[] nameAndPort = input.readLine().split(":");
//                ClientConnection client_connection = new ClientConnection(nameAndPort[0], nameAndPort[1], client_socket);
//                clientConnections.add(client_connection);
//                Thread read = new Thread(() -> read(client_connection));
//                read.start();
//            } catch (IOException e) {
//                // ignore
//            }
//        });
//    }

//    private void read(ClientConnection client) {
//        try {
//            BufferedReader input = client.getInputStream();
//            String message;
//            while ((message = input.readLine()) != null) {
//                switch (message) {
//                    case "m":
//                        sendToAll(client.getName(), input.readLine(), message);
//                        break;
//                    case "f":
//                        String fileOwner = input.readLine();
//                        String fileName = input.readLine();
//                        for (ClientConnection c : clientConnections) {
//                            if (c.getName().equals(fileOwner)) {
//                                c.write(message);
//                                c.write(fileName + ":" + fileOwner + ":" + client.getListeningPort());
//                            }
//                        }
//                        break;
//                }
//            }
//        } catch (IOException e) {};
//    }
//
    private void sendToAll(String name, String message, String command) throws Exception {
        for (ClientConnection c : clientConnections) {
            if (!c.getName().equals(name)) {
                try {
                    c.write(command);
                    c.write(name + ": " + message);
                } catch (IOException e) {}
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
