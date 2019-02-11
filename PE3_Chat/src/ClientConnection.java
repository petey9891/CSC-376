import java.io.*;
import java.net.Socket;

/**
 * Andrew Peterson
 * CSC 376
 */

public class ClientConnection {
    private String name;
    private Socket sock;

    ClientConnection(String name, Socket sock) {
        this.name = name;
        this.sock = sock;
    }

    String getName() {
        return name;
    }

    void write(String message) throws IOException {
        PrintWriter output = getOutputStream();
        output.println(message);
    }

    BufferedReader getInputStream() throws  IOException {
        return new BufferedReader(new InputStreamReader(sock.getInputStream()));
    }

    private PrintWriter getOutputStream() throws IOException {
        return new PrintWriter(sock.getOutputStream(), true);
    }
}
