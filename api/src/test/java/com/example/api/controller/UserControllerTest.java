package com.example.api.controller;

import com.example.api.domain.UserStatus;
import com.example.api.service.UserService;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.UUID;

import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(UserController.class)
class UserControllerTest {

  @Autowired
  private MockMvc mvc;

  @Autowired
  private UserService service;

  @TestConfiguration
  static class MockConfig {
    @Bean
    UserService userServiceMock() {
      return Mockito.mock(UserService.class);
    }
  }

  @Test
  void post_create_retornar201ComBody() throws Exception {
    var u = new UserStatus();
    u.setId(UUID.randomUUID());
    u.setName("Maria");
    u.setEmail("maria@example.com");
    u.setStatus(UserStatus.ProcessingStatus.PENDING);

    Mockito.when(service.createAndPublish(anyString(), anyString()))
            .thenReturn(u);

    mvc.perform(post("/api/users")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content("{\"name\":\"Maria\",\"email\":\"maria@example.com\"}"))
            .andExpect(status().isCreated())
            .andExpect(header().string("Location", "/api/users/" + u.getId()))
            .andExpect(jsonPath("$.id", is(u.getId().toString())))
            .andExpect(jsonPath("$.status", is("PENDING")));
  }

  @Test
  void get_byId_retornar200() throws Exception {
    var u = new UserStatus();
    u.setId(UUID.randomUUID());
    u.setName("A");
    u.setEmail("a@e.com");
    u.setStatus(UserStatus.ProcessingStatus.PENDING);

    Mockito.when(service.find(eq(u.getId()))).thenReturn(u);

    mvc.perform(get("/api/users/" + u.getId()))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.name", is("A")));
  }

  @Test
  void put_update_retornar200() throws Exception {
    var id = UUID.randomUUID();
    var u = new UserStatus();
    u.setId(id);
    u.setName("B");
    u.setEmail("b@e.com");
    u.setStatus(UserStatus.ProcessingStatus.PENDING);

    Mockito.when(service.update(eq(id), any(), any()))
            .thenReturn(u);

    mvc.perform(put("/api/users/" + id)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content("{\"name\":\"B\",\"email\":\"b@e.com\"}"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.email", is("b@e.com")));
  }

  @Test
  void delete_retornar204() throws Exception {
    var id = UUID.randomUUID();

    mvc.perform(delete("/api/users/" + id))
            .andExpect(status().isNoContent());

    Mockito.verify(service).delete(eq(id));
  }

  @Test
  void put_status_retornar200() throws Exception {
    var id = UUID.randomUUID();
    var u = new UserStatus();
    u.setId(id);
    u.setName("C");
    u.setEmail("c@e.com");
    u.setStatus(UserStatus.ProcessingStatus.PROCESSED);
    u.setProcessedName("SEE");

    Mockito.when(service.updateStatus(
            eq(id),
            eq(UserStatus.ProcessingStatus.PROCESSED),
            eq("SEE")
    )).thenReturn(u);

    mvc.perform(put("/api/users/" + id + "/status")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content("{\"status\":\"PROCESSED\",\"processedName\":\"SEE\"}"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.processedName", is("SEE")));
  }
}
