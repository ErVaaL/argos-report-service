package com.erval.argos.report.adapters.pdf;

import java.nio.charset.StandardCharsets;
import java.util.List;

import com.erval.argos.report.application.port.out.PdfReportGeneratorPort;
import com.erval.argos.report.core.domain.report.ReportJob;
import com.erval.argos.report.core.domain.snapshot.MeasurementSnapshot;

public class SimplePdfReportGeneratorAdapter implements PdfReportGeneratorPort {

    @Override
    public byte[] generatePdfReport(ReportJob job, List<MeasurementSnapshot> measurements) {
        String text = "REPORT\njobId=" + job.id() + "\ndeviceId=" + job.deviceId() +
                "\ncount=" + measurements.size();
        return text.getBytes(StandardCharsets.UTF_8);
    }

}
