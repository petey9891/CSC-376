import java.io.*;
import java.net.Socket;

/**
 * Andrew Peterson
 * CSC 376
 */

class ClientConnection {
    private Socket sock;
    private String name;

    ClientConnection(Socket sock) {
        this.sock = sock;
    }

    void write(String message) throws IOException {
        PrintWriter output = getOutputStream();
        output.println(message);
    }

    String getName() throws Exception {
        if (name == null) throw new Exception("Name not provided");
        return this.name;
    }

    void setName(String name) {
        this.name = name;
    }

    BufferedReader getInputStream() throws  IOException {
        return new BufferedReader(new InputStreamReader(sock.getInputStream()));
    }

    private PrintWriter getOutputStream() throws IOException {
        return new PrintWriter(sock.getOutputStream(), true);
    }
}