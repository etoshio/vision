package com.example.api.service;

import com.example.api.domain.User;
import com.example.api.repository.InMemoryUserRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.Map;
import java.util.UUID;

@Service
public class UserService {
  private static final Logger log = LogManager.getLogger(UserService.class);
  private final InMemoryUserRepository repository;
  private final KafkaTemplate<String, String> kafkaTemplate;
  private final ObjectMapper mapper = new ObjectMapper();

  @Value("${topics.user-register}")
  private String userRegisterTopic;

  public UserService(InMemoryUserRepository repository, KafkaTemplate<String, String> kafkaTemplate) {
    this.repository = repository;
    this.kafkaTemplate = kafkaTemplate;
  }

  public User createAndPublish(String name, String email) {
    var user = new User();
    user.setId(UUID.randomUUID());
    user.setName(name);
    user.setEmail(email);
    repository.save(user);

    try {
      String payload = mapper.writeValueAsString(Map.of(
              "id", user.getId().toString(),
              "name", user.getName(),
              "email", user.getEmail(),
              "createdAt", Instant.now().toString()
      ));

      kafkaTemplate.send(userRegisterTopic, user.getId().toString(), payload);

      log.info("Usuário {} publicado no Kafka (topic={}): {}", user.getId(), userRegisterTopic, payload);
    } catch (Exception e) {
      log.error("Erro ao publicar usuário no Kafka", e);
      throw new RuntimeException(e);
    }

    return user;
  }

  public User update(UUID id, String name, String email) {
    var u = repository.find(id).orElseThrow();
    if (name != null) u.setName(name);
    if (email != null) u.setEmail(email);

    repository.save(u);
    log.info("Usuário {} atualizado: name='{}', email='{}'", id, u.getName(), u.getEmail());
    return u;
  }

  public User find(UUID id) {
    var u = repository.find(id).orElseThrow();
    log.debug("Consulta de usuário {}", id);
    return u;
  }

  public void delete(UUID id) {
    repository.delete(id);
    log.info("Usuário {} removido", id);
  }
}
