package com.erval.argos.report.application.port.out;

/**
 * Port for storing and loading report artifacts.
 */
public interface ReportStoragePort {

    /**
     * Stores a report artifact.
     *
     * @param jobId   report job identifier
     * @param pdfData report PDF bytes
     * @return URL or path where the report is stored
     */
    String store(String jobId, byte[] pdfData);

    /**
     * Loads a report artifact by path or URL.
     *
     * @param artifactPath stored artifact path
     * @return report bytes
     */
    byte[] load(String artifactPath);

    /**
     * Returns a default filename for a report.
     *
     * @param jobId report job identifier
     * @return filename
     */
    default String filenameFor(String jobId) {
        return "report-" + jobId + ".pdf";
    }

    /**
     * Returns the default content type for report downloads.
     *
     * @return content type string
     */
    default String contentType() {
        return "application/pdf";
    }
}
