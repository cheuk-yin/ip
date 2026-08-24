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

    /** Fixed-size storage for tasks, filled from index 0 upwards. */
    private static Task[] taskStorage = new Task[MAX_TASKS];

    /** Number of tasks currently stored; also the next free index in the array above. */
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
    public static void storeTask(String msg) {
        if (currentIndex >= MAX_TASKS) {
            System.out.println("Storage full, unable to add.");
        } else {
            taskStorage[currentIndex] = new Task(msg);
            currentIndex++;
        }
    }

    /**
     * Prints all stored tasks, numbered from 1, between divider lines.
     */
    public static void listTasks() {
        System.out.println(LINE);
        System.out.println("Here are the tasks in your list:");
        for (int i = 0; i < currentIndex; i++) {
            Task task = taskStorage[i];
            System.out.println((i + 1) + ".[" + task.getStatusIcon() + "] " + task.getDescription());
        }
        System.out.println(LINE);
    }

    /**
     * Checks whether the given 1-based index refers to an existing task,
     * printing an error message if it does not.
     *
     * @param index the 1-based position to check
     * @return true if the index is within range of the stored tasks
     */
    private static boolean isValidIndex(int index) {
        if (index < 1 || index > currentIndex) {
            printBoxed("OOPS!!! Task " + index + " does not exist.");
            return false;
        }
        return true;
    }

    /**
     * Marks the task at the given 1-based index as done and prints a
     * confirmation showing the updated task. Prints an error instead if
     * the index does not refer to an existing task.
     *
     * @param index the 1-based position of the task in the list
     */
    public static void markTask(int index) {
        if (!isValidIndex(index)) {
            return;
        }
        System.out.println(LINE);
        Task task = taskStorage[index - 1];
        task.markAsDone();
        System.out.println("Nice! I've marked this task as done:");
        System.out.println("  [" + task.getStatusIcon() + "] " + task.getDescription());
        System.out.println(LINE);
    }

    /**
     * Marks the task at the given 1-based index as not done and prints a
     * confirmation showing the updated task. Prints an error instead if
     * the index does not refer to an existing task.
     *
     * @param index the 1-based position of the task in the list
     */
    public static void unmarkTask(int index) {
        if (!isValidIndex(index)) {
            return;
        }
        System.out.println(LINE);
        Task task = taskStorage[index - 1];
        task.markAsNotDone();
        System.out.println("OK, I've marked this task as not done yet:");
        System.out.println("  [" + task.getStatusIcon() + "] " + task.getDescription());
        System.out.println(LINE);
    }

    /**
     * Runs Buddy: prints the greeting banner, then reads commands from
     * standard input until the user types "bye".
     *
     * @param args not used
     */
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
            String[] array = line.split(" ");
            if (array[0].equals("bye")) {
                printBoxed("Bye. Hope to see you again soon!");
                break;
            } else if (array[0].equals("list")) {
                listTasks();
            } else if (array[0].equals("mark")) {
                markTask(Integer.parseInt(array[1]));
            } else if (array[0].equals("unmark")) {
                unmarkTask(Integer.parseInt(array[1]));
            } else {
                storeTask(line);
                printBoxed("added: " + line);
            }
        }
        scanner.close();
    }
}

