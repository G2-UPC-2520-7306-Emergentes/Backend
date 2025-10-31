package com.foodchain.blockchain_worker_context.application.internal.commandservices;

import com.foodchain.blockchain_worker_context.application.outbound.blockchain.BlockchainService;
import com.foodchain.blockchain_worker_context.domain.model.commands.AnchorEventCommand;
import com.foodchain.blockchain_worker_context.domain.repository.TraceabilityRepository;
import com.foodchain.blockchain_worker_context.domain.services.AnchoringCommandService;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

// application/internal/commandservices/AnchoringCommandServiceImpl.java
@Service
public class AnchoringCommandServiceImpl implements AnchoringCommandService {
    private final BlockchainService blockchainService;
    private final TraceabilityRepository traceabilityRepository;

    public AnchoringCommandServiceImpl(BlockchainService blockchainService,
                                       TraceabilityRepository traceabilityRepository) {
        this.blockchainService = blockchainService;
        this.traceabilityRepository = traceabilityRepository;
    }

    @Override
    @Transactional
    public void handle(AnchorEventCommand command) {
        try {
            // 1. Llama al servicio de infraestructura para anclar el hash
            String txHash = blockchainService.anchorHash(command.eventHash());

            // 2. Carga la entidad del evento desde la base de datos
            var event = traceabilityRepository.findById(command.eventId())
                    .orElseThrow(() -> new IllegalArgumentException("Event not found: " + command.eventId()));

            // 3. Llama al método de negocio en la entidad para actualizar su estado
            event.confirmAnchoring(txHash);

            // 4. Guarda los cambios
            traceabilityRepository.save(event);

            System.out.println("EVENTO ANCLADO: " + command.eventId() + " con TxHash: " + txHash);

        } catch (Exception e) {
            // Manejo de errores: ¿Qué pasa si el anclaje falla?
            System.err.println("FALLO EL ANCLAJE para el evento " + command.eventId() + ": " + e.getMessage());
        }
    }
}