package com.erval.argos.report.bootstrap.config;

import com.erval.argos.contracts.resource.v1.ResourceQueryServiceGrpc;
import com.erval.argos.report.adapters.grpc.GrpcResourceQueryAdapter;
import com.erval.argos.report.adapters.pdf.SimplePdfReportGeneratorAdapter;
import com.erval.argos.report.application.port.in.DownloadReportUseCase;
import com.erval.argos.report.application.port.in.GenerateReportUseCase;
import com.erval.argos.report.application.port.out.*;
import com.erval.argos.report.application.service.DownloadReportService;
import com.erval.argos.report.application.service.ReportService;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import jakarta.annotation.PreDestroy;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ReportWiringConfig {

    private ManagedChannel channel;

    @Bean
    ManagedChannel resourceChannel(
            @Value("${argos.resource.grpc.host}") String host,
            @Value("${argos.resource.grpc.port}") int port) {
        this.channel = ManagedChannelBuilder.forAddress(host, port).usePlaintext().build();
        return this.channel;
    }

    @PreDestroy
    public void close() {
        if (channel == null)
            return;
        channel.shutdown();
        try {
            channel.awaitTermination(2, java.util.concurrent.TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        } finally {
            channel.shutdownNow();
        }
    }

    @Bean
    ResourceQueryServiceGrpc.ResourceQueryServiceBlockingStub resourceStub(ManagedChannel ch) {
        return ResourceQueryServiceGrpc.newBlockingStub(ch);
    }

    @Bean
    ResourceQueryPort resourceQueryPort(ResourceQueryServiceGrpc.ResourceQueryServiceBlockingStub stub) {
        return new GrpcResourceQueryAdapter(stub);
    }

    @Bean
    PdfReportGeneratorPort pdfReportGeneratorPort() {
        return new SimplePdfReportGeneratorAdapter();
    }

    @Bean
    DownloadReportUseCase downloadReportUseCase(
            ReportJobRepositoryPort repo,
            ReportStoragePort storage) {
        return new DownloadReportService(repo, storage);
    }

    @Bean
    GenerateReportUseCase generateReportUseCase(
            ReportJobRepositoryPort repo,
            ResourceQueryPort resourceQry,
            PdfReportGeneratorPort pdfGen,
            ReportStoragePort storage,
            ReportEventPublisherPort publisher) {
        return new ReportService(
                repo, resourceQry, pdfGen, storage, publisher);
    }

}
