FROM gradle:jdk21-ubi9 AS builder

#WORKDIR /app

#COPY . .
#
#RUN gradle dependencies --no-daemon
#
#RUN gradle clean build -x test --no-daemon

#FROM eclipse-temurin:21-jdk-alpine

WORKDIR /app

COPY . .

#
#COPY --from=builder /app/build/libs/*.jar app.jar
#
#EXPOSE 8761

RUN gradle build -x test

ENTRYPOINT ["java", "-jar", "/app/build/libs/airport-service-0.0.1.jar"]

# ENTRYPOINT ["sh", "-c", "ls -l"]