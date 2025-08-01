package kanban;

import kanban.managers.FileBackedTaskManager;
import kanban.managers.TaskManager;
import kanban.tasks.*;

import java.io.File;
import java.time.Duration;
import java.time.LocalDateTime;

public class Main {
    public static void main(String[] args) {
        File file = new File("tasks.csv");
        FileBackedTaskManager manager = new FileBackedTaskManager(file);

        // Создание задач
        Task task1 = new Task("Уборка", "Пропылесосить и вымыть пол", Status.NEW,
                Duration.ofMinutes(60), LocalDateTime.of(2025, 7, 20, 10, 0));
        Task task2 = new Task("Покупка еды", "Купить продукты на неделю", Status.IN_PROGRESS,
                Duration.ofMinutes(90), LocalDateTime.of(2025, 7, 20, 12, 0));

        // Эпики
        Epic epic1 = new Epic("Подготовка к отпуску", "Собрать документы, вещи и т.д.");
        Epic epic2 = new Epic("Ремонт в квартире", "Покрасить стены, обновить мебель");

        manager.addEpic(epic1);
        manager.addEpic(epic2);

        // Подзадачи
        Subtask subtask1 = new Subtask("Сбор документов", "Паспорта, билеты", Status.NEW,
                Duration.ofMinutes(30), LocalDateTime.of(2025, 7, 21, 9, 0), epic1);
        Subtask subtask2 = new Subtask("Сбор чемодана", "Одежда и принадлежности", Status.DONE,
                Duration.ofMinutes(45), LocalDateTime.of(2025, 7, 21, 10, 0), epic1);
        Subtask subtask3 = new Subtask("Покупка краски", "Белая и серая краска", Status.IN_PROGRESS,
                Duration.ofMinutes(60), LocalDateTime.of(2025, 7, 22, 15, 0), epic2);

        manager.addTask(task1);
        manager.addTask(task2);
        manager.addSubtask(subtask1);
        manager.addSubtask(subtask2);
        manager.addSubtask(subtask3);

        // Просмотр истории
        manager.getTaskById(task1.getId());
        manager.getSubtaskById(subtask2.getId());
        manager.getEpicById(epic1.getId());

        System.out.println("== Источник: Память ==");
        printState(manager);

        // Загрузка из файла
        //FileBackedTaskManager restored = FileBackedTaskManager.loadFromFile(file);
        //System.out.println("\n== Загружено из файла ==");
        //printState(restored);
    }

    private static void printState(TaskManager manager) {
        System.out.println("\n== Задачи ==");
        for (Task task : manager.getAllTasks()) {
            printTask(task);
        }

        System.out.println("\n== Эпики ==");
        for (Epic epic : manager.getAllEpics()) {
            printTask(epic);
        }

        System.out.println("\n== Подзадачи ==");
        for (Subtask subtask : manager.getAllSubtasks()) {
            printTask(subtask);
        }

         System.out.println("\n== История ==");
        for (Task task : manager.getHistory()) {
            System.out.println(task.getId() + ": " + task.getName());
        }
    }

    private static void printTask(Task task) {
        System.out.printf("ID: %d | %s | Статус: %s | Старт: %s | Длительность: %s | Конец: %s%n",
                task.getId(),
                task.getName(),
                task.getStatus(),
                task.getStartTime(),
                (task.getDuration() != null ? task.getDuration().toMinutes() + " мин" : "не указана"),
                task.getEndTime()
        );
    }
}
