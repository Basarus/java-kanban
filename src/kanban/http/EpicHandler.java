package kanban.http;

import com.google.gson.JsonSyntaxException;
import com.google.gson.Gson;
import com.sun.net.httpserver.HttpExchange;
import kanban.tasks.Epic;
import kanban.managers.TaskManager;

import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URI;

public class EpicHandler extends BaseHttpHandler {
    private final TaskManager manager;
    private final Gson gson = HttpTaskServer.getGson();

    public EpicHandler(TaskManager manager) {
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
                    if (q == null) ok(exchange, gson.toJson(manager.getAllEpics()));
                    else {
                        int id = Integer.parseInt(q.split("=")[1]);
                        var e = manager.getEpicById(id);
                        if (e != null) ok(exchange, gson.toJson(e));
                        else notFound(exchange);
                    }
                    break;
                case "POST":
                    Epic epic = gson.fromJson(new InputStreamReader(exchange.getRequestBody(), "UTF-8"), Epic.class);
                    if (epic.getId() == 0) {
                        manager.addEpic(epic);
                        created(exchange);
                    } else if (manager.getEpicById(epic.getId()) != null) {
                        manager.updateEpic(epic);
                        created(exchange);
                    } else {
                        notFound(exchange);
                    }
                    break;
                case "DELETE":
                    if (q == null) {
                        manager.removeAllEpics();
                        ok(exchange, "");
                    } else {
                        int id = Integer.parseInt(q.split("=")[1]);
                        if (manager.getEpicById(id) != null) {
                            manager.removeEpicById(id);
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