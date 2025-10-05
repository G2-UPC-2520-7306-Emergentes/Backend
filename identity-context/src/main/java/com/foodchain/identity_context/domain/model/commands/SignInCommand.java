// domain/model/commands/SignInCommand.java
package com.foodchain.identity_context.domain.model.commands;

public record SignInCommand(String email, String password) {}