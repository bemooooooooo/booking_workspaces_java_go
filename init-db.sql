-- Инициализация базы данных для сервиса бронирования

-- Включение расширения для UUID
CREATE EXTENSION IF NOT EXISTS "uuid-ossp";

-- Создание схемы для auth-service
CREATE SCHEMA IF NOT EXISTS auth;

-- Таблица пользователей
CREATE TABLE IF NOT EXISTS auth.users (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    username VARCHAR(50) UNIQUE NOT NULL,
    email VARCHAR(100) UNIQUE NOT NULL,
    password_hash VARCHAR(255) NOT NULL,
    role VARCHAR(20) NOT NULL DEFAULT 'user',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Создание схемы для booking-service
CREATE SCHEMA IF NOT EXISTS booking;

-- Таблица рабочих мест
CREATE TABLE IF NOT EXISTS booking.workspaces (
    id SERIAL PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    description TEXT,
    capacity INTEGER NOT NULL DEFAULT 1,
    is_active BOOLEAN DEFAULT TRUE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Таблица бронирований
CREATE TABLE IF NOT EXISTS booking.reservations (
    id SERIAL PRIMARY KEY,
    user_id INTEGER NOT NULL,
    workspace_id INTEGER NOT NULL,
    start_time TIMESTAMP NOT NULL,
    end_time TIMESTAMP NOT NULL,
    status VARCHAR(20) DEFAULT 'ACTIVE',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (workspace_id) REFERENCES booking.workspaces(id),
    CONSTRAINT valid_time_range CHECK (end_time > start_time)
);

-- Индексы для оптимизации запросов
CREATE INDEX IF NOT EXISTS idx_reservations_user_id ON booking.reservations(user_id);
CREATE INDEX IF NOT EXISTS idx_reservations_workspace_id ON booking.reservations(workspace_id);
CREATE INDEX IF NOT EXISTS idx_reservations_time_range ON booking.reservations(start_time, end_time);
CREATE INDEX IF NOT EXISTS idx_users_username ON auth.users(username);
CREATE INDEX IF NOT EXISTS idx_users_email ON auth.users(email);

-- Уникальный индекс для предотвращения двойного бронирования
CREATE UNIQUE INDEX IF NOT EXISTS idx_unique_workspace_time 
ON booking.reservations(workspace_id, start_time, end_time) 
WHERE status = 'ACTIVE';

-- Вставка тестовых данных
INSERT INTO booking.workspaces (name, description, capacity) VALUES
('Рабочее место 1', 'Удобное место у окна с естественным освещением', 1),
('Рабочее место 2', 'Тихое место в углу для сосредоточенной работы', 1),
('Конференц-зал А', 'Зал для встреч до 10 человек', 10),
('Конференц-зал Б', 'Малый зал для встреч до 5 человек', 5),
('Коворкинг зона', 'Открытое пространство для групповой работы', 20)
ON CONFLICT DO NOTHING;

-- Вставка тестового администратора
INSERT INTO auth.users (username, email, password_hash, role) VALUES
('admin', 'admin@coworking.com', '$2a$10$J5hqI/JTYvt1C8oXErMWneqSxS6/30gJ5bg5qEfc/1DwFjO34.xAK', 'admin') 
ON CONFLICT DO NOTHING; 

--Superadmin : admin admin123456