package makondr.lab_1.frontend.frontend.controllers;

import org.apache.logging.log4j.message.Message;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Map;

@Controller
@RequestMapping("/")
public class MainController {

    @GetMapping
    public String index() {
        return "index";
    }

    @PostMapping
    public String sendMessage(@RequestBody String message) {

        getHttpRequest(message);
        return "index";
    }

    private static void getHttpRequest(String message) {
        HttpClient client = HttpClient.newHttpClient();

        HttpRequest request = HttpRequest.newBuilder(URI.create("http://localhost:8081/api/v1"))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(message))
                .build();

        client.sendAsync(request, HttpResponse.BodyHandlers.ofString())
                .thenApply(HttpResponse::body)
                .thenAccept(System.out::println)
                .join();
    }
}
