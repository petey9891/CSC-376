import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;

public class ChatClient {
    private ServerSocket server_socket;
    private Socket sock;
    private int server_port;
    private int listening_port;

    private PrintWriter output;
    private BufferedReader input;
    private BufferedReader stdIn;

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

    void read() {
        try {
            String message;
            while ((message = input.readLine()) != null) {
                switch(message) {
                    case "m":
                        String userMessage = input.readLine();
                        System.out.println(userMessage);
                        break;
                    case "f":
                        String file_name = input.readLine();
                        String file_port = input.readLine();
                        Thread read_files = new Thread(() -> readFiles(file_name, Integer.parseInt(file_port)));
                        read_files.start();
                        break;
                    case "x":
                        closeClient();
                        break;
                }
            }
        } catch (Exception e) {
            // ignore
        }
    }

    void write() {
        try {
            System.out.println("Please enter your name: ");
            String user_name = stdIn.readLine();
            output.println(user_name);
            output.println(listening_port);
            printMessage();
            String message;
            while ((message  = stdIn.readLine()) != null) {
                output.println(message);
                switch (message) {
                    case "m":
                        System.out.println("Enter your message:");
                        String userMessage = stdIn.readLine();
                        output.println(userMessage);
                        printMessage();
                        break;
                    case "f":
                        System.out.println("Who owns the file?");
                        String file_owner = stdIn.readLine();
                        output.println(file_owner);
                        System.out.println("Which file do you want?");
                        String file_name = stdIn.readLine();
                        output.println(file_name);
                        printMessage();
                    break;
                    case "x":
                        closeClient();
                        break;
                }
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
            DataInputStream input = new DataInputStream(file_socket.getInputStream());
            DataOutputStream output = new DataOutputStream(file_socket.getOutputStream());

            output.writeUTF(file_name);

            FileOutputStream fileOut = new FileOutputStream(file_name);

            int numRead;
            byte [] buffer = new byte[1500];
            while((numRead = input.read(buffer)) != -1) {
                fileOut.write(buffer, 0, numRead);
            }
            fileOut.close();
            file_socket.close();
        }
        catch (Exception e){
            // ignore
        };
    }


    private void writeFiles() {
        try {
            server_socket = new ServerSocket(listening_port);
            while (true) {
                Socket file_socket = server_socket.accept();

                DataInputStream input = new DataInputStream(file_socket.getInputStream());
                DataOutputStream output = new DataOutputStream(file_socket.getOutputStream());

                String fileName = input.readUTF();
                File file = new File(fileName);
                //if file doesn't exist
                if(!file.exists() || !file.canRead() || file.length() == 0) {
                    file_socket.close();
                    continue;
                }

                FileInputStream fileInput = new FileInputStream(file);
                int numRead;
                byte [] buffer = new byte[1500];
                while((numRead = fileInput.read(buffer)) != -1) {
                    output.write(buffer, 0, numRead);
                }
                fileInput.close();
                file_socket.close();
            }
        }
        catch(Exception e) {
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
