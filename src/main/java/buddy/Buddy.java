package buddy;

import java.util.Scanner;

import buddy.exception.BuddyException;
import buddy.parser.Parser;
import buddy.storage.Storage;
import buddy.task.Task;
import buddy.task.TaskList;
import buddy.ui.Ui;

/**
 * Buddy is a simple command-line chatbot that can store short pieces of
 * text ("tasks") entered by the user and list them back on request.
 * Tasks are saved to disk as they change and reloaded on startup.
 */
public class Buddy {
    /** The tasks Buddy currently knows about, in the order they were added. */
    private TaskList taskList;

    /** Handles all console output shown to the user. */
    private final Ui ui;

    /** Loads and saves the task list to disk. */
    private final Storage storage;

    /**
     * Creates a Buddy that loads its tasks from, and saves its tasks to,
     * the given file.
     *
     * @param filePath where Buddy's tasks are saved between runs
     */
    public Buddy(String filePath) {
        ui = new Ui();
        storage = new Storage(filePath);
        taskList = new TaskList(storage.load());
    }

    /**
     * Marks the task at the given 1-based index as done or not done, and
     * prints a confirmation showing the updated task. Prints an error
     * instead if the index does not refer to an existing task.
     *
     * @param index the 1-based position of the task in the list
     * @param isDone true to mark the task done, false to mark it not done
     */
    private void setTaskStatus(int index, boolean isDone) {
        Task task = taskList.get(index - 1);
        if (isDone) {
            task.markAsDone();
        } else {
            task.markAsNotDone();
        }
        storage.save(taskList.asList());
        ui.showTaskStatusChanged(task, isDone);
    }

    /**
     * Marks the task at the given 1-based index as done.
     *
     * @param line the command given by the user
     */
    private void markTask(String line) throws BuddyException {
        int index = Parser.parseTaskIndex(line.split(" "), "mark", taskList.size());
        setTaskStatus(index, true);
    }

    /**
     * Marks the task at the given 1-based index as not done.
     *
     * @param line the command given by the user
     */
    private void unmarkTask(String line) throws BuddyException {
        int index = Parser.parseTaskIndex(line.split(" "), "unmark", taskList.size());
        setTaskStatus(index, false);
    }

    /**
     * Removes the task at the given 1-based index and prints a
     * confirmation showing the removed task and the new task count.
     *
     * @param line the command given by the user
     */
    private void deleteTask(String line) throws BuddyException {
        int index = Parser.parseTaskIndex(line.split(" "), "delete", taskList.size());
        Task removedTask = taskList.remove(index - 1);
        storage.save(taskList.asList());
        ui.showTaskDeleted(removedTask, taskList.size());
    }

    /**
     * Stores the given task and prints a confirmation that it was added.
     *
     * @param task the task to store and confirm
     */
    private void addAndPrintTask(Task task) {
        taskList.add(task);
        storage.save(taskList.asList());
        ui.showTaskAdded(task, taskList.size());
    }

    /**
     * Executes a single line of user input as a command.
     *
     * @param line the full line of user input
     * @return true if Buddy should keep running, false if the "bye"
     *     command was given
     */
    private boolean handleCommand(String line) {
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
                case "todo" -> addAndPrintTask(Parser.parseTodo(line));
                case "deadline" -> addAndPrintTask(Parser.parseDeadline(line));
                case "event" -> addAndPrintTask(Parser.parseEvent(line));
                case "delete" -> deleteTask(line);
                default -> throw new BuddyException("Buddy there is no such command.");
            }

        } catch (BuddyException e) {
            ui.showMessage(e.getMessage());
        }
        return true;
    }

    /**
     * Runs Buddy: prints the greeting banner, then reads commands from
     * standard input until the user types "bye".
     */
    public void run() {
        ui.showGreeting();
        Scanner scanner = new Scanner(System.in);
        boolean isRunning = true;
        while (isRunning && scanner.hasNextLine()) {
            isRunning = handleCommand(scanner.nextLine());
        }
        scanner.close();
    }

    /**
     * Starts Buddy, saving and loading tasks from "data/buddy.txt".
     *
     * @param args not used
     */
    public static void main(String[] args) {
        new Buddy("data/buddy.txt").run();
    }
}
