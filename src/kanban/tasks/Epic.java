package kanban.tasks;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class Epic extends Task {

    private final List<Subtask> subtasks;

    public Epic(String name, String description) {
        super(name, description, Status.NEW, Duration.ZERO, null);
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
        updateStatus();
        updateTimeFields();
    }

    public void removeSubtask(Subtask subtask) {
        subtasks.remove(subtask);
        updateStatus();
        updateTimeFields();
    }

    public void updateStatus() {
        if (subtasks.isEmpty()) {
            setStatus(Status.NEW);
            return;
        }

        long doneCount = subtasks.stream()
                .filter(s -> s.getStatus() == Status.DONE)
                .count();
        long newCount = subtasks.stream()
                .filter(s -> s.getStatus() == Status.NEW)
                .count();

        if (doneCount == subtasks.size()) {
            setStatus(Status.DONE);
        } else if (newCount == subtasks.size()) {
            setStatus(Status.NEW);
        } else {
            setStatus(Status.IN_PROGRESS);
        }
    }


    private void updateTimeFields() {
        Duration totalDuration = Duration.ZERO;
        LocalDateTime minStart = null;
        LocalDateTime maxEnd = null;

        for (Subtask sub : subtasks) {
            if (sub.getStartTime() == null || sub.getDuration() == null) {
                continue;
            }

            totalDuration = totalDuration.plus(sub.getDuration());

            if (minStart == null || sub.getStartTime().isBefore(minStart)) {
                minStart = sub.getStartTime();
            }

            LocalDateTime end = sub.getEndTime();
            if (maxEnd == null || (end != null && end.isAfter(maxEnd))) {
                maxEnd = end;
            }
        }

        this.duration = totalDuration;
        this.startTime = minStart;
    }

    @Override
    public LocalDateTime getEndTime() {
        if (startTime == null || duration == null) return null;
        return startTime.plus(duration);
    }

    @Override
    public TaskType getType() {
        return TaskType.EPIC;
    }
}
