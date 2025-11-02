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

    // Coordenadas exactas del GPS
    private Double latitude;
    private Double longitude;

    // Datos enriquecidos por geocodificación inversa
    private String address;
    private String city;
    private String region;
    private String country;

    // Constructor para la creación inicial desde el frontend
    public Location(Double latitude, Double longitude) {
        if (latitude == null || longitude == null || latitude < -90 || latitude > 90 || longitude < -180 || longitude > 180) {
            throw new IllegalArgumentException("Coordenadas geográficas inválidas.");
        }
        this.latitude = latitude;
        this.longitude = longitude;
    }

    // Método para que el servicio de aplicación actualice los datos enriquecidos
    public void enrich(String address, String city, String region, String country) {
        this.address = address;
        this.city = city;
        this.region = region;
        this.country = country;
    }
}