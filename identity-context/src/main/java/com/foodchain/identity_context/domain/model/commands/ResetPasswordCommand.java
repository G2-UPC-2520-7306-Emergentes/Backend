package com.foodchain.identity_context.domain.model.commands;

public record ResetPasswordCommand(String token, String newPassword) {}
