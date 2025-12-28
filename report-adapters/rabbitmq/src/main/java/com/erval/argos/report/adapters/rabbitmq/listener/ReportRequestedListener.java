package com.erval.argos.report.adapters.rabbitmq.listener;

import com.erval.argos.report.application.port.in.GenerateReportUseCase;

import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;

import java.util.Map;

@Component
@RequiredArgsConstructor
public class ReportRequestedListener {

    private final GenerateReportUseCase useCase;

    @RabbitListener(queues = "${argos.rabbitmq.queues.reportRequested:report.requested.v1}")
    public void onMessage(Map<String, Object> msg) {
        var jobId = (String) msg.get("jobId");
        var deviceId = (String) msg.get("deviceId");
        var from = (String) msg.get("from");
        var to = (String) msg.get("to");

        useCase.generate(new GenerateReportUseCase.GenerateReportCommand(jobId, deviceId, from, to));
    }

}
