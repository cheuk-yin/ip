import java.util.Scanner;

/**
 * Buddy is a simple command-line chatbot that can store short pieces of
 * text ("tasks") entered by the user and list them back on request.
 * Data is kept in memory only and is lost when the program exits.
 */
public class Buddy {
    private static final String LINE = "____________________________________________________________";

    /** Maximum number of tasks Buddy can remember at once. */
    private static final int MAX_TASKS = 100;

    /** Fixed-size storage for task text, filled from index 0 upwards. */
    private static String[] textStorage = new String[MAX_TASKS];

    /** Number of tasks currently stored; also the next free index in textStorage. */
    private static int currentIndex = 0;

    /**
     * Prints a message surrounded by horizontal divider lines.
     *
     * @param msg the message to display
     */
    public static void printBoxed(String msg) {
        System.out.println(LINE);
        System.out.println(msg);
        System.out.println(LINE);
    }

    /**
     * Stores the given text as a new task, unless storage is already full.
     *
     * @param msg the task text to store
     */
    public static void storeMessage(String msg) {
        if (currentIndex >= MAX_TASKS) {
            System.out.println("Storage full, unable to add.");
        } else {
            textStorage[currentIndex] = msg;
            currentIndex++;
        }
    }

    /**
     * Prints all stored tasks, numbered from 1, between divider lines.
     */
    public static void listMessages() {
        System.out.println(LINE);
        for (int i = 0; i < currentIndex; i++) {
            System.out.println((i + 1) + ". " + textStorage[i]);
        }
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
                printBoxed("Bye. Hope to see you again soon!");
                break;
            } else if (line.equals("list")) {
                listMessages();
            } else {
                storeMessage(line);
                printBoxed("added: " + line);
            }
        }
        scanner.close();
    }
}
