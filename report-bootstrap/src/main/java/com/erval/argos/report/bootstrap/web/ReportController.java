package com.erval.argos.report.bootstrap.web;


import com.erval.argos.report.application.port.in.DownloadReportUseCase;
import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
public class ReportController {

    private final DownloadReportUseCase download;

    @GetMapping("/jobs/{jobId}/pdf")
    public ResponseEntity<byte[]> downloadPdfReport(@PathVariable String jobId) {
        var res = download.download(new DownloadReportUseCase.Query(jobId));

        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(res.contentType()))
                .header(HttpHeaders.CONTENT_DISPOSITION,
                        ContentDisposition.attachment().filename(res.filename()).build().toString())
                .body(res.bytes());
    }
}
