// application/outbound/geolocation/GeocodingService.java
package com.foodchain.traceability_context.application.outbound.geolocation;

import com.foodchain.traceability_context.domain.model.valueobjects.Location;

public interface GeocodingService {
    /**
     * Realiza una geocodificación inversa para obtener una dirección a partir de coordenadas.
     * @param latitude La latitud.
     * @param longitude La longitud.
     * @return Un objeto Location enriquecido con los datos de la dirección.
     */
    Location reverseGeocode(Double latitude, Double longitude);
}