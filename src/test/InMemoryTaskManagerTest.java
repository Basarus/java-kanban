package test;
import kanban.*;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class InMemoryTaskManagerTest {

    @Test
    void addAndGetTask() {
        TaskManager manager = new InMemoryTaskManager();
        Task task = new Task("Test task", "Description", Status.NEW);
        manager.addTask(task);

        Task retrieved = manager.getTaskById(task.getId());
        assertNotNull(retrieved);
        assertEquals(task, retrieved);
    }

    @Test
    void historyShouldContainLastViewedTasks() {
        TaskManager manager = new InMemoryTaskManager();

        for (int i = 1; i <= 12; i++) {
            Task task = new Task("Task " + i, "desc", Status.NEW);
            manager.addTask(task);
            manager.getTaskById(task.getId());
        }

        List<Task> history = manager.getHistory();
        assertEquals(10, history.size());
        assertEquals("Task 3", history.get(0).getName());
    }

    @Test
    void managerHandlesAllTaskTypes() {
        TaskManager manager = new InMemoryTaskManager();
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
        TaskManager manager = new InMemoryTaskManager();
        Task t1 = new Task("A", "B", Status.NEW);
        t1.setId(100);
        manager.addTask(t1);

        Task t2 = new Task("Another", "B", Status.NEW);
        manager.addTask(t2);

        assertNotEquals(100, t2.getId());
    }

    @Test
    void taskRemainsUnchangedAfterAdding() {
        TaskManager manager = new InMemoryTaskManager();
        Task original = new Task("Fixed", "Desc", Status.NEW);
        manager.addTask(original);
        Task copy = manager.getTaskById(original.getId());

        assertEquals(original.getName(), copy.getName());
        assertEquals(original.getDescription(), copy.getDescription());
        assertEquals(original.getStatus(), copy.getStatus());
    }
}
