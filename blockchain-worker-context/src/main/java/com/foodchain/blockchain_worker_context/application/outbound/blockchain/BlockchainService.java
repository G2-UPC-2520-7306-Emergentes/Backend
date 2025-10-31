package com.foodchain.blockchain_worker_context.application.outbound.blockchain;
// application/outbound/blockchain/BlockchainService.java
public interface BlockchainService {
    String anchorHash(String hash) throws Exception; // Puede lanzar excepciones de red/contrato
}