package com.erval.argos.report.bootstrap.config;

import com.erval.argos.contracts.resource.v1.ResourceQueryServiceGrpc;
import com.erval.argos.report.adapters.grpc.GrpcResourceQueryAdapter;
import com.erval.argos.report.adapters.pdf.SimplePdfReportGeneratorAdapter;
import com.erval.argos.report.application.port.in.DownloadReportUseCase;
import com.erval.argos.report.application.port.in.GenerateReportUseCase;
import com.erval.argos.report.application.port.in.QueryReportJobsUseCase;
import com.erval.argos.report.application.port.out.*;
import com.erval.argos.report.application.service.DownloadReportService;
import com.erval.argos.report.application.service.ReportQueryService;
import com.erval.argos.report.application.service.ReportService;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import jakarta.annotation.PreDestroy;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Wires application services and external adapters for the report service.
 */
@Configuration
public class ReportWiringConfig {

    private ManagedChannel channel;

    /**
     * Creates a gRPC channel to the resource service.
     *
     * @param host resource gRPC host
     * @param port resource gRPC port
     * @return managed channel
     */
    @Bean
    ManagedChannel resourceChannel(
            @Value("${argos.resource.grpc.host}") String host,
            @Value("${argos.resource.grpc.port}") int port) {
        this.channel = ManagedChannelBuilder.forAddress(host, port).usePlaintext().build();
        return this.channel;
    }

    /**
     * Closes the gRPC channel on shutdown.
     */
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

    /**
     * Builds a blocking stub for resource queries.
     *
     * @param ch gRPC channel
     * @return blocking stub
     */
    @Bean
    ResourceQueryServiceGrpc.ResourceQueryServiceBlockingStub resourceStub(ManagedChannel ch) {
        return ResourceQueryServiceGrpc.newBlockingStub(ch);
    }

    /**
     * Exposes the resource query port backed by gRPC.
     *
     * @param stub gRPC blocking stub
     * @return resource query port
     */
    @Bean
    ResourceQueryPort resourceQueryPort(ResourceQueryServiceGrpc.ResourceQueryServiceBlockingStub stub) {
        return new GrpcResourceQueryAdapter(stub);
    }

    /**
     * Provides the PDF generator adapter.
     *
     * @return PDF generator port
     */
    @Bean
    PdfReportGeneratorPort pdfReportGeneratorPort() {
        return new SimplePdfReportGeneratorAdapter();
    }

    /**
     * Wires the report download use case.
     *
     * @param repo report job repository
     * @param storage report storage port
     * @return download use case
     */
    @Bean
    DownloadReportUseCase downloadReportUseCase(
            ReportJobRepositoryPort repo,
            ReportStoragePort storage) {
        return new DownloadReportService(repo, storage);
    }

    /**
     * Wires the report generation use case.
     *
     * @param repo report job repository
     * @param resourceQry resource query port
     * @param pdfGen PDF generator port
     * @param storage report storage port
     * @param publisher report event publisher
     * @return generate report use case
     */
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

    /**
     * Wires the report listing use case.
     *
     * @param repo report job repository
     * @param resourceQuery resource query port
     * @return query use case
     */
    @Bean
    QueryReportJobsUseCase queryReportJobsUseCase(
        ReportJobRepositoryPort repo,
        ResourceQueryPort resourceQuery
    ) {
        return new ReportQueryService(repo, resourceQuery);
    }

}
