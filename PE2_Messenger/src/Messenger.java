import com.sun.security.ntlm.Server;

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
                    AMessenger s = new AMessenger(Integer.parseInt(args[1]), null, true);
                    readerThread = new Thread(() -> s.read());
                    writerThread = new Thread(() -> s.write());
                } else {
                    AMessenger c = new AMessenger(Integer.parseInt(args[0]), "localhost", false);
                    readerThread = new Thread(() -> c.read());
                    writerThread = new Thread(() -> c.write());
                }
                readerThread.start();
                writerThread.start();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}

class AMessenger {
    private ServerSocket server_socket;
    private Socket client_socket;
    private DataOutputStream output;
    private DataInputStream input;
    private BufferedReader reader;
    private boolean isServer;

    AMessenger(int port, String addr, boolean isServer) {
        this.isServer = isServer;
        try {
            if (this.isServer) {
                server_socket = new ServerSocket(port);
                client_socket = server_socket.accept();
            } else {
                client_socket = new Socket(addr, port);
            }
            output = new DataOutputStream(client_socket.getOutputStream());
            input = new DataInputStream(client_socket.getInputStream());
            reader = new BufferedReader(new InputStreamReader(System.in));
        } catch (Exception e) {
            close();
        }
    }

    public void read() {
        try {
            while (client_socket.isConnected() && !client_socket.isClosed()) {
                try {
                    System.out.println(input.readUTF());
                } catch (EOFException eof) {
                    input.close();
                    close();
                }
            }
            close();
        } catch (IOException e) {
            close();
        }
    }

    public void write() {
        try {
            while (client_socket.isConnected() && !client_socket.isClosed()) {
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
            close();
        }
    }

    public void close() {
        try {
            if (isServer)
                server_socket.close();
            client_socket.shutdownOutput();
            client_socket.close();
            System.exit(0);
        } catch (IOException e) {
//            e.printStackTrace();
        }
    }

}

//class Server {
//    private ServerSocket server_socket;
//    private Socket client_socket;
//    private DataOutputStream output;
//    private DataInputStream input;
//    private BufferedReader reader;
//    Thread readerThread;
//    Thread writerThread;
//
//    Server(int port) {
//        try {
//            server_socket = new ServerSocket(port);
//            client_socket = server_socket.accept();
//            output = new DataOutputStream(client_socket.getOutputStream());
//            input = new DataInputStream(client_socket.getInputStream());
//            reader = new BufferedReader(new InputStreamReader(System.in));
//        } catch (Exception e) {
//            System.out.println(e.getMessage());
//        }
//    }
//
//    public void read() {
//        try {
//            while(client_socket.isConnected() && !client_socket.isClosed()) {
//                try {
//                    System.out.println(input.readUTF());
//                } catch (EOFException eofx) {
//                    input.close();
//                    System.out.println("server reader closed");
//                    close();
//                }
//            }
//        } catch (IOException e) {
//            System.out.println(e.getMessage());
//        }
//    }
//
//    public void write() {
//        try {
//            while(client_socket.isConnected() && !client_socket.isClosed()) {
//                String message;
//                if((message = reader.readLine()) != null && message.length() > 0) {
//                    output.writeUTF(message);
//                } else {
//                    reader.close();
//                    System.out.println("server writer closed");
//                    close();
//                }
//            }
//            close();
//        } catch (IOException e) {
//            System.out.println(e.getMessage());
//        }
//    }
//
//    public void run() {
//        readerThread = new Thread(() -> read());
//        writerThread = new Thread(() -> write());
//        readerThread.start();
//        writerThread.start();
//    }
//
//    private void close() {
//        try {
//            server_socket.close();
//            client_socket.shutdownOutput();
//            client_socket.close();
//            System.exit(0);
//            readerThread.interrupt();
//            writerThread.interrupt();
//        } catch (IOException e) {
//            System.out.println(e.getMessage());
//        }
//    }
//}
//
//class Client {
//    private Socket client_socket;
//    private DataOutputStream output;
//    private DataInputStream input;
//    private BufferedReader reader;
//    Thread readerThread;
//    Thread writerThread;
//
//    Client(int port, String addr) {
//        try {
//            client_socket = new Socket(addr, port);
//            output = new DataOutputStream(client_socket.getOutputStream());
//            input = new DataInputStream(client_socket.getInputStream());
//            reader = new BufferedReader(new InputStreamReader(System.in));
//        } catch (Exception e) {
//            System.out.println(e.getMessage());
//        }
//    }
//
//    public void read() {
//        try {
//            while(client_socket.isConnected() && !client_socket.isClosed()) {
//                try {
//                    System.out.println(input.readUTF());
//                } catch (EOFException eofx) {
//                    input.close();
//                    System.out.println("client reader closed");
//                    close();
//
//                }
//            }
//        } catch (IOException e) {
//            System.out.println(e.getMessage());
//        }
//    }
//
//    public void write() {
//        try {
//            while(client_socket.isConnected() && !client_socket.isClosed()) {
//                String message;
//                if((message = reader.readLine()) != null && message.length() > 0) {
//                    output.writeUTF(message);
//                } else {
//                    reader.close();
//                    System.out.println("client writer closed");
//                    close();
//                }
//            }
//            close();
//        } catch (IOException e) {
//            System.out.println(e.getMessage());
//        }
//    }
//
//    public void run() {
//        System.out.println("Here");
//        readerThread = new Thread(() -> read());
//        writerThread = new Thread(() -> write());
//        readerThread.start();
//        writerThread.start();
//    }
//
//    private void close() {
//        try {
//            client_socket.shutdownOutput();
//            client_socket.close();
//            readerThread.interrupt();
//            writerThread.interrupt();
//        } catch (IOException e) {
//            System.out.println(e.getMessage());
//        }
//    }
//}