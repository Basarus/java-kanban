package kanban.http;

import com.sun.net.httpserver.HttpExchange;
import kanban.managers.TaskManager;
import com.google.gson.Gson;

import java.io.IOException;

public class HistoryHandler extends BaseHttpHandler {
    private final TaskManager manager;
    private final Gson gson = HttpTaskServer.getGson();

    public HistoryHandler(TaskManager manager) {
        this.manager = manager;
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        if (!"GET".equals(exchange.getRequestMethod())) {
            sendInternalError(exchange);
            return;
        }
        var history = manager.getHistory();
        sendOK(exchange, gson.toJson(history));
    }
}
