package com.erval.argos.report.application.port.out;

import java.util.Optional;

import com.erval.argos.report.core.domain.report.ReportJob;

public interface ReportJobRepositoryPort {

    Optional<ReportJob> findById(String jobId);

    ReportJob save(ReportJob reportJob);
}
