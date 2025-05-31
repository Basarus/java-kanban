package test;

import kanban.managers.InMemoryTaskManager;
import kanban.managers.TaskManager;
import kanban.tasks.Epic;
import kanban.tasks.Status;
import kanban.tasks.Subtask;
import kanban.tasks.Task;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

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
        Task task = new Task("Test task", "Description", Status.NEW);
        manager.addTask(task);

        Task retrieved = manager.getTaskById(task.getId());
        assertNotNull(retrieved);
        assertEquals(task, retrieved);
    }

    @Test
    void historyShouldContainLastViewedTasks() {
        for (int i = 1; i <= 12; i++) {
            Task task = new Task("Task " + i, "desc", Status.NEW);
            manager.addTask(task);
            manager.getTaskById(task.getId());
        }

        List<Task> history = manager.getHistory();
        assertEquals(10, history.size());
        assertEquals("Task 3", history.get(0).getName()); // Task 1 и Task 2 вытеснены
    }

    @Test
    void managerHandlesAllTaskTypes() {
        Task t = new Task("T", "d", Status.NEW);
        Epic e = new Epic("E", "d");
        Subtask s = new Subtask("S", "d", Status.NEW, e);

        manager.addTask(t);
        manager.addEpic(e);
        manager.addSubtask(s);

        assertEquals(t, manager.getTaskById(t.getId()));
        assertEquals(e, manager.getEpicById(e.getId()));
        assertEquals(s, manager.getSubtaskById(s.getId()));
    }

    @Test
    void manuallySetIdDoesNotConflict() {
        Task t1 = new Task("A", "B", Status.NEW);
        t1.setId(100); // вручную устанавливаем id
        manager.addTask(t1);

        Task t2 = new Task("Another", "B", Status.NEW);
        manager.addTask(t2); // должен получить новый id

        assertNotEquals(100, t2.getId());
    }

    @Test
    void taskRemainsUnchangedAfterAdding() {
        Task original = new Task("Fixed", "Desc", Status.NEW);
        manager.addTask(original);
        Task copy = manager.getTaskById(original.getId());

        assertEquals(original.getName(), copy.getName());
        assertEquals(original.getDescription(), copy.getDescription());
        assertEquals(original.getStatus(), copy.getStatus());
    }
}
