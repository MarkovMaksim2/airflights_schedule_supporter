plugins {
    id("java")
    id("org.springframework.boot") version "3.5.7"
    id("io.spring.dependency-management") version "1.1.7"
}

group = "com.airflights"
version = "0.0.1"

repositories {
    mavenCentral()
}

dependencyManagement {
    imports {
        mavenBom("org.springframework.cloud:spring-cloud-dependencies:2025.0.0") // актуальная версия на 2025
    }
}

dependencies {
    testImplementation(platform("org.junit:junit-bom:5.10.0"))
    testImplementation("org.junit.jupiter:junit-jupiter")
    implementation("org.springframework.cloud:spring-cloud-starter-config")
    implementation("org.springframework.retry:spring-retry")
    implementation("org.springframework.boot:spring-boot-starter-aop")
    implementation("org.springframework.cloud:spring-cloud-starter-gateway")
//	implementation("org.springframework.cloud:spring-cloud-gateway-server-webflux")
    implementation("org.springframework.boot:spring-boot-starter-webflux")
    implementation("org.springframework.cloud:spring-cloud-starter-netflix-eureka-client")
    implementation("org.springdoc:springdoc-openapi-starter-webflux-ui:2.8.4")
}

tasks.test {
    useJUnitPlatform()
}