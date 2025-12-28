package com.erval.argos.report.application.port.out;

import java.time.Instant;
import java.util.List;

import com.erval.argos.report.core.domain.snapshot.MeasurementSnapshot;

public interface ResourceQueryPort {

    DeviceInfo getDevice(String deviceId);

    List<MeasurementSnapshot> getLastMeasurements(String deviceId, int limit, Instant to);

    record DeviceInfo(
            String id,
            boolean active,
            boolean found) {
    }
}
