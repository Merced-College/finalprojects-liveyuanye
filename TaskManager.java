
// File: TaskManager.java
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

/**
 * Log entry record (Java record demo).
 */
record LogEntry(LocalDateTime timestamp, String action) {}

// core
public class TaskManager {
    private static final DateTimeFormatter FMT = DateTimeFormatter.ofPattern("yyyy-MM-dd' 'HH:mm:ss");

    // A) Hash table for user authentication
    private final Map<String, User> users = new HashMap<>();
    // A) Priority queue (min-heap) for task ordering
    private final PriorityQueue<Task> taskQueue = new PriorityQueue<>();
    // A) Linked list for activity log
    private final LinkedList<LogEntry> log = new LinkedList<>();
    // A) Stack (Deque) for undo/redo actions
    private final Deque<Runnable> undoStack = new ArrayDeque<>();
    private final Deque<Runnable> redoStack = new ArrayDeque<>();
    // A) Array for recent completed tasks
    private final Task[] recentCompleted = new Task[10];
    private int recentIndex = 0;

    public TaskManager() {
        // initial user
        users.put("alice", new User("alice", "1234"));
    }

    /**
     * Authenticate via hash table in O(1).
     */
    public boolean login(String u, String p) {
        User user = users.get(u);
        if (user != null && user.getPasswordHash().equals(p)) {
            log.add(new LogEntry(LocalDateTime.now(), "Logged in: " + u));
            return true;
        }
        return false;
    }

    /**
     * Add a task and record undo action.
     */
    public void addTask(Task t) {
        taskQueue.offer(t);
        log.add(new LogEntry(LocalDateTime.now(), "Added: " + t.getTitle()));
        undoStack.push(() -> {
            taskQueue.remove(t);
            log.add(new LogEntry(LocalDateTime.now(), "Undo add: " + t.getTitle()));
        });
        redoStack.clear();
    }

    /**
     * Complete highest-priority task, record in array and undo.
     */
    public void completeTask() {
        Task t = taskQueue.poll();
        if (t == null) {
            System.out.println("No tasks to complete.");
            return;
        }
        // circular array insert
        recentCompleted[recentIndex % recentCompleted.length] = t;
        recentIndex++;
        log.add(new LogEntry(LocalDateTime.now(), "Completed: " + t.getTitle()));
        undoStack.push(() -> {
            taskQueue.offer(t);
            log.add(new LogEntry(LocalDateTime.now(), "Undo complete: " + t.getTitle()));
        });
        redoStack.clear();
    }

    /**
     * Undo last action in O(1).
     */
    public void undo() {
        if (undoStack.isEmpty()) {
            System.out.println("Nothing to undo.");
            return;
        }
        Runnable action = undoStack.pop();
        action.run();
        redoStack.push(action);
    }

    /**
     * Redo last undone action in O(1).
     */
    public void redo() {
        if (redoStack.isEmpty()) {
            System.out.println("Nothing to redo.");
            return;
        }
        Runnable action = redoStack.pop();
        action.run();
        undoStack.push(action);
    }

    /**
     * List tasks sorted by priority and show added-time.
     */
    public void listTasks() {
        if (taskQueue.isEmpty()) {
            System.out.println("No pending tasks.");
            return;
        }
        List<Task> list = new ArrayList<>(taskQueue);
        Collections.sort(list);  // O(n log n)
        System.out.println("Pending tasks:");
        for (Task t : list) {
            String added = t.getAddedTime().format(FMT);
            System.out.println("- " + t.getTitle() + " (prior:" + t.getPriority() + ", added:" + added + ")");
        }
    }

    /**
     * Show full activity log (linked list traversal).
     */
    public void showLog() {
        for (LogEntry e : log) {
            System.out.println(e.timestamp().format(FMT) + " - " + e.action());
        }
    }

    /**
     * Recursion demo: find a subtask by title.
     */
    public void searchSubtasks(String title) {
        for (Task t : taskQueue) {
            Task found = t.findSubtask(title);
            if (found != null) {
                System.out.println("Found: " + found.getTitle());
                return;
            }
        }
        System.out.println("Not found: " + title);
    }

    public static void main(String[] args) {
        TaskManager tm = new TaskManager();
        Scanner sc = new Scanner(System.in);

        // login
        System.out.print("Username: "); String u = sc.nextLine();
        System.out.print("Password: "); String p = sc.nextLine();
        if (!tm.login(u, p)) { System.out.println("Login failed."); return; }

        boolean exit = false;
        while (!exit) {
            System.out.println("\nCommands: add, next, search, undo, redo, list, log, exit");
            String cmd = sc.nextLine().trim().toLowerCase();
            switch (cmd) {
                case "add" -> {
                    System.out.print("Title: "); String t = sc.nextLine();
                    System.out.print("Priority (int): "); int pr = Integer.parseInt(sc.nextLine());
                    Task task = new Task(t, LocalDateTime.now().truncatedTo(java.time.temporal.ChronoUnit.SECONDS), pr);
                    tm.addTask(task);
                }
                case "next"  -> tm.completeTask();
                case "undo"  -> tm.undo();
                case "search" -> {
                    System.out.print("Search title: ");
                    String title = sc.nextLine();
                    tm.searchSubtasks(title);
                }
                case "redo"  -> tm.redo();
                case "list"  -> tm.listTasks();
                case "log"   -> tm.showLog();
                case "exit"  -> exit = true;
                default       -> System.out.println("Unknown command.");
            }
        }
        sc.close();
        System.out.println("Goodbye!");
    }
}
