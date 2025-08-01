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
            String query = uri.getQuery();
            switch (method) {
                case "GET":
                    if (query == null) sendOK(exchange, gson.toJson(manager.getAllEpics()));
                    else {
                        int id = Integer.parseInt(query.split("=")[1]);
                        var epic = manager.getEpicById(id);
                        if (epic != null) sendOK(exchange, gson.toJson(epic));
                        else sendNotFound(exchange);
                    }
                    break;
                case "POST":
                    var epic = gson.fromJson(new InputStreamReader(exchange.getRequestBody(), "UTF-8"), Epic.class);
                    if (epic.getId() == 0) {
                        manager.addEpic(epic);
                        sendCreated(exchange);
                    } else if (manager.getEpicById(epic.getId()) != null) {
                        manager.updateEpic(epic);
                        sendCreated(exchange);
                    } else sendNotFound(exchange);
                    break;
                case "DELETE":
                    if (query == null) {
                        manager.removeAllEpics();
                        sendOK(exchange, "");
                    } else {
                        int id = Integer.parseInt(query.split("=")[1]);
                        if (manager.getEpicById(id) != null) {
                            manager.removeEpicById(id);
                            sendOK(exchange, "");
                        } else sendNotFound(exchange);
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