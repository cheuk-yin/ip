package buddy.parser;

import buddy.command.AddCommand;
import buddy.command.Command;
import buddy.command.DeleteCommand;
import buddy.command.ExitCommand;
import buddy.command.ListCommand;
import buddy.command.MarkCommand;
import buddy.exception.BuddyException;
import buddy.task.Deadline;
import buddy.task.Event;
import buddy.task.Todo;

/**
 * Makes sense of raw lines of user input, turning each one into the
 * {@link Command} it represents. Only checks a command's syntax (e.g. is
 * a task number present and numeric); whether that task number actually
 * refers to an existing task is checked later, when the command runs.
 */
public class Parser {

    /**
     * Parses a full line of user input into the command it represents.
     *
     * @param fullCommand the full line of user input
     * @return the command to execute
     * @throws BuddyException if the line isn't a recognized, well-formed command
     */
    public static Command parse(String fullCommand) throws BuddyException {
        String[] commandParts = fullCommand.split(" ");
        return switch (commandParts[0]) {
            case "bye" -> new ExitCommand();
            case "list" -> new ListCommand();
            case "mark" -> new MarkCommand(parseTaskIndex(commandParts, "mark"), true);
            case "unmark" -> new MarkCommand(parseTaskIndex(commandParts, "unmark"), false);
            case "todo" -> new AddCommand(parseTodo(fullCommand));
            case "deadline" -> new AddCommand(parseDeadline(fullCommand));
            case "event" -> new AddCommand(parseEvent(fullCommand));
            case "delete" -> new DeleteCommand(parseTaskIndex(commandParts, "delete"));
            default -> throw new BuddyException("Buddy there is no such command.");
        };
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
     * Parses a todo from a "todo &lt;description&gt;" command line.
     *
     * @param line the full command line
     * @return the new todo
     */
    private static Todo parseTodo(String line) throws BuddyException {
        String description = stripCommandWord(line);
        if (description.isBlank()) {
            throw new BuddyException("Buddy you cant leave the description of a todo empty.");
        }
        return new Todo(description);
    }

    /**
     * Parses a deadline from a "deadline &lt;description&gt; /by &lt;when&gt;"
     * command line.
     *
     * @param line the full command line
     * @return the new deadline
     */
    private static Deadline parseDeadline(String line) throws BuddyException {
        String description = stripCommandWord(line);
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
     * Parses an event from an
     * "event &lt;description&gt; /from &lt;start&gt; /to &lt;end&gt;" command line.
     *
     * @param line the full command line
     * @return the new event
     */
    private static Event parseEvent(String line) throws BuddyException {
        String description = stripCommandWord(line);
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
     * Parses and validates that the task number given as the second word
     * of a mark/unmark/delete command line is present and numeric.
     *
     * @param commandParts the command line split by spaces
     * @param action the action being attempted (e.g. "mark"), used to
     *     phrase the error message if no task number was given
     * @return the parsed 1-based task number
     * @throws BuddyException if no task number was given or it isn't an integer
     */
    private static int parseTaskIndex(String[] commandParts, String action) throws BuddyException {
        if (commandParts.length < 2) {
            throw new BuddyException("Buddy you need to provide a task number to " + action + ".");
        }
        try {
            return Integer.parseInt(commandParts[1]);
        } catch (NumberFormatException e) {
            throw new BuddyException("Buddy you need to provide an int for the task number, " +
                    "not " + commandParts[1] + ".");
        }
    }
}
