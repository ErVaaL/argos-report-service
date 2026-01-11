package com.erval.argos.report.bootstrap.dto;

/**
 * REST-facing DTO for report job data.
 *
 * @param id           report job identifier
 * @param deviceId     device identifier
 * @param deviceName   device name
 * @param format       report format name
 * @param status       report status name
 * @param from         start timestamp
 * @param to           end timestamp
 * @param failureReason failure details
 * @param createdAt    creation timestamp
 */
public record ReportJobDto(
        String id,
        String deviceId,
        String deviceName,
        String format,
        String status,
        String from,
        String to,
        String failureReason,
        String createdAt) {
    /**
     * Maps a domain job to its DTO representation.
     *
     * @param job domain job
     * @return report job DTO
     */
    public static ReportJobDto fromDomain(com.erval.argos.report.core.domain.report.ReportJob job) {
        return new ReportJobDto(
                job.id(),
                job.deviceId(),
                job.deviceName(),
                job.format().name(),
                job.status().name(),
                job.from().toString(),
                job.to().toString(),
                job.failureReason(),
                job.createdAt().toString());
    }
}
