package kanban.http;

import com.google.gson.JsonSyntaxException;
import com.google.gson.Gson;
import com.sun.net.httpserver.HttpExchange;
import kanban.tasks.Subtask;
import kanban.managers.TaskManager;

import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URI;

public class SubtaskHandler extends BaseHttpHandler {
    private final TaskManager manager;
    private final Gson gson = HttpTaskServer.getGson();

    public SubtaskHandler(TaskManager manager) {
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
                    if (q == null) ok(exchange, gson.toJson(manager.getAllSubtasks()));
                    else {
                        int id = Integer.parseInt(q.split("=")[1]);
                        var s = manager.getSubtaskById(id);
                        if (s != null) ok(exchange, gson.toJson(s));
                        else notFound(exchange);
                    }
                    break;
                case "POST":
                    Subtask sub = gson.fromJson(new InputStreamReader(exchange.getRequestBody(), "UTF-8"), Subtask.class);
                    try {
                        if (sub.getId() == 0) {
                            manager.addSubtask(sub);
                            created(exchange);
                        } else if (manager.getSubtaskById(sub.getId()) != null) {
                            manager.updateSubtask(sub);
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
                        manager.removeAllSubtasks();
                        ok(exchange, "");
                    } else {
                        int id = Integer.parseInt(q.split("=")[1]);
                        if (manager.getSubtaskById(id) != null) {
                            manager.removeSubtaskById(id);
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