package com.erval.argos.report.adapters.mongo;

import java.util.Optional;

import com.erval.argos.report.adapters.mongo.model.ReportJobDocument;
import com.erval.argos.report.adapters.mongo.repositories.ReportJobMongoRepository;
import com.erval.argos.report.application.port.out.ReportJobRepositoryPort;
import com.erval.argos.report.core.domain.report.ReportJob;

import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class MongoReportJobRepositoryAdapter implements ReportJobRepositoryPort {
    private final ReportJobMongoRepository repo;

    @Override
    public Optional<ReportJob> findById(String jobId) {
        return repo.findById(jobId).map(ReportJobDocument::toDomain);
    }

    @Override
    public ReportJob save(ReportJob reportJob) {
        return repo.save(ReportJobDocument.fromDomain(reportJob)).toDomain();
    }

}
