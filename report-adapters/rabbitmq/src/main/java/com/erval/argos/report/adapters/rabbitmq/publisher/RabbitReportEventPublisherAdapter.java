package com.erval.argos.report.adapters.rabbitmq.publisher;

import java.time.Instant;
import java.util.Map;

import com.erval.argos.report.application.port.out.ReportEventPublisherPort;

import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class RabbitReportEventPublisherAdapter implements ReportEventPublisherPort {

    private final RabbitTemplate rabbit;

    private static final String EXCHANGE = "argos.events";
    private static final String RK_GENERATED = "report.generated.v1";
    private static final String RK_FAILED = "report.failed.v1";

    @Override
    public void reportFailed(String jobId, String reason) {
        rabbit.convertAndSend(EXCHANGE, RK_FAILED, Map.of(
                "jobId", jobId,
                "reason", reason));
    }

    @Override
    public void reportGenerated(String jobId, String pdfUrl) {
        rabbit.convertAndSend(EXCHANGE, RK_GENERATED,
                Map.of(
                        "jobId", jobId,
                        "pdfUrl", pdfUrl,
                        "generatedAt", Instant.now().toString()));
    }

}
