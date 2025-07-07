package kanban.managers;

import kanban.tasks.Subtask;
import kanban.tasks.Task;
import kanban.tasks.Epic;

import java.util.*;

public class InMemoryTaskManager implements TaskManager {

    private static int idCounter = 1;

    private final Map<Integer, Task> tasks;

    private final Map<Integer, Epic> epics;

    private final Map<Integer, Subtask> subtasks;

    private final HistoryManager historyManager = Managers.getDefaultHistory();

    public InMemoryTaskManager() {
        tasks = new HashMap<>();
        epics = new HashMap<>();
        subtasks = new HashMap<>();
    }

    public void addTask(Task task) {
        task.setId(idCounter++);
        tasks.put(task.getId(), task);
    }

    public void addEpic(Epic epic) {
        epic.setId(idCounter++);
        epics.put(epic.getId(), epic);
    }

    public void addSubtask(Subtask subtask) {
        if (subtask.getEpic() == null) {
            throw new IllegalArgumentException("Подзадача должна быть привязана к эпику.");
        }

        Epic epic = subtask.getEpic();

        if (subtask.getId() == epic.getId()) {
            throw new IllegalArgumentException("Подзадача не может ссылаться на свой же эпик (ID совпадают).");
        }

        subtask.setId(idCounter++);

        subtasks.put(subtask.getId(), subtask);
        epic.addSubtask(subtask);
        epic.updateStatus();
    }

    public Task getTaskById(int id) {
        Task task = tasks.get(id);
        historyManager.add(task);
        return task;
    }

    public Epic getEpicById(int id) {
        Epic epic = epics.get(id);
        if (epic != null) {
            historyManager.add(epic);
        }
        return epic;
    }

    public Subtask getSubtaskById(int id) {
        Subtask subtask = subtasks.get(id);
        if (subtask != null) {
            historyManager.add(subtask);
        }
        return subtask;
    }

    public void updateTask(Task task) {
        tasks.put(task.getId(), task);
    }

    public void updateEpic(Epic epic) {
        epics.put(epic.getId(), epic);
    }

    public void updateSubtask(Subtask subtask) {
        subtasks.put(subtask.getId(), subtask);
        Epic epic = subtask.getEpic();
        epic.updateStatus();
    }

    public void removeTaskById(int id) {
        tasks.remove(id);
        historyManager.remove(id);
    }

    public void removeEpicById(int id) {
        Epic epic = epics.remove(id);
        if (epic != null) {
            for (Subtask subtask : epic.getSubtasks()) {
                subtasks.remove(subtask.getId());
                historyManager.remove(subtask.getId());
            }
            historyManager.remove(id);
        }
    }

    public void removeSubtaskById(int id) {
        Subtask subtask = subtasks.remove(id);
        if (subtask != null) {
            Epic epic = subtask.getEpic();
            epic.removeSubtask(subtask);
            epic.updateStatus();
            historyManager.remove(id);
        }
    }

    public void removeAllTasks() {
        for (Integer id : tasks.keySet()) {
            historyManager.remove(id);
        }
        tasks.clear();
    }

    public void removeAllEpics() {
        for (Epic epic : epics.values()) {
            historyManager.remove(epic.getId());
            for (Subtask subtask : epic.getSubtasks()) {
                historyManager.remove(subtask.getId());
            }
        }
        epics.clear();
        subtasks.clear();
    }

    public void removeAllSubtasks() {
        for (Integer id : subtasks.keySet()) {
            historyManager.remove(id);
        }
        subtasks.clear();
    }

    public List<Task> getAllTasks() {
        return new ArrayList<>(tasks.values());
    }

    public List<Epic> getAllEpics() {
        return new ArrayList<>(epics.values());
    }

    public List<Subtask> getAllSubtasks() {
        return new ArrayList<>(subtasks.values());
    }

    public List<Subtask> getSubtasksByEpic(Epic epic) {
        return epic.getSubtasks();
    }

    @Override
    public List<Task> getHistory() {
        return historyManager.getHistory();
    }

}
