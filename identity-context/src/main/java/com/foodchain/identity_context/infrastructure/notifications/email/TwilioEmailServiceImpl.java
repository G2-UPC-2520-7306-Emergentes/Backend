// infrastructure/notifications/email/TwilioEmailServiceImpl.java
package com.foodchain.identity_context.infrastructure.notifications.email;

import com.foodchain.identity_context.application.outbound.notifications.EmailService;
import com.sendgrid.Method;
import com.sendgrid.Request;
import com.sendgrid.Response;
import com.sendgrid.SendGrid;
import com.sendgrid.helpers.mail.Mail;
import com.sendgrid.helpers.mail.objects.Content;
import com.sendgrid.helpers.mail.objects.Email;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;

@Service
public class TwilioEmailServiceImpl implements EmailService {

    private final SendGrid sendGridClient;
    private final String fromEmail;

    public TwilioEmailServiceImpl(
            @Value("${twilio.sendgrid.api-key}") String apiKey,
            @Value("${twilio.sendgrid.from-email}") String fromEmail) {
        this.sendGridClient = new SendGrid(apiKey);
        this.fromEmail = fromEmail;
    }

    @Override
    public void sendPasswordResetEmail(String to, String token) {
        Email from = new Email(this.fromEmail);
        String subject = "Restablece tu contraseña de FoodChain";
        Email toEmail = new Email(to);

        // Creamos un contenido HTML para el email, que es más profesional
        String resetUrl = "http://foodchain.com/reset-password?token=" + token; // URL del frontend (simulada)
        Content content = new Content("text/html", buildHtmlContent(to, resetUrl, token));

        Mail mail = new Mail(from, subject, toEmail, content);
        Request request = new Request();

        try {
            request.setMethod(Method.POST);
            request.setEndpoint("mail/send");
            request.setBody(mail.build());

            Response response = sendGridClient.api(request);

            System.out.println("Email de reseteo enviado a " + to + ". Status Code: " + response.getStatusCode());
            if (response.getStatusCode() >= 400) {
                System.err.println("Error al enviar email: " + response.getBody());
            }

        } catch (IOException ex) {
            // En un sistema real, aquí habría un logging más robusto
            System.err.println("Error de IO al intentar enviar email de reseteo: " + ex.getMessage());
            throw new RuntimeException("No se pudo enviar el email de reseteo.", ex);
        }
    }

    // Método privado para construir un cuerpo de email HTML simple pero efectivo
    private String buildHtmlContent(String user, String resetUrl, String token) {
        return "<html><body>" +
                "<h2>Hola " + user + ",</h2>" +
                "<p>Recibimos una solicitud para restablecer tu contraseña en la plataforma FoodChain.</p>" +
                "<p>Puedes hacer clic en el siguiente enlace para establecer una nueva contraseña:</p>" +
                "<p><a href='" + resetUrl + "' style='background-color:#4CAF50; color:white; padding:14px 25px; text-align:center; text-decoration:none; display:inline-block;'>Restablecer Contraseña</a></p>" +
                "<p>Si el botón no funciona, copia y pega la siguiente URL en tu navegador:</p>" +
                "<p>" + resetUrl + "</p>" +
                "<p>Si prefieres, puedes usar este token directamente en la aplicación: <strong>" + token + "</strong></p>" +
                "<p>Si no solicitaste este cambio, puedes ignorar este email de forma segura.</p>" +
                "<br/>" +
                "<p>Gracias,</p>" +
                "<p>El equipo de FoodChain</p>" +
                "</body></html>";
    }
}