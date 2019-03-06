import java.io.*;
import java.net.Socket;

/**
 * Andrew Peterson
 * CSC 376
 */

class ClientConnection {
    private String name;
    private int listening_port;
    private Socket sock;

    ClientConnection(String name, String port, Socket sock) {
        this.name = name;
        this.listening_port = Integer.parseInt(port);
        this.sock = sock;
    }

    public String getName() { return name; }

    public int getListeningPort() { return listening_port; }

    void write(String message) throws IOException {
        PrintWriter output = getOutputStream();
        output.println(message);
    }

    void writeUtf(String message) throws IOException {
        DataOutputStream output = getDataOutputStream();
        output.writeUTF(message);
    }

    BufferedReader getInputStream() throws  IOException {
        return new BufferedReader(new InputStreamReader(sock.getInputStream()));
    }

    private PrintWriter getOutputStream() throws IOException {
        return new PrintWriter(sock.getOutputStream(), true);
    }

    private DataOutputStream getDataOutputStream() throws IOException {
        return new DataOutputStream(sock.getOutputStream());

    }
}