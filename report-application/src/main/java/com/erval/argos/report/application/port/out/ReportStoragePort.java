package com.erval.argos.report.application.port.out;

public interface ReportStoragePort {

    String store(String jobId, byte[] pdfData); // returns the URL or path where the report is stored

    byte[] load(String artifactPath);

    default String filenameFor(String jobId) {
        return "report-" + jobId + ".pdf";
    }

    default String contentType() {
        return "application/pdf";
    }
}
