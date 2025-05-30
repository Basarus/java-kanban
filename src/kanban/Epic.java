package kanban;

import java.util.ArrayList;
import java.util.List;

public class Epic extends Task {
    private final List<Subtask> subtasks;

    public Epic(String name, String description) {
        super(name, description, Status.NEW);
        this.subtasks = new ArrayList<>();
    }

    public List<Subtask> getSubtasks() {
        return subtasks;
    }

    public void addSubtask(Subtask subtask) {

        if (subtask != null && subtask.getId() == this.getId()) {
            throw new IllegalArgumentException("Эпик не может быть своей собственной подзадачей");
        }
        subtasks.add(subtask);

    }

    public void removeSubtask(Subtask subtask) {
        subtasks.remove(subtask);
    }

    public void updateStatus() {
        boolean allDone = true;
        boolean allNew = true;

        for (Subtask subtask : subtasks) {
            if (subtask.getStatus() != Status.DONE) {
                allDone = false;
            }
            if (subtask.getStatus() != Status.NEW) {
                allNew = false;
            }
        }

        if (allDone) {
            setStatus(Status.DONE);
        } else if (allNew) {
            setStatus(Status.NEW);
        } else {
            setStatus(Status.IN_PROGRESS);
        }
    }
}