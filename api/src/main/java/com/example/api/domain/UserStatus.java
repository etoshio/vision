package com.example.api.domain;

import java.util.UUID;

public class UserStatus {
  public enum ProcessingStatus { PENDING, PROCESSED }

  private UUID id;
  private String name;
  private String email;
  private ProcessingStatus status;
  private String processedName;

  public UUID getId() { return id; }
  public void setId(UUID id) { this.id = id; }
  public String getName() { return name; }
  public void setName(String name) { this.name = name; }
  public String getEmail() { return email; }
  public void setEmail(String email) { this.email = email; }
  public ProcessingStatus getStatus() { return status; }
  public void setStatus(ProcessingStatus status) { this.status = status; }
  public String getProcessedName() { return processedName; }
  public void setProcessedName(String processedName) { this.processedName = processedName; }
}
