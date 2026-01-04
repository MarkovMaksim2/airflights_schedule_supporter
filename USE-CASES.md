# Use Cases

Base URL (gateway): http://localhost:8080

Common headers:
- Authorization: Bearer <TOKEN>
- Content-Type: application/json

Roles used in examples: PASSENGER, AIRLINE_COMPANY, GOVERNMENT, AIRPORT_MANAGER, SUPERVISOR

Role-specific auth headers (replace <TOKEN> with login response token):
- Government: Authorization: Bearer <GOV_TOKEN>
- Supervisor: Authorization: Bearer <SUPERVISOR_TOKEN>
- Airline company: Authorization: Bearer <AIRLINE_TOKEN>
- Airport manager: Authorization: Bearer <MANAGER_TOKEN>
- Passenger: Authorization: Bearer <PASSENGER_TOKEN>

## 1) Login (get JWT)

POST /api/auth/login
```json
{
  "username": "gov_user",
  "password": "password123"
}
```

Response includes:
```json
{
  "token": "<JWT>",
  "tokenType": "Bearer",
  "expiresIn": 3600000
}
```

## 2) Create users (SUPERVISOR only)

POST /api/users
```json
{
  "username": "gov_user",
  "email": "gov@example.com",
  "password": "password123",
  "roles": ["GOVERNMENT"]
}
```

POST /api/users
```json
{
  "username": "airline_user",
  "email": "airline@example.com",
  "password": "password123",
  "roles": ["AIRLINE_COMPANY"]
}
```

POST /api/users
```json
{
  "username": "manager_dep",
  "email": "dep.manager@example.com",
  "password": "password123",
  "roles": ["AIRPORT_MANAGER"]
}
```

POST /api/users
```json
{
  "username": "manager_arr",
  "email": "arr.manager@example.com",
  "password": "password123",
  "roles": ["AIRPORT_MANAGER"]
}
```

POST /api/users
```json
{
  "username": "passenger_user",
  "email": "passenger@example.com",
  "password": "password123",
  "roles": ["PASSENGER"]
}
```

## 3) Government: create airports

POST /api/airports
```json
{
  "name": "John F Kennedy International",
  "code": "JFK",
  "city": "New York"
}
```

POST /api/airports
```json
{
  "name": "Los Angeles International",
  "code": "LAX",
  "city": "Los Angeles"
}
```

## 4) Government: create airport manager mappings

POST /api/airport-managers
```json
{
  "airport_id": 1,
  "user_email": "dep.manager@example.com"
}
```

POST /api/airport-managers
```json
{
  "airport_id": 2,
  "user_email": "arr.manager@example.com"
}
```

## 5) Airline company: create airline (email must match user email)

POST /api/airlines
```json
{
  "name": "Example Air",
  "contact_email": "airline@example.com"
}
```

## 6) Airline company: create flight (status auto WAITING_APPROVAL)

POST /api/flights
```json
{
  "airline_id": 1,
  "departure_airport_id": 1,
  "arrival_airport_id": 2,
  "departure_time": "2025-01-01T10:00:00",
  "arrival_time": "2025-01-01T14:00:00",
  "status": "WAITING_APPROVAL"
}
```

## 7) Airport managers: approve flight (split approval)

Departure airport approval:
PATCH /api/flights/1/approve

Arrival airport approval:
PATCH /api/flights/1/approve

After both approvals, status becomes APPROVED.

## 8) Airport managers: depart flight

PATCH /api/flights/1/depart

## 9) Airport managers: arrive flight

PATCH /api/flights/1/arrive

## 10) Passenger: create passenger record (email must match user email)

POST /api/passengers
```json
{
  "first_name": "Jane",
  "last_name": "Doe",
  "email": "passenger@example.com",
  "passport_number": "AA1234567"
}
```

## 11) Passenger: book ticket (for own passenger only)

POST /api/bookings
```json
{
  "passenger_id": 1,
  "flight_id": 1
}
```

## 12) Passenger: list own bookings

GET /api/bookings?page=0&size=20

## 13) Government: create restricted zone

POST /api/restricted-zones
```json
{
  "region": "NYC",
  "start_time": "2025-01-01T08:00:00",
  "end_time": "2025-01-01T12:00:00"
}
```

## 14) Government: update flights due to restriction

PATCH /api/flights/update-due-to-restriction
```json
{
  "region": "NYC",
  "start_time": "2025-01-01T08:00:00",
  "end_time": "2025-01-01T12:00:00"
}
```
