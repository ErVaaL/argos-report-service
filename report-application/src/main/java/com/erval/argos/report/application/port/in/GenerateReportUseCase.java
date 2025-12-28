package com.erval.argos.report.application.port.in;

import com.erval.argos.report.core.domain.report.ReportJob;

public interface GenerateReportUseCase {
    ReportJob generate(GenerateReportCommand cmd);

    record GenerateReportCommand(
            String jobId,
            String deviceId,
            String from,
            String to) {
    }
}
