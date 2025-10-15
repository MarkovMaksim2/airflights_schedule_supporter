
FROM gradle:9.1.0-jdk21-alpine AS builder

WORKDIR /app

COPY build.gradle.kts settings.gradle.kts ./
COPY gradle gradle

RUN gradle dependencies --no-daemon

COPY src src

RUN gradle clean build -x test --no-daemon


FROM eclipse-temurin:21-jdk-alpine

WORKDIR /app

COPY --from=builder /app/build/libs/*.jar app.jar

ENTRYPOINT ["java", "-agentlib:jdwp=transport=dt_socket,server=y,suspend=n,address=*:5005", "-jar", "app.jar"]
