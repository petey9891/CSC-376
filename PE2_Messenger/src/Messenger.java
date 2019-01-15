public class Messenger {
    public static void main(String[] args) {
        boolean runServer = false;
        int port = -1;
        String server = "localhost";

        for (String arg : args) {
            if (arg.equals("-l")) {
                runServer = true;
            }
            if (arg.matches("(\\d{4})")) {
                port = Integer.parseInt(arg);
            }
            if (arg.matches("(\\d{1,3}[.]\\d{1,3}[.]\\d{1,3}[.]\\d{1,3})")) {
                server = arg;
            }

        }


        System.out.println(runServer);
        System.out.println(port);
        System.out.println(server);
    }
}
