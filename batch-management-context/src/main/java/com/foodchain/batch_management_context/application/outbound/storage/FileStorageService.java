// application/outbound/storage/FileStorageService.java
package com.foodchain.batch_management_context.application.outbound.storage;

import org.springframework.web.multipart.MultipartFile;

public interface FileStorageService {
    /**
     * Almacena un archivo y devuelve su URL pública.
     * @param file El archivo a almacenar.
     * @return La URL pública y accesible del archivo almacenado.
     */
    String store(MultipartFile file);
}