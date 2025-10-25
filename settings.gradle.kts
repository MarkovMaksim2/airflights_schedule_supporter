pluginManagement {
    repositories {
        mavenCentral()
        gradlePluginPortal()
        maven { url = uri("https://repo.spring.io/release") }
        maven { url = uri("https://repo.spring.io/milestone") }
    }
}


rootProject.name = "airflights-microservices"

include("config-server", "eureka-server", "gateway", "airline-service", "airport-service", "passenger-service", "booking-service", "flight-service", "restricktedZone-service")
