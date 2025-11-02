package com.foodchain.identity_context.application.outbound.notifications;

public interface EmailService {
    void sendPasswordResetEmail(String to, String token);
}