package com.erval.argos.report.application.port.out;

public interface ReportStoragePort {

    String store(String jobId, byte[] pdfData); // returns the URL or path where the report is stored
}
