
/*
    Andrew Peterson
    CSC 376
 */

public class MessengerWithFiles {
    public static void main(String[] args) {
        if (args.length > 4) {
            System.out.println("Usage:");
            System.out.println("\tServer -l <port number>\n\tClient <port number>");
        } else {
            Messenger m = new Messenger();
            m.parseArgs(args);

            Thread writeFiles = new Thread(() -> m.writeFiles());
            writeFiles.start();

            Thread read = new Thread(() -> m.read());
            read.start();

            m.write();

        }
    }
}
