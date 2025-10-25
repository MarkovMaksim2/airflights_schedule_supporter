# Gateway Server

API шлюз для микросервисной архитектуры.

## Описание

Gateway Server предоставляет единую точку входа для всех клиентских запросов и маршрутизирует их к соответствующим микросервисам. Он также обеспечивает балансировку нагрузки, безопасность и мониторинг трафика.

## Основные функции

- Маршрутизация запросов к соответствующим микросервисам
- Балансировка нагрузки между экземплярами сервисов
- Аутентификация и авторизация
- Ограничение скорости запросов
- Логирование и мониторинг трафика
- Преобразование запросов и ответов

## Технологии

- Spring Boot
- Spring Cloud Gateway
- Docker

## Запуск сервиса

```bash
./gradlew bootRun
```

или через Docker:

```bash
docker build -t gateway-server .
docker run -p 8080:8080 gateway-server
```

## Доступ к API

После запуска API Gateway будет доступен по адресу:
http://localhost:8080

Запросы к микросервисам направляются через префиксы:
- /airlines/* -> airline-service
- /airports/* -> airport-service
- /bookings/* -> booking-service
- /flights/* -> flight-service
- /passengers/* -> passenger-service
- /restricted-zones/* -> restrictedzone-service
