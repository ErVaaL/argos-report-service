package com.erval.argos.report.application.port.out;

import java.time.Instant;
import java.util.List;

import com.erval.argos.report.core.domain.snapshot.MeasurementSnapshot;

/**
 * Port for querying resource service data needed by report generation.
 */
public interface ResourceQueryPort {

    /**
     * Retrieves device metadata by id.
     *
     * @param deviceId device identifier
     * @return device info and found flag
     */
    DeviceInfo getDevice(String deviceId);

    /**
     * Fetches latest measurements for a device.
     *
     * @param deviceId device identifier
     * @param limit    maximum number of measurements
     * @param to       upper bound timestamp
     * @return list of measurement snapshots
     */
    List<MeasurementSnapshot> getLastMeasurements(String deviceId, int limit, Instant to);

    /**
     * Lightweight device data needed for report validation.
     *
     * @param id     device identifier
     * @param name   device name
     * @param active whether the device is active
     * @param found  whether the device exists
     */
    record DeviceInfo(
            String id,
            String name,
            boolean active,
            boolean found) {
    }
}
