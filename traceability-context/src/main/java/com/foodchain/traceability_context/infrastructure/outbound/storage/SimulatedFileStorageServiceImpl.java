// traceability-context/src/main/java/com/foodchain/traceability_context/infrastructure/outbound/storage/SimulatedFileStorageServiceImpl.java
package com.foodchain.traceability_context.infrastructure.outbound.storage;

import com.foodchain.traceability_context.application.outbound.storage.FileStorageService;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.util.Objects;
import java.util.UUID;

/**
 * ADAPTADOR DE SALIDA (Outbound Adapter)
 *
 * Implementación simulada del FileStorageService.
 * En un proyecto real, esta clase contendría la lógica para interactuar con
 * un servicio de almacenamiento de objetos como AWS S3, Google Cloud Storage, o Azure Blob Storage.
 */
@Service
public class SimulatedFileStorageServiceImpl implements FileStorageService {

    @Override
    public String store(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new IllegalArgumentException("No se puede almacenar un archivo vacío o nulo.");
        }

        // 1. Obtenemos el nombre original del archivo para mantener su extensión.
        String originalFilename = StringUtils.cleanPath(Objects.requireNonNull(file.getOriginalFilename()));

        // 2. Generamos un nombre de archivo único para evitar colisiones.
        String uniqueFilename = UUID.randomUUID() + "_" + originalFilename;

        // 3. Simulación de la subida.
        // En una implementación real, aquí estaría el código de la SDK de AWS/Google:
        // s3Client.putObject(bucketName, uniqueFilename, file.getInputStream(), metadata);
        System.out.println("SIMULACIÓN: Subiendo archivo '" + uniqueFilename + "' al Cloud Storage...");
        System.out.println("Tamaño: " + file.getSize() + " bytes, Tipo: " + file.getContentType());

        // 4. Devolvemos una URL pública simulada y predecible.
        // Esta URL apunta a un CDN (Content Delivery Network) hipotético.
        return "https://cdn.foodchain-demo.com/proofs/events/" + uniqueFilename;
    }
}