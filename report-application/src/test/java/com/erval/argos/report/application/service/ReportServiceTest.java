package com.erval.argos.report.application.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.Instant;
import java.util.List;

import com.erval.argos.report.application.port.in.GenerateReportUseCase.GenerateReportCommand;
import com.erval.argos.report.application.port.out.PdfReportGeneratorPort;
import com.erval.argos.report.application.port.out.ReportEventPublisherPort;
import com.erval.argos.report.application.port.out.ReportJobRepositoryPort;
import com.erval.argos.report.application.port.out.ReportStoragePort;
import com.erval.argos.report.application.port.out.ResourceQueryPort;
import com.erval.argos.report.core.domain.report.ReportFormat;
import com.erval.argos.report.core.domain.report.ReportJob;
import com.erval.argos.report.core.domain.report.ReportStatus;
import com.erval.argos.report.core.domain.snapshot.MeasurementSnapshot;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class ReportServiceTest {

    @Mock
    private ReportJobRepositoryPort repo;

    @Mock
    private ResourceQueryPort resourceQuery;

    @Mock
    private PdfReportGeneratorPort pdfGen;

    @Mock
    private ReportStoragePort storage;

    @Mock
    private ReportEventPublisherPort publisher;

    @Test
    void generate_createsReadyReport() {
        when(resourceQuery.getDevice("dev-1"))
                .thenReturn(new ResourceQueryPort.DeviceInfo("dev-1", "Device One", true, true));
        when(repo.save(any())).thenAnswer(invocation -> invocation.getArgument(0));
        when(resourceQuery.getLastMeasurements(anyString(), anyInt(), any()))
                .thenReturn(List.of(new MeasurementSnapshot("m1", "dev-1", null, 1.0, 1, Instant.EPOCH, List.of())));
        when(pdfGen.generatePdfReport(any(), any())).thenReturn(new byte[] { 1, 2, 3 });
        when(storage.store(anyString(), any())).thenReturn("/tmp/report.pdf");

        var service = new ReportService(repo, resourceQuery, pdfGen, storage, publisher);
        var cmd = new GenerateReportCommand("job-1", "dev-1", "2024-01-01T00:00:00Z", "2024-01-02T00:00:00Z");

        ReportJob result = service.generate(cmd);

        assertNotNull(result);
        assertEquals(ReportStatus.READY, result.status());
        assertEquals(ReportFormat.PDF, result.format());
        assertEquals("/tmp/report.pdf", result.artifactPath());
        verify(publisher).reportGenerated("job-1", "/tmp/report.pdf");
        verify(publisher, never()).reportFailed(anyString(), anyString());
    }

    @Test
    void generate_marksFailedOnException() {
        when(resourceQuery.getDevice("dev-1"))
                .thenReturn(new ResourceQueryPort.DeviceInfo("dev-1", "Device One", true, true));
        when(repo.save(any())).thenAnswer(invocation -> invocation.getArgument(0));
        when(resourceQuery.getLastMeasurements(anyString(), anyInt(), any()))
                .thenReturn(List.of());
        when(pdfGen.generatePdfReport(any(), any())).thenThrow(new IllegalStateException("boom"));

        var service = new ReportService(repo, resourceQuery, pdfGen, storage, publisher);
        var cmd = new GenerateReportCommand("job-1", "dev-1", "2024-01-01T00:00:00Z", "2024-01-02T00:00:00Z");

        ReportJob result = service.generate(cmd);

        assertEquals(ReportStatus.FAILED, result.status());
        assertTrue(result.failureReason().contains("boom"));
        verify(publisher).reportFailed("job-1", "boom");
        verify(storage, never()).store(anyString(), any());
    }

    @Test
    void generate_rejectsMissingDevice() {
        when(resourceQuery.getDevice("dev-404"))
                .thenReturn(new ResourceQueryPort.DeviceInfo(null, null, false, false));

        var service = new ReportService(repo, resourceQuery, pdfGen, storage, publisher);
        var cmd = new GenerateReportCommand("job-1", "dev-404", "2024-01-01T00:00:00Z", "2024-01-02T00:00:00Z");

        var ex = assertThrows(IllegalArgumentException.class, () -> service.generate(cmd));
        assertTrue(ex.getMessage().contains("Device not found"));
    }

    @Test
    void generate_rejectsInactiveDevice() {
        when(resourceQuery.getDevice("dev-2"))
                .thenReturn(new ResourceQueryPort.DeviceInfo("dev-2", "Device Two", false, true));

        var service = new ReportService(repo, resourceQuery, pdfGen, storage, publisher);
        var cmd = new GenerateReportCommand("job-1", "dev-2", "2024-01-01T00:00:00Z", "2024-01-02T00:00:00Z");

        var ex = assertThrows(IllegalArgumentException.class, () -> service.generate(cmd));
        assertTrue(ex.getMessage().contains("not active"));
    }

    @Test
    void generate_persistsReadyStatus() {
        when(resourceQuery.getDevice("dev-1"))
                .thenReturn(new ResourceQueryPort.DeviceInfo("dev-1", "Device One", true, true));
        when(repo.save(any())).thenAnswer(invocation -> invocation.getArgument(0));
        when(resourceQuery.getLastMeasurements(anyString(), anyInt(), any()))
                .thenReturn(List.of());
        when(pdfGen.generatePdfReport(any(), any())).thenReturn(new byte[] { 4 });
        when(storage.store(anyString(), any())).thenReturn("/tmp/report.pdf");

        var service = new ReportService(repo, resourceQuery, pdfGen, storage, publisher);
        var cmd = new GenerateReportCommand("job-1", "dev-1", "2024-01-01T00:00:00Z", "2024-01-02T00:00:00Z");

        service.generate(cmd);

        var captor = ArgumentCaptor.forClass(ReportJob.class);
        verify(repo, org.mockito.Mockito.atLeast(2)).save(captor.capture());
        assertEquals(ReportStatus.READY, captor.getAllValues().get(captor.getAllValues().size() - 1).status());
    }
}
