/**
 * Represents a task that needs to be done before a specific date or time.
 */
public class Deadline extends Task {
    /** The date/time text by which this task should be completed, e.g. "Sunday". */
    protected String by;

    /**
     * Creates a new deadline with the given description and due date/time.
     *
     * @param description the text describing this task
     * @param by the date/time text by which this task should be done
     */
    public Deadline(String description, String by) {
        super(description);
        this.by = by;
    }

    @Override
    public String toString() {
        return "[D]" + super.toString() + " (by: " + this.by + ")";
    }
}
