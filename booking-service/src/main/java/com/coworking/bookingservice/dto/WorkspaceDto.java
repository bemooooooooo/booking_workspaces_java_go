package com.coworking.bookingservice.dto;

import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonFormat;

import io.swagger.v3.oas.annotations.media.Schema;

/**
 * DTO для передачи данных о рабочем месте
 * 
 * Используется для API ответов при получении информации о рабочих местах
 */
@Schema(description = "Данные рабочего места")
public class WorkspaceDto {

    @Schema(description = "Уникальный идентификатор", example = "1")
    private Integer id;

    @Schema(description = "Название рабочего места", example = "Рабочее место 1")
    private String name;

    @Schema(description = "Описание рабочего места", example = "Удобное место у окна с естественным освещением")
    private String description;

    @Schema(description = "Вместимость (количество человек)", example = "1")
    private Integer capacity;

    @Schema(description = "Активно ли рабочее место", example = "true")
    private Boolean isActive;

    @Schema(description = "Дата создания")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createdAt;

    // Конструкторы
    public WorkspaceDto() {}

    public WorkspaceDto(Integer id, String name, String description, Integer capacity, Boolean isActive, LocalDateTime createdAt) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.capacity = capacity;
        this.isActive = isActive;
        this.createdAt = createdAt;
    }

    // Геттеры и сеттеры
    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Integer getCapacity() {
        return capacity;
    }

    public void setCapacity(Integer capacity) {
        this.capacity = capacity;
    }

    public Boolean getIsActive() {
        return isActive;
    }

    public void setIsActive(Boolean isActive) {
        this.isActive = isActive;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    @Override
    public String toString() {
        return "WorkspaceDto{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", capacity=" + capacity +
                ", isActive=" + isActive +
                '}';
    }
} 