package makondr.lab_1.frontend.frontend.controllers;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@Controller
@RequestMapping("/")
public class MainController {

    @GetMapping
    public String index() {
        return "index";
    }

    @PostMapping("getText")
    public String getText(RedirectAttributes redirectAttributes) {
        redirectAttributes.addFlashAttribute("text", getHttpRequest());
        return "redirect:/";
    }

    @PostMapping
    public String sendMessage(@RequestParam String message) {

        postHttpRequest(message);
        return "redirect:/";
    }

    private static String getHttpRequest() {
        HttpClient client = HttpClient.newHttpClient();

        HttpRequest request = HttpRequest.newBuilder(URI.create("http://localhost:8081/api/v2"))
                .GET()
                .build();

        return client.sendAsync(request, HttpResponse.BodyHandlers.ofString())
                .thenApply(HttpResponse::body)
                .thenApply((body) -> {
                    ObjectMapper mapper = new ObjectMapper();
                    String messages;
                    try {
                        JsonNode arrJson = mapper.readTree(body);
                        messages = StreamSupport.stream(arrJson.spliterator(), false)
                                .map(node -> node.get("message").asText())
                                .collect(Collectors.joining("\n"));
                    } catch (JsonProcessingException e) {
                        throw new RuntimeException(e);
                    }
                    return messages;
                })
                .join();
    }

    private static void postHttpRequest(String message) {
        HttpClient client = HttpClient.newHttpClient();

        HttpRequest request = HttpRequest.newBuilder(URI.create("http://localhost:8081/api/v2"))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(convertToJson(message)))
                .build();

        String response = client.sendAsync(request, HttpResponse.BodyHandlers.ofString())
                .thenApply(HttpResponse::body)
                .join();

        System.out.println(response);

    }

    private static String convertToJson(String message) {
        Map<String, String> body = new HashMap<>() {{
            put("message", message);
        }};
        ObjectMapper mapper = new ObjectMapper();
        try {
            return mapper.writeValueAsString(body);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }
}
