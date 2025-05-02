// taskmanager/Task.java


import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Task interface and implementation.
 * Supports nested subtasks via recursion.
 */
public class Task implements Comparable<Task> {
    private final String title;
    private final LocalDateTime dueDate;
    private final int priority;  // lower number = higher priority
    private final List<Task> subtasks = new ArrayList<>();

    public Task(String title, LocalDateTime dueDate, int priority) {
        this.title = title;
        this.dueDate = dueDate;
        this.priority = priority;
    }

    public String getTitle() {
        return title;
    }

    public LocalDateTime getDueDate() {
        return dueDate;
    }

    public int getPriority() {
        return priority;
    }

    public void addSubtask(Task sub) {
        subtasks.add(sub);
    }

    public List<Task> getSubtasks() {
        return subtasks;
    }

    /**
     * Recursive search for a subtask by title.
     */
    public Task findSubtask(String searchTitle) {
        if (this.title.equalsIgnoreCase(searchTitle)) {
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
        // priority sorting: O(1) comparison
        return Integer.compare(this.priority, other.priority);
    }
}
