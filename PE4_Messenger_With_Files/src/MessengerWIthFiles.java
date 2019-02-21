import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;

public class MessengerWIthFiles {
    public static void main(String[] args) {
        if (args.length > 2) {
            System.out.println("Usage:");
            System.out.println("\tServer -l <port number>\n\tClient <port number>");
        } else {
            Messenger m;
            if (args[0].equals("-l")) {
                m = new Messenger(Integer.parseInt(args[1]), true);
                m.startServer();
            } else {
                m = new Messenger(Integer.parseInt(args[0]), false);
                m.startClient();
            }


            Thread read = new Thread(() -> {
                try {
                    BufferedReader input = m.getInputStream();
                    String message;
                    while ((message = input.readLine()) != null) {
                        switch (message) {
                            case "m":
                                String userMessage = input.readLine();
                                System.out.println(userMessage);
                                break;
                            case "f":
                                m.serviceRequest();
                                break;
                            case "x":
                                input.close();
                                m.close();
                                System.exit(0);
                                break;
                        }
                    }
                    input.close();
                } catch (IOException e) {
                    // ignore
                } catch (Exception e) {
                    System.err.println("eof error: " + e.getMessage());
                }
            });
            read.start();

            try {
                PrintWriter output = m.getOutputStream();
                BufferedReader stdIn = new BufferedReader(new InputStreamReader(System.in));
                String message;
                m.printMessage();
                while ((message = stdIn.readLine()) != null) {
                    output.println(message);
                    switch (message) {
                        case "m":
                            System.out.println("Enter your message: ");
                            String userMessage = stdIn.readLine();
                            output.println(userMessage);
                            m.printMessage();
                            break;
                        case "f":
                            System.out.println("Which file do you want?");
                            String fileMessage = stdIn.readLine();
                            m.getFile(fileMessage);
                            m.printMessage();
                            break;
                        case "x":
                            output.close();
                            m.close();
                            System.exit(0);
                            break;
                    }
                }
                output.close();
            } catch (IOException e) {
                // ignore
            }
        }


    }
}
