package com.erval.argos.report.core.domain.report;

import java.time.Instant;

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
