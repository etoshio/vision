package com.example.api.repository;

import com.example.api.domain.UserStatus;
import org.springframework.stereotype.Repository;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.UUID;

@Repository
public class InMemoryUserRepository {
  private final ConcurrentMap<UUID, UserStatus> db = new ConcurrentHashMap<>();

  public UserStatus save(UserStatus u) { db.put(u.getId(), u); return u; }
  public Optional<UserStatus> find(UUID id){ return Optional.ofNullable(db.get(id)); }
  public void delete(UUID id){ db.remove(id); }
}
