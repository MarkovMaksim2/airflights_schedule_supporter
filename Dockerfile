FROM eclipse-temurin:21-jdk-alpine


WORKDIR /app


COPY build/libs/airline-scheduler-0.0.1.jar app.jar


ENTRYPOINT ["java", "-jar", "app.jar"]