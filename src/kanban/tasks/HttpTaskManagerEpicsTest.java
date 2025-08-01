package kanban.tasks;

import kanban.http.HttpTaskServer;
import kanban.managers.TaskManager;
import kanban.managers.InMemoryTaskManager;
import kanban.tasks.Epic;
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

public class HttpTaskManagerEpicsTest {
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
    void testCreateGetDeleteEpic() throws Exception {
        Epic e = new Epic("EpicTest","DescEpic");
        String json = gson.toJson(e);
        HttpRequest post = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:3000/epics"))
                .header("Content-Type","application/json")
                .POST(HttpRequest.BodyPublishers.ofString(json))
                .build();
        HttpResponse<Void> postResp = client.send(post, HttpResponse.BodyHandlers.discarding());
        assertEquals(201, postResp.statusCode());

        HttpRequest getAll = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:3000/epics"))
                .GET().build();
        HttpResponse<String> getAllResp = client.send(getAll, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, getAllResp.statusCode());
        Epic[] list = gson.fromJson(getAllResp.body(), Epic[].class);
        assertEquals(1, list.length);
        int id = list[0].getId();

        HttpRequest getOne = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:3000/epics?id="+id))
                .GET().build();
        HttpResponse<String> getOneResp = client.send(getOne, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, getOneResp.statusCode());
        Epic fetched = gson.fromJson(getOneResp.body(), Epic.class);
        assertEquals("EpicTest", fetched.getName());

        HttpRequest delete = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:3000/epics?id="+id))
                .DELETE().build();
        HttpResponse<Void> delResp = client.send(delete, HttpResponse.BodyHandlers.discarding());
        assertEquals(200, delResp.statusCode());

        HttpResponse<String> emptyResp = client.send(getAll, HttpResponse.BodyHandlers.ofString());
        Epic[] empty = gson.fromJson(emptyResp.body(), Epic[].class);
        assertEquals(0, empty.length);
    }
}
