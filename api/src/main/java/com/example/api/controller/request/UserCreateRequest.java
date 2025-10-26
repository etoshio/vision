package com.example.api.controller.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record UserCreateRequest(
  @NotBlank String name,
  @Email @NotBlank String email
) {}
