package kanban.tasks;

import kanban.http.HttpTaskServer;
import kanban.managers.TaskManager;
import kanban.managers.InMemoryTaskManager;
import kanban.tasks.Task;
import kanban.tasks.Status;
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

public class HttpTaskManagerPrioritizedTest {
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
    void testPrioritizedOrder() throws Exception {
        Task t1 = new Task("A", "A1", Status.NEW, Duration.ofMinutes(10), LocalDateTime.of(2025, 8, 1, 10, 0));
        Task t2 = new Task("B", "B1", Status.NEW, Duration.ofMinutes(5), LocalDateTime.of(2025, 8, 1, 9, 0));
        manager.addTask(t1);
        manager.addTask(t2);

        HttpRequest getPrior = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:3000/prioritized"))
                .GET().build();
        HttpResponse<String> priorResp = client.send(getPrior, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, priorResp.statusCode());
        Task[] ordered = gson.fromJson(priorResp.body(), Task[].class);
        assertEquals(2, ordered.length);
        assertEquals(t2.getName(), ordered[0].getName());
        assertEquals(t1.getName(), ordered[1].getName());
    }
}