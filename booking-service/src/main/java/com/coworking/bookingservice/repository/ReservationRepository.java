package com.coworking.bookingservice.repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.coworking.bookingservice.entity.Reservation;
import com.coworking.bookingservice.entity.ReservationStatus;

/**
 * Репозиторий для работы с бронированиями
 * 
 * Предоставляет методы для поиска, создания и управления бронированиями,
 * включая проверки на пересечение временных интервалов.
 */
@Repository
public interface ReservationRepository extends JpaRepository<Reservation, Integer> {

    /**
     * Находит все активные бронирования пользователя
     * 
     * @param userId ID пользователя
     * @return список активных бронирований
     */
    List<Reservation> findByUserIdAndStatusOrderByStartTimeDesc(Integer userId, ReservationStatus status);

    /**
     * Находит все активные бронирования рабочего места
     * 
     * @param workspaceId ID рабочего места
     * @return список активных бронирований
     */
    List<Reservation> findByWorkspaceIdAndStatusOrderByStartTime(Integer workspaceId, ReservationStatus status);

    /**
     * Находит все бронирования пользователя
     * 
     * @param userId ID пользователя
     * @return список всех бронирований пользователя
     */
    List<Reservation> findByUserIdOrderByStartTimeDesc(Integer userId);

    /**
     * Проверяет, есть ли пересекающиеся бронирования для рабочего места
     * 
     * @param workspaceId ID рабочего места
     * @param startTime время начала
     * @param endTime время окончания
     * @param excludeReservationId ID бронирования для исключения (при обновлении)
     * @return список пересекающихся бронирований
     */
        @Query("""
            SELECT r FROM Reservation r 
            WHERE r.workspace.id = :workspaceId 
            AND r.status = 'ACTIVE'
            AND r.startTime < :endTime 
            AND r.endTime > :startTime
            AND (:excludeReservationId IS NULL OR r.id != :excludeReservationId)
            """)
    List<Reservation> findOverlappingReservations(@Param("workspaceId") Integer workspaceId,
                                                 @Param("startTime") LocalDateTime startTime,
                                                 @Param("endTime") LocalDateTime endTime,
                                                 @Param("excludeReservationId") Integer excludeReservationId);

    /**
     * Находит бронирования в указанном временном диапазоне
     * 
     * @param startTime время начала диапазона
     * @param endTime время окончания диапазона
     * @return список бронирований в диапазоне
     */
    @Query("""
            SELECT r FROM Reservation r 
            WHERE r.startTime >= :startTime 
            AND r.endTime <= :endTime
            ORDER BY r.startTime
            """)
    List<Reservation> findReservationsInTimeRange(@Param("startTime") LocalDateTime startTime,
                                                 @Param("endTime") LocalDateTime endTime);

    /**
     * Находит активные бронирования рабочего места в указанном временном диапазоне
     * 
     * @param workspaceId ID рабочего места
     * @param startTime время начала диапазона
     * @param endTime время окончания диапазона
     * @return список активных бронирований
     */
    @Query("""
            SELECT r FROM Reservation r 
            WHERE r.workspace.id = :workspaceId 
            AND r.status = 'ACTIVE'
            AND r.startTime >= :startTime 
            AND r.endTime <= :endTime
            ORDER BY r.startTime
            """)
    List<Reservation> findActiveReservationsInTimeRange(@Param("workspaceId") Integer workspaceId,
                                                       @Param("startTime") LocalDateTime startTime,
                                                       @Param("endTime") LocalDateTime endTime);

    /**
     * Находит бронирование по ID с загрузкой связанного рабочего места
     * 
     * @param id ID бронирования
     * @return Optional с бронированием
     */
    @Query("SELECT r FROM Reservation r JOIN FETCH r.workspace WHERE r.id = :id")
    Optional<Reservation> findByIdWithWorkspace(@Param("id") Integer id);

    /**
     * Подсчитывает количество активных бронирований пользователя
     * 
     * @param userId ID пользователя
     * @return количество активных бронирований
     */
    Integer countByUserIdAndStatus(Integer userId, ReservationStatus status);
} 