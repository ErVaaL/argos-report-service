plugins {
    `java-library`
}

dependencies {
    api(project(":report-core"))

    testImplementation("org.mockito:mockito-junit-jupiter:5.18.0")
}
