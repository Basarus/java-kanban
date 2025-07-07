package kanban;

import kanban.managers.Managers;
import kanban.managers.TaskManager;
import kanban.tasks.Epic;
import kanban.tasks.Status;
import kanban.tasks.Subtask;
import kanban.tasks.Task;

public class Main {
    public static void main(String[] args) {
        TaskManager manager = Managers.getDefault();

        Task task1 = new Task("Переезд", "Собрать вещи и перевезти их", Status.NEW);
        Task task2 = new Task("Покупка мебели", "Купить мебель для новой квартиры", Status.IN_PROGRESS);

        Epic epic1 = new Epic("Покупка квартиры", "Подготовить все для покупки квартиры");
        Epic epic2 = new Epic("Семейный праздник", "Организовать праздник для всей семьи");

        Subtask subtask1 = new Subtask("Поиск квартиры", "Исследовать рынок недвижимости", Status.NEW, epic1);
        Subtask subtask2 = new Subtask("Проверка документов", "Проверить все документы на квартиру", Status.IN_PROGRESS, epic1);
        Subtask subtask3 = new Subtask("Аренда зала", "Забронировать зал для праздника", Status.DONE, epic2);

        manager.addTask(task1);
        manager.addTask(task2);
        manager.addEpic(epic1);
        manager.addEpic(epic2);
        manager.addSubtask(subtask1);
        manager.addSubtask(subtask2);
        manager.addSubtask(subtask3);

        manager.getTaskById(task1.getId());
        manager.getTaskById(task2.getId());
        manager.getEpicById(epic1.getId());
        manager.getSubtaskById(subtask2.getId());

        for (int i = 1; i <= 10; i++) {
            Task extra = new Task("Задача " + i, "Описание " + i, Status.NEW);
            manager.addTask(extra);
            manager.getTaskById(extra.getId());
        }

        printAll(manager);

        manager.getSubtaskById(subtask1.getId());
        manager.getEpicById(epic1.getId());
        manager.getSubtaskById(subtask2.getId());
        manager.getTaskById(task2.getId());
        manager.getSubtaskById(subtask1.getId());
        manager.getEpicById(epic2.getId());

        printHistory(manager, "После повторных запросов:");

        manager.removeTaskById(task2.getId());
        printHistory(manager, "После удаления task2:");

        manager.removeEpicById(epic1.getId());
        printHistory(manager, "После удаления epic1 и его подзадач:");

        task2.setStatus(Status.DONE);
        epic1.updateStatus();
        epic2.updateStatus();

        manager.removeTaskById(task1.getId());
        manager.removeEpicById(epic2.getId());

        printAll(manager);
    }

    private static void printAll(TaskManager manager) {
        System.out.println("\n==== Текущие задачи ====");
        for (Task task : manager.getAllTasks()) {
            System.out.println(task);
        }

        System.out.println("\n==== Эпики ====");
        for (Epic epic : manager.getAllEpics()) {
            System.out.println(epic);
            for (Subtask sub : manager.getSubtasksByEpic(epic)) {
                System.out.println("  --> " + sub);
            }
        }

        System.out.println("\n==== Подзадачи ====");
        for (Subtask subtask : manager.getAllSubtasks()) {
            System.out.println(subtask);
        }

        printHistory(manager, "История просмотров:");
    }

    private static void printHistory(TaskManager manager, String title) {
        System.out.println("\n==== " + title + " ====");
        for (Task task : manager.getHistory()) {
            System.out.println(task);
        }
    }
}
