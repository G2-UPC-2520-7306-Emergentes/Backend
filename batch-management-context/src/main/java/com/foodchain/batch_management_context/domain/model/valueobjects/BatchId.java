// domain/model/valueobjects/BatchId.java
package com.foodchain.batch_management_context.domain.model.valueobjects;

import jakarta.persistence.Embeddable;
import lombok.Value;
import java.io.Serializable;
import java.util.UUID;

@Value
@Embeddable
public class BatchId implements Serializable {
    UUID value;

    public BatchId() {
        this.value = UUID.randomUUID();
    }

    public BatchId(UUID value) {
        this.value = value;
    }
}