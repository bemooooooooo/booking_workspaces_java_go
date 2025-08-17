# Сервис бронирования мест для коворкинга

## Описание проекта

Сервис для бронирования рабочих мест и помещений в коворкинге. Позволяет студентам, сотрудникам и гостям быстро и удобно бронировать места для работы над проектами.

## Архитектура проекта

Проект состоит из следующих компонентов:

### Backend сервисы

- **booking-service** (Java + Spring Boot) - основной сервис бронирования
- **auth-service** (Go) - сервис авторизации и аутентификации
- **PostgreSQL** - база данных

### Frontend

- **booking-frontend** (React) - SPA интерфейс для пользователей

### Инфраструктура

- **Docker Compose** - оркестрация всех сервисов
- **Swagger** - документация API
- **CI/CD** - автоматические тесты

## Функциональные возможности

### Для пользователей

- Быстрое бронирование мест (минимум шагов)
- Просмотр доступности мест в реальном времени
- Отмена и перенос бронирований
- Интуитивно понятный интерфейс

### Для администраторов

- Управление бронированиями
- Редактирование данных пользователей
- Просмотр статистики загруженности

## Технические требования

- Java 17+ с Spring Boot
- Go 1.21+ для auth-service
- PostgreSQL 15+
- React 18+
- Docker & Docker Compose
- Swagger для API документации

## Быстрый старт

### Предварительные требования

- Docker и Docker Compose
- Git

### Запуск проекта

```bash
# Клонирование репозитория
git clone <repository-url>
cd booking_java_go

# Запуск всех сервисов
docker-compose up -d

# Проверка статуса
docker-compose ps
```

### Доступные сервисы

- **Frontend**: <http://localhost:3000>
- **Booking API**: <http://localhost:8080>
- **Auth API**: <http://localhost:8081>
- **Swagger UI**: <http://localhost:8080/swagger-ui.html>
- **PostgreSQL**: localhost:5432

## Структура проекта

```md
booking_java_go/
├── booking-service/          # Java Spring Boot сервис
├── auth-service/            # Go сервис авторизации
├── booking-frontend/        # React SPA
├── docker-compose.yml       # Оркестрация сервисов
├── docs/                    # Документация
└── README.md               # Основная документация
```

## Разработка

### Запуск тестов

```bash
# Unit тесты для booking-service
cd booking-service
./mvnw test

# Интеграционные тесты
./mvnw verify
```

### Локальная разработка

```bash
# Запуск только базы данных
docker-compose up postgres -d

# Запуск сервисов локально
cd booking-service && ./mvnw spring-boot:run
cd auth-service && go run main.go
cd booking-frontend && npm start
```

## API документация

После запуска проекта доступна Swagger документация:

- Booking API: <http://localhost:8080/swagger-ui.html>
- Auth API: <http://localhost:8081/swagger-ui.html>

## Лицензия

MIT License
