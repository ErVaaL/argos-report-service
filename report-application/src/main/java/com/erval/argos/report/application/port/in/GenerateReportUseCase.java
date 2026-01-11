package com.erval.argos.report.application.port.in;

import com.erval.argos.report.core.domain.report.ReportJob;

/**
 * Use case for generating reports.
 */
public interface GenerateReportUseCase {
    /**
     * Generates a report for the provided command.
     *
     * @param cmd command containing job metadata
     * @return updated report job
     */
    ReportJob generate(GenerateReportCommand cmd);

    /**
     * Input command for report generation.
     *
     * @param jobId    job identifier
     * @param deviceId device identifier
     * @param from     inclusive start timestamp (ISO-8601)
     * @param to       inclusive end timestamp (ISO-8601)
     */
    record GenerateReportCommand(
            String jobId,
            String deviceId,
            String from,
            String to) {
    }
}
