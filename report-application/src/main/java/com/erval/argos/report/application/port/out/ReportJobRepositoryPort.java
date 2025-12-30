package com.erval.argos.report.application.port.out;

import java.util.Optional;

import com.erval.argos.report.core.domain.PageRequest;
import com.erval.argos.report.core.domain.PageResult;
import com.erval.argos.report.core.domain.report.ReportJob;

public interface ReportJobRepositoryPort {

    Optional<ReportJob> findById(String jobId);

    PageResult<ReportJob> findAll(PageRequest pageable);

    ReportJob save(ReportJob reportJob);
}
