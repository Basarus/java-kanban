package kanban.http;

import com.google.gson.JsonSyntaxException;
import com.google.gson.Gson;
import com.sun.net.httpserver.HttpExchange;
import kanban.managers.TaskManager;
import kanban.tasks.Task;

import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URI;

public class TaskHandler extends BaseHttpHandler {
    private final TaskManager manager;
    private final Gson gson = HttpTaskServer.getGson();

    public TaskHandler(TaskManager manager) {
        this.manager = manager;
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        try {
            String method = exchange.getRequestMethod();
            URI uri = exchange.getRequestURI();
            String query = uri.getQuery();

            switch (method) {
                case "GET":
                    if (query == null) {
                        String json = gson.toJson(manager.getAllTasks());
                        sendOK(exchange, json);
                    } else {
                        int id = Integer.parseInt(query.split("=")[1]);
                        Task task = manager.getTaskById(id);
                        if (task != null) {
                            sendOK(exchange, gson.toJson(task));
                        } else {
                            sendNotFound(exchange);
                        }
                    }
                    break;
                case "POST":
                    Task task = gson.fromJson(new InputStreamReader(exchange.getRequestBody(), "UTF-8"), Task.class);
                    if (task.getId() == 0) {
                        manager.addTask(task);
                        sendCreated(exchange);
                    } else {
                        if (manager.getTaskById(task.getId()) != null) {
                            manager.updateTask(task);
                            sendCreated(exchange);
                        } else {
                            sendNotFound(exchange);
                        }
                    }
                    break;
                case "DELETE":
                    if (query == null) {
                        manager.removeAllTasks();
                        sendOK(exchange, "");
                    } else {
                        int id = Integer.parseInt(query.split("=")[1]);
                        if (manager.getTaskById(id) != null) {
                            manager.removeTaskById(id);
                            sendOK(exchange, "");
                        } else {
                            sendNotFound(exchange);
                        }
                    }
                    break;
                default:
                    sendInternalError(exchange);
            }
        } catch (NumberFormatException | JsonSyntaxException e) {
            sendNotAcceptable(exchange);
        } catch (Exception e) {
            sendInternalError(exchange);
        }
    }
}