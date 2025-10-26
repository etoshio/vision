package com.example.api.controller;

import com.example.api.controller.request.UserCreateRequest;
import com.example.api.controller.request.UserUpdateRequest;
import com.example.api.domain.User;
import com.example.api.service.UserService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.UUID;

@RestController
@RequestMapping("/api/users")
public class UserController {

  private final UserService service;
  public UserController(UserService service) { this.service = service; }

  @PostMapping
  public ResponseEntity<User> create(@Valid @RequestBody UserCreateRequest req){
    var u = service.createAndPublish(req.name(), req.email());
    return ResponseEntity.created(URI.create("/api/users/" + u.getId())).body(u);
  }

  @GetMapping("/{id}")
  public User get(@PathVariable("id") UUID id){
    return service.find(id);
  }

  @PutMapping("/{id}")
  public User update(@PathVariable("id") UUID id,
                     @RequestBody UserUpdateRequest req){
    return service.update(id, req.name(), req.email());
  }

  @DeleteMapping("/{id}")
  public ResponseEntity<Void> delete(@PathVariable("id") UUID id){
    service.delete(id);
    return ResponseEntity.noContent().build();
  }
}
