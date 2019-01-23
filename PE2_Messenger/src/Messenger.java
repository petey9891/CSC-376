import java.io.InputStreamReader;
import java.io.BufferedReader;
import java.net.ServerSocket;
import java.net.Socket;
import java.io.DataOutputStream;
import java.io.DataInputStream;
import java.io.EOFException;
import java.util.Scanner;

public class Messenger {
    public static void main(String[] args) {
        if (args.length > 3) {
            System.out.println("Usage:");
            System.out.println("\tServer -l <port number> \n\t Client <port number> [<server address>]");
            return;
        } else {
            if (args[0].equals("-l")) {
                // run server
            } else {
                // run client
            }
        }

    }

    private static void server(int port) {
        try {
            ServerSocket server_socket = new ServerSocket(port);
            Socket client_socket = server_socket.accept();

            DataOutputStream output = new DataOutputStream(client_socket.getOutputStream());
            DataInputStream input = new DataInputStream(client_socket.getInputStream());

            BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
            String clientMessage = "";
            String serverMessage = "";
            while ((clientMessage = input.readUTF()) != null || (serverMessage = reader.readLine()) != null) {
                if (clientMessage != null)
                    System.out.println(clientMessage);
                else
                    output.writeUTF(serverMessage);
            }

////            while (input.available() > 0) {
////                System.out.println()
////            }
//
//            String clientMessage = input.readUTF();
//            System.out.println(clientMessage);
//            Scanner in = new Scanner(System.in);
//            while (in.hasNextLine()) {
//                output.writeUTF(in.nextLine());
//            }

        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    private static void client(int port, String server) {
        try {
            Socket client_socket = new Socket(server, port );
            DataOutputStream output = new DataOutputStream(client_socket.getOutputStream());
            BufferedReader input = new BufferedReader(new InputStreamReader(client_socket.getInputStream()));

            BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));

            String clientMessage = "";
            while ((clientMessage = reader.readLine()) != null && clientMessage.length() > 0) {
                output.writeUTF(clientMessage);
            }
//
//            while (input.available() > 0) {
//                System.out.println(input.readUTF());
//            }
//
//            String serverMessage = input.readUTF();
//            System.out.println(serverMessage);

            client_socket.close();

        } catch (EOFException eofx) {
            System.out.println("EOF encountered; other side shut down");
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }
}


//    boolean runServer = false;
//    int port = -1;
//    String server = "localhost";
//
//            for (String arg : args) {
//                    if (arg.equals("-l"))
//                    runServer = true;
//                    if (arg.matches("(\\d{4})"))
//                    port = Integer.parseInt(arg);
//                    if (arg.matches("(\\d{1,3}[.]\\d{1,3}[.]\\d{1,3}[.]\\d{1,3})"))
//                    server = arg;
//                    }