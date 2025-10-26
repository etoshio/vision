package com.example.worker.consumer;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import java.io.FileWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Map;

@Component
public class UserProcessor {
  private static final Logger log = LogManager.getLogger(UserProcessor.class);

  private final ObjectMapper mapper = new ObjectMapper();
  private final RestClient http;
  private final String logPath;

  public UserProcessor(
          @Value("${api.base-url}") String apiBaseUrl,
          @Value("${log.path:/data/users-processed.log}") String logPath
  ) {
    this.http = RestClient.builder().baseUrl(apiBaseUrl).build();
    this.logPath = logPath;
  }

  @KafkaListener(
          topics = "${topics.user-register}",
          containerFactory = "kafkaListenerContainerFactory"
  )
  public void onMessage(String payload) {
    try {
      log.info("Mensagem recebida do Kafka: {}", payload);

      JsonNode json = mapper.readTree(payload);
      String id = json.get("id").asText();
      String name = json.get("name").asText();
      String processed = name.toUpperCase();

      // grava em arquivo
      Path logFile = Path.of(logPath);
      if (logFile.getParent() != null) {
        Files.createDirectories(logFile.getParent());
      }
      try (var fw = new FileWriter(logFile.toFile(), true)) {
        fw.write(
                mapper.writeValueAsString(
                        Map.of(
                                "id", id,
                                "originalName", name,
                                "processedName", processed
                        )
                ) + System.lineSeparator()
        );
      }
      log.info("Gravou processamento no arquivo {} para id {}", logPath, id);

      var body = Map.of("status", "PROCESSED", "processedName", processed);
      http.put()
              .uri("/users/{id}/status", id)
              .contentType(MediaType.APPLICATION_JSON)
              .body(body)
              .retrieve()
              .toBodilessEntity();

      log.info("Atualizou status do usuário {} para PROCESSED via API", id);

    } catch (Exception e) {
      log.error("Erro ao processar mensagem Kafka", e);
    }
  }
}
