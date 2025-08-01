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
            String query = uri.getQuery();
            switch (method) {
                case "GET":
                    if (query == null) sendOK(exchange, gson.toJson(manager.getAllSubtasks()));
                    else {
                        int id = Integer.parseInt(query.split("=")[1]);
                        var sub = manager.getSubtaskById(id);
                        if (sub != null) sendOK(exchange, gson.toJson(sub));
                        else sendNotFound(exchange);
                    }
                    break;
                case "POST":
                    var subtask = gson.fromJson(new InputStreamReader(exchange.getRequestBody(), "UTF-8"), Subtask.class);
                    if (subtask.getId() == 0) { manager.addSubtask(subtask); sendCreated(exchange); }
                    else if (manager.getSubtaskById(subtask.getId()) != null) { manager.updateSubtask(subtask); sendCreated(exchange); }
                    else sendNotFound(exchange);
                    break;
                case "DELETE":
                    if (query == null) { manager.removeAllSubtasks(); sendOK(exchange, ""); }
                    else {
                        int id = Integer.parseInt(query.split("=")[1]);
                        if (manager.getSubtaskById(id) != null) { manager.removeSubtaskById(id); sendOK(exchange, ""); }
                        else sendNotFound(exchange);
                    }
                    break;
                default: sendInternalError(exchange);
            }
        } catch (NumberFormatException | JsonSyntaxException e) { sendNotAcceptable(exchange); }
        catch (Exception e) { sendInternalError(exchange); }
    }
}