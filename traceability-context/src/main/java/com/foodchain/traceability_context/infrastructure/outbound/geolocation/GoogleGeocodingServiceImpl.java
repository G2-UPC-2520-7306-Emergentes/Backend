// infrastructure/outbound/geolocation/GoogleGeocodingServiceImpl.java
package com.foodchain.traceability_context.infrastructure.outbound.geolocation;

import com.foodchain.traceability_context.application.outbound.geolocation.GeocodingService;
import com.foodchain.traceability_context.domain.model.valueobjects.Location;
import com.google.maps.GeoApiContext;
import com.google.maps.GeocodingApi;
import com.google.maps.model.AddressComponent;
import com.google.maps.model.AddressComponentType;
import com.google.maps.model.GeocodingResult;
import com.google.maps.model.LatLng;
import org.springframework.stereotype.Service;

@Service
public class GoogleGeocodingServiceImpl implements GeocodingService {

    private final GeoApiContext geoApiContext;

    public GoogleGeocodingServiceImpl(GeoApiContext geoApiContext) {
        this.geoApiContext = geoApiContext;
    }

    @Override
    public Location reverseGeocode(Double latitude, Double longitude) {
        try {
            System.out.println("REAL: Realizando geocodificación inversa para lat:" + latitude + ", lon:" + longitude);

            // 1. Crear el objeto LatLng que la API de Google espera
            LatLng latLng = new LatLng(latitude, longitude);

            // 2. Llamar a la API de geocodificación inversa y esperar la respuesta
            GeocodingResult[] results = GeocodingApi.reverseGeocode(geoApiContext, latLng).await();

            // 3. Crear el objeto Location con las coordenadas originales
            Location location = new Location(latitude, longitude);

            // 4. Si la API devuelve al menos un resultado, lo procesamos
            if (results != null && results.length > 0) {
                GeocodingResult bestResult = results[0]; // El primer resultado suele ser el más específico

                String address = bestResult.formattedAddress;
                String city = findComponent(bestResult, AddressComponentType.LOCALITY);
                String region = findComponent(bestResult, AddressComponentType.ADMINISTRATIVE_AREA_LEVEL_1);
                String country = findComponent(bestResult, AddressComponentType.COUNTRY);

                // 5. Enriquecer nuestro objeto de dominio con los datos obtenidos
                location.enrich(address, city, region, country);
            } else {
                System.err.println("ADVERTENCIA: La API de Geocodificación no devolvió resultados para las coordenadas.");
            }

            return location;

        } catch (Exception e) {
            // En un sistema real, aquí habría un logging más robusto y quizás un reintento.
            System.err.println("Error al llamar a la API de Geocodificación: " + e.getMessage());
            // Devolvemos la ubicación sin enriquecer para no detener el flujo.
            return new Location(latitude, longitude);
        }
    }

    /**
     * Método de utilidad para buscar un tipo específico de componente en la respuesta de la API.
     */
    private String findComponent(GeocodingResult result, AddressComponentType type) {
        for (AddressComponent component : result.addressComponents) {
            for (AddressComponentType componentType : component.types) {
                if (componentType == type) {
                    return component.longName;
                }
            }
        }
        return null; // O un string vacío
    }
}