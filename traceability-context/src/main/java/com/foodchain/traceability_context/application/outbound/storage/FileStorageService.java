// traceability-context/src/main/java/com/foodchain/traceability_context/application/outbound/storage/FileStorageService.java
package com.foodchain.traceability_context.application.outbound.storage;

import org.springframework.web.multipart.MultipartFile;

/**
 * PUERTO DE SALIDA (Outbound Port)
 *
 * Define el contrato para cualquier servicio que necesite almacenar archivos.
 * La capa de aplicación dependerá de esta interfaz, no de una implementación concreta.
 */
public interface FileStorageService {
    /**
     * Almacena un archivo (ej. en un bucket S3, un servidor de ficheros, etc.)
     * y devuelve la URL pública y accesible para recuperarlo.
     *
     * @param file El archivo de imagen o documento recibido en la petición.
     * @return La URL única y pública del archivo almacenado.
     * @throws IllegalArgumentException si el fichero está vacío o es inválido.
     */
    String store(MultipartFile file);
}