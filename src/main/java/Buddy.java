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
     * @param message the message to display
     */
    private static void printMessage(String message) {
        System.out.println(LINE);
        System.out.println(" " + message);
        System.out.println(LINE);
    }

    /**
     * Prints a confirmation that the given task was added, followed by
     * how many tasks are now stored, surrounded by divider lines.
     *
     * @param task the task that was just added
     */
    private static void printBoxed(Task task) {
        System.out.println(LINE);
        System.out.println(" Got it. I've added this task:");
        System.out.println("   " + task);
        System.out.println(" Now you have " + currentIndex + " tasks in the list.");
        System.out.println(LINE);
    }

    /**
     * Stores the given task, unless storage is already full.
     *
     * @param task the task to store
     */
    private static void storeTask(Task task) {
        if (currentIndex >= MAX_TASKS) {
            printMessage("Storage full, unable to add.");
        } else {
            taskStorage[currentIndex] = task;
            currentIndex++;
        }
    }

    /**
     * Prints all stored tasks, numbered from 1, between divider lines.
     */
    private static void listTasks() {
        System.out.println(LINE);
        System.out.println(" Here are the tasks in your list:");
        for (int i = 0; i < currentIndex; i++) {
            Task task = taskStorage[i];
            System.out.println(" " + (i + 1) + "." + task);
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
            printMessage("OOPS!!! Task " + index + " does not exist.");
            return false;
        }
        return true;
    }

    /**
     * Marks the task at the given 1-based index as done or not done, and
     * prints a confirmation showing the updated task. Prints an error
     * instead if the index does not refer to an existing task.
     *
     * @param index the 1-based position of the task in the list
     * @param isDone true to mark the task done, false to mark it not done
     */
    private static void setTaskStatus(int index, boolean isDone) {
        if (!isValidIndex(index)) {
            return;
        }
        Task task = taskStorage[index - 1];
        if (isDone) {
            task.markAsDone();
        } else {
            task.markAsNotDone();
        }
        System.out.println(LINE);
        String confirmation = isDone
                ? " Nice! I've marked this task as done:"
                : " OK, I've marked this task as not done yet:";
        System.out.println(confirmation);
        System.out.println("   " + task);
        System.out.println(LINE);
    }

    /**
     * Marks the task at the given 1-based index as done.
     *
     * @param index the 1-based position of the task in the list
     */
    private static void markTask(int index) {
        setTaskStatus(index, true);
    }

    /**
     * Marks the task at the given 1-based index as not done.
     *
     * @param index the 1-based position of the task in the list
     */
    private static void unmarkTask(int index) {
        setTaskStatus(index, false);
    }

    /**
     * Removes the leading command word (e.g. "todo") from a line of user
     * input, returning only the text after the first space.
     *
     * @param line the full line of user input, including the command word
     * @return the remainder of the line after the first space
     */
    private static String stripCommandWord(String line) {
        return line.substring(line.indexOf(' ') + 1);
    }

    /**
     * Creates a todo from a "todo &lt;description&gt;" command line.
     *
     * @param line the full command line
     * @return the new todo
     */
    private static Todo createTodo(String line) {
        return new Todo(stripCommandWord(line));
    }

    /**
     * Creates a deadline from a "deadline &lt;description&gt; /by &lt;when&gt;"
     * command line.
     *
     * @param line the full command line
     * @return the new deadline
     */
    private static Deadline createDeadline(String line) {
        String[] descriptionAndBy = stripCommandWord(line).split(" /by ");
        return new Deadline(descriptionAndBy[0], descriptionAndBy[1]);
    }

    /**
     * Creates an event from an
     * "event &lt;description&gt; /from &lt;start&gt; /to &lt;end&gt;" command line.
     *
     * @param line the full command line
     * @return the new event
     */
    private static Event createEvent(String line) {
        String[] descriptionAndFrom = stripCommandWord(line).split(" /from ");
        String[] fromAndTo = descriptionAndFrom[1].split(" /to ");
        return new Event(descriptionAndFrom[0], fromAndTo[0], fromAndTo[1]);
    }

    /**
     * Stores the given task and prints a confirmation that it was added.
     *
     * @param task the task to store and confirm
     */
    private static void addAndPrintTask(Task task) {
        storeTask(task);
        printBoxed(task);
    }

    /**
     * Prints Buddy's startup banner and greeting.
     */
    private static void printGreeting() {
        String banner = " ____   _   _  ____   ____  __   __\n"
                + "| __ ) | | | ||  _ \\ |  _ \\ \\ \\ / /\n"
                + "|  _ \\ | | | || | | || | | | \\ V / \n"
                + "| |_) || |_| || |_| || |_| |  | |  \n"
                + "|____/  \\___/ |____/ |____/   |_|  \n";
        System.out.println(LINE);
        System.out.println(banner);
        System.out.println(" Hello! I'm Buddy.");
        System.out.println(" What can I do for you?");
        System.out.println(LINE);
    }

    /**
     * Executes a single line of user input as a command.
     *
     * @param line the full line of user input
     * @return true if Buddy should keep running, false if the "bye"
     *     command was given
     */
    private static boolean handleCommand(String line) {
        String[] commandParts = line.split(" ");
        switch (commandParts[0]) {
            case "bye" -> {
                printMessage("Bye. Hope to see you again soon!");
                return false;
            }
            case "list" -> listTasks();
            case "mark" -> markTask(Integer.parseInt(commandParts[1]));
            case "unmark" -> unmarkTask(Integer.parseInt(commandParts[1]));
            case "todo" -> addAndPrintTask(createTodo(line));
            case "deadline" -> addAndPrintTask(createDeadline(line));
            case "event" -> addAndPrintTask(createEvent(line));
            default -> addAndPrintTask(new Task(line));
        }
        return true;
    }

    /**
     * Runs Buddy: prints the greeting banner, then reads commands from
     * standard input until the user types "bye".
     *
     * @param args not used
     */
    public static void main(String[] args) {
        printGreeting();
        Scanner scanner = new Scanner(System.in);
        boolean isRunning = true;
        while (isRunning && scanner.hasNextLine()) {
            isRunning = handleCommand(scanner.nextLine());
        }
        scanner.close();
    }
}
