import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.InputStreamReader;

public class ChatServer {
    public static void main(String[] args) {
        if (args.length != 1) {
            System.out.println("Usage:");
            System.out.println("\tChatServer <port number>");
        } else {
            try {
                Server s = new Server(Integer.parseInt(args[0]));
                s.startServer();

            } catch (Exception e) {
                System.out.println("ChatServer error " + e.getMessage());
                System.exit(1);
            }
        }
    }

    // 0:35:14
}
