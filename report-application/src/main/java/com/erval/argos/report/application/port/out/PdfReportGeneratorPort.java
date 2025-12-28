package com.erval.argos.report.application.port.out;

import java.util.List;

import com.erval.argos.report.core.domain.report.ReportJob;
import com.erval.argos.report.core.domain.snapshot.MeasurementSnapshot;

public interface PdfReportGeneratorPort {
    byte[] generatePdfReport(ReportJob job, List<MeasurementSnapshot> measurements);
}
