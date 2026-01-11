package com.erval.argos.report.application.port.out;

import java.util.Optional;

import com.erval.argos.report.core.domain.PageRequest;
import com.erval.argos.report.core.domain.PageResult;
import com.erval.argos.report.core.domain.report.ReportJob;

/**
 * Port for persisting and retrieving report jobs.
 */
public interface ReportJobRepositoryPort {

    /**
     * Finds a report job by id.
     *
     * @param jobId report job identifier
     * @return optional job
     */
    Optional<ReportJob> findById(String jobId);

    /**
     * Lists report jobs using pagination settings.
     *
     * @param pageable paging and sorting settings
     * @return page of report jobs
     */
    PageResult<ReportJob> findAll(PageRequest pageable);

    /**
     * Saves a report job aggregate.
     *
     * @param reportJob job to persist
     * @return saved job
     */
    ReportJob save(ReportJob reportJob);
}
