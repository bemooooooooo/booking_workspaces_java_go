package com.coworking.bookingservice.dto;

import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonFormat;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotNull;

/**
 * DTO для запроса на создание бронирования
 * 
 * Используется для валидации входных данных при создании нового бронирования
 */
@Schema(description = "Запрос на создание бронирования")
public class CreateReservationRequest {

    @Schema(description = "ID рабочего места", example = "1")
    @NotNull(message = "ID рабочего места обязателен")
    private Integer workspaceId;

    @Schema(description = "Время начала бронирования", example = "2024-01-15 10:00:00")
    @NotNull(message = "Время начала обязательно")
    @Future(message = "Время начала должно быть в будущем")
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")
    private LocalDateTime startTime;

    @Schema(description = "Время окончания бронирования", example = "2024-01-15 12:00:00")
    @NotNull(message = "Время окончания обязательно")
    @Future(message = "Время окончания должно быть в будущем")
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")
    private LocalDateTime endTime;

    // Конструкторы
    public CreateReservationRequest() {}

    public CreateReservationRequest(Integer workspaceId, LocalDateTime startTime, LocalDateTime endTime) {
        this.workspaceId = workspaceId;
        this.startTime = startTime;
        this.endTime = endTime;
    }

    // Геттеры и сеттеры
    public Integer getWorkspaceId() {
        return workspaceId;
    }

    public void setWorkspaceId(Integer workspaceId) {
        this.workspaceId = workspaceId;
    }

    public LocalDateTime getStartTime() {
        return startTime;
    }

    public void setStartTime(LocalDateTime startTime) {
        this.startTime = startTime;
    }

    public LocalDateTime getEndTime() {
        return endTime;
    }

    public void setEndTime(LocalDateTime endTime) {
        this.endTime = endTime;
    }

    @Override
    public String toString() {
        return "CreateReservationRequest{" +
                "workspaceId=" + workspaceId +
                ", startTime=" + startTime +
                ", endTime=" + endTime +
                '}';
    }
} 