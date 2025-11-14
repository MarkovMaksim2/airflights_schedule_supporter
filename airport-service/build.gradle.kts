plugins {
    id("org.springframework.boot") version "3.5.7"
    id("io.spring.dependency-management") version "1.1.7"
    id("java")
}

group = "com.airflights"
version = "0.0.1"
java.sourceCompatibility = JavaVersion.VERSION_21

repositories {
    mavenCentral()
}

// ВАЖНО: Используйте совместимую версию Spring Cloud
dependencyManagement {
    imports {
        mavenBom("org.springframework.cloud:spring-cloud-dependencies:2025.0.0") // Совместимо с Spring Boot 3.2.x+
    }
}

dependencies {
    // ВАЖНО: Для JPA используйте блокирующий стек Web, а не WebFlux
    implementation("org.springframework.boot:spring-boot-starter-web")
    implementation("org.springframework.boot:spring-boot-starter-data-jpa")

    // PostgreSQL драйвер
    runtimeOnly("org.postgresql:postgresql")

    // Cloud компоненты (обновленные версии)
    implementation("org.springframework.cloud:spring-cloud-starter-netflix-eureka-client")
    implementation("org.springframework.cloud:spring-cloud-starter-config")
    implementation("org.springframework.retry:spring-retry")
    implementation("org.springframework.boot:spring-boot-starter-aop")

    // OpenFeign (блокирующая версия)
    implementation("org.springframework.cloud:spring-cloud-starter-openfeign")

    // Circuit Breaker (блокирующая версия для JPA)
    implementation("org.springframework.cloud:spring-cloud-starter-circuitbreaker-resilience4j")

    // Swagger для Spring MVC (не WebFlux)
    implementation("org.springdoc:springdoc-openapi-starter-webmvc-ui:2.8.4")

    // Lombok
    compileOnly("org.projectlombok:lombok")
    annotationProcessor("org.projectlombok:lombok")

    // Flyway
    implementation("org.flywaydb:flyway-core:10.15.2") // Совместимая версия
    implementation("org.flywaydb:flyway-database-postgresql:10.15.2")
    // MapStruct
    implementation("org.mapstruct:mapstruct:1.5.5.Final")
    annotationProcessor("org.mapstruct:mapstruct-processor:1.5.5.Final")

    // Валидация
    implementation("org.springframework.boot:spring-boot-starter-validation")

    // Actuator для health checks
    implementation("org.springframework.boot:spring-boot-starter-actuator")

    // Тесты
    testImplementation("org.springframework.boot:spring-boot-starter-test")
    testImplementation("org.testcontainers:junit-jupiter")
    testImplementation("org.testcontainers:postgresql")
}

tasks.withType<Test> {
    useJUnitPlatform()
    jvmArgs(
        "--add-opens", "java.base/java.lang=ALL-UNNAMED",
        "--add-opens", "java.base/java.util=ALL-UNNAMED",
        "-XX:+EnableDynamicAgentLoading"
    )
}

tasks.named<org.springframework.boot.gradle.tasks.run.BootRun>("bootRun") {
    jvmArgs = listOf(
        "-agentlib:jdwp=transport=dt_socket,server=y,suspend=n,address=*:5005"
    )
}