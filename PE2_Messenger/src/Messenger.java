import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Scanner;

public class Messenger {
    public static void main(String[] args) {
        if (args.length > 3) {
            System.out.println("Usage:");
            System.out.println("\tServer -l <port number> \n\t Client <port number> [<server address>]");
            return;
        } else {
            try {

                Thread readerThread;
                Thread writerThread;
                if (args[0].equals("-l")) {
                    // run server
                    Server s = new Server(Integer.parseInt(args[1]));
                    readerThread = new Thread(() -> s.read());
                    writerThread = new Thread(() -> s.write());
                } else {
                    Client c = new Client(Integer.parseInt(args[0]), "localhost");
                    readerThread = new Thread(() -> c.read());
                    writerThread = new Thread(() -> c.write());
                }
                readerThread.start();
                writerThread.start();
                readerThread.interrupt();
                writerThread.interrupt();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}


class Server {
    private ServerSocket server_socket;
    private Socket client_socket;
    private DataOutputStream output;
    private DataInputStream input;
    private BufferedReader reader;

    Server(int port) {
        try {
            server_socket = new ServerSocket(port);
            client_socket = server_socket.accept();
            output = new DataOutputStream(client_socket.getOutputStream());
            input = new DataInputStream(client_socket.getInputStream());
            reader = new BufferedReader(new InputStreamReader(System.in));
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    public void read() {
        try {
            while(client_socket.isConnected() && !client_socket.isClosed()) {
                String message;
                if ((message = input.readUTF()) != null && message.length() > 0) {
                    System.out.println(message);
                } else {
                    input.close();
                    close();
                }
            }
            close();
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
    }

    public void write() {
        try {
            while(client_socket.isConnected() && !client_socket.isClosed()) {
                String message;
                if((message = reader.readLine()) != null && message.length() > 0) {
                    output.writeUTF(message);
                } else {
                    reader.close();
                    close();
                }
            }
            close();
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
    }

    private void close() {
        System.out.println("here");
        try {
            server_socket.close();
            client_socket.shutdownOutput();
            client_socket.close();
            System.exit(0);
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
    }
}

class Client {
    private Socket client_socket;
    private DataOutputStream output;
    private DataInputStream input;
    private BufferedReader reader;

    Client(int port, String addr) {
        try {
            client_socket = new Socket(addr, port);
            output = new DataOutputStream(client_socket.getOutputStream());
            input = new DataInputStream(client_socket.getInputStream());
            reader = new BufferedReader(new InputStreamReader(System.in));
        } catch (EOFException eofx) {
            System.out.println("EOF encountered; other sid e shut down");
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    public void read() {
        try {
            while(client_socket.isConnected() && !client_socket.isClosed()) {
                String message;
                if ((message = input.readUTF()) != null && message.length() > 0) {
                    System.out.println(message);
                } else {
                    input.close();
                    close();
                }
            }
            close();
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
    }

    public void write() {
        try {
            while(client_socket.isConnected() && !client_socket.isClosed()) {
                String message;
                if((message = reader.readLine()) != null && message.length() > 0) {
                    output.writeUTF(message);
                } else {
                    reader.close();
                    close();
                }
            }
            close();
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
    }

    private void close() {
        System.out.println("here");
        try {
            client_socket.shutdownOutput();
            client_socket.close();
            System.exit(0);
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
    }
}