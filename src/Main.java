public class Main {
    public static void main(String[] args) {
        TaskManager taskManager = new TaskManager();

        Task task1 = new Task("Переезд", "Собрать вещи и перевезти их", Status.NEW);
        Task task2 = new Task("Покупка мебели", "Купить мебель для новой квартиры", Status.IN_PROGRESS);

        Epic epic1 = new Epic("Покупка квартиры", "Подготовить все для покупки квартиры");
        Epic epic2 = new Epic("Семейный праздник", "Организовать праздник для всей семьи");

        Subtask subtask1 = new Subtask("Поиск квартиры", "Исследовать рынок недвижимости", Status.NEW, epic1);
        Subtask subtask2 = new Subtask("Проверка документов", "Проверить все документы на квартиру", Status.IN_PROGRESS, epic1);
        Subtask subtask3 = new Subtask("Аренда зала", "Забронировать зал для праздника", Status.DONE, epic2);

        taskManager.addTask(task1);
        taskManager.addTask(task2);
        taskManager.addEpic(epic1);
        taskManager.addEpic(epic2);
        taskManager.addSubtask(subtask1);
        taskManager.addSubtask(subtask2);
        taskManager.addSubtask(subtask3);

        taskManager.printTasks();
        taskManager.printEpics();

        task2.setStatus(Status.DONE);
        epic1.updateStatus();
        epic2.updateStatus();

        taskManager.printTasks();
        taskManager.printEpics();

        taskManager.removeTaskById(task1.getId());
        taskManager.removeEpicById(epic2.getId());

        taskManager.printTasks();
        taskManager.printEpics();
    }
}