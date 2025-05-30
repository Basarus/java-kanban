package test;

import kanban.*;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class InMemoryHistoryManagerTest {

    @Test
    void addSingleTaskToHistory() {
        HistoryManager historyManager = new InMemoryHistoryManager();
        Task task = new Task("Task", "desc", Status.NEW);
        historyManager.add(task);

        List<Task> history = historyManager.getHistory();
        assertEquals(1, history.size());
        assertEquals(task, history.get(0));
    }

    @Test
    void historyShouldContainMax10Tasks() {
        HistoryManager historyManager = new InMemoryHistoryManager();
        for (int i = 1; i <= 12; i++) {
            Task task = new Task("Task " + i, "desc", Status.NEW);
            task.setId(i);
            historyManager.add(task);
        }

        List<Task> history = historyManager.getHistory();
        assertEquals(10, history.size());
        assertEquals("Task 3", history.get(0).getName());
    }

    @Test
    void historyPreservesTaskState() {
        HistoryManager history = new InMemoryHistoryManager();
        Task task = new Task("X", "Y", Status.NEW);
        task.setId(1);

        history.add(task);
        task.setStatus(Status.DONE);

        List<Task> hist = history.getHistory();
        assertEquals(Status.DONE, hist.get(0).getStatus());
    }
}
