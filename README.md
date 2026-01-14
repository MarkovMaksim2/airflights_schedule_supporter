# AirFlights Schedule Supporter v2.0.0

Микросервисная платформа для управления расписанием авиаперелетов, бронированиями и регуляторными ограничениями. Проект построен вокруг API Gateway, централизованной конфигурации и роли-ориентированной модели доступа.

## Архитектура и сервисы

### Основные сервисы
- **Airline Service** — авиакомпании и их контактные данные
- **Airport Service** — аэропорты и связанные справочники
- **Flight Service** — расписание и статусы рейсов
- **Booking Service** — бронирования и билеты
- **Passenger Service** — пассажиры и профили
- **Restricted Zone Service** — зоны ограничения полетов
- **Auth Service** — регистрация пользователей и выпуск JWT

### Инфраструктура
- **Gateway Server** — единая точка входа, маршрутизация, безопасность
- **Config Server** — централизованные конфигурации
- **Eureka Server** — обнаружение сервисов
- **PostgreSQL** — единая БД с раздельными схемами по сервисам

### Подготовленные, но не подключенные сервисы
В репозитории есть `file-service/` и `notification-service/`, а также маршруты в `cfgs/config-server-main/gateway-server-dev.yml`, но они не включены в `settings.gradle.kts` и `docker-compose.yml`. Подключите их при необходимости.

## Связность и взаимодействие сервисов

- **Обнаружение сервисов**: все сервисы регистрируются в Eureka и доступны по имени (`lb://<service>`).
- **Централизованная конфигурация**: Config Server читает профили из `cfgs/` (например, `cfgs/config-server-main/*-dev.yml`).
- **Маршрутизация**: Gateway проксирует все клиентские запросы по `/api/**` к соответствующим сервисам и собирает Swagger-эндпоинты по `/docs/**`.
- **Межсервисные вызовы**: используются Feign-клиенты с Resilience4j Circuit Breaker (например, `flight-service` вызывает `airline-service` и `airport-service`, `booking-service` — `flight-service` и `passenger-service`).
- **Единая БД с раздельными схемами**: каждый сервис работает в своей схеме PostgreSQL (например, `auth`, `flight`, `booking`), миграции выполняет Flyway.

## Безопасность и роли

- **JWT-аутентификация**: `auth-service` выпускает токены, Gateway валидирует JWT.
- **Проброс контекста пользователя**: Gateway добавляет служебные заголовки в запросы к сервисам:
  - `X-Auth-User`
  - `X-Auth-Email`
  - `X-Auth-Roles`
- **Роли** (из `auth-service`):
  - `USER`
  - `PASSENGER`
  - `AIRLINE_COMPANY`
  - `GOVERNMENT`
  - `AIRPORT_MANAGER`
  - `AIRPORT_ASSISTANCE`
  - `SUPERVISOR`

### Примеры разграничения доступа
- **SUPERVISOR** — создание пользователей и администрирование доступа
- **GOVERNMENT** — управление аэропортами и зонами ограничений
- **AIRLINE_COMPANY** — создание/изменение рейсов и авиакомпаний
- **AIRPORT_MANAGER** — утверждение/отправка/прибытие рейсов
- **PASSENGER** — создание пассажира и управление бронированиями

Полные сценарии использования описаны в `USE-CASES.md`.

## Требования

- Docker и Docker Compose
- Java 21+
- Gradle

## Запуск

1. Сборка всех сервисов:
```bash
./gradlew clean build
```

2. Запуск стека:
```bash
docker-compose up --build
```

3. Базовые точки доступа:
- **API Gateway**: http://localhost:8080
- **Swagger UI**: http://localhost:8080/swagger-ui.html
- **Eureka Dashboard**: http://localhost:8761
- **Config Server**: http://localhost:9991
- **PostgreSQL**: localhost:5432
- **pgAdmin**: http://localhost:5050

В `docker-compose.yml` задан bootstrap-пользователь для `auth-service`:
`AUTH_BOOTSTRAP_USERNAME=admin`, `AUTH_BOOTSTRAP_EMAIL=admin@itmo.ru`, `AUTH_BOOTSTRAP_PASSWORD=1234`.

## Конечные точки и доступ

| Сервис | Путь | Доступ |
|--------|------|--------|
| Gateway | `/` | Публичная точка входа |
| Auth | `/api/auth/**`, `/api/users/**` | Через Gateway |
| Airline | `/api/airlines/**` | Через Gateway |
| Airport | `/api/airports/**`, `/api/airport-managers/**` | Через Gateway |
| Flight | `/api/flights/**` | Через Gateway |
| Booking | `/api/bookings/**` | Через Gateway |
| Passenger | `/api/passengers/**` | Через Gateway |
| Restricted Zone | `/api/restricted-zones/**` | Через Gateway |

Порты бизнес-сервисов в профиле `dev` динамические (`server.port=0`), поэтому внешние обращения делаются только через Gateway.

## Документация API

Swagger UI агрегируется Gateway и подтягивает документы каждого сервиса:
- `/docs/flight/v3/api-docs`
- `/docs/booking/v3/api-docs`
- `/docs/airline/v3/api-docs`
- `/docs/airport/v3/api-docs`
- `/docs/passenger/v3/api-docs`
- `/docs/restricted-zone/v3/api-docs`
- `/docs/auth/v3/api-docs`

## Стек технологий

- **Spring Boot 3.x**
- **Spring Cloud (Gateway, Config, Eureka)**
- **Spring WebFlux** (Gateway и reactive-сервисы)
- **Spring Data JPA / R2DBC**
- **PostgreSQL + Flyway**
- **Feign + Resilience4j**
- **Docker / Docker Compose**

## Основные преимущества микросервисного подхода

1. Масштабирование и обновление сервисов независимо друг от друга
2. Изоляция сбоев и защита от каскадных ошибок
3. Гибкая конфигурация и маршрутизация через Gateway
4. Ролевой контроль доступа и централизованная аутентификация

## Тестирование

```bash
./gradlew test
```

## Разработка

Для локальной отладки сервиса:
1. Перейдите в каталог сервиса
2. Запустите: `./gradlew bootRun`
3. Убедитесь, что Eureka и Config Server доступны
