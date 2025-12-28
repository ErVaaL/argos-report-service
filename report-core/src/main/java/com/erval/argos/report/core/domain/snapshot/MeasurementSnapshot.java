package com.erval.argos.report.core.domain.snapshot;

import java.time.Instant;
import java.util.List;

public record MeasurementSnapshot(
        String id,
        String deviceId,
        MeasurementType type,
        double value,
        int sequenceNumber,
        Instant timestamp,
        List<String> tags) {
}
