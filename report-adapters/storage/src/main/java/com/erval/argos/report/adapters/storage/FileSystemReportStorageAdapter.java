package com.erval.argos.report.adapters.storage;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import com.erval.argos.report.application.port.out.ReportStoragePort;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class FileSystemReportStorageAdapter implements ReportStoragePort {

    private final Path baseDir;

    public FileSystemReportStorageAdapter(@Value("${argos.report.storage.dir:./data/reports}") String dir) {
        this.baseDir = Path.of(dir);
    }

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

}
