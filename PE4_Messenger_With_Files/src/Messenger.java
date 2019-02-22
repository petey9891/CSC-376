import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;

/*
    Andrew Peterson
    CSC 376
 */

class Messenger {
    private int file_server_port;
    private int file_client_port;
    private boolean isServer = false;

    private PrintWriter output;
    private BufferedReader input;
    private BufferedReader stdIn;

    private DataInputStream dataInput;
    private DataOutputStream dataOutput;

    private Socket file_client_socket;
    private Socket client_socket;

    private void startServer(int port) {
        this.file_server_port = 9000;
        this.file_client_port = 6002;
        this.isServer = true;
        try {
            ServerSocket server_socket = new ServerSocket(port);
            client_socket = server_socket.accept();
            output = new PrintWriter(client_socket.getOutputStream(), true);
            input = new BufferedReader(new InputStreamReader(client_socket.getInputStream()));
            stdIn = new BufferedReader(new InputStreamReader(System.in));
        } catch (Exception e) {
            System.err.println("Failed to startup server: " + e.getMessage());
            System.exit(1);
        }
    }

    private void startClient(int file_client_port, int listening_port) {
        this.file_server_port = 9000;
        this.file_client_port = file_client_port;

        try {
            client_socket = new Socket("localhost", listening_port);
            output = new PrintWriter(client_socket.getOutputStream(), true);
            input = new BufferedReader(new InputStreamReader(client_socket.getInputStream()));
            stdIn = new BufferedReader(new InputStreamReader(System.in));
        } catch (Exception e) {
            System.err.println("Failed to startup client: " + e.getMessage());
            System.exit(1);
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
                        Thread read_files = new Thread(() -> readFiles(file_name));
                        read_files.start();
                        break;
                    case "x":
                        closeMessenger();
                        break;
                }
            }
        } catch (Exception e) {
            // ignore
        }
    }


    void write() {
        try {
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
                        System.out.println("Which file do you want?");
                        String file_name = stdIn.readLine();
                        output.println(file_name);
                        printMessage();
                        break;
                    case "x":
                        closeMessenger();
                        break;
                }
            }
        } catch (Exception e) {
            // ignore
        }
    }

    private void readFiles(String file_name) {
        try {
            int port = isServer ? file_client_port : file_server_port;
            file_client_socket = new Socket("localhost", port);

            dataInput = new DataInputStream(file_client_socket.getInputStream());
            dataOutput = new DataOutputStream(file_client_socket.getOutputStream());

            dataOutput.writeUTF(file_name);
            File file = new File(file_name);

            if (!file.exists() || !file.canRead() || file.length() == 0) {
                file_client_socket.close();
                return;
            }

            FileInputStream file_input = new FileInputStream(file);
            int number_read;
            byte[] buffer = new byte[1500];
            while ((number_read = file_input.read(buffer)) != -1) {
                dataOutput.write(buffer, 0, number_read);
            }
            file_input.close();
            file_client_socket.close();
        } catch (Exception e) {
            // ignore
        }
    }

    void writeFiles() {
        try {
            int port = isServer ? file_server_port : file_client_port;
            ServerSocket file_server = new ServerSocket(port);
            while (true) {
                file_client_socket = file_server.accept();
                dataInput = new DataInputStream(file_client_socket.getInputStream());
                dataOutput = new DataOutputStream(file_client_socket.getOutputStream());

                String file_name = dataInput.readUTF();
                FileOutputStream file_output = new FileOutputStream(file_name);

                byte[] buffer = new byte[1500];
                int number_read;
                while ((number_read = dataInput.read(buffer)) != -1) {
                    file_output.write(buffer, 0, number_read);
                }
                file_output.close();
                file_client_socket.close();
            }
        } catch (Exception e) {
            // ignore
        }
    }

    private void closeMessenger() {
        try {
            client_socket.shutdownOutput();
            client_socket.close();
            System.exit(0);
        } catch (Exception e) {
            // ignore
        }
    }

    private void printMessage() {
        System.out.println("Enter an option ('m', 'f', 'x'):\n" +
                "\t(M)essage (send)\n" +
                "\t(F)ile (request)\n" +
                "\te(X)it");
    }

    void parseArgs(String[] args) {
        if (args.length < 4) {
            startServer(Integer.parseInt(args[1]));
        } else {
            startClient(Integer.parseInt(args[1]), Integer.parseInt(args[3]));
        }
    }
}
