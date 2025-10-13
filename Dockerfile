
FROM gradle:9.1.0-jdk21-alpine AS builder

WORKDIR /home/gradle/project

COPY build.gradle settings.gradle ./
COPY gradle gradle

RUN gradle dependencies --no-daemon

COPY . .

RUN gradle clean build -x test --no-daemon


FROM eclipse-temurin:21-jdk-alpine

WORKDIR /app

COPY --from=builder /home/gradle/project/build/libs/*.jar app.jar

ENTRYPOINT ["java", "-jar", "app.jar"]
