package test;

import kanban.tasks.Epic;
import kanban.tasks.Status;
import kanban.tasks.Subtask;
import kanban.tasks.Task;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class TaskTest {

    @Test
    void tasksWithSameIdShouldBeEqual() {
        Task task1 = new Task("A", "B", Status.NEW);
        Task task2 = new Task("A", "B", Status.NEW);
        task1.setId(1);
        task2.setId(1);

        assertEquals(task1, task2);
    }

    @Test
    void epicAndSubtaskWithSameIdAreEqual() {
        Epic epic1 = new Epic("E1", "desc");
        epic1.setId(5);
        Epic epic2 = new Epic("E1", "desc");
        epic2.setId(5);
        assertEquals(epic1, epic2);

        Epic parent = new Epic("parent", "epic");
        parent.setId(100);

        Subtask s1 = new Subtask("S", "desc", Status.NEW, parent);
        s1.setId(7);
        Subtask s2 = new Subtask("S", "desc", Status.NEW, parent);
        s2.setId(7);
        assertEquals(s1, s2);
    }


    @Test
    void epicCannotContainItselfAsSubtask() {
        Epic epic = new Epic("Эпик", "Описание");
        epic.setId(100);
        Subtask invalidSubtask = new Subtask("Подзадача", "Описание", Status.NEW, epic);
        invalidSubtask.setId(100);

        assertThrows(IllegalArgumentException.class, () -> {
            epic.addSubtask(invalidSubtask);
        });
    }

    @Test
    void subtaskCannotBeItsOwnEpic() {
        Epic epic = new Epic("Epic", "desc");
        assertThrows(IllegalArgumentException.class, () -> {
            new Subtask("Sub", "desc", Status.NEW, (Epic)(Task) epic);
        });
    }
}
