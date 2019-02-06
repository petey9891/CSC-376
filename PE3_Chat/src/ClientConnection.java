import java.io.*;
import java.net.Socket;

public class ClientConnection implements Messenger {
    private String name;
    private Socket sock;

    ClientConnection(String name, Socket sock) {
        this.name = name;
        this.sock = sock;
    }

    public String getName() {
        return name;
    }

    public void write(String message) throws IOException {
        PrintWriter output = getOutputStream();
//        try {
            output.println(message);
//            output.writeUTF(message);
//
//        } catch (IOException e) {
//            close();
//        }
    }

    public void close() throws IOException {
        sock.close();
    }

//    public DataInputStream getInputStream() throws IOException {
//        return new DataInputStream(sock.getInputStream());
//    }



//    public DataOutputStream getOutputStream() throws IOException {
//        return new DataOutputStream(sock.getOutputStream());
//    }

    public BufferedReader getInputStream() throws  IOException {
        return new BufferedReader(new InputStreamReader(sock.getInputStream()));
    }

    public PrintWriter getOutputStream() throws IOException {
        return new PrintWriter(sock.getOutputStream(), true);
    }
}
