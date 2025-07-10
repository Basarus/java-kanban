package kanban.managers;

import kanban.managers.InMemoryTaskManager;
import kanban.tasks.*;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;

import static kanban.tasks.TaskType.SUBTASK;

public class FileBackedTaskManager extends InMemoryTaskManager {
    private final File file;

    public FileBackedTaskManager(File file) {
        this.file = file;
    }

    @Override
    public void updateSubtask(Subtask subtask) {
        super.updateSubtask(subtask);
        save();
    }

    @Override
    public void removeTaskById(int id) {
        super.removeTaskById(id);
        save();
    }

    @Override
    public void removeEpicById(int id) {
        super.removeEpicById(id);
        save();
    }

    @Override
    public void removeSubtaskById(int id) {
        super.removeSubtaskById(id);
        save();
    }

    @Override
    public void removeAllTasks() {
        super.removeAllTasks();
        save();
    }

    @Override
    public void removeAllEpics() {
        super.removeAllEpics();
        save();
    }

    @Override
    public void removeAllSubtasks() {
        super.removeAllSubtasks();
        save();
    }

    private void save() {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(file))) {

            writer.write("id,type,name,status,description,epic");
            writer.newLine();

            for (Task task : getAllTasks()) {
                writer.write(taskToString(task));
                writer.newLine();
            }

            for (Epic epic : getAllEpics()) {
                writer.write(taskToString(epic));
                writer.newLine();
            }

            for (Subtask subtask : getAllSubtasks()) {
                writer.write(taskToString(subtask));
                writer.newLine();
            }

            writer.newLine();
            writer.write(historyToString());

        } catch (IOException e) {
            throw new ManagerSaveException("Ошибка при сохранении задач в файл: " + file.getName(), e);
        }
    }

    private String taskToString(Task task) {
        StringBuilder sb = new StringBuilder();
        sb.append(task.getId()).append(",");
        sb.append(task.getType()).append(",");
        sb.append(task.getName()).append(",");
        sb.append(task.getStatus()).append(",");
        sb.append(task.getDescription()).append(",");

        if (task instanceof Subtask subtask) {
            sb.append(subtask.getEpicId());
        }

        return sb.toString();
    }

    public Task taskFromString(String line) {
        String[] parts = line.split(",", -1);

        int id = Integer.parseInt(parts[0]);
        TaskType type = TaskType.valueOf(parts[1]);
        String name = parts[2];
        Status status = Status.valueOf(parts[3]);
        String description = parts[4];

        return switch (type) {
            case TASK -> new Task(name, description, status);
            case EPIC -> new Epic(name, description);
            case SUBTASK -> {
                int epicId = Integer.parseInt(parts[5]);
                yield new Subtask(name, description, status, epicId);
            }
        };
    }

    public static FileBackedTaskManager loadFromFile(File file) {
        FileBackedTaskManager manager = new FileBackedTaskManager(file);

        try {
            List<String> lines = Files.readAllLines(file.toPath());
            boolean readingTasks = true;

            for (int i = 1; i < lines.size(); i++) {
                String line = lines.get(i);

                if (line.isBlank()) {
                    readingTasks = false;
                    continue;
                }

                if (readingTasks) {
                    Task task = manager.taskFromString(line);
                    int id = task.getId();
                    manager.updateNextId(id);

                    switch (task.getType()) {
                        case TASK -> manager.tasks.put(id, task);
                        case EPIC -> manager.epics.put(id, (Epic) task);
                        case SUBTASK -> {
                            Subtask subtask = (Subtask) task;
                            manager.subtasks.put(id, subtask);
                            Epic epic = manager.epics.get(subtask.getEpicId());
                            if (epic != null) {
                                epic.addSubtask(subtask);
                            }
                        }
                    }
                } else {
                    List<Integer> historyIds = historyFromString(line);
                    for (Integer id : historyIds) {
                        if (manager.tasks.containsKey(id)) {
                            manager.historyManager.add(manager.tasks.get(id));
                        } else if (manager.epics.containsKey(id)) {
                            manager.historyManager.add(manager.epics.get(id));
                        } else if (manager.subtasks.containsKey(id)) {
                            manager.historyManager.add(manager.subtasks.get(id));
                        }
                    }
                }
            }

        } catch (IOException e) {
            throw new ManagerSaveException("Ошибка при загрузке из файла: " + file.getName(), e);
        }

        return manager;
    }

    private String historyToString() {
        List<Task> history = getHistory();
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < history.size(); i++) {
            sb.append(history.get(i).getId());
            if (i < history.size() - 1) {
                sb.append(",");
            }
        }
        return sb.toString();
    }

    private static List<Integer> historyFromString(String line) {
        List<Integer> history = new ArrayList<>();
        if (line == null || line.isBlank()) return history;

        String[] ids = line.split(",");
        for (String id : ids) {
            history.add(Integer.parseInt(id.trim()));
        }
        return history;
    }
}