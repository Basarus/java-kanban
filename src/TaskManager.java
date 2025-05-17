import java.util.*;

class TaskManager {
    private final Map<Integer, Task> tasks;
    private final Map<Integer, Epic> epics;

    public TaskManager() {
        tasks = new HashMap<>();
        epics = new HashMap<>();
    }

    public void addTask(Task task) {
        tasks.put(task.getId(), task);
    }

    public void addEpic(Epic epic) {
        epics.put(epic.getId(), epic);
        addTask(epic);
    }

    public void addSubtask(Subtask subtask) {
        Epic epic = subtask.getEpic();
        epic.addSubtask(subtask);
        addTask(subtask);
    }

    public Task getTaskById(int id) {
        return tasks.get(id);
    }

    public Epic getEpicById(int id) {
        return epics.get(id);
    }

    public void updateTask(Task task) {
        tasks.put(task.getId(), task);
    }

    public void updateEpic(Epic epic) {
        epics.put(epic.getId(), epic);
    }

    public void removeTaskById(int id) {
        tasks.remove(id);
    }

    public void removeEpicById(int id) {
        epics.remove(id);
    }

    public List<Task> getAllTasks() {
        return new ArrayList<>(tasks.values());
    }

    public List<Epic> getAllEpics() {
        return new ArrayList<>(epics.values());
    }

    public List<Subtask> getSubtasksByEpic(Epic epic) {
        return epic.getSubtasks();
    }

    public void printTasks() {
        tasks.values().forEach(System.out::println);
    }

    public void printEpics() {
        epics.values().forEach(System.out::println);
    }
}