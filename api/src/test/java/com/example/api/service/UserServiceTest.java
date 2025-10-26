package com.example.api.service;

import com.example.api.domain.UserStatus;
import com.example.api.repository.InMemoryUserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.kafka.core.KafkaTemplate;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class UserServiceTest {

  InMemoryUserRepository repo;
  KafkaTemplate<String, String> kafka;
  UserService service;

  @BeforeEach
  void setup() {
    repo = new InMemoryUserRepository();
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
    assertEquals(UserStatus.ProcessingStatus.PENDING, u.getStatus());

    ArgumentCaptor<String> key = ArgumentCaptor.forClass(String.class);
    ArgumentCaptor<String> payload = ArgumentCaptor.forClass(String.class);
    verify(kafka, times(1)).send(eq("users.register.v1"), key.capture(), payload.capture());
    assertEquals(u.getId().toString(), key.getValue());
    assertTrue(payload.getValue().contains("\"id\":\"" + u.getId() + "\""));
  }

  @Test
  void update_deveAlterarNomeEEmail() {
    var u = service.createAndPublish("X", "x@e.com");
    var updated = service.update(u.getId(), "Y", "y@e.com");
    assertEquals("Y", updated.getName());
    assertEquals("y@e.com", updated.getEmail());
  }

  @Test
  void updateStatus_deveMarcarComoProcessed_eSalvarProcessedName() {
    var u = service.createAndPublish("Z", "z@e.com");
    var updated = service.updateStatus(u.getId(), UserStatus.ProcessingStatus.PROCESSED, "ZED");
    assertEquals(UserStatus.ProcessingStatus.PROCESSED, updated.getStatus());
    assertEquals("ZED", updated.getProcessedName());
  }
}
