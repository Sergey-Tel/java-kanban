package dev.service;

import com.google.gson.JsonElement;
import com.google.gson.JsonParser;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Optional;

public class KVTaskClient {
    private final String host;
    private final int port;
    private String API_TOKEN;
    private final HttpClient client;

    public KVTaskClient(String host, int port) {
        client = HttpClient.newHttpClient();
        this.host = host;
        this.port = port;
    }

    public void put(String key, String json) throws IOException, InterruptedException {
        URI url = URI.create(host + ":" + port + "/save/"+key+"?API_TOKEN=" + API_TOKEN);
        final HttpRequest.BodyPublisher body = HttpRequest.BodyPublishers.ofString(json);
        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .POST(body)
                .build();
        client.send(request, HttpResponse.BodyHandlers.ofString());
    }

    public Optional<String> load(String key) {
        URI url = URI.create(host + ":" + port + "/load/"+key+"?API_TOKEN=" + API_TOKEN);
        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .GET()
                .build();
        try {
            final HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            if (response.statusCode() == 200) {
                return Optional.of(response.body());
            }
        } catch (Exception ex) {
            System.out.println("Во время выполнения запроса возникла ошибка.\n" +
                    "Проверьте, пожалуйста, адрес и повторите попытку.");
        }
        return Optional.empty();
    }

    public void register() {
        URI url = URI.create(host + ":" + port + "/register");
        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .GET()
                .build();
        try {
            final HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            if (response.statusCode() == 200) {
                JsonElement jsonElement = JsonParser.parseString(response.body());
                API_TOKEN = jsonElement.getAsString();
            }
        } catch (Exception ex) {
            System.out.println("Во время выполнения запроса возникла ошибка.\n" +
                    "Проверьте, пожалуйста, адрес и повторите попытку.");
        }
    }
}