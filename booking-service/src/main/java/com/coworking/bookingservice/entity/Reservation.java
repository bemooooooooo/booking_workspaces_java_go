package com.coworking.bookingservice.entity;

import java.time.LocalDateTime;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;

/**
 * Сущность бронирования рабочего места
 * 
 * Представляет бронирование конкретного рабочего места пользователем
 * на определенный период времени. Включает проверки на пересечение
 * временных интервалов для предотвращения двойного бронирования.
 */
@Entity
@Table(name = "reservations", schema = "booking")
public class Reservation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @NotNull(message = "ID пользователя обязателен")
    @Column(name = "user_id", nullable = false)
    private Integer userId;

    @NotNull(message = "Рабочее место должно быть указано")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "workspace_id", nullable = false)
    private Workspace workspace;

    @NotNull(message = "Время начала обязательно")
    @Column(name = "start_time", nullable = false)
    private LocalDateTime startTime;

    @NotNull(message = "Время окончания обязательно")
    @Column(name = "end_time", nullable = false)
    private LocalDateTime endTime;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private ReservationStatus status = ReservationStatus.ACTIVE;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    // Конструкторы
    public Reservation() {}

    public Reservation(Integer userId, Workspace workspace, LocalDateTime startTime, LocalDateTime endTime) {
        this.userId = userId;
        this.workspace = workspace;
        this.startTime = startTime;
        this.endTime = endTime;
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

    public Workspace getWorkspace() {
        return workspace;
    }

    public void setWorkspace(Workspace workspace) {
        this.workspace = workspace;
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

    /**
     * Проверяет, пересекается ли данное бронирование с другим
     * 
     * @param other другое бронирование
     * @return true если есть пересечение
     */
    public boolean overlapsWith(Reservation other) {
        return this.startTime.isBefore(other.endTime) && 
               this.endTime.isAfter(other.startTime);
    }

    /**
     * Проверяет, активно ли бронирование
     * 
     * @return true если статус ACTIVE
     */
    public boolean isActive() {
        return ReservationStatus.ACTIVE.equals(this.status);
    }

    @Override
    public String toString() {
        return "Reservation{" +
                "id=" + id +
                ", userId=" + userId +
                ", workspaceId=" + (workspace != null ? workspace.getId() : null) +
                ", startTime=" + startTime +
                ", endTime=" + endTime +
                ", status=" + status +
                '}';
    }
} 