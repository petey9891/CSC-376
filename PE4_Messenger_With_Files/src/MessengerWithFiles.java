public class MessengerWithFiles {
    public static void main(String[] args) {
        if (args.length != 2) {
            System.out.println("Usage:");
            System.out.println("\tServer -l <port number>\n\tClient <port number>");
        } else {
            Messenger m;
            if (args[0].equals("-l")) {
                m = new Messenger(true, Integer.parseInt(args[1]));
                m.startServer();
            } else {
                m = new Messenger(false, Integer.parseInt(args[0]));
                m.startClient();
            }


        }
    }
}
