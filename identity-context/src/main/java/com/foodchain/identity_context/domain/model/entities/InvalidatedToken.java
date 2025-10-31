// domain/model/entities/InvalidatedToken.java
package com.foodchain.identity_context.domain.model.entities;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import java.util.Date;

@Entity
@Table(name = "invalidated_tokens")
@Getter
@NoArgsConstructor
public class InvalidatedToken {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String tokenId; // El JTI (JWT ID) del token invalidado

    @Temporal(TemporalType.TIMESTAMP)
    @Column(nullable = false)
    private Date expiryDate; // Guardamos la fecha de expiración para poder limpiar la tabla periódicamente

    public InvalidatedToken(String tokenId, Date expiryDate) {
        this.tokenId = tokenId;
        this.expiryDate = expiryDate;
    }
}