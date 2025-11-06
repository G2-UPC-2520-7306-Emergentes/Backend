// domain/model/entities/PasswordResetToken.java
package com.foodchain.identity_context.domain.model.entities;

import com.foodchain.identity_context.domain.model.aggregates.User;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import java.util.Date;
import java.util.UUID;

@Entity
@Table(name = "password_reset_tokens")
@Getter
@NoArgsConstructor
public class PasswordResetToken {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String token;

    @OneToOne(targetEntity = User.class, fetch = FetchType.EAGER)
    @JoinColumn(nullable = false, name = "user_id")
    private User user;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(nullable = false)
    private Date expiryDate;

    public PasswordResetToken(User user) {
        this.user = user;
        this.token = UUID.randomUUID().toString();
        // El token expira en 15 minutos
        this.expiryDate = new Date(System.currentTimeMillis() + 900000); // 15 * 60 * 1000
    }

    public boolean isExpired() {
        return new Date().after(this.expiryDate);
    }
}