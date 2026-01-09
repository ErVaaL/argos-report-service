FROM gradle:9.2.1-jdk21 AS build
WORKDIR /app
COPY gradle gradle
COPY gradlew gradlew
COPY gradlew.bat gradlew.bat
COPY settings.gradle.kts build.gradle.kts ./

COPY report-core/build.gradle.kts report-core/build.gradle.kts
COPY report-application/build.gradle.kts report-application/build.gradle.kts
COPY report-adapters/grpc/build.gradle.kts report-adapters/grpc/build.gradle.kts
COPY report-adapters/rabbitmq/build.gradle.kts report-adapters/rabbitmq/build.gradle.kts
COPY report-adapters/pdf/build.gradle.kts report-adapters/pdf/build.gradle.kts
COPY report-adapters/storage/build.gradle.kts report-adapters/storage/build.gradle.kts
COPY report-adapters/mongo/build.gradle.kts report-adapters/mongo/build.gradle.kts
COPY report-bootstrap/build.gradle.kts report-bootstrap/build.gradle.kts

RUN --mount=type=cache,target=/home/gradle/.gradle \
    --mount=type=secret,id=gh_user \
    --mount=type=secret,id=gh_token \
    set -eu; \
    U="$(cat /run/secrets/gh_user)"; \
    T="$(cat /run/secrets/gh_token)"; \
    GITHUB_ACTOR="$U" GITHUB_TOKEN="$T" ./gradlew -q :report-bootstrap:dependencies --no-daemon || true


COPY report-core report-core
COPY report-application report-application
COPY report-adapters report-adapters
COPY report-bootstrap report-bootstrap

RUN --mount=type=cache,target=/home/gradle/.gradle \
    --mount=type=secret,id=gh_user \
    --mount=type=secret,id=gh_token \
    set -eu; \
    GH_USER="$(cat /run/secrets/gh_user)"; \
    GH_TOKEN="$(cat /run/secrets/gh_token)"; \
    GH_USER="$GH_USER" GH_TOKEN="$GH_TOKEN" ./gradlew :report-bootstrap:bootJar --no-daemon

FROM eclipse-temurin:21-jre-alpine
WORKDIR /app

RUN addgroup -S app && adduser -S app -G app
USER app

COPY --from=build /app/report-bootstrap/build/libs/*SNAPSHOT.jar /app/app.jar

EXPOSE 8083

ENV JAVA_OPTS="-XX:MaxRAMPercentage=75 -XX:InitialRAMPercentage=50"

ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar /app/app.jar"]


