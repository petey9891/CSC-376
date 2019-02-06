import java.io.*;

public class ChatClient {
    public static void main(String[] args) {
        if (args.length != 1) {
            System.out.println("Usage:");
            System.out.println("\tChatClient <port number>");
        }
        Client c = new Client("localhost", Integer.parseInt(args[0]));
        c.startClient();

//        System.out.println("Started");

        Thread read = new Thread(() -> {
//            System.out.println("Read started");
            try {
//                DataInputStream input = c.getInputStream();
                BufferedReader input = c.getInputStream();
                while (true) {
                    try {
                        System.out.println(input.readLine());
//                        System.out.println(input.readUTF());
                    } catch (EOFException eof) {
//                        System.out.println("Here");
                        input.close();
                        c.close();
                    }
                }
            } catch (IOException e) {
//                System.out.println("read error");

            }
        });
        read.start();
//        System.out.println("Write started");
        try {
//            DataOutputStream output = c.getOutputStream();
            PrintWriter output = c.getOutputStream();
            try {
                String message;
                BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
                while (true) {
                    if((message = reader.readLine()) != null) {
                        output.println(message);
//                        output.writeUTF(message);
                    }
                }
            } catch (IOException e) {
                output.close();
                c.close();
            }
        } catch (IOException e) {
//            System.out.println("write error");
        }


    }
}
