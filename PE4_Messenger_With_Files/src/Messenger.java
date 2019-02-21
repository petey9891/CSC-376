import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;

class Messenger {
    private int port;
    boolean isServer;
    private Socket sock;
    private Socket fileSock;
    private ServerSocket server_socket;

    Messenger(int port, boolean isServer) {
        this.port = port;
        this.isServer = isServer;
    }

    void startServer() {
        try {
            ServerSocket file_server_socket = new ServerSocket(8083);
            Thread accept = new Thread(() -> {
                try {
                    while (true) {
                        fileSock = file_server_socket.accept();
                    }
                } catch (Exception e) {
                    System.err.println("Failed to startup server - file server: " + e.getMessage());
                    System.exit(1);
                }
            });
            accept.start();

            server_socket = new ServerSocket(port);
            sock = server_socket.accept();

            server_socket.close();
        } catch (Exception e) {
            System.err.println("Failed to startup server: " + e.getMessage());
            System.exit(1);
        }
    }

    void startClient() {
        try {
            ServerSocket file_server_socket = new ServerSocket(8082);
            Thread accept = new Thread(() -> {
                try {
                    while (true) {
                        fileSock = file_server_socket.accept();
                    }
                } catch (Exception e) {
                    System.err.println("Failed to startup client - file server: " + e.getMessage());
                    System.exit(1);
                }
            });
            accept.start();
            sock = new Socket("localhost", port);

        } catch (Exception e) {
            System.err.println("Failed to startup client: " + e.getMessage());
            System.exit(1);
        }
    }

    void connectToFileServer() {
        try {
            if (isServer) {
                System.err.println("is indeed a server");
                fileSock = new Socket("localhost", 8082);
            } else {
                System.err.println("not a server");
                fileSock = new Socket("localhost", 8083);
            }
        } catch (Exception e) {
            System.err.println(("Failed to connect ot file server: " + e.getMessage()));
            System.exit(1);
        }
    }

    void closeFileSocket() throws IOException {
        fileSock.close();
    }

    void close() throws IOException {
        sock.close();
    }


    BufferedReader getInputStream() throws IOException {
        return new BufferedReader(new InputStreamReader(sock.getInputStream()));
    }

    PrintWriter getOutputStream() throws IOException {
        return new PrintWriter(sock.getOutputStream(), true);
    }

    DataInputStream getFileInputStream() throws IOException {
        return new DataInputStream(fileSock.getInputStream());
    }

    DataOutputStream getFileOutputStream() throws IOException {
        return new DataOutputStream(fileSock.getOutputStream());
    }

    void serviceRequest() throws IOException {
        connectToFileServer();
        DataInputStream input = getFileInputStream();
        DataOutputStream output = getFileOutputStream();

        String file_name = input.readUTF();

        File file = new File( file_name );
        if (!file.exists() || !file.canRead()) {
            closeFileSocket();
            return;
        }

        FileInputStream file_input = new FileInputStream(file);

        byte[] file_buffer = new byte[1500];
        int number_read;
        while( (number_read = file_input.read( file_buffer )) != -1 )
            output.write( file_buffer, 0, number_read );
        closeFileSocket();
    }

    void getFile(String file_name) throws IOException {
            DataInputStream input = getFileInputStream();
            DataOutputStream output = getFileOutputStream();
            output.writeUTF( file_name );
            FileOutputStream file_out = new FileOutputStream( file_name );

            byte[] buffer = new byte[1500];
            int number_read;
            while( (number_read = input.read( buffer)) != -1 )
                file_out.write( buffer, 0, number_read );
            file_out.close();
    }

    void printMessage() {
        System.out.println("Enter an option ('m', 'f', 'x'):\n" +
                "\t(M)essage (send)\n" +
                "\t(F)ile (request)\n" +
                "\te(X)it");
    }
}
