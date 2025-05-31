package test;

import kanban.managers.HistoryManager;
import kanban.managers.Managers;
import kanban.managers.TaskManager;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ManagersTest {

    @Test
    void managersShouldReturnWorkingInstances() {
        assertNotNull(Managers.getDefault());
        assertNotNull(Managers.getDefaultHistory());
    }

    @Test
    void getDefaultTaskManagerShouldNotBeNull() {
        TaskManager manager = Managers.getDefault();
        assertNotNull(manager);
    }

    @Test
    void getDefaultHistoryManagerShouldNotBeNull() {
        HistoryManager historyManager = Managers.getDefaultHistory();
        assertNotNull(historyManager);
    }
}
