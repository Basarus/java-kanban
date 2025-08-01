package kanban.tasks;

import kanban.http.HttpTaskServer;
import kanban.managers.TaskManager;
import kanban.managers.InMemoryTaskManager;
import kanban.tasks.Subtask;
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

public class HttpTaskManagerSubtasksTest {
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
    void testCreateGetDeleteSubtask() throws Exception {
        Epic parent = new Epic("ParentEpic", "DescParent");
        String epicJson = gson.toJson(parent);
        HttpRequest postEpic = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:3000/epics"))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(epicJson))
                .build();
        HttpResponse<Void> epicResp = client.send(postEpic, HttpResponse.BodyHandlers.discarding());
        assertEquals(201, epicResp.statusCode());

        HttpRequest getEpics = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:3000/epics"))
                .GET().build();
        HttpResponse<String> getEpicsResp = client.send(getEpics, HttpResponse.BodyHandlers.ofString());
        Epic[] epics = gson.fromJson(getEpicsResp.body(), Epic[].class);
        int epicId = epics[0].getId();

        Subtask s = new Subtask("SubTest", "DescSub", Status.NEW, Duration.ofMinutes(20), LocalDateTime.now(), epicId);
        String json = gson.toJson(s);
        HttpRequest post = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:3000/subtasks"))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(json))
                .build();
        HttpResponse<Void> postResp = client.send(post, HttpResponse.BodyHandlers.discarding());
        assertEquals(201, postResp.statusCode());

        HttpRequest getAll = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:3000/subtasks"))
                .GET().build();
        HttpResponse<String> getAllResp = client.send(getAll, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, getAllResp.statusCode());
        Subtask[] list = gson.fromJson(getAllResp.body(), Subtask[].class);
        assertEquals(1, list.length);
        int id = list[0].getId();

        HttpRequest getOne = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:3000/subtasks?id=" + id))
                .GET().build();
        HttpResponse<String> getOneResp = client.send(getOne, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, getOneResp.statusCode());
        Subtask fetched = gson.fromJson(getOneResp.body(), Subtask.class);
        assertEquals("SubTest", fetched.getName());

        HttpRequest delete = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:3000/subtasks?id=" + id))
                .DELETE().build();
        HttpResponse<Void> delResp = client.send(delete, HttpResponse.BodyHandlers.discarding());
        assertEquals(200, delResp.statusCode());

        HttpResponse<String> emptyResp = client.send(getAll, HttpResponse.BodyHandlers.ofString());
        Subtask[] empty = gson.fromJson(emptyResp.body(), Subtask[].class);
        assertEquals(0, empty.length);
    }
}

