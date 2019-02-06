import java.io.*;
import java.net.Socket;

public class ChatClient {
    private Socket sock;
    private String serverAddress;
    private int port;

    private ChatClient(String serverAdress, int port) {
        this.serverAddress = serverAdress;
        this.port = port;
    }

    private void startClient() {
        try {
            sock = new Socket(serverAddress, port);
        } catch (Exception e) {
            System.out.println("startClient() exception " + e.getMessage());
        }
    }

    private void close() {
        try {
            sock.close();
        } catch (IOException e) { e.printStackTrace();};
    }

    private BufferedReader getInputStream() throws IOException {
        return new BufferedReader(new InputStreamReader(sock.getInputStream()));
    }

    private PrintWriter getOutputStream() throws IOException {
        return new PrintWriter(sock.getOutputStream(), true);
    }

    public static void main(String[] args) {
        if (args.length != 1) {
            System.out.println("Usage:");
            System.out.println("\tChatClient <port number>");
        }
        ChatClient c = new ChatClient("localhost", Integer.parseInt(args[0]));
        c.startClient();

        Thread read = new Thread(() -> {
            try {
                BufferedReader input = c.getInputStream();

                String message;
                while ((message = input.readLine()) != null) {
                    System.out.println(message);
                }
                input.close();
            } catch (IOException e) {
                // other side closed
            } finally {
                c.close();
                System.exit(0);
            }
        });

        read.start();

        try {
            PrintWriter output = c.getOutputStream();
                String message;
                BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));

                while ((message = reader.readLine()) != null) {
                    output.println(message);
                }
                reader.close();
                output.close();

        } catch(IOException e) {}
        finally {
            c.close();
            System.exit(0);
        }
    }
}
