package com.erval.argos.report.adapters.pdf;

import static org.junit.jupiter.api.Assertions.assertTrue;

import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.List;

import com.erval.argos.report.core.domain.report.ReportFormat;
import com.erval.argos.report.core.domain.report.ReportJob;
import com.erval.argos.report.core.domain.report.ReportStatus;
import com.erval.argos.report.core.domain.snapshot.MeasurementSnapshot;

import org.junit.jupiter.api.Test;

class SimplePdfReportGeneratorAdapterTest {

    @Test
    void generatePdfReport_containsJobData() {
        var job = new ReportJob("job-1", "dev-1", "Device", ReportFormat.PDF, ReportStatus.READY,
                Instant.EPOCH, Instant.EPOCH, null, null, Instant.EPOCH, Instant.EPOCH);
        var measurements = List.of(new MeasurementSnapshot("m1", "dev-1", null, 1.0, 1, Instant.EPOCH, List.of()));

        var adapter = new SimplePdfReportGeneratorAdapter();
        byte[] bytes = adapter.generatePdfReport(job, measurements);

        var text = new String(bytes, StandardCharsets.UTF_8);
        assertTrue(text.contains("jobId=job-1"));
        assertTrue(text.contains("deviceId=dev-1"));
        assertTrue(text.contains("count=1"));
    }
}
