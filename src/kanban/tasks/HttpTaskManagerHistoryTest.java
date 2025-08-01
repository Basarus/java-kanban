package kanban.tasks;

import kanban.http.HttpTaskServer;
import kanban.managers.TaskManager;
import kanban.managers.InMemoryTaskManager;
import kanban.tasks.Task;
import com.google.gson.Gson;
import org.junit.jupiter.api.*;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

public class HttpTaskManagerHistoryTest {
    private TaskManager manager;
    private HttpTaskServer server;
    private HttpClient client;
    private Gson gson;

    @BeforeEach
    void setUp() throws IOException {
        manager = new InMemoryTaskManager();
        server = new HttpTaskServer(manager);
        server.start();
        client = HttpClient.newHttpClient();
        gson = HttpTaskServer.getGson();
    }

    @AfterEach
    void tearDown() {
        server.stop();
    }

    @Test
    void testHistoryEmptyAndAfterAccess() throws Exception {
        HttpRequest getHistory = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:3000/history"))
                .GET().build();
        HttpResponse<String> historyResp = client.send(getHistory, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, historyResp.statusCode());
        assertEquals("[]", historyResp.body());

        Task t = new Task("HTest", "HD", null, Duration.ofMinutes(10), LocalDateTime.now());
        manager.addTask(t);
        int id = manager.getAllTasks().get(0).getId();
        HttpRequest getOne = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:3000/tasks?id=" + id))
                .GET().build();
        client.send(getOne, HttpResponse.BodyHandlers.ofString());

        historyResp = client.send(getHistory, HttpResponse.BodyHandlers.ofString());
        Task[] hist = gson.fromJson(historyResp.body(), Task[].class);
        assertEquals(1, hist.length);
        assertEquals(id, hist[0].getId());
    }
}