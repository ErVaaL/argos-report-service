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

@RequiredArgsConstructor
public class GrpcResourceQueryAdapter implements ResourceQueryPort {

    private final ResourceQueryServiceGrpc.ResourceQueryServiceBlockingStub stub;

    @Override
    public DeviceInfo getDevice(String deviceId) {
        var res = stub.getDevice(GetDeviceRequest.newBuilder().setDeviceId(deviceId).build());

        if (!res.getFound())
            return new DeviceInfo(null, null, false, false);

        var d = res.getDevice();
        return new DeviceInfo(d.getId(), d.getName(), d.getActive(), true);
    }

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
