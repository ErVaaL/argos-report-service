package com.erval.argos.report.application.service;

import com.erval.argos.report.application.port.in.QueryReportJobsUseCase;
import java.util.HashMap;
import java.util.Map;

import com.erval.argos.report.application.port.out.ReportJobRepositoryPort;
import com.erval.argos.report.application.port.out.ResourceQueryPort;
import com.erval.argos.report.core.domain.PageRequest;
import com.erval.argos.report.core.domain.PageResult;
import com.erval.argos.report.core.domain.report.ReportJob;

public record ReportQueryService(
        ReportJobRepositoryPort repo,
        ResourceQueryPort resourceQuery) implements QueryReportJobsUseCase {

    @Override
    public PageResult<ReportJob> listReportJobs(PageRequest pageRequest) {
        var page = repo.findAll(pageRequest);
        if (page.content().isEmpty()) {
            return page;
        }

        Map<String, String> deviceNameCache = new HashMap<>();
        var updated = page.content().stream().map(job -> {
            if (job.deviceName() != null && !job.deviceName().isBlank()) {
                return job;
            }
            var cached = deviceNameCache.get(job.deviceId());
            if (cached == null) {
                var info = resourceQuery.getDevice(job.deviceId());
                cached = info.found() ? info.name() : null;
                deviceNameCache.put(job.deviceId(), cached);
            }
            if (cached == null || cached.isBlank()) {
                return job;
            }

            var refreshed = new ReportJob(
                    job.id(),
                    job.deviceId(),
                    cached,
                    job.format(),
                    job.status(),
                    job.from(),
                    job.to(),
                    job.artifactPath(),
                    job.failureReason(),
                    job.createdAt(),
                    job.updatedAt());
            repo.save(refreshed);
            return refreshed;
        }).toList();

        return new PageResult<>(updated, page.totalElements(), page.page(), page.size());
    }
}
