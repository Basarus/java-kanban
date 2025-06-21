package kanban.test;

import kanban.managers.HistoryManager;

import kanban.managers.InMemoryHistoryManager;

import kanban.tasks.Status;

import kanban.tasks.Task;

import org.junit.jupiter.api.BeforeEach;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class InMemoryHistoryManagerTest {

    private HistoryManager historyManager;

    @BeforeEach
    void setUp() {
        historyManager = new InMemoryHistoryManager();
    }

    @Test
    void addSingleTaskToHistory() {
        Task task = new Task("Task", "desc", Status.NEW);
        task.setId(1);
        historyManager.add(task);

        List<Task> history = historyManager.getHistory();
        assertEquals(1, history.size());
        assertEquals(task, history.get(0));
    }

    @Test
    void historyShouldNotContainDuplicates() {
        Task task = new Task("Task", "desc", Status.NEW);
        task.setId(1);

        historyManager.add(task);
        historyManager.add(task);
        historyManager.add(task);

        List<Task> history = historyManager.getHistory();
        assertEquals(1, history.size());
        assertEquals(task, history.get(0));
    }

    @Test
    void historyMaintainsCorrectOrder() {
        Task t1 = new Task("T1", "desc", Status.NEW);
        Task t2 = new Task("T2", "desc", Status.NEW);
        Task t3 = new Task("T3", "desc", Status.NEW);
        t1.setId(1);
        t2.setId(2);
        t3.setId(3);

        historyManager.add(t1);
        historyManager.add(t2);
        historyManager.add(t3);

        List<Task> history = historyManager.getHistory();
        assertEquals(List.of(t1, t2, t3), history);
    }

    @Test
    void duplicateMovesTaskToEnd() {
        Task t1 = new Task("T1", "desc", Status.NEW);
        Task t2 = new Task("T2", "desc", Status.NEW);
        t1.setId(1);
        t2.setId(2);

        historyManager.add(t1);
        historyManager.add(t2);
        historyManager.add(t1);

        List<Task> history = historyManager.getHistory();
        assertEquals(List.of(t2, t1), history);
    }

    @Test
    void removeTaskFromHistory() {
        Task t1 = new Task("T1", "desc", Status.NEW);
        Task t2 = new Task("T2", "desc", Status.NEW);
        t1.setId(1);
        t2.setId(2);

        historyManager.add(t1);
        historyManager.add(t2);
        historyManager.remove(1);

        List<Task> history = historyManager.getHistory();
        assertEquals(1, history.size());
        assertEquals(t2, history.get(0));
    }

    @Test
    void removeHeadAndTail() {
        Task t1 = new Task("Head", "desc", Status.NEW);
        Task t2 = new Task("Middle", "desc", Status.NEW);
        Task t3 = new Task("Tail", "desc", Status.NEW);
        t1.setId(1);
        t2.setId(2);
        t3.setId(3);

        historyManager.add(t1);
        historyManager.add(t2);
        historyManager.add(t3);
        historyManager.remove(1);
        historyManager.remove(3);

        List<Task> history = historyManager.getHistory();
        assertEquals(List.of(t2), history);
    }

    @Test
    void historyPreservesTaskState() {
        Task task = new Task("X", "Y", Status.NEW);
        task.setId(1);

        historyManager.add(task);
        task.setStatus(Status.DONE);

        List<Task> hist = historyManager.getHistory();
        assertEquals(Status.DONE, hist.get(0).getStatus());
    }

    @Test
    void removeNonExistentTaskDoesNothing() {
        Task task = new Task("Task", "desc", Status.NEW);
        task.setId(1);
        historyManager.add(task);
        historyManager.remove(999);

        List<Task> history = historyManager.getHistory();
        assertEquals(1, history.size());
        assertEquals(task, history.get(0));
    }

    @Test
    void addNullTaskIsIgnored() {
        historyManager.add(null);
        List<Task> history = historyManager.getHistory();
        assertTrue(history.isEmpty());
    }
}
