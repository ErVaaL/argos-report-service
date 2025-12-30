package com.erval.argos.report.bootstrap.dto;

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
