import java.util.Scanner;

public class Buddy {
    private static final String LINE = "____________________________________________________________";

    public static void printMessage(String msg) {
        System.out.println(LINE);
        System.out.println(msg);
        System.out.println(LINE);
    }
    public static void main(String[] args) {
        String banner = " ____   _   _  ____   ____  __   __\n"
                + "| __ ) | | | ||  _ \\ |  _ \\ \\ \\ / /\n"
                + "|  _ \\ | | | || | | || | | | \\ V / \n"
                + "| |_) || |_| || |_| || |_| |  | |  \n"
                + "|____/  \\___/ |____/ |____/   |_|  \n";
        System.out.println(LINE);
        System.out.println(banner);
        System.out.println("Hello! I'm Buddy.");
        System.out.println("What can I do for you?");
        System.out.println(LINE);
        Scanner scanner = new Scanner(System.in);
        while (scanner.hasNextLine()) {
            String line = scanner.nextLine();
            if (line.equals("bye")) {
                printMessage("Bye. Hope to see you again soon!");
                break;
            }
            printMessage(line);
        }
    }
}
