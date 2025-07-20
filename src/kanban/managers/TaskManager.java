package kanban.managers;

import kanban.tasks.Task;
import kanban.tasks.Epic;
import kanban.tasks.Subtask;

import java.util.List;

public interface TaskManager {

    void addTask(Task task);

    void addEpic(Epic epic);

    void addSubtask(Subtask subtask);

    Task getTaskById(int id);

    Epic getEpicById(int id);

    Subtask getSubtaskById(int id);

    void updateTask(Task task);

    void updateEpic(Epic epic);

    void updateSubtask(Subtask subtask);

    void removeTaskById(int id);

    void removeEpicById(int id);

    void removeSubtaskById(int id);

    void removeAllTasks();

    void removeAllEpics();

    void removeAllSubtasks();

    List<Task> getAllTasks();

    List<Epic> getAllEpics();

    List<Subtask> getAllSubtasks();

    List<Subtask> getSubtasksByEpic(Epic epic);

    List<Task> getHistory();

    List<Task> getPrioritizedTasks();
}
