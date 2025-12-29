package com.erval.argos.report.application.service;

import com.erval.argos.report.application.port.in.DownloadReportUseCase;
import com.erval.argos.report.application.port.out.ReportJobRepositoryPort;
import com.erval.argos.report.application.port.out.ReportStoragePort;
import com.erval.argos.report.core.domain.report.ReportStatus;

public record DownloadReportService(
        ReportJobRepositoryPort repo,
        ReportStoragePort storage) implements DownloadReportUseCase {

    @Override
    public Result download(Query query) {
        var job = repo.findById(query.jobId())
                .orElseThrow(() -> new IllegalArgumentException("Report job not found: " + query.jobId()));

        if (job.status() != ReportStatus.READY) {
            throw new IllegalStateException("Report job is not ready: " + query.jobId());
        }

        if (job.artifactPath() == null || job.artifactPath().isBlank()) {
            throw new IllegalStateException("Report job has no artifact path: " + query.jobId());
        }

        byte[] bytes = storage.load(job.artifactPath());

        return new Result(
                storage.filenameFor(job.id()),
                storage.contentType(),
                bytes);
    }

}
