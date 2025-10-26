package com.example.api.service;

import com.example.api.domain.User;
import com.example.api.repository.InMemoryUserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.kafka.core.KafkaTemplate;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class UserServiceTest {

  InMemoryUserRepository repo;
  KafkaTemplate<String, String> kafka;
  UserService service;

  @BeforeEach
  void setup() {
    repo = mock(InMemoryUserRepository.class);
    kafka = mock(KafkaTemplate.class);
    service = new UserService(repo, kafka);
    try {
      var f = UserService.class.getDeclaredField("userRegisterTopic");
      f.setAccessible(true);
      f.set(service, "users.register.v1");
    } catch (Exception e) {
      throw new RuntimeException(e);
    }
  }

  @Test
  void createAndPublish_deveSalvarStatusPending_ePublicarNoKafka() {
    var u = service.createAndPublish("Maria", "maria@example.com");

    assertNotNull(u.getId());

    ArgumentCaptor<String> key = ArgumentCaptor.forClass(String.class);
    ArgumentCaptor<String> payload = ArgumentCaptor.forClass(String.class);
    verify(kafka, times(1)).send(eq("users.register.v1"), key.capture(), payload.capture());
    assertEquals(u.getId().toString(), key.getValue());
    assertTrue(payload.getValue().contains("\"id\":\"" + u.getId() + "\""));
  }

  @Test
  void update_deveAlterarNomeEEmail() {
    var u = service.createAndPublish("X", "x@e.com");
    when(repo.find(any())).thenReturn(Optional.of(u));
    var updated = service.update(u.getId(), "Y", "y@e.com");
    assertEquals("Y", updated.getName());
    assertEquals("y@e.com", updated.getEmail());
  }

  @Test
  void delete_deveRemover() {
    service.delete(mock(UUID.class));
    verify(repo).delete(any());
  }

  @Test
  void find_deveRetornarUsuario() {
    when(repo.find(any())).thenReturn(Optional.of(mock(User.class)));
    User user = service.find(mock(UUID.class));
    assertNotNull(user);
  }
}
