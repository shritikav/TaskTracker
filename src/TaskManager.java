import java.io.*;
import java.util.*;

class Task {
    int id;
    String title;
    String status; // "not done", "in progress", "done"

    Task(int id, String title, String status) {
        this.id = id;
        this.title = title;
        this.status = status;
    }

    public String toJson() {
        return String.format("{\"id\":%d,\"title\":\"%s\",\"status\":\"%s\"}", id, title, status);
    }

    public static Task fromJson(String json) {
        json = json.trim().replaceAll("[{}\"]", "");
        String[] parts = json.split(",");
        int id = 0;
        String title = "", status = "";
        for (String part : parts) {
            String[] kv = part.split(":");
            switch (kv[0]) {
                case "id" -> id = Integer.parseInt(kv[1]);
                case "title" -> title = kv[1];
                case "status" -> status = kv[1];
            }
        }
        return new Task(id, title, status);
    }
}

public class TaskManager {
    static final String FILE_NAME = "tasks.json";
    static final Scanner scanner = new Scanner(System.in);

    public static void main(String[] args) {
        List<Task> tasks = loadTasks();

        while (true) {
            System.out.println("\n===== Task Manager Menu =====");
            System.out.println("1. Add a task");
            System.out.println("2. Update a task title");
            System.out.println("3. Delete a task");
            System.out.println("4. Mark task as done / in-progress / not-done");
            System.out.println("5. List all tasks");
            System.out.println("6. List done tasks");
            System.out.println("7. List in-progress tasks");
            System.out.println("8. List not-done tasks");
            System.out.println("9. Exit");
            System.out.print("Choose an option (1-9): ");

            String choice = scanner.nextLine().trim();

            switch (choice) {
                case "1" -> addTask(tasks);
                case "2" -> updateTask(tasks);
                case "3" -> deleteTask(tasks);
                case "4" -> markTask(tasks);
                case "5" -> listTasks(tasks, "all");
                case "6" -> listTasks(tasks, "done");
                case "7" -> listTasks(tasks, "in progress");
                case "8" -> listTasks(tasks, "not done");
                case "9" -> {
                    System.out.println("👋 Exiting Task Manager. Bye!");
                    saveTasks(tasks);
                    return;
                }
                default -> System.out.println("❌ Invalid option. Please try again.");
            }
        }
    }

    static void addTask(List<Task> tasks) {
        System.out.print("Enter task title: ");
        String title = scanner.nextLine();
        int newId = tasks.isEmpty() ? 1 : tasks.get(tasks.size() - 1).id + 1;
        tasks.add(new Task(newId, title, "not done"));
        saveTasks(tasks);
        System.out.println("✅ Task added.");
    }

    static void updateTask(List<Task> tasks) {
        System.out.print("Enter task ID to update: ");
        int id = Integer.parseInt(scanner.nextLine());
        System.out.print("Enter new title: ");
        String newTitle = scanner.nextLine();
        boolean found = false;
        for (Task t : tasks) {
            if (t.id == id) {
                t.title = newTitle;
                found = true;
                break;
            }
        }
        saveTasks(tasks);
        System.out.println(found ? "✅ Task updated." : "❌ Task not found.");
    }

    static void deleteTask(List<Task> tasks) {
        System.out.print("Enter task ID to delete: ");
        int id = Integer.parseInt(scanner.nextLine());
        boolean removed = tasks.removeIf(t -> t.id == id);
        saveTasks(tasks);
        System.out.println(removed ? "🗑️ Task deleted." : "❌ Task not found.");
    }

    static void markTask(List<Task> tasks) {
        System.out.print("Enter task ID to mark: ");
        int id = Integer.parseInt(scanner.nextLine());
        System.out.print("Enter new status (done/in progress/not done): ");
        String status = scanner.nextLine().trim().toLowerCase();
        boolean found = false;
        for (Task t : tasks) {
            if (t.id == id) {
                t.status = status;
                found = true;
                break;
            }
        }
        saveTasks(tasks);
        System.out.println(found ? "✅ Task status updated." : "❌ Task not found.");
    }

    static void listTasks(List<Task> tasks, String filter) {
        System.out.println("\n===== Tasks =====");
        boolean foundAny = false;
        for (Task t : tasks) {
            if (filter.equals("all") || t.status.equalsIgnoreCase(filter)) {
                System.out.printf("📌 [%d] %s - %s%n", t.id, t.title, t.status);
                foundAny = true;
            }
        }
        if (!foundAny) {
            System.out.println("No tasks found for this filter.");
        }
    }

    static List<Task> loadTasks() {
        List<Task> tasks = new ArrayList<>();
        File file = new File(FILE_NAME);
        if (!file.exists()) return tasks;

        try (BufferedReader reader = new BufferedReader(new FileReader(FILE_NAME))) {
            String line;
            while ((line = reader.readLine()) != null) {
                if (!line.trim().isEmpty()) {
                    tasks.add(Task.fromJson(line));
                }
            }
        } catch (IOException e) {
            System.out.println("Error loading tasks: " + e.getMessage());
        }
        return tasks;
    }

    static void saveTasks(List<Task> tasks) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(FILE_NAME))) {
            for (Task t : tasks) {
                writer.write(t.toJson());
                writer.newLine();
            }
        } catch (IOException e) {
            System.out.println("Error saving tasks: " + e.getMessage());
        }
    }
}
