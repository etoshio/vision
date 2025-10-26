package com.example.worker;

import com.example.worker.consumer.UserProcessor;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;

import static org.junit.jupiter.api.Assertions.*;

public class UserProcessorTest {

  private HttpServer server;
  private int port;
  private Path tempLog;

  @BeforeEach
  void setup() throws Exception {
    tempLog = Files.createTempFile("users-processed", ".log");
    server = HttpServer.create(new InetSocketAddress(0), 0);
    port = server.getAddress().getPort();
    server.createContext("/api", new HttpHandler() {
      @Override public void handle(HttpExchange exchange) throws IOException {
        // aceita qualquer requisicao e retorna 200
        byte[] body = "ok".getBytes(StandardCharsets.UTF_8);
        exchange.sendResponseHeaders(200, body.length);
        exchange.getResponseBody().write(body);
        exchange.close();
      }
    });
    server.start();
  }

  @AfterEach
  void teardown() throws Exception {
    if (server != null) server.stop(0);
    if (tempLog != null) Files.deleteIfExists(tempLog);
  }

  @Test
  void onMessage_deveEscreverNoArquivo_eChamarAPI() throws Exception {
    String apiBase = "http://localhost:" + port + "/api";
    UserProcessor processor = new UserProcessor(apiBase, tempLog.toString());

    String json = "{\"id\":\"11111111-1111-1111-1111-111111111111\",\"name\":\"Maria\",\"email\":\"m@example.com\",\"createdAt\":\"2025-10-23T10:00:00Z\"}";
    processor.onMessage(json);

    String content = Files.readString(tempLog);
    assertTrue(content.contains("\"processedName\":\"MARIA\""));
    assertTrue(content.contains("\"originalName\":\"Maria\""));
  }
}
