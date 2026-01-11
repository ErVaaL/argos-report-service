package com.erval.argos.report.adapters.grpc;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

import java.time.Instant;

import com.erval.argos.contracts.resource.v1.Device;
import com.erval.argos.contracts.resource.v1.GetDeviceRequest;
import com.erval.argos.contracts.resource.v1.GetDeviceResponse;
import com.erval.argos.contracts.resource.v1.GetLastMeasurementsRequest;
import com.erval.argos.contracts.resource.v1.GetLastMeasurementsResponse;
import com.erval.argos.contracts.resource.v1.Measurement;
import com.erval.argos.contracts.resource.v1.ResourceQueryServiceGrpc;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class GrpcResourceQueryAdapterTest {

    @Mock
    private ResourceQueryServiceGrpc.ResourceQueryServiceBlockingStub stub;

    @Test
    void getDevice_returnsNotFound() {
        when(stub.getDevice(GetDeviceRequest.newBuilder().setDeviceId("dev-404").build()))
                .thenReturn(GetDeviceResponse.newBuilder().setFound(false).build());

        var adapter = new GrpcResourceQueryAdapter(stub);
        var info = adapter.getDevice("dev-404");

        assertFalse(info.found());
        assertFalse(info.active());
    }

    @Test
    void getDevice_mapsFoundDevice() {
        var device = Device.newBuilder()
                .setId("dev-1")
                .setName("Device One")
                .setActive(true)
                .build();
        when(stub.getDevice(GetDeviceRequest.newBuilder().setDeviceId("dev-1").build()))
                .thenReturn(GetDeviceResponse.newBuilder().setFound(true).setDevice(device).build());

        var adapter = new GrpcResourceQueryAdapter(stub);
        var info = adapter.getDevice("dev-1");

        assertTrue(info.found());
        assertTrue(info.active());
        assertEquals("Device One", info.name());
    }

    @Test
    void getLastMeasurements_mapsSnapshots() {
        var measurement = Measurement.newBuilder()
                .setId("m1")
                .setDeviceId("dev-1")
                .setType("TEMP")
                .setValue(12.3)
                .setSequenceNumber(1)
                .setTimestamp("")
                .addTags("room-1")
                .build();
        when(stub.getLastMeasurements(GetLastMeasurementsRequest.newBuilder()
                .setDeviceId("dev-1")
                .setLimit(10)
                .setTo("2024-01-02T00:00:00Z")
                .build()))
                .thenReturn(GetLastMeasurementsResponse.newBuilder().addMeasurements(measurement).build());

        var adapter = new GrpcResourceQueryAdapter(stub);
        var snapshots = adapter.getLastMeasurements("dev-1", 10, Instant.parse("2024-01-02T00:00:00Z"));

        assertEquals(1, snapshots.size());
        assertEquals(Instant.EPOCH, snapshots.get(0).timestamp());
        assertNotNull(snapshots.get(0).tags());
    }

    @Test
    void getLastMeasurements_rejectsUnknownType() {
        var measurement = Measurement.newBuilder()
                .setId("m1")
                .setDeviceId("dev-1")
                .setType("UNKNOWN")
                .setValue(1.0)
                .setSequenceNumber(1)
                .setTimestamp("2024-01-02T00:00:00Z")
                .build();
        when(stub.getLastMeasurements(GetLastMeasurementsRequest.newBuilder()
                .setDeviceId("dev-1")
                .setLimit(1)
                .setTo("2024-01-02T00:00:00Z")
                .build()))
                .thenReturn(GetLastMeasurementsResponse.newBuilder().addMeasurements(measurement).build());

        var adapter = new GrpcResourceQueryAdapter(stub);

        assertThrows(IllegalStateException.class,
                () -> adapter.getLastMeasurements("dev-1", 1, Instant.parse("2024-01-02T00:00:00Z")));
    }
}
