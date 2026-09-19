package buddy;

import java.util.Scanner;

import buddy.exception.BuddyException;
import buddy.storage.Storage;
import buddy.task.Deadline;
import buddy.task.Event;
import buddy.task.Task;
import buddy.task.TaskList;
import buddy.task.Todo;
import buddy.ui.Ui;

/**
 * Buddy is a simple command-line chatbot that can store short pieces of
 * text ("tasks") entered by the user and list them back on request.
 * Tasks are saved to disk as they change and reloaded on startup.
 */
public class Buddy {
    /** The tasks Buddy currently knows about, in the order they were added. */
    private static TaskList taskList = new TaskList();

    /** Handles all console output shown to the user. */
    private static Ui ui = new Ui();

    /**
     * Marks the task at the given 1-based index as done or not done, and
     * prints a confirmation showing the updated task. Prints an error
     * instead if the index does not refer to an existing task.
     *
     * @param index the 1-based position of the task in the list
     * @param isDone true to mark the task done, false to mark it not done
     */
    private static void setTaskStatus(int index, boolean isDone) {
        Task task = taskList.get(index - 1);
        if (isDone) {
            task.markAsDone();
        } else {
            task.markAsNotDone();
        }
        Storage.save(taskList.asList());
        ui.showTaskStatusChanged(task, isDone);
    }

    /**
     * Parses and validates the task number given as the second word of a
     * mark/unmark/delete command line.
     *
     * @param commandParts the command line split by spaces
     * @param action the action being attempted (e.g. "mark"), used to
     *     phrase the error message if no task number was given
     * @return the validated 1-based task number
     * @throws BuddyException if no task number was given, it isn't an
     *     integer, or it doesn't refer to an existing task
     */
    private static int parseTaskIndex(String[] commandParts, String action) throws BuddyException {
        if (commandParts.length < 2) {
            throw new BuddyException("Buddy you need to provide a task number to " + action + ".");
        }
        try {
            int index = Integer.parseInt(commandParts[1]);
            if (index < 1 || index > taskList.size()) {
                throw new BuddyException("Buddy task number " + index + " does not exist.");
            }
            return index;
        } catch (NumberFormatException e) {
            throw new BuddyException("Buddy you need to provide an int for the task number, " +
                    "not " + commandParts[1] + ".");
        }
    }

    /**
     * Marks the task at the given 1-based index as done.
     *
     * @param line the command given by the user
     */
    private static void markTask(String line) throws BuddyException {
        int index = parseTaskIndex(line.split(" "), "mark");
        setTaskStatus(index, true);
    }

    /**
     * Marks the task at the given 1-based index as not done.
     *
     * @param line the command given by the user
     */
    private static void unmarkTask(String line) throws BuddyException {
        int index = parseTaskIndex(line.split(" "), "unmark");
        setTaskStatus(index, false);
    }

    /**
     * Removes the task at the given 1-based index and prints a
     * confirmation showing the removed task and the new task count.
     *
     * @param line the command given by the user
     */
    private static void deleteTask(String line) throws BuddyException {
        int index = parseTaskIndex(line.split(" "), "delete");
        Task removedTask = taskList.remove(index - 1);
        Storage.save(taskList.asList());
        ui.showTaskDeleted(removedTask, taskList.size());
    }

    /**
     * Removes the leading command word (e.g. "todo") from a line of user
     * input, returning only the text after the first space.
     *
     * @param line the full line of user input, including the command word
     * @return the remainder of the line after the first space
     */
    private static String stripCommandWord(String line) {
        if (line.indexOf(' ') == -1) {
            return "";
        }
        return line.substring(line.indexOf(' ') + 1);
    }

    /**
     * Creates a todo from a "todo &lt;description&gt;" command line.
     *
     * @param line the full command line
     * @return the new todo
     */
    private static Todo createTodo(String line) throws BuddyException {
        String description = stripCommandWord((line));
        if (description.isBlank()) {
            throw new BuddyException("Buddy you cant leave the description of a todo empty.");
        }
        return new Todo(description);
    }

    /**
     * Creates a deadline from a "deadline &lt;description&gt; /by &lt;when&gt;"
     * command line.
     *
     * @param line the full command line
     * @return the new deadline
     */
    private static Deadline createDeadline(String line) throws BuddyException {
        String description = stripCommandWord((line));
        if (description.isBlank()) {
            throw new BuddyException("Buddy you cant leave the description of a deadline empty.");
        }
        String[] descriptionAndBy = description.split(" /by ");

        if (descriptionAndBy.length < 2) {
            throw new BuddyException("Buddy you need to specify the deadline using '/by'.");
        }
        return new Deadline(descriptionAndBy[0], descriptionAndBy[1]);
    }

    /**
     * Creates an event from an
     * "event &lt;description&gt; /from &lt;start&gt; /to &lt;end&gt;" command line.
     *
     * @param line the full command line
     * @return the new event
     */
    private static Event createEvent(String line) throws BuddyException {
        String description = stripCommandWord((line));
        if (description.isBlank()) {
            throw new BuddyException("Buddy you cant leave the description of an event empty.");
        }
        String[] descriptionAndFrom = description.split(" /from ");
        if (descriptionAndFrom.length < 2) {
            throw new BuddyException("Buddy you need to specify the start of an event using '/from'.");
        }
        String[] fromAndTo = descriptionAndFrom[1].split(" /to ");
        if (fromAndTo.length < 2) {
            throw new BuddyException("Buddy you need to specify the end of an event using '/to'.");
        }
        return new Event(descriptionAndFrom[0], fromAndTo[0], fromAndTo[1]);
    }

    /**
     * Stores the given task and prints a confirmation that it was added.
     *
     * @param task the task to store and confirm
     */
    private static void addAndPrintTask(Task task) {
        taskList.add(task);
        Storage.save(taskList.asList());
        ui.showTaskAdded(task, taskList.size());
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
        try {
            switch (commandParts[0]) {
                case "bye" -> {
                    ui.showMessage("Bye. Hope to see you again soon!");
                    return false;
                }
                case "list" -> ui.showTaskList(taskList);
                case "mark" -> markTask(line);
                case "unmark" -> unmarkTask(line);
                case "todo" -> addAndPrintTask(createTodo(line));
                case "deadline" -> addAndPrintTask(createDeadline(line));
                case "event" -> addAndPrintTask(createEvent(line));
                case "delete" -> deleteTask(line);
                default -> throw new BuddyException("Buddy there is no such command.");
            }

        } catch (BuddyException e) {
            ui.showMessage(e.getMessage());
        }
        return true;
    }

    /**
     * Runs Buddy: loads previously saved tasks, prints the greeting
     * banner, then reads commands from standard input until the user
     * types "bye".
     *
     * @param args not used
     */
    public static void main(String[] args) {
        taskList = new TaskList(Storage.load());
        ui.showGreeting();
        Scanner scanner = new Scanner(System.in);
        boolean isRunning = true;
        while (isRunning && scanner.hasNextLine()) {
            isRunning = handleCommand(scanner.nextLine());
        }
        scanner.close();
    }
}
