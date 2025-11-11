package com.uade.tpo.views.util;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

public class HttpJson {
    private final HttpClient http = HttpClient.newHttpClient();
    private final ObjectMapper mapper = new ObjectMapper();
    private final String base;

    public HttpJson(String baseUrl) { this.base = baseUrl; }

    public String get(String pathAndQuery) {
        try {
            HttpRequest rq = HttpRequest.newBuilder(URI.create(base + pathAndQuery)).GET().build();
            HttpResponse<String> rs = http.send(rq, HttpResponse.BodyHandlers.ofString());
            if (rs.statusCode() >= 200 && rs.statusCode() < 300) return rs.body();
            throw new RuntimeException("GET " + pathAndQuery + " -> " + rs.statusCode() + " | " + rs.body());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public String postJson(String path, Object body) {
        try {
            String json = mapper.writeValueAsString(body);
            HttpRequest rq = HttpRequest.newBuilder(URI.create(base + path))
                    .header("Content-Type", "application/json; charset=utf-8")
                    .POST(HttpRequest.BodyPublishers.ofString(json))
                    .build();
            HttpResponse<String> rs = http.send(rq, HttpResponse.BodyHandlers.ofString());
            if (rs.statusCode() >= 200 && rs.statusCode() < 300) return rs.body();
            throw new RuntimeException("POST " + path + " -> " + rs.statusCode() + " | " + rs.body());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public String post(String path) {
        try {
            HttpRequest rq = HttpRequest.newBuilder(URI.create(base + path))
                    .POST(HttpRequest.BodyPublishers.noBody())
                    .build();
            HttpResponse<String> rs = http.send(rq, HttpResponse.BodyHandlers.ofString());
            if (rs.statusCode() >= 200 && rs.statusCode() < 300) return rs.body();
            throw new RuntimeException("POST " + path + " -> " + rs.statusCode() + " | " + rs.body());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public <T> T read(String json, TypeReference<T> type) {
        try { return mapper.readValue(json, type); } catch (Exception e) { throw new RuntimeException(e); }
    }
}
