// File: Task.java
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * title, timestamp, priority, and recursive subtasks.
 */
public class Task implements Comparable<Task> {
    private final String title;
    private final LocalDateTime addedTime;
    private final int priority;  // lower = higher priority
    private final List<Task> subtasks = new ArrayList<>();

    public Task(String title, LocalDateTime addedTime, int priority) {
        this.title = title;
        this.addedTime = addedTime;
        this.priority = priority;
    }

    public String getTitle() {
        return title;
    }

    public LocalDateTime getAddedTime() {
        return addedTime;
    }

    public int getPriority() {
        return priority;
    }

    public void addSubtask(Task sub) {
        subtasks.add(sub);
    }

    public List<Task> getSubtasks() {
        return List.copyOf(subtasks);
    }

    /**
     * Recursive search for a subtask by title.
     */
    public Task findSubtask(String searchTitle) {
        if (title.equalsIgnoreCase(searchTitle)) {
            return this;
        }
        for (Task sub : subtasks) {
            Task found = sub.findSubtask(searchTitle);
            if (found != null) return found;
        }
        return null;
    }

    @Override
    public int compareTo(Task other) {
        // priority-based ordering (heap sort algorithm under the hood)
        return Integer.compare(this.priority, other.priority);
    }
}
