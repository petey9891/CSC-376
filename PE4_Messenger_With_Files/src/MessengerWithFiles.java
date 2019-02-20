import javax.xml.crypto.Data;
import java.io.*;

public class MessengerWithFiles {
    public static void main(String[] args) {
        if (args.length > 2) {
            System.out.println("Usage:");
            System.out.println("\tServer -l <port number>\n\tClient <port number>");
        } else {
            Messenger m;
            if (args[0].equals("-l")) {
                m = new Messenger(Integer.parseInt(args[1]));
                m.startServer();
            } else {
                m = new Messenger(Integer.parseInt(args[0]));
                m.startClient();
            }

            Thread textReader = new Thread(() -> {
                try {
//                    BufferedReader input = m.getInputStream();
                    DataInputStream input = m.getInputStream();
                    try {
                        while(true) {
                            String command = input.readUTF();
                            System.err.println(command);
                            switch (command) {
                                case "m":
                                    String userMessage = input.readUTF();
                                    System.out.println(userMessage);
                                    break;
                                case "f":
                                    String fileMessage = input.readUTF();
//                                m.serviceRequest(fileMessage);
                                    File file = new File(fileMessage);
                                    System.err.println(file.exists() && file.canRead());
                                    if (!file.exists() || !file.canRead()) {
                                        break;
                                    }
                                    DataOutputStream output = m.getOutputStream();
                                    FileInputStream file_input = new FileInputStream(file);
                                    byte[] file_buffer = new byte[1500];
                                    int number_read;
                                    output.writeUTF("-r");
                                    output.writeUTF(fileMessage);
                                    while ((number_read = file_input.read(file_buffer)) != -1)
                                        output.write(file_buffer, 0, number_read);
                                    file_input.close();
//                                    output.close();
                                    break;
                                case "-r":
                                    String file_name = input.readUTF();
                                    FileOutputStream file_out = new FileOutputStream(file_name);
                                    int number_read1;
                                    byte[] buffer = new byte[1500];
                                    while ((number_read1 = input.read(buffer)) != -1)
                                        file_out.write(buffer, 0, number_read1);
                                    file_out.close();
                                    break;
                                case "x":
                                    input.close();
                                    m.close();
                                    System.exit(0);
                                    break;
                            }
                        }
                    }
                    catch (EOFException eof) {
                        System.err.println("eof error: " + eof.getMessage());
                        input.close();
                        m.close();
                        System.exit(0);
                    }
                    catch (IOException e) {
                        System.err.println(e.getMessage());
                    }
                } catch (IOException e) {
                    System.err.println(e.getMessage());
                }

//                    String message;
//                    while ((message = input.readLine()) != null) {
//                        switch (message) {
//                            case "m":
//                                String userMessage = input.readLine();
//                                System.out.println(userMessage);
//                                break;
//                            case "f":
//                                String fileMessage = input.readLine();
////                                m.serviceRequest(fileMessage);
//                                File file = new File(fileMessage);
//                                if (!file.exists() || !file.canRead()) {
//                                    return;
//                                }
//                                PrintWriter output = m.getOutputStream();
//                                BufferedReader file_reader = new BufferedReader(new FileReader(file));
//                                String line;
//                                output.println("-r");
//                                output.println(fileMessage);
//                                while ((line = file_reader.readLine()) != null) {
//                                    output.println(line);
//                                }
//                                file_reader.close();
//                                break;
//                            case "-r":
//                                String fileName = input.readLine();
//                                PrintWriter file_writer = new PrintWriter(new BufferedWriter(new FileWriter(fileName)));
//                                String line_writer;
//                                while ((line_writer = input.readLine()) != null) {
//                                    file_writer.println(line_writer);
//                                }
//                                file_writer.close();
//                            case "x":
//                                input.close();
//                                m.close();
//                                System.exit(0);
//                                break;
//                        }
//
//                    }
//                    input.close();
//                } catch (IOException e) {
//                    // ignore
//                    System.err.println(e.getMessage());
//                }
            });
            textReader.start();

//            Thread fileHandler = new Thread(() -> {
//                try {
//                    BufferedReader input = m.getInputStream();
//                    String message;
//                    while ((message = input.readLine()) != null) {
//                        m.serviceRequest(message);
//                    }
//                } catch (IOException e) {
//                    System.err.println(e.getMessage());
//                }
//            });
//            fileHandler.start();

            try {
                DataOutputStream output = m.getOutputStream();
                BufferedReader stdIn = new BufferedReader(new InputStreamReader(System.in));
                String message;
                m.printMessage();
                while ((message = stdIn.readLine()) != null) {
                    output.writeUTF(message);
                    switch (message) {
                        case "m":
                            System.out.println("Enter your message: ");
                            String userMessage = stdIn.readLine();
                            output.writeUTF(userMessage);
                            m.printMessage();
                            break;
                        case "f":
                            System.out.println("Which file do you want?");
                            String fileMessage = stdIn.readLine();
                            output.writeUTF(fileMessage);
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
