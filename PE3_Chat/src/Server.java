import java.io.*;
//import java.io.DataOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;

class Server {
    private ServerSocket server_socket;
    private ArrayList<ClientConnection> clientConnections;
    private Socket sock;
    private int port;

    Server(int port) {
        this.port = port;
        this.clientConnections = new ArrayList<>();
    }

    public void startServer() {
        try {
            server_socket = new ServerSocket(port);
            Thread accept = new Thread(() -> {
                try {
                    while (true) {
                        sock = server_socket.accept();
                        Thread addConnection = new Thread(() -> {
                            try {
//                                DataInputStream input = new DataInputStream(sock.getInputStream());
                                BufferedReader input = new BufferedReader(new InputStreamReader(sock.getInputStream()));
//                                DataOutputStream output = new DataOutputStream(sock.getOutputStream());
//                                output.writeUTF("What is your name?");
                                ClientConnection cc = new ClientConnection(input.readLine(), sock);

//                                ClientConnection cc = new ClientConnection(input.readUTF(), sock);
                                clientConnections.add(cc);
                                Thread read = new Thread(() -> read(cc));
                                read.start();
                            } catch (IOException e) {
//                                System.out.println("Server adding client error: " + e.getMessage());
                            }
                        });
                        addConnection.start();
                    }
                } catch (IOException e) {
//                    System.out.println("Server accepting error: " + e.getMessage());
                };
            });
            accept.start();
        } catch (Exception e) {
            System.out.println("startServer() exception " + e.getMessage());
            System.exit(1);
        }
    }

    public void read(ClientConnection client) {
        try {
//            DataInputStream input = client.getInputStream();
            BufferedReader input = client.getInputStream();

            while (true) {
                try {
//                    sendToAll(client.getName(), input.readUTF());
                    sendToAll(client.getName(), input.readLine());

                } catch (EOFException eof) {
                    input.close();
                    client.close();
                }
            }
        } catch (IOException e) {
//            System.out.println("Server read error: " + e.getMessage());
        };
    }

    public void sendToAll(String name, String message) {
        for (ClientConnection c : clientConnections) {
            if (!c.getName().equals(name)) {
                try {
                    c.write(name + ": " + message);
                } catch (IOException e) {
//                    System.out.println("Server write error: " + e.getMessage());
                };
            }
        }
    }
}
