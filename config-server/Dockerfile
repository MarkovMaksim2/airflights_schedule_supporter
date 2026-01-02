FROM gradle:jdk21-ubi9 AS builder

WORKDIR /app
COPY . .
RUN gradle clean bootJar -x test --no-daemon --max-workers=1

FROM eclipse-temurin:21-jre

WORKDIR /app
RUN apt-get update \
    && apt-get install -y --no-install-recommends curl \
    && rm -rf /var/lib/apt/lists/* \
    && useradd -r -u 10001 appuser
COPY --from=builder /app/build/libs/*.jar /app/app.jar
USER appuser

ENTRYPOINT ["java", "-jar", "/app/app.jar"]
