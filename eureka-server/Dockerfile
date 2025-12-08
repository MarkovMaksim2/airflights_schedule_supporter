FROM gradle:9.1.0-jdk21-alpine AS builder

WORKDIR /app

COPY . .

RUN gradle dependencies --no-daemon

RUN gradle clean build -x test --no-daemon

FROM eclipse-temurin:21-jdk-alpine

WORKDIR /app

COPY --from=builder /app/build/libs/*.jar app.jar

EXPOSE 8761

ENTRYPOINT ["java", "-jar", "app.jar"]

# ENTRYPOINT ["sh", "-c", "ls -l"]