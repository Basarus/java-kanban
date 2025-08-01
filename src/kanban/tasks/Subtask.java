package kanban.tasks;

public class Subtask extends Task {
    private final int epicId;

    public Subtask(String name, String description, Status status, Epic epic) {
        super(name, description, status);
        this.epicId = epic.getId();
    }

    public Subtask(String name, String description, Status status, int epicId) {
        super(name, description, status);
        this.epicId = epicId;
    }

    public int getEpicId() {
        return epicId;
    }

    @Override
    public TaskType getType() {
        return TaskType.SUBTASK;
    }
}

