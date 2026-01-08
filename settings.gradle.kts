rootProject.name = "argos-v2-report-service"

include(
    ":report-core",
    ":report-application",
    ":report-adapters:grpc",
    ":report-adapters:mongo",
    ":report-adapters:pdf",
    ":report-adapters:rabbitmq",
    ":report-adapters:storage",
    ":report-bootstrap",
)

