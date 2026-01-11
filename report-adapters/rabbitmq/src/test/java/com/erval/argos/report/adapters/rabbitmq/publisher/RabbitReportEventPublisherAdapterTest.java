package com.erval.argos.report.adapters.rabbitmq.publisher;

import static org.mockito.Mockito.verify;

import java.util.Map;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.amqp.rabbit.core.RabbitTemplate;

@ExtendWith(MockitoExtension.class)
class RabbitReportEventPublisherAdapterTest {

    @Mock
    private RabbitTemplate rabbitTemplate;

    @Test
    void reportFailed_sendsPayload() {
        var adapter = new RabbitReportEventPublisherAdapter(rabbitTemplate);

        adapter.reportFailed("job-1", "boom");

        var captor = ArgumentCaptor.forClass(Map.class);
        verify(rabbitTemplate)
                .convertAndSend(
                        org.mockito.Mockito.eq("argos.events"),
                        org.mockito.Mockito.eq("report.failed.v1"),
                        captor.capture());

        Map<String, Object> payload = captor.getValue();
        org.junit.jupiter.api.Assertions.assertEquals("job-1", payload.get("jobId"));
        org.junit.jupiter.api.Assertions.assertEquals("boom", payload.get("reason"));
    }

    @Test
    void reportGenerated_sendsPayload() {
        var adapter = new RabbitReportEventPublisherAdapter(rabbitTemplate);

        adapter.reportGenerated("job-1", "/tmp/report.pdf");

        var captor = ArgumentCaptor.forClass(Map.class);
        verify(rabbitTemplate)
                .convertAndSend(
                        org.mockito.Mockito.eq("argos.events"),
                        org.mockito.Mockito.eq("report.generated.v1"),
                        captor.capture());

        Map<String, Object> payload = captor.getValue();
        org.junit.jupiter.api.Assertions.assertEquals("job-1", payload.get("jobId"));
        org.junit.jupiter.api.Assertions.assertEquals("/tmp/report.pdf", payload.get("pdfUrl"));
        org.junit.jupiter.api.Assertions.assertNotNull(payload.get("generatedAt"));
    }
}
