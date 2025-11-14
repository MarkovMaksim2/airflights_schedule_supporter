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

dependencyManagement {
    imports {
        mavenBom("org.springframework.cloud:spring-cloud-dependencies:2025.0.0")
        mavenBom("org.springframework.boot:spring-boot-dependencies:3.5.7")
    }
}

dependencies {
    // Reactor Web
    implementation("org.springframework.boot:spring-boot-starter-webflux")

    // R2DBC (заменяем JPA)
    implementation("org.springframework.boot:spring-boot-starter-data-r2dbc")
    runtimeOnly("org.postgresql:r2dbc-postgresql")

    // Реактивный драйвер для базы данных (важно!)
    implementation("org.postgresql:r2dbc-postgresql:1.0.7.RELEASE")

    // Cloud компоненты
    implementation("org.springframework.cloud:spring-cloud-starter-netflix-eureka-client")
    implementation("org.springframework.cloud:spring-cloud-starter-config")
    implementation("org.springframework.retry:spring-retry")
    implementation("org.springframework.boot:spring-boot-starter-aop")

    // Реактивный Feign (WebClient based)
    implementation("org.springframework.cloud:spring-cloud-starter-openfeign")

    // Реактивный Circuit Breaker
    implementation("org.springframework.cloud:spring-cloud-starter-circuitbreaker-reactor-resilience4j")

    // Swagger для Reactive
    implementation("org.springdoc:springdoc-openapi-starter-webflux-ui:2.8.4")

    // Lombok
    compileOnly("org.projectlombok:lombok")
    annotationProcessor("org.projectlombok:lombok")

    // Flyway (для миграций - работает с блокирующим JDBC, но можно использовать)
    implementation("org.flywaydb:flyway-core")
    implementation("org.flywaydb:flyway-database-postgresql")

    // JDBC драйвер для Flyway (обязательно)
    runtimeOnly("org.postgresql:postgresql")

    // MapStruct
    implementation("org.mapstruct:mapstruct:1.5.5.Final")
    annotationProcessor("org.mapstruct:mapstruct-processor:1.5.5.Final")

    // Валидация
    implementation("org.springframework.boot:spring-boot-starter-validation")

    // Тесты
    testImplementation("org.springframework.boot:spring-boot-starter-test") {
        exclude(group = "org.junit.vintage", module = "junit-vintage-engine")
    }
    testImplementation("io.projectreactor:reactor-test")
    testImplementation("io.r2dbc:r2dbc-h2")
    testImplementation("org.testcontainers:junit-jupiter")
    testImplementation("org.testcontainers:postgresql")
    testImplementation("org.testcontainers:r2dbc")
    testImplementation("org.springframework.boot:spring-boot-testcontainers")
    testImplementation("org.mockito:mockito-core")
    testImplementation("org.mockito:mockito-junit-jupiter")
    testImplementation("org.assertj:assertj-core")
}

// Конфигурация для MapStruct + Lombok
tasks.withType<JavaCompile> {
    options.compilerArgs.addAll(listOf(
        "-Amapstruct.defaultComponentModel=spring",
        "-Amapstruct.unmappedTargetPolicy=IGNORE"
    ))
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