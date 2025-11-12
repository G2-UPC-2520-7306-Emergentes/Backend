// application/internal/queryservices/BatchQueryServiceImpl.java
package com.foodchain.batch_management_context.application.internal.queryservices;

import com.foodchain.batch_management_context.domain.model.aggregates.Batch;
import com.foodchain.batch_management_context.domain.model.queries.GetBatchByIdQuery;
import com.foodchain.batch_management_context.domain.model.queries.GetBatchCountByEnterprise;
import com.foodchain.batch_management_context.domain.repositories.BatchRepository;
import com.foodchain.batch_management_context.domain.services.BatchQueryService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class BatchQueryServiceImpl implements BatchQueryService {
    private final BatchRepository batchRepository;


    public BatchQueryServiceImpl(BatchRepository batchRepository) {
        this.batchRepository = batchRepository;
    }

    @Override
    public List<Batch> handle(UUID enterpriseId) {
        return batchRepository.findByEnterpriseId(enterpriseId);
    }

    @Override
    public Optional<Batch> handle(GetBatchByIdQuery query) {
        return batchRepository.findById(query.batchId());
    }

    @Override
    public long handle(GetBatchCountByEnterprise query) {
        return batchRepository.countByEnterpriseId(query.enterpriseId());
    }
}