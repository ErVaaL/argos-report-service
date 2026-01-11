package com.erval.argos.report.adapters.rabbitmq.listener;

import static org.mockito.Mockito.verify;

import java.util.Map;

import com.erval.argos.report.application.port.in.GenerateReportUseCase;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class ReportRequestedListenerTest {

    @Mock
    private GenerateReportUseCase useCase;

    @Test
    void onMessage_triggersGenerate() {
        var listener = new ReportRequestedListener(useCase);
        Map<String, Object> msg = Map.of(
                "jobId", "job-1",
                "deviceId", "dev-1",
                "from", "2024-01-01T00:00:00Z",
                "to", "2024-01-02T00:00:00Z");

        listener.onMessage(msg);

        var captor = ArgumentCaptor.forClass(GenerateReportUseCase.GenerateReportCommand.class);
        verify(useCase).generate(captor.capture());
        var cmd = captor.getValue();
        org.junit.jupiter.api.Assertions.assertEquals("job-1", cmd.jobId());
        org.junit.jupiter.api.Assertions.assertEquals("dev-1", cmd.deviceId());
    }
}
