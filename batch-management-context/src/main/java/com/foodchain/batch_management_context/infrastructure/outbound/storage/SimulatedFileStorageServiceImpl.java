// infrastructure/outbound/storage/SimulatedFileStorageServiceImpl.java
package com.foodchain.batch_management_context.infrastructure.outbound.storage;

import com.foodchain.batch_management_context.application.outbound.storage.FileStorageService;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import java.util.UUID;

@Service
public class SimulatedFileStorageServiceImpl implements FileStorageService {

    @Override
    public String store(MultipartFile file) {
        if (file.isEmpty()) {
            throw new IllegalArgumentException("No se puede almacenar un fichero vacío.");
        }
        // Simulación: Generamos un nombre de fichero único y construimos una URL falsa.
        // En una implementación real, aquí iría el código para subir a AWS S3.
        String filename = UUID.randomUUID() + "-" + file.getOriginalFilename();
        System.out.println("SIMULACIÓN: Subiendo fichero '" + filename + "' al Cloud Storage...");

        // Devolvemos una URL pública simulada.
        return "https://cdn.foodchain-demo.com/images/batches/" + filename;
    }
}