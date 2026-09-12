package buddy.storage;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import buddy.task.Deadline;
import buddy.task.Event;
import buddy.task.Task;
import buddy.task.Todo;

/**
 * Saves Buddy's task list to a fixed location on disk, so tasks survive
 * between runs. Uses a relative path so the project works the same way
 * regardless of which computer or operating system it runs on.
 */
public class Storage {
    private static final Path FILE_PATH = Paths.get("data", "buddy.txt");

    /**
     * Writes the given tasks to the save file, one per line, overwriting
     * whatever was there before. Creates the containing folder first if
     * it doesn't exist yet, e.g. on someone's first run of the project.
     *
     * @param tasks the current tasks to save
     */
    public static void save(List<Task> tasks) {
        StringBuilder fileContent = new StringBuilder();
        for (Task task : tasks) {
            fileContent.append(task.toSaveFormat()).append(System.lineSeparator());
        }
        try {
            Files.createDirectories(FILE_PATH.getParent());
            Files.writeString(FILE_PATH, fileContent.toString());
        } catch (IOException e) {
            System.err.println("Buddy could not save your tasks: " + e.getMessage());
        }
    }

    /**
     * Reads previously saved tasks from the save file, in the format
     * written by {@link #save}. Returns an empty list if the save file
     * doesn't exist yet, e.g. on someone's first run of the project.
     * Lines that are missing fields or name an unrecognized task type
     * (e.g. the file was manually edited and corrupted) are skipped
     * rather than crashing startup.
     *
     * @return the tasks read from the save file, in save order
     */
    public static List<Task> load() {
        List<Task> tasks = new ArrayList<>();
        if (Files.notExists(FILE_PATH)) {
            return tasks;
        }
        try {
            for (String line : Files.readAllLines(FILE_PATH)) {
                try {
                    tasks.add(parseTask(line));
                } catch (ArrayIndexOutOfBoundsException | IllegalStateException e) {
                    System.err.println("Buddy skipped a corrupted saved task: " + line);
                }
            }
        } catch (IOException e) {
            System.err.println("Buddy could not load your saved tasks: " + e.getMessage());
        }
        return tasks;
    }

    /**
     * Parses a single save-file line back into the matching kind of task.
     *
     * @param line one line in the format written by {@link Task#toSaveFormat()}
     * @return the task the line represents, with its done status restored
     */
    private static Task parseTask(String line) {
        String[] fields = line.split(" \\| ");
        String type = fields[0];
        boolean isDone = fields[1].equals("1");
        String description = fields[2];

        Task task = switch (type) {
            case "T" -> new Todo(description);
            case "D" -> new Deadline(description, fields[3]);
            case "E" -> new Event(description, fields[3], fields[4]);
            default -> throw new IllegalStateException("Unknown saved task type: " + type);
        };
        if (isDone) {
            task.markAsDone();
        }
        return task;
    }
}
