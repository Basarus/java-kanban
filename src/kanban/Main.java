package kanban;

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

        printAll(manager);

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

        System.out.println("\n==== История просмотров ====");
        for (Task task : manager.getHistory()) {
            System.out.println(task);
        }
    }
}
