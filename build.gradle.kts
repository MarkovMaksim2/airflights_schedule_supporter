plugins {
    id("org.springframework.boot") version "3.5.6"
    id("io.spring.dependency-management") version "1.1.7"
    id("java")
}


group = "com.airflights"
version = "0.0.1"
java.sourceCompatibility = JavaVersion.VERSION_21


repositories {
    mavenCentral()
}


dependencies {
    // Spring
    implementation("org.springframework.boot:spring-boot-starter-web")
    implementation("org.springframework.boot:spring-boot-starter-data-jpa")

    // ✅ Flyway — совместимая версия с PostgreSQL 16
    implementation("org.flywaydb:flyway-core:11.9.1")
    implementation("org.flywaydb:flyway-database-postgresql:11.9.1")

    // OpenAPI
    implementation("org.springdoc:springdoc-openapi-starter-webmvc-ui:2.8.11")

    // MapStruct
    implementation("org.mapstruct:mapstruct:1.5.5.Final")
    annotationProcessor("org.mapstruct:mapstruct-processor:1.5.5.Final")

    // Lombok
    compileOnly("org.projectlombok:lombok")
    annotationProcessor("org.projectlombok:lombok")

    // PostgreSQL JDBC
    runtimeOnly("org.postgresql:postgresql:42.7.4")

    // Тесты
    testImplementation("org.springframework.boot:spring-boot-starter-test") {
        exclude(group = "org.junit.vintage", module = "junit-vintage-engine")
    }
    testImplementation("org.testcontainers:junit-jupiter:1.20.3")
    testImplementation("org.testcontainers:postgresql:1.20.3")
    testImplementation("org.mockito:mockito-core")
    testImplementation("org.mockito:mockito-junit-jupiter")
    testImplementation("org.assertj:assertj-core")
    testImplementation("jakarta.persistence:jakarta.persistence-api:3.1.0")
}



tasks.withType<Test> {
    useJUnitPlatform()

    // Убираем предупреждения ByteBuddy/Mockito для JDK 21+
    jvmArgs(
        "--add-opens", "java.base/java.lang=ALL-UNNAMED",
        "--add-opens", "java.base/java.util=ALL-UNNAMED"
    )
}

dependencyManagement {
    imports {
        mavenBom("org.flywaydb:flyway-bom:11.9.1")
    }
}

