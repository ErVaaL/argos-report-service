package com.erval.argos.report.adapters.storage;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.nio.file.Files;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

class FileSystemReportStorageAdapterTest {

    @TempDir
    java.nio.file.Path tempDir;

    @Test
    void storeAndLoad_roundTrip() throws Exception {
        var adapter = new FileSystemReportStorageAdapter(tempDir.toString());
        byte[] payload = new byte[] { 1, 2, 3 };

        String path = adapter.store("job-1", payload);

        assertTrue(Files.exists(java.nio.file.Path.of(path)));
        byte[] loaded = adapter.load(path);
        assertArrayEquals(payload, loaded);
    }
}
