import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * Andrew Peterson
 * CSC 376
 */

public class ChatClient {
    private ServerSocket server_socket;
    private Socket sock;

    private int server_port;
    private int listening_port;

    private PrintWriter output;
    private BufferedReader input;
    private BufferedReader stdIn;
    private DataInputStream dataInput;
    private DataOutputStream dataOutput;

    private ChatClient(String listening_port, String server_port) {
        this.server_port = Integer.parseInt(server_port);
        this.listening_port = Integer.parseInt(listening_port);
    }

    private void startClient() {
        try {
            sock = new Socket("localhost", server_port);
            output = new PrintWriter(sock.getOutputStream(), true);
            input = new BufferedReader(new InputStreamReader(sock.getInputStream()));
            stdIn = new BufferedReader(new InputStreamReader(System.in));
        } catch (IOException e) {
            System.err.println("[ChatClient:startClient()]: " + e.getMessage());
        }
    }

    private void read() {
        try {
            String message;
            while ((message = input.readLine()) != null) {
                if (message.equals("f")) {
                    String file_name = input.readLine();
                    String file_port = input.readLine();
                    Thread read_files = new Thread(() -> readFiles(file_name, Integer.parseInt(file_port)));
                    read_files.start();
                } else {
                    System.out.println(message);
                }
            }
            closeClient();
        } catch (Exception e) {
            // ignore
        }
    }

    private void write() {
        try {
            // send the client's name and port to the server
            output.println(stdIn.readLine());
            output.println(listening_port);

            printMessage();

            String message;
            while ((message  = stdIn.readLine()) != null) {
                switch (message) {
                    case "m":
                        System.out.println("Enter your message:");
                        String userMessage = stdIn.readLine();
                        output.println(userMessage);
                        break;
                    case "f":
                        output.println(message);    // sends f command

                        System.out.println("Who owns the file?");
                        String file_owner = stdIn.readLine();

                        System.out.println("Which file do you want?");
                        String file_name = stdIn.readLine();

                        output.println(file_owner);
                        output.println(file_name);
                        break;
                    case "x":
                        closeClient();
                        break;
                }
                printMessage();
            }
        } catch (Exception e) {
            // ignore
        }
    }

    private void closeClient() {
        try {
            server_socket.close();
            sock.shutdownOutput();
            sock.close();
            System.exit(0);
        } catch (Exception e) {
            // ignore
        }
    }


    private void readFiles(String file_name, int client_port) {
        try {
            Socket file_socket = new Socket("localhost", client_port);
            dataInput = new DataInputStream(file_socket.getInputStream());
            dataOutput = new DataOutputStream(file_socket.getOutputStream());

            dataOutput.writeUTF(file_name);

            FileOutputStream fileOut = new FileOutputStream(file_name);

            int numRead;
            byte [] buffer = new byte[1500];
            while((numRead = dataInput.read(buffer)) != -1) {
                fileOut.write(buffer, 0, numRead);
            }
            fileOut.close();
            file_socket.close();
        } catch (Exception e){
            // ignore
        };
    }


    private void writeFiles() {
        try {
            server_socket = new ServerSocket(listening_port);
            while (true) {
                Socket file_socket = server_socket.accept();

                dataInput = new DataInputStream(file_socket.getInputStream());
                dataOutput = new DataOutputStream(file_socket.getOutputStream());

                String fileName = dataInput.readUTF();
                File file = new File(fileName);

                if(!file.exists() || !file.canRead() || file.length() == 0) {
                    file_socket.close();
                    continue;
                }

                FileInputStream fileInput = new FileInputStream(file);
                int numRead;
                byte [] buffer = new byte[1500];
                while((numRead = fileInput.read(buffer)) != -1) {
                    dataOutput.write(buffer, 0, numRead);
                }
                fileInput.close();
                file_socket.close();
            }
        } catch(Exception e) {
            // ignore
        }
    }

    private void printMessage() {
        System.out.println("Enter an option ('m', 'f', 'x'):\n" +
                "\t(M)essage (send)\n" +
                "\t(F)ile (request)\n" +
                "\te(X)it");
    }

    public static void main(String[] args) {
        if (args.length > 4) {
            System.out.println("Usage:");
            System.out.println("\tChatClient -l <listening port> -p <server port>");
        } else {
            ChatClient c = new ChatClient(args[1], args[3]);
            c.startClient();

            Thread writeFiles = new Thread(() -> c.writeFiles());
            writeFiles.start();

            Thread read = new Thread(() -> c.read());
            read.start();

            c.write();
        }
    }
}
