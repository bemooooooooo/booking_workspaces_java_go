package com.coworking.bookingservice.dto;

import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonFormat;

import io.swagger.v3.oas.annotations.media.Schema;

/**
 * DTO для передачи данных о бронировании
 * 
 * Используется для API запросов и ответов при работе с бронированиями
 */
@Schema(description = "Данные бронирования")
public class ReservationDto {

    @Schema(description = "Уникальный идентификатор", example = "1")
    private Integer id;

    @Schema(description = "ID пользователя", example = "123")
    private Integer userId;

    @Schema(description = "ID рабочего места", example = "1")
    private Integer workspaceId;

    @Schema(description = "Название рабочего места", example = "Рабочее место 1")
    private String workspaceName;

    @Schema(description = "Время начала бронирования")
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")
    private LocalDateTime startTime;

    @Schema(description = "Время окончания бронирования")
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")
    private LocalDateTime endTime;

    @Schema(description = "Статус бронирования", example = "ACTIVE")
    private ReservationStatus status;

    @Schema(description = "Дата создания бронирования")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createdAt;

    @Schema(description = "Дата последнего обновления")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime updatedAt;

    // Конструкторы
    public ReservationDto() {}

    public ReservationDto(Integer id, Integer userId, Integer workspaceId, String workspaceName,
                         LocalDateTime startTime, LocalDateTime endTime, ReservationStatus status,
                         LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.id = id;
        this.userId = userId;
        this.workspaceId = workspaceId;
        this.workspaceName = workspaceName;
        this.startTime = startTime;
        this.endTime = endTime;
        this.status = status;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    // Геттеры и сеттеры
    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public Integer getWorkspaceId() {
        return workspaceId;
    }

    public void setWorkspaceId(Integer workspaceId) {
        this.workspaceId = workspaceId;
    }

    public String getWorkspaceName() {
        return workspaceName;
    }

    public void setWorkspaceName(String workspaceName) {
        this.workspaceName = workspaceName;
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

    public ReservationStatus getStatus() {
        return status;
    }

    public void setStatus(ReservationStatus status) {
        this.status = status;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }

    @Override
    public String toString() {
        return "ReservationDto{" +
                "id=" + id +
                ", userId=" + userId +
                ", workspaceId=" + workspaceId +
                ", workspaceName='" + workspaceName + '\'' +
                ", startTime=" + startTime +
                ", endTime=" + endTime +
                ", status=" + status +
                '}';
    }
} 