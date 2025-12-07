FROM eclipse-temurin:21-jre-alpine

WORKDIR /app

COPY . .

RUN mvn -pl auth-service -am package -DskipTests

FROM eclipse-temurin:21-jre

WORKDIR /app

COPY --from=build /app/auth-service/target/*.jar app.jar

ENTRYPOINT ["java", "-jar", "/app/app.jar"]