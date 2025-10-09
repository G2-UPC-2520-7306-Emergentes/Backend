// domain/model/valueobjects/Location.java
package com.foodchain.traceability_context.domain.model.valueobjects;

import jakarta.persistence.Embeddable;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * VALUE OBJECT: Location
 * Representa una coordenada geográfica. Es inmutable y se compara por valor.
 */
@Embeddable
@Getter
@NoArgsConstructor
public class Location {

    private Double latitude;
    private Double longitude;

    public Location(Double latitude, Double longitude) {
        if (latitude < -90 || latitude > 90 || longitude < -180 || longitude > 180) {
            throw new IllegalArgumentException("Invalid geographic coordinates.");
        }
        this.latitude = latitude;
        this.longitude = longitude;
    }
}