// shared-domain/src/main/java/com/foodchain/shared_domain/domain/model/aggregates/AuditableAbstractAggregateRoot.java
package com.foodchain.shared_domain.domain.model.aggregates;

import jakarta.persistence.EntityListeners;
import jakarta.persistence.MappedSuperclass;
import lombok.Getter;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.domain.AbstractAggregateRoot;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.util.Date;

/**
 * Clase base para Agregados que requieren auditoría (fechas de creación y actualización).
 * Usa @MappedSuperclass para que sus campos se incluyan en las tablas de las entidades hijas.
 * @EnableJpaAuditing debe ser añadido en la clase principal de la aplicación que la use.
 */
@MappedSuperclass
@EntityListeners(AuditingEntityListener.class)
@Getter
public abstract class AuditableAbstractAggregateRoot<T extends AbstractAggregateRoot<T>> extends AbstractAggregateRoot<T> {

    @CreatedDate
    private Date createdAt;

    @LastModifiedDate
    private Date updatedAt;
}