package com.erval.argos.report.application.port.out;

import java.util.List;

import com.erval.argos.report.core.domain.report.ReportJob;
import com.erval.argos.report.core.domain.snapshot.MeasurementSnapshot;

/**
 * Port for generating PDF reports from job data.
 */
public interface PdfReportGeneratorPort {
    /**
     * Generates a PDF report for the given job and measurement snapshots.
     *
     * @param job report job metadata
     * @param measurements snapshots to include
     * @return PDF bytes
     */
    byte[] generatePdfReport(ReportJob job, List<MeasurementSnapshot> measurements);
}
