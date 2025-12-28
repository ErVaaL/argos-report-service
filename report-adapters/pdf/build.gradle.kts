plugins {
    id("org.springframework.boot") apply false
    `java-library`
}

dependencies {
    implementation(project(":services:report-service:report-application"))
    implementation(project(":services:report-service:report-core"))

    implementation("org.apache.pdfbox:pdfbox:3.0.6")

    compileOnly("org.projectlombok:lombok:1.18.42")
    annotationProcessor("org.projectlombok:lombok:1.18.42")

    testCompileOnly("org.projectlombok:lombok:1.18.42")
    testAnnotationProcessor("org.projectlombok:lombok:1.18.42")
}

