package kanban.tasks;

import com.google.gson.Gson;
import kanban.http.HttpTaskServer;
import kanban.managers.InMemoryTaskManager;
import kanban.managers.TaskManager;
import org.junit.jupiter.api.*;

import java.io.IOException;
import java.net.http.*;
import java.net.URI;
import java.time.Duration;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

public class HttpTaskManagerTasksTest {

    private TaskManager manager;
    private HttpTaskServer server;
    private HttpClient client;
    private Gson gson = HttpTaskServer.getGson();

    @BeforeEach
    void setUp() throws IOException {
        manager = new InMemoryTaskManager();
        server = new HttpTaskServer(manager);
        server.start();
        client = HttpClient.newBuilder()
                .connectTimeout(Duration.ofSeconds(1))
                .build();
    }

    @AfterEach
    void tearDown() {
        server.stop();
    }

    @Test
    void testCreateGetDeleteTask() throws Exception {
        Task t = new Task("Test", "Desc", Status.NEW, Duration.ofMinutes(30), LocalDateTime.now());
        String json = gson.toJson(t);
        HttpRequest post = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:3000/tasks"))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(json))
                .build();
        HttpResponse<Void> postResp = client.send(post, HttpResponse.BodyHandlers.discarding());
        assertEquals(201, postResp.statusCode());

        HttpRequest getAll = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:3000/tasks"))
                .GET().build();
        HttpResponse<String> getAllResp = client.send(getAll, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, getAllResp.statusCode());
        Task[] list = gson.fromJson(getAllResp.body(), Task[].class);
        assertEquals(1, list.length);
        int id = list[0].getId();

        HttpRequest getOne = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:3000/tasks?id=" + id))
                .GET().build();
        HttpResponse<String> getOneResp = client.send(getOne, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, getOneResp.statusCode());
        Task fetched = gson.fromJson(getOneResp.body(), Task.class);
        assertEquals("Test", fetched.getName());

        HttpRequest delete = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:3000/tasks?id=" + id))
                .DELETE().build();
        HttpResponse<Void> delResp = client.send(delete, HttpResponse.BodyHandlers.discarding());
        assertEquals(200, delResp.statusCode());

        HttpResponse<String> emptyResp = client.send(getAll, HttpResponse.BodyHandlers.ofString());
        Task[] empty = gson.fromJson(emptyResp.body(), Task[].class);
        assertEquals(0, empty.length);
    }
}
