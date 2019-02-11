import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;

/**
 * Andrew Peterson
 * CSC 376
 */

public class ChatServer {
    private ServerSocket serverSocket;
    private ArrayList<ClientConnection> clientConnections;
    private int port;

    private ChatServer(int port) {
        this.port = port;
        this.clientConnections = new ArrayList<>();
    }

    private void startServer() {
        try {
            serverSocket = new ServerSocket(port);
            Thread accept = new Thread(() -> {
                try {
                    while (true) {
                        Socket sock = serverSocket.accept();
                        createNewClientConnection(sock).start();
                    }
                } catch (IOException e) {}
            });
            accept.start();
        } catch (IOException e) {
            System.out.println("startServer() exception " + e.getMessage());
        };
    }

    private Thread createNewClientConnection(Socket sock) {
        return new Thread(() -> {
            try {
                BufferedReader input = new BufferedReader(new InputStreamReader(sock.getInputStream()));
                ClientConnection cc = new ClientConnection(input.readLine(), sock);
                clientConnections.add(cc);
                Thread read = new Thread(() -> read(cc));
                read.start();
            } catch (IOException e) {};
        });
    }

    private void read(ClientConnection client) {
        try {
            BufferedReader input = client.getInputStream();
            String message;
            while ((message = input.readLine()) != null) {
                sendToAll(client.getName(), message);
            }
        } catch (IOException e) {};
    }

    private void sendToAll(String name, String message) {
        for (ClientConnection c : clientConnections) {
            if (!c.getName().equals(name)) {
                try {
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
                System.out.println("ChatServer error " + e.getMessage());
                System.exit(1);
            }
        }
    }
}
