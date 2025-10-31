// domain/model/commands/AnchorEventCommand.java
package com.foodchain.blockchain_worker_context.domain.model.commands;

import java.util.UUID;

/**
 * Comando para anclar un evento de trazabilidad en la blockchain.
 * Es el DTO que inicia el caso de uso principal de este worker.
 * @param eventId El ID del evento en la base de datos de trazabilidad que necesita ser actualizado.
 * @param eventHash El hash de los datos del evento que será anclado en el Smart Contract.
 */
public record AnchorEventCommand(UUID eventId, String eventHash) {
}