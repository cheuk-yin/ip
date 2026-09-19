package buddy.parser;

import buddy.exception.BuddyException;
import buddy.task.Deadline;
import buddy.task.Event;
import buddy.task.Todo;

/**
 * Makes sense of raw lines of user input: validates them and turns them
 * into the tasks or values Buddy needs, without touching any task-list
 * or storage state itself.
 */
public class Parser {

    /**
     * Removes the leading command word (e.g. "todo") from a line of user
     * input, returning only the text after the first space.
     *
     * @param line the full line of user input, including the command word
     * @return the remainder of the line after the first space
     */
    public static String stripCommandWord(String line) {
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
    public static Todo parseTodo(String line) throws BuddyException {
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
    public static Deadline parseDeadline(String line) throws BuddyException {
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
    public static Event parseEvent(String line) throws BuddyException {
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
     * Parses and validates the task number given as the second word of a
     * mark/unmark/delete command line.
     *
     * @param commandParts the command line split by spaces
     * @param action the action being attempted (e.g. "mark"), used to
     *     phrase the error message if no task number was given
     * @param taskCount how many tasks currently exist, used to check the
     *     task number refers to an existing task
     * @return the validated 1-based task number
     * @throws BuddyException if no task number was given, it isn't an
     *     integer, or it doesn't refer to an existing task
     */
    public static int parseTaskIndex(String[] commandParts, String action, int taskCount) throws BuddyException {
        if (commandParts.length < 2) {
            throw new BuddyException("Buddy you need to provide a task number to " + action + ".");
        }
        try {
            int index = Integer.parseInt(commandParts[1]);
            if (index < 1 || index > taskCount) {
                throw new BuddyException("Buddy task number " + index + " does not exist.");
            }
            return index;
        } catch (NumberFormatException e) {
            throw new BuddyException("Buddy you need to provide an int for the task number, " +
                    "not " + commandParts[1] + ".");
        }
    }
}
