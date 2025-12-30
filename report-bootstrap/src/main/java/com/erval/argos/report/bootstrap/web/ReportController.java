package com.erval.argos.report.bootstrap.web;

import com.erval.argos.report.application.port.in.DownloadReportUseCase;
import com.erval.argos.report.application.port.in.QueryReportJobsUseCase;
import com.erval.argos.report.bootstrap.dto.ReportJobDto;
import com.erval.argos.report.core.domain.PageRequest;
import com.erval.argos.report.core.domain.PageResult;
import com.erval.argos.report.core.domain.SortDirection;
import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
public class ReportController {

    private final DownloadReportUseCase download;
    private final QueryReportJobsUseCase listJobs;

    @GetMapping("/jobs/{jobId}/pdf")
    public ResponseEntity<byte[]> downloadPdfReport(@PathVariable String jobId) {
        var res = download.download(new DownloadReportUseCase.Query(jobId));

        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(res.contentType()))
                .header(HttpHeaders.CONTENT_DISPOSITION,
                        ContentDisposition.attachment().filename(res.filename()).build().toString())
                .body(res.bytes());
    }

    @GetMapping("/jobs/list")
    public ResponseEntity<PageResult<ReportJobDto>> listReportJobs(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(required = false) String sortBy,
            @RequestParam(defaultValue = "DESC") SortDirection direction) {
        var pageRequest = new PageRequest(page, size, sortBy, direction);
        var jobs = listJobs.listReportJobs(pageRequest);

        PageResult<ReportJobDto> reports = jobs.content().stream().map(ReportJobDto::fromDomain).toList()
            .stream()
            .collect(PageResult.collector(jobs.totalElements(), jobs.page(), jobs.size()));
        return ResponseEntity.ok(reports);
    }
}
