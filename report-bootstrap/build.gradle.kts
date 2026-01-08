plugins {
    id("org.springframework.boot")
}

dependencies {
    implementation(project(":report-application"))
    implementation(project(":report-adapters:grpc"))
    implementation(project(":report-adapters:mongo"))
    implementation(project(":report-adapters:rabbitmq"))
    implementation(project(":report-adapters:storage"))
    implementation(project(":report-adapters:pdf"))

    implementation("org.springframework.boot:spring-boot-starter-amqp")

    compileOnly("org.projectlombok:lombok:1.18.42")
    annotationProcessor("org.projectlombok:lombok:1.18.42")

    testCompileOnly("org.projectlombok:lombok:1.18.42")
    testAnnotationProcessor("org.projectlombok:lombok:1.18.42")
    implementation("org.springframework.boot:spring-boot-starter-web")
    implementation("org.springframework.boot:spring-boot-starter-data-mongodb")
    implementation("org.springframework.boot:spring-boot-starter-security")
    implementation("org.springframework.boot:spring-boot-starter-oauth2-resource-server")
}
