package test;

import kanban.managers.FileBackedTaskManager;
import kanban.managers.InMemoryTaskManager;
import kanban.managers.TaskManager;
import kanban.tasks.Epic;
import kanban.tasks.Status;
import kanban.tasks.Subtask;
import kanban.tasks.Task;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class InMemoryTaskManagerTest {

    private TaskManager manager;

    @BeforeEach
    void setUp() {
        manager = new InMemoryTaskManager();
    }

    @Test
    void addAndGetTask() {
        Task task = new Task("Test task", "Description", Status.NEW,
                Duration.ofMinutes(15), LocalDateTime.now());
        manager.addTask(task);

        Task retrieved = manager.getTaskById(task.getId());
        assertNotNull(retrieved);
        assertEquals(task, retrieved);
    }

    @Test
    void historyShouldContainLastViewedTasks() {
        for (int i = 1; i <= 12; i++) {
            Task task = new Task("Task " + i, "desc", Status.NEW,
                    Duration.ofMinutes(5), LocalDateTime.of(2025, 7, 20, 10, 0).plusMinutes(i * 10));
            manager.addTask(task);
            manager.getTaskById(task.getId());
        }

        List<Task> history = manager.getHistory();
        assertEquals(12, history.size());
        assertEquals("Task 3", history.get(2).getName());
    }

    @Test
    void managerHandlesAllTaskTypes() {
        Task t = new Task("T", "d", Status.NEW,
                Duration.ofMinutes(20), LocalDateTime.now());
        Epic e = new Epic("E", "d");
        manager.addTask(t);
        manager.addEpic(e);

        Subtask s = new Subtask("S", "d", Status.NEW,
                Duration.ofMinutes(25), LocalDateTime.now(), e.getId());
        manager.addSubtask(s);

        assertEquals(t, manager.getTaskById(t.getId()));
        assertEquals(e, manager.getEpicById(e.getId()));
        assertEquals(s, manager.getSubtaskById(s.getId()));
    }

    @Test
    void manuallySetIdDoesNotConflict() {
        Task t1 = new Task("A", "B", Status.NEW,
                Duration.ofMinutes(15), LocalDateTime.of(2025, 7, 20, 10, 0));
        t1.setId(100);
        manager.addTask(t1);

        Task t2 = new Task("Another", "B", Status.NEW,
                Duration.ofMinutes(15), LocalDateTime.of(2025, 7, 20, 11, 0));
        manager.addTask(t2);

        assertNotEquals(100, t2.getId());
    }

    @Test
    void taskRemainsUnchangedAfterAdding() {
        Task original = new Task("Fixed", "Desc", Status.NEW,
                Duration.ofMinutes(20), LocalDateTime.now());
        manager.addTask(original);
        Task copy = manager.getTaskById(original.getId());

        assertEquals(original.getName(), copy.getName());
        assertEquals(original.getDescription(), copy.getDescription());
        assertEquals(original.getStatus(), copy.getStatus());
    }

    @Test
    void subtaskCannotBeItsOwnEpic() {
        InMemoryTaskManager manager = new InMemoryTaskManager();

        Epic epic = new Epic("Epic", "desc");
        manager.addEpic(epic);

        Subtask subtask = new Subtask("Sub", "desc", Status.NEW,
                Duration.ofMinutes(30), LocalDateTime.now(), epic);
        subtask.setId(epic.getId());

        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> manager.addSubtask(subtask)
        );

        assertEquals("Подзадача не может ссылаться на свой же эпик (ID совпадают).", exception.getMessage());
    }

    @Test
    void shouldThrowIfTasksIntersectInTime() {
        Task t1 = new Task("T1", "desc", Status.NEW,
                Duration.ofMinutes(60), LocalDateTime.of(2025, 7, 20, 10, 0));
        Task t2 = new Task("T2", "desc", Status.NEW,
                Duration.ofMinutes(30), LocalDateTime.of(2025, 7, 20, 10, 30));
        manager.addTask(t1);
        assertThrows(IllegalArgumentException.class, () -> manager.addTask(t2));
    }

    @Test
    void prioritizedTasksShouldBeSortedByStartTime() {
        Task t1 = new Task("T1", "desc", Status.NEW,
                Duration.ofMinutes(30), LocalDateTime.of(2025, 7, 20, 12, 0));
        Task t2 = new Task("T2", "desc", Status.NEW,
                Duration.ofMinutes(30), LocalDateTime.of(2025, 7, 20, 10, 0));
        Task t3 = new Task("T3", "desc", Status.NEW,
                Duration.ofMinutes(30), LocalDateTime.of(2025, 7, 20, 11, 0));

        manager.addTask(t1);
        manager.addTask(t2);
        manager.addTask(t3);

        List<Task> sorted = manager.getPrioritizedTasks();
        assertEquals(List.of(t2, t3, t1), sorted);
    }

    @Test
    void shouldSaveAndLoadTaskWithStartTimeAndDuration() {
        File file = new File("test-tasks.csv");
        FileBackedTaskManager manager = new FileBackedTaskManager(file);

        Task task = new Task("Serializable", "Test", Status.NEW,
                Duration.ofMinutes(45), LocalDateTime.of(2025, 7, 21, 13, 0));
        manager.addTask(task);  // автоматически сохранит в файл

        FileBackedTaskManager reloaded = FileBackedTaskManager.loadFromFile(file);
        Task loaded = reloaded.getTaskById(task.getId());

        assertEquals(task.getStartTime(), loaded.getStartTime());
        assertEquals(task.getDuration(), loaded.getDuration());
    }

    @Test
    void getEndTimeShouldReturnCorrectTime() {
        LocalDateTime start = LocalDateTime.of(2025, 7, 22, 9, 0);
        Duration duration = Duration.ofMinutes(90);
        Task task = new Task("EndTime", "Check", Status.NEW, duration, start);

        assertEquals(start.plusMinutes(90), task.getEndTime());
    }
}
