package com.erval.argos.report.adapters.mongo.model;

import java.time.Instant;

import com.erval.argos.report.core.domain.report.ReportFormat;
import com.erval.argos.report.core.domain.report.ReportJob;
import com.erval.argos.report.core.domain.report.ReportStatus;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

/**
 * MongoDB document representation of {@link ReportJob}.
 *
 * @param id            report job identifier
 * @param deviceId      device identifier
 * @param deviceName    device name
 * @param format        report format
 * @param status        report status
 * @param from          range start
 * @param to            range end
 * @param artifactPath  report artifact path
 * @param failureReason failure reason
 * @param createdAt     creation timestamp
 * @param updatedAt     last update timestamp
 */
@Document("report_jobs")
public record ReportJobDocument(
        @Id String id,
        String deviceId,
        String deviceName,
        ReportFormat format,
        ReportStatus status,
        Instant from,
        Instant to,
        String artifactPath,
        String failureReason,
        Instant createdAt,
        Instant updatedAt) {

    /**
     * Maps a domain job to a Mongo document.
     *
     * @param job domain job
     * @return Mongo document
     */
    public static ReportJobDocument fromDomain(ReportJob job) {
        return new ReportJobDocument(
                job.id(),
                job.deviceId(),
                job.deviceName(),
                job.format(),
                job.status(),
                job.from(),
                job.to(),
                job.artifactPath(),
                job.failureReason(),
                job.createdAt(),
                job.updatedAt());
    }

    /**
     * Maps this document to the domain aggregate.
     *
     * @return report job aggregate
     */
    public ReportJob toDomain() {
        return new ReportJob(
                id,
                deviceId,
                deviceName,
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
