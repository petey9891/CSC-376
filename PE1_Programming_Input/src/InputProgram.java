import java.util.Scanner;

/*
    Andrew Peterson - CSC 376
 */

public class InputProgram {
    public static void main(String[] args) {
        String o1 = null;
        String o2 = null;
        String o3 = null;
        for (int i = 0; i < args.length; i++) {
            switch (args[i]) {
                case "-o":
                    o1 = args[i + 1];     // should never reach out of bounds error because value always follows -o
                    break;
                case "-t":
                    o2 = args[i + 1];      // should never reach out of bounds error because value always follows -t
                    break;
                case "-h":
                    o3 = "";
                    break;
            }
        }

        Scanner in = new Scanner(System.in);

        System.out.println("Standard Input:");
        while (in.hasNextLine()) {
            System.out.println(in.nextLine());
        }


        System.out.println("Command line arguments:");
        if (o1 != null)
            System.out.println("option 1: " + o1);
        if (o2 != null)
            System.out.println("option 2: " + o2);
        if(o3 != null)
            System.out.println("option 3");


    }
}
