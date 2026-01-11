package com.erval.argos.report.application.service;

import java.time.Instant;

import com.erval.argos.report.application.port.in.GenerateReportUseCase;
import com.erval.argos.report.application.port.out.PdfReportGeneratorPort;
import com.erval.argos.report.application.port.out.ReportEventPublisherPort;
import com.erval.argos.report.application.port.out.ReportJobRepositoryPort;
import com.erval.argos.report.application.port.out.ReportStoragePort;
import com.erval.argos.report.application.port.out.ResourceQueryPort;
import com.erval.argos.report.core.domain.report.ReportFormat;
import com.erval.argos.report.core.domain.report.ReportJob;
import com.erval.argos.report.core.domain.report.ReportStatus;

/**
 * Application service that generates report artifacts and updates job status.
 * <p>
 * Responsibilities:
 * <ul>
 * <li>validating target devices via the resource query port</li>
 * <li>generating PDFs and storing artifacts</li>
 * <li>publishing report events on success or failure</li>
 * </ul>
 */
public record ReportService(
        ReportJobRepositoryPort repo,
        ResourceQueryPort resourceQry,
        PdfReportGeneratorPort pdfGen,
        ReportStoragePort storage,
        ReportEventPublisherPort publisher) implements GenerateReportUseCase {

    /**
     * Generates a report for a job request and persists updates.
     *
     * @param cmd command containing job id, device id, and time range
     * @return final job state (READY or FAILED)
     * @throws IllegalArgumentException when the device is missing or inactive
     */
    @Override
    public ReportJob generate(GenerateReportCommand cmd) {
        var deviceInfo = resourceQry.getDevice(cmd.deviceId());
        if (!deviceInfo.found())
            throw new IllegalArgumentException("Device not found: " + cmd.deviceId());
        if (!deviceInfo.active())
            throw new IllegalArgumentException("Device is not active: " + cmd.deviceId());

        Instant from = Instant.parse(cmd.from());
        Instant to = Instant.parse(cmd.to());

        ReportJob job = new ReportJob(
                cmd.jobId(),
                cmd.deviceId(),
                deviceInfo.name(),
                ReportFormat.PDF,
                ReportStatus.GENERATING,
                from,
                to,
                null,
                null,
                Instant.now(),
                Instant.now());

        job = repo.save(job);

        try {
            var measurements = resourceQry.getLastMeasurements(cmd.deviceId(), 100, to);

            byte[] pdf = pdfGen.generatePdfReport(job, measurements);

            String path = storage.store(cmd.jobId(), pdf);

            ReportJob ready = new ReportJob(
                    job.id(),
                    job.deviceId(),
                    deviceInfo.name(),
                    job.format(),
                    ReportStatus.READY,
                    job.from(),
                    job.to(),
                    path,
                    null,
                    job.createdAt(),
                    Instant.now());

            ready = repo.save(ready);

            publisher.reportGenerated(cmd.jobId(), path);

            return ready;

        } catch (Exception e) {
            var failed = new ReportJob(
                    job.id(),
                    job.deviceId(),
                    deviceInfo.name(),
                    job.format(),
                    ReportStatus.FAILED,
                    job.from(),
                    job.to(),
                    null,
                    e.getMessage(),
                    job.createdAt(),
                    Instant.now());

            failed = repo.save(failed);

            publisher.reportFailed(cmd.jobId(), e.getMessage());

            return failed;

        }
    }

}
