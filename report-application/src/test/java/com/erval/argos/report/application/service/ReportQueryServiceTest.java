package com.erval.argos.report.application.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.Instant;
import java.util.List;

import com.erval.argos.report.application.port.out.ReportJobRepositoryPort;
import com.erval.argos.report.application.port.out.ResourceQueryPort;
import com.erval.argos.report.core.domain.PageRequest;
import com.erval.argos.report.core.domain.PageResult;
import com.erval.argos.report.core.domain.SortDirection;
import com.erval.argos.report.core.domain.report.ReportFormat;
import com.erval.argos.report.core.domain.report.ReportJob;
import com.erval.argos.report.core.domain.report.ReportStatus;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class ReportQueryServiceTest {

    @Mock
    private ReportJobRepositoryPort repo;

    @Mock
    private ResourceQueryPort resourceQuery;

    @Test
    void listReportJobs_returnsEmptyPage() {
        var pageRequest = new PageRequest(0, 10, "createdAt", SortDirection.DESC);
        var empty = new PageResult<ReportJob>(List.of(), 0, 0, 10);
        when(repo.findAll(pageRequest)).thenReturn(empty);

        var service = new ReportQueryService(repo, resourceQuery);
        PageResult<ReportJob> result = service.listReportJobs(pageRequest);

        assertSame(empty, result);
    }

    @Test
    void listReportJobs_refreshesMissingDeviceName() {
        var pageRequest = new PageRequest(0, 10, "createdAt", SortDirection.DESC);
        var job1 = new ReportJob("job-1", "dev-1", "", ReportFormat.PDF, ReportStatus.READY,
                Instant.EPOCH, Instant.EPOCH, "/tmp/r1", null, Instant.EPOCH, Instant.EPOCH);
        var job2 = new ReportJob("job-2", "dev-1", null, ReportFormat.PDF, ReportStatus.READY,
                Instant.EPOCH, Instant.EPOCH, "/tmp/r2", null, Instant.EPOCH, Instant.EPOCH);
        var page = new PageResult<>(List.of(job1, job2), 2, 0, 10);
        when(repo.findAll(pageRequest)).thenReturn(page);
        when(resourceQuery.getDevice("dev-1"))
                .thenReturn(new ResourceQueryPort.DeviceInfo("dev-1", "Device One", true, true));

        var service = new ReportQueryService(repo, resourceQuery);
        PageResult<ReportJob> result = service.listReportJobs(pageRequest);

        assertEquals(2, result.content().size());
        assertEquals("Device One", result.content().get(0).deviceName());
        assertEquals("Device One", result.content().get(1).deviceName());
        verify(resourceQuery, times(1)).getDevice("dev-1");

        var captor = ArgumentCaptor.forClass(ReportJob.class);
        verify(repo, times(2)).save(captor.capture());
    }
}
