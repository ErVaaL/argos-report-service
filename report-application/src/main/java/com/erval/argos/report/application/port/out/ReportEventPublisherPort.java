package com.erval.argos.report.application.port.out;

public interface ReportEventPublisherPort {

    void reportGenerated(String jobId, String pdfUrl);

    void reportFailed(String jobId, String reason);
}
