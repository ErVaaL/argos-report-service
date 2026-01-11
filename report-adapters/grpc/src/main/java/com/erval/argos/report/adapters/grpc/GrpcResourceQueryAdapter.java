package com.erval.argos.report.adapters.grpc;

import java.time.Instant;
import java.util.List;

import com.erval.argos.contracts.resource.v1.GetDeviceRequest;
import com.erval.argos.contracts.resource.v1.GetLastMeasurementsRequest;
import com.erval.argos.contracts.resource.v1.ResourceQueryServiceGrpc;
import com.erval.argos.report.application.port.out.ResourceQueryPort;
import com.erval.argos.report.core.domain.snapshot.MeasurementSnapshot;
import com.erval.argos.report.core.domain.snapshot.MeasurementType;

import lombok.RequiredArgsConstructor;

/**
 * gRPC adapter that queries resource service for device data and measurements.
 */
@RequiredArgsConstructor
public class GrpcResourceQueryAdapter implements ResourceQueryPort {

    private final ResourceQueryServiceGrpc.ResourceQueryServiceBlockingStub stub;

    /**
     * Fetches device metadata by id.
     *
     * @param deviceId device identifier
     * @return device info and found flag
     */
    @Override
    public DeviceInfo getDevice(String deviceId) {
        var res = stub.getDevice(GetDeviceRequest.newBuilder().setDeviceId(deviceId).build());

        if (!res.getFound())
            return new DeviceInfo(null, null, false, false);

        var d = res.getDevice();
        return new DeviceInfo(d.getId(), d.getName(), d.getActive(), true);
    }

    /**
     * Fetches recent measurements for a device.
     *
     * @param deviceId device identifier
     * @param limit    max number of measurements
     * @param to       upper bound timestamp
     * @return measurement snapshots
     */
    @Override
    public List<MeasurementSnapshot> getLastMeasurements(String deviceId, int limit, Instant to) {
        var req = GetLastMeasurementsRequest.newBuilder()
                .setDeviceId(deviceId)
                .setLimit(limit)
                .setTo(to.toString())
                .build();

        var res = stub.getLastMeasurements(req);

        return res.getMeasurementsList().stream()
                .map(m -> new MeasurementSnapshot(
                        m.getId(),
                        m.getDeviceId(),
                        mapType(m.getType()),
                        m.getValue(),
                        m.getSequenceNumber(),
                        m.getTimestamp().isEmpty() ? Instant.EPOCH : Instant.parse(m.getTimestamp()),
                        m.getTagsList()))
                .toList();
    }

    /**
     * Maps raw measurement type values to the local enum.
     *
     * @param raw raw measurement type value
     * @return mapped measurement type
     */
    private MeasurementType mapType(String raw) {
        if (raw == null || raw.isBlank())
            return null;

        try {
            return MeasurementType.valueOf(raw);
        } catch (IllegalArgumentException e) {
            throw new IllegalStateException("Unknown measurement type from resource-service: " + raw);
        }
    }

}
