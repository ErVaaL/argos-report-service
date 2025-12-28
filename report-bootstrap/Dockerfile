FROM gradle:9.2.1-jdk21 AS build
WORKDIR /app
COPY . .
RUN gradle :services:report-service:report-bootstrap:bootJar --no-daemon

FROM eclipse-temurin:21-jre-alpine
WORKDIR /app
COPY --from=build /app/services/report-service/report-bootstrap/build/libs/*.jar app.jar
EXPOSE 8083
ENTRYPOINT ["java", "-jar", "/app/app.jar"]
