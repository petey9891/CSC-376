import java.io.*;
import java.net.Socket;

class Client implements Messenger{
    private Socket sock;
    private String serverAddress;
    private int port;

    Client(String serverAddress, int port) {
        this.serverAddress = serverAddress;
        this.port = port;
    }

    public void startClient() {
        try {
            sock = new Socket(serverAddress, port);
        } catch (Exception e) {
            System.out.println("startClient() exception " + e.getMessage());
            System.exit(1);
        }
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

    public BufferedReader getInputStream() throws IOException {
        return new BufferedReader(new InputStreamReader(sock.getInputStream()));
    }

    public PrintWriter getOutputStream() throws IOException {
        return new PrintWriter(sock.getOutputStream(), true);
    }
}
