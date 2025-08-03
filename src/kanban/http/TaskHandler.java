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
            String q = uri.getQuery();
            switch (method) {
                case "GET":
                    if (q == null) ok(exchange, gson.toJson(manager.getAllTasks()));
                    else {
                        int id = Integer.parseInt(q.split("=")[1]);
                        var t = manager.getTaskById(id);
                        if (t != null) ok(exchange, gson.toJson(t));
                        else notFound(exchange);
                    }
                    break;
                case "POST":
                    Task task = gson.fromJson(new InputStreamReader(exchange.getRequestBody(), "UTF-8"), Task.class);
                    try {
                        if (task.getId() == 0) {
                            manager.addTask(task);
                            created(exchange);
                        } else if (manager.getTaskById(task.getId()) != null) {
                            manager.updateTask(task);
                            created(exchange);
                        } else {
                            notFound(exchange);
                        }
                    } catch (IllegalArgumentException e) {
                        notAcceptable(exchange);
                    }
                    break;
                case "DELETE":
                    if (q == null) {
                        manager.removeAllTasks();
                        ok(exchange, "");
                    } else {
                        int id = Integer.parseInt(q.split("=")[1]);
                        if (manager.getTaskById(id) != null) {
                            manager.removeTaskById(id);
                            ok(exchange, "");
                        } else notFound(exchange);
                    }
                    break;
                default:
                    error(exchange);
            }
        } catch (NumberFormatException | JsonSyntaxException e) {
            notAcceptable(exchange);
        } catch (Exception e) {
            error(exchange);
        }
    }
}