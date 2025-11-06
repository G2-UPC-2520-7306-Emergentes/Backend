// application/outbound/hashing/HashingService.java
package com.foodchain.traceability_context.application.outbound.hashing;

import org.springframework.web.multipart.MultipartFile;

public interface HashingService {
    String hashFile(MultipartFile file);
}