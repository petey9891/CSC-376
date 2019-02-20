import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;

class Messenger {
    private int port;
    private Socket sock;

    Messenger(int port) {
        this.port = port;
    }

    void close() {
        try {
            sock.close();
        } catch (IOException e) {
            // ignore
        }
    }

//    BufferedReader getInputStream() throws IOException {
//        return new BufferedReader(new InputStreamReader(sock.getInputStream()));
//    }

    DataInputStream getInputStream() throws IOException {
        return new DataInputStream(sock.getInputStream());
    }

    DataOutputStream getOutputStream() throws IOException {
        return new DataOutputStream(sock.getOutputStream());
    }
//    PrintWriter getOutputStream() throws IOException {
//        return new PrintWriter(sock.getOutputStream(), true);
//    }

    void startServer() {
        try {
            ServerSocket server_socket = new ServerSocket(port);
            sock = server_socket.accept();
            server_socket.close();
        } catch (Exception e) {
            System.err.println("Failed to startup server: " + e.getMessage());
            System.exit(1);
        }
    }

    void startClient() {
        try {
            sock = new Socket("localhost", port);
        } catch (Exception e) {
            System.err.println("Failed to startup client: " + e.getMessage());
            System.exit(1);
        }
    }

    void printMessage() {
        System.out.println("Enter an option ('m', 'f', 'x'):\n" +
                "\t(M)essage (send)\n" +
                "\t(F)ile (request)\n" +
                "\te(X)it");
    }

//    void serviceRequest(String fileName) throws IOException {
//        File file = new File(fileName);
//        PrintWriter output = getOutputStream();
//        if (!file.exists() || !file.canRead()) {
//            return;
//        }
//        BufferedReader reader = new BufferedReader(new FileReader(file));
//        String line;
//        while ((line = reader.readLine()) != null) {
//            System.out.println(line);
//            output.println(line);
//        }
//        reader.close();
//    }
//
//    void writeFile(String fileName) throws IOException {
//        BufferedReader input = getInputStream();
//        PrintWriter file = new PrintWriter(new BufferedWriter( new FileWriter(fileName)));
//        String line;
//        while ((line = input.readLine()) != null) {
//            file.println(line);
//        }
//    }
}
