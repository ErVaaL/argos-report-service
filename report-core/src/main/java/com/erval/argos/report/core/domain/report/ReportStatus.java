package com.erval.argos.report.core.domain.report;

/**
 * Enumerates the lifecycle states of a report job.
 */
public enum ReportStatus {
    REQUESTED,
    GENERATING,
    READY,
    FAILED
}
