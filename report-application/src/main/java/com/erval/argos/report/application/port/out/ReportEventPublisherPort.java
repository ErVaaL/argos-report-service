package com.erval.argos.report.application.port.out;

/**
 * Port for publishing report lifecycle events.
 */
public interface ReportEventPublisherPort {

    /**
     * Publishes a report generated event.
     *
     * @param jobId  report job identifier
     * @param pdfUrl URL or path to the generated report
     */
    void reportGenerated(String jobId, String pdfUrl);

    /**
     * Publishes a report failed event.
     *
     * @param jobId  report job identifier
     * @param reason failure reason
     */
    void reportFailed(String jobId, String reason);
}
