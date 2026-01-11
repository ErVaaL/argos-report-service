package com.erval.argos.report.application.port.in;

/**
 * Use case for downloading report artifacts.
 */
public interface DownloadReportUseCase {
    /**
     * Query payload for downloading a report.
     *
     * @param jobId report job identifier
     */
    record Query(String jobId) {
    }

    /**
     * Download result payload.
     *
     * @param filename    suggested filename
     * @param contentType content type for the payload
     * @param bytes       report bytes
     */
    record Result(String filename, String contentType, byte[] bytes) {
    }

    /**
     * Downloads a report artifact for the given query.
     *
     * @param query download query
     * @return download payload
     */
    Result download(Query query);
}
