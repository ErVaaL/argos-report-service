package com.erval.argos.report.application.port.in;

import com.erval.argos.report.core.domain.PageRequest;
import com.erval.argos.report.core.domain.PageResult;
import com.erval.argos.report.core.domain.report.ReportJob;

public interface QueryReportJobsUseCase {

    PageResult<ReportJob> listReportJobs(PageRequest pageRequest);

}
