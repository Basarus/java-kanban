package kanban;

public class Subtask extends Task {
    private final Epic epic;

    public Subtask(String name, String description, Status status, Epic epic) {
        super(name, description, status);

        if (epic == null) {
            throw new IllegalArgumentException("Эпик не может быть null");
        }

        if (epic.getId() == this.getId()) {
            throw new IllegalArgumentException("Подзадача не может быть своим же эпиком");
        }

        this.epic = epic;
    }

    public Epic getEpic() {
        return epic;
    }
}
