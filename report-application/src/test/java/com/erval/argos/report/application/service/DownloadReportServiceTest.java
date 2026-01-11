package com.erval.argos.report.application.service;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

import java.time.Instant;
import java.util.Optional;

import com.erval.argos.report.application.port.in.DownloadReportUseCase;
import com.erval.argos.report.application.port.out.ReportJobRepositoryPort;
import com.erval.argos.report.application.port.out.ReportStoragePort;
import com.erval.argos.report.core.domain.report.ReportFormat;
import com.erval.argos.report.core.domain.report.ReportJob;
import com.erval.argos.report.core.domain.report.ReportStatus;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class DownloadReportServiceTest {

    @Mock
    private ReportJobRepositoryPort repo;

    @Mock
    private ReportStoragePort storage;

    @Test
    void download_rejectsMissingJob() {
        when(repo.findById("job-1")).thenReturn(Optional.empty());

        var service = new DownloadReportService(repo, storage);
        assertThrows(IllegalArgumentException.class, () -> service.download(new DownloadReportUseCase.Query("job-1")));
    }

    @Test
    void download_rejectsNotReady() {
        var job = new ReportJob("job-1", "dev", "Device", ReportFormat.PDF, ReportStatus.GENERATING,
                Instant.EPOCH, Instant.EPOCH, null, null, Instant.EPOCH, Instant.EPOCH);
        when(repo.findById("job-1")).thenReturn(Optional.of(job));

        var service = new DownloadReportService(repo, storage);
        assertThrows(IllegalStateException.class, () -> service.download(new DownloadReportUseCase.Query("job-1")));
    }

    @Test
    void download_rejectsMissingArtifact() {
        var job = new ReportJob("job-1", "dev", "Device", ReportFormat.PDF, ReportStatus.READY,
                Instant.EPOCH, Instant.EPOCH, " ", null, Instant.EPOCH, Instant.EPOCH);
        when(repo.findById("job-1")).thenReturn(Optional.of(job));

        var service = new DownloadReportService(repo, storage);
        assertThrows(IllegalStateException.class, () -> service.download(new DownloadReportUseCase.Query("job-1")));
    }

    @Test
    void download_returnsPayload() {
        var job = new ReportJob("job-1", "dev", "Device", ReportFormat.PDF, ReportStatus.READY,
                Instant.EPOCH, Instant.EPOCH, "/tmp/report.pdf", null, Instant.EPOCH, Instant.EPOCH);
        when(repo.findById("job-1")).thenReturn(Optional.of(job));
        when(storage.load("/tmp/report.pdf")).thenReturn(new byte[] { 1, 2, 3 });
        when(storage.filenameFor("job-1")).thenReturn("report-job-1.pdf");
        when(storage.contentType()).thenReturn("application/pdf");

        var service = new DownloadReportService(repo, storage);
        var result = service.download(new DownloadReportUseCase.Query("job-1"));

        assertEquals("report-job-1.pdf", result.filename());
        assertEquals("application/pdf", result.contentType());
        assertArrayEquals(new byte[] { 1, 2, 3 }, result.bytes());
    }
}
