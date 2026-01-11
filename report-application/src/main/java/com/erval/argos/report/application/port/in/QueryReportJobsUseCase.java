package com.erval.argos.report.application.port.in;

import com.erval.argos.report.core.domain.PageRequest;
import com.erval.argos.report.core.domain.PageResult;
import com.erval.argos.report.core.domain.report.ReportJob;

/**
 * Use case for listing report jobs.
 */
public interface QueryReportJobsUseCase {

    /**
     * Returns a page of report jobs.
     *
     * @param pageRequest paging and sorting settings
     * @return page of report jobs
     */
    PageResult<ReportJob> listReportJobs(PageRequest pageRequest);

}
