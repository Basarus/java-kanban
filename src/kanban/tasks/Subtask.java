package kanban.tasks;

public class Subtask extends Task {

    private final Epic epic;

    public Subtask(String name, String description, Status status, Epic epic) {
        super(name, description, status);

        if (epic == null) {
            throw new IllegalArgumentException("Эпик не может быть null");
        }

        this.epic = epic;
    }

    public Epic getEpic() {
        return epic;
    }
}
