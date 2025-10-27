FROM eclipse-temurin:21-jre-alpine

RUN apk add --no-cache curl

WORKDIR /app

ARG JAR_FILE=build/libs/eureka-server-*.jar
COPY ${JAR_FILE} app.jar

EXPOSE 8761

ENTRYPOINT ["java", "-jar", "app.jar"]
