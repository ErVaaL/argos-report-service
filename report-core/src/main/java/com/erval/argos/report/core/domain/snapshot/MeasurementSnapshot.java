package com.erval.argos.report.core.domain.snapshot;

import java.time.Instant;
import java.util.List;

/**
 * Immutable snapshot of a measurement returned by the resource service.
 *
 * @param id             measurement identifier
 * @param deviceId       device identifier
 * @param type           measurement type
 * @param value          measured value
 * @param sequenceNumber sequence number from device
 * @param timestamp      measurement timestamp
 * @param tags           optional tags for the measurement
 */
public record MeasurementSnapshot(
        String id,
        String deviceId,
        MeasurementType type,
        double value,
        int sequenceNumber,
        Instant timestamp,
        List<String> tags) {
}
