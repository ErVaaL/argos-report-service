package com.erval.argos.report.adapters.storage;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import com.erval.argos.report.application.port.in.DownloadReportUseCase;
import com.erval.argos.report.application.port.out.ReportStoragePort;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * File system storage adapter for report artifacts.
 */
@Component
public class FileSystemReportStorageAdapter implements ReportStoragePort {

    private final Path baseDir;

    /**
     * Creates a storage adapter using the configured base directory.
     *
     * @param dir base directory for report files
     */
    public FileSystemReportStorageAdapter(@Value("${argos.report.storage.dir:./data/reports}") String dir) {
        this.baseDir = Path.of(dir);
    }

    /**
     * Stores the report bytes on disk using the job id as filename.
     *
     * @param jobId job identifier
     * @param pdfData report bytes
     * @return absolute path to the stored file
     */
    @Override
    public String store(String jobId, byte[] pdfData) {
        try {
            Files.createDirectories(baseDir);
            Path target = baseDir.resolve(jobId + ".pdf");
            Files.write(target, pdfData);

            return target.toAbsolutePath().toString();
        } catch (IOException e) {
            throw new IllegalStateException("Failed to store report: " + jobId, e);
        }
    }

    /**
     * Loads report bytes from the given artifact path.
     *
     * @param artifactPath stored report path
     * @return report bytes
     */
    @Override
    public byte[] load(String artifactPath) {
        try {
            return Files.readAllBytes(Path.of(artifactPath));
        } catch (IOException e) {
            throw new IllegalStateException("Failed to load report from: " + artifactPath, e);
        }
    }

}
