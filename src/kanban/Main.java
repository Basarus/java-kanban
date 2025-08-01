package kanban;

import kanban.managers.FileBackedTaskManager;
import kanban.managers.TaskManager;
import kanban.tasks.*;

import java.io.File;

public class Main {
    public static void main(String[] args) {
        File file = new File("tasks.csv");
        FileBackedTaskManager manager = new FileBackedTaskManager(file);

        Task task1 = new Task("Уборка", "Пропылесосить и вымыть пол", Status.NEW);
        Task task2 = new Task("Покупка еды", "Купить продукты на неделю", Status.IN_PROGRESS);

        Epic epic1 = new Epic("Подготовка к отпуску", "Собрать документы, вещи и т.д.");
        Epic epic2 = new Epic("Ремонт в квартире", "Покрасить стены, обновить мебель");

        manager.addEpic(epic1);
        manager.addEpic(epic2);

        Subtask subtask1 = new Subtask("Сбор документов", "Паспорта, билеты", Status.NEW, epic1);
        Subtask subtask2 = new Subtask("Сбор чемодана", "Одежда и принадлежности", Status.DONE, epic1);
        Subtask subtask3 = new Subtask("Покупка краски", "Белая и серая краска", Status.IN_PROGRESS, epic2);

        manager.addTask(task1);
        manager.addTask(task2);
        manager.addSubtask(subtask1);
        manager.addSubtask(subtask2);
        manager.addSubtask(subtask3);

        manager.getTaskById(task1.getId());
        manager.getSubtaskById(subtask2.getId());
        manager.getEpicById(epic1.getId());

        System.out.println("== Источник: Память ==");
        printState(manager);

        FileBackedTaskManager restored = FileBackedTaskManager.loadFromFile(file);

        System.out.println("\n== Загружено из файла ==");
        printState(restored);
    }

    private static void printState(TaskManager manager) {
        System.out.println("\n== Задачи ==");
        for (Task task : manager.getAllTasks()) {
            System.out.println(task);
        }

        System.out.println("\n== Эпики ==");
        for (Epic epic : manager.getAllEpics()) {
            System.out.println(epic);
        }

        System.out.println("\n== Подзадачи ==");
        for (Subtask subtask : manager.getAllSubtasks()) {
            System.out.println(subtask);
        }

        System.out.println("\n== История ==");
        for (Task task : manager.getHistory()) {
            System.out.println(task);
        }
    }
}

