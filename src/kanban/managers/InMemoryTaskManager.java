package kanban.managers;

import kanban.tasks.Subtask;
import kanban.tasks.Task;
import kanban.tasks.Epic;

import java.util.*;

public class InMemoryTaskManager implements TaskManager {

    protected static int idCounter = 1;

    protected final Map<Integer, Task> tasks;
    protected final Map<Integer, Epic> epics;
    protected final Map<Integer, Subtask> subtasks;

    protected final HistoryManager historyManager = Managers.getDefaultHistory();

    private final Set<Task> prioritizedTasks = new TreeSet<>(Comparator
            .comparing(Task::getStartTime, Comparator.nullsLast(Comparator.naturalOrder()))
            .thenComparing(Task::getId));

    private boolean isOverlapping(Task a, Task b) {
        if (a.getStartTime() == null || a.getEndTime() == null ||
                b.getStartTime() == null || b.getEndTime() == null) {
            return false;
        }

        return !a.getEndTime().isBefore(b.getStartTime()) &&
                !a.getStartTime().isAfter(b.getEndTime());
    }

    private boolean hasIntersection(Task newTask) {
        return prioritizedTasks.stream()
                .filter(existing -> existing.getId() != newTask.getId())
                .anyMatch(existing -> isOverlapping(existing, newTask));
    }

    public InMemoryTaskManager() {
        tasks = new HashMap<>();
        epics = new HashMap<>();
        subtasks = new HashMap<>();
    }

    public void addTask(Task task) {
        if (task.getStartTime() != null && hasIntersection(task)) {
            throw new IllegalArgumentException("Пересечение задач по времени!");
        }
        task.setId(idCounter++);
        tasks.put(task.getId(), task);
        if (task.getStartTime() != null) {
            prioritizedTasks.add(task);
        }
    }

    public void addEpic(Epic epic) {
        epic.setId(idCounter++);
        epics.put(epic.getId(), epic);
    }

    public void addSubtask(Subtask subtask) {
        if (epics.get(subtask.getEpicId()) == null) {
            throw new IllegalArgumentException("Подзадача должна быть привязана к эпику.");
        }

        Epic epic = epics.get(subtask.getEpicId());

        if (subtask.getId() == epic.getId()) {
            throw new IllegalArgumentException("Подзадача не может ссылаться на свой же эпик (ID совпадают).");
        }

        subtask.setId(idCounter++);
        subtasks.put(subtask.getId(), subtask);
        epic.addSubtask(subtask);
        epic.updateStatus();

        if (subtask.getStartTime() != null) {
            prioritizedTasks.add(subtask);
        }
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
        if (task.getStartTime() != null) {
            prioritizedTasks.remove(tasks.get(task.getId()));
            prioritizedTasks.add(task);
        }
        tasks.put(task.getId(), task);
    }

    public void updateEpic(Epic epic) {
        epics.put(epic.getId(), epic);
    }

    public void updateSubtask(Subtask subtask) {
        if (subtask.getStartTime() != null) {
            prioritizedTasks.remove(subtasks.get(subtask.getId()));
            prioritizedTasks.add(subtask);
        }

        subtasks.put(subtask.getId(), subtask);
        Epic epic = epics.get(subtask.getEpicId());
        epic.updateStatus();
    }

    public void removeTaskById(int id) {
        Task removed = tasks.remove(id);
        if (removed != null) {
            prioritizedTasks.remove(removed);
            historyManager.remove(id);
        }
    }

    public void removeEpicById(int id) {
        Epic epic = epics.remove(id);
        if (epic != null) {
            for (Subtask subtask : epic.getSubtasks()) {
                prioritizedTasks.remove(subtask);
                subtasks.remove(subtask.getId());
                historyManager.remove(subtask.getId());
            }
            historyManager.remove(id);
        }
    }

    public void removeSubtaskById(int id) {
        Subtask subtask = subtasks.remove(id);
        if (subtask != null) {
            Epic epic = epics.get(subtask.getEpicId());
            epic.removeSubtask(subtask);
            epic.updateStatus();
            prioritizedTasks.remove(subtask);
            historyManager.remove(id);
        }
    }

    public void removeAllTasks() {
        for (Integer id : tasks.keySet()) {
            prioritizedTasks.remove(tasks.get(id));
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
            prioritizedTasks.remove(subtasks.get(id));
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
        return epic.getSubtasks().stream()
                .toList();
    }

    @Override
    public List<Task> getHistory() {
        return historyManager.getHistory();
    }

    protected void updateNextId(int lastId) {
        if (lastId >= idCounter) {
            idCounter = lastId + 1;
        }
    }

    public List<Task> getPrioritizedTasks() {
        return new ArrayList<>(prioritizedTasks);
    }
}
