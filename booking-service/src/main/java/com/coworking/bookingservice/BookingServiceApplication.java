package com.coworking.bookingservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Основной класс приложения сервиса бронирования
 *
 * Этот сервис предоставляет API для:
 * - Бронирования рабочих мест
 * - Управления бронированиями
 * - Просмотра доступности мест
 * - Административных функций
 */
@SpringBootApplication
public class BookingServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(BookingServiceApplication.class, args);
    }
}