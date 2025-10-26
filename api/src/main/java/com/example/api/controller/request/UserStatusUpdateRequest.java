package com.example.api.controller.request;

import com.example.api.domain.UserStatus.ProcessingStatus;
import jakarta.validation.constraints.NotNull;

public record UserStatusUpdateRequest(
  @NotNull ProcessingStatus status,
  String processedName
) {}
