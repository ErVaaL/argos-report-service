package com.erval.argos.report.adapters.mongo.model;

import java.time.Instant;

import com.erval.argos.report.core.domain.report.ReportFormat;
import com.erval.argos.report.core.domain.report.ReportJob;
import com.erval.argos.report.core.domain.report.ReportStatus;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document("report_jobs")
public record ReportJobDocument(
        @Id String id,
        String deviceId,
        ReportFormat format,
        ReportStatus status,
        Instant from,
        Instant to,
        String artifactPath,
        String failureReason,
        Instant createdAt,
        Instant updatedAt) {

    public static ReportJobDocument fromDomain(ReportJob job) {
        return new ReportJobDocument(
                job.id(),
                job.deviceId(),
                job.format(),
                job.status(),
                job.from(),
                job.to(),
                job.artifactPath(),
                job.failureReason(),
                job.createdAt(),
                job.updatedAt());
    }

    public ReportJob toDomain() {
        return new ReportJob(
                id,
                deviceId,
                format,
                status,
                from,
                to,
                artifactPath,
                failureReason,
                createdAt,
                updatedAt);
    }
}
