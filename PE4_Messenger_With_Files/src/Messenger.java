import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class Messenger {
    private boolean isServer;
    private int port;
    String server_address;
    Socket sock;

    Messenger(boolean isServer, int port) {
        this.isServer = isServer;
        this.port = port;
        server_address = "localhost";
    }

    public void close() throws IOException {
        sock.close();
    }

    public DataInputStream getInputStream() throws IOException {
        return new DataInputStream(sock.getInputStream());
    }

    public DataOutputStream getOutputStream() throws IOException {
        return new DataOutputStream(sock.getOutputStream());
    }

    public void startServer() {
        try {
            ServerSocket server_socket = new ServerSocket(port);
            sock = server_socket.accept();
            server_socket.close();
        } catch (Exception e) {
            System.err.println("Failed to startup server: " + e.getMessage());
            System.exit(1);
        }
    }

    public void startClient() {
        try {
            sock = new Socket(server_address, port);
        } catch (Exception e) {
            System.err.println("Failed to startup client: " + e.getMessage());
            System.exit(1);
        }
    }
}
