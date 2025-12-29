package com.erval.argos.report.application.port.in;

public interface DownloadReportUseCase {
    record Query(String jobId) {
    }

    record Result(String filename, String contentType, byte[] bytes) {
    }

    Result download(Query query);
}
