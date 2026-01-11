package com.erval.argos.report.core.domain.report;

import java.time.Instant;

/**
 * Represents a report generation job and its lifecycle metadata.
 *
 * @param id            report job identifier
 * @param deviceId      target device identifier
 * @param deviceName    human-readable device name (may be cached)
 * @param format        report format
 * @param status        report status
 * @param from          requested range start
 * @param to            requested range end
 * @param artifactPath  storage path or URL of the generated report
 * @param failureReason error detail when generation fails
 * @param createdAt     job creation timestamp
 * @param updatedAt     last update timestamp
 */
public record ReportJob(
        String id,
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
}
