package com.coworking.bookingservice.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.coworking.bookingservice.entity.Workspace;

/**
 * Репозиторий для работы с рабочими местами
 * 
 * Предоставляет методы для поиска и управления рабочими местами,
 * включая поиск доступных мест на определенное время.
 */
@Repository
public interface WorkspaceRepository extends JpaRepository<Workspace, Integer> {

    /**
     * Находит все активные рабочие места
     * 
     * @return список активных рабочих мест
     */
    List<Workspace> findByIsActiveTrue();

    /**
     * Находит рабочие места с указанной вместимостью или больше
     * 
     * @param minCapacity минимальная вместимость
     * @return список подходящих рабочих мест
     */
    List<Workspace> findByCapacityGreaterThanEqualAndIsActiveTrue(Integer minCapacity);

    /**
     * Находит доступные рабочие места на указанный период времени
     * 
     * @param startTime время начала
     * @param endTime время окончания
     * @return список доступных рабочих мест
     */
    @Query("""
            SELECT DISTINCT w FROM Workspace w
            WHERE w.isActive = true 
            AND w.id NOT IN (
                SELECT DISTINCT r.workspace.id 
                FROM Reservation r 
                WHERE r.status = 'ACTIVE' 
                AND r.startTime < :endTime 
                AND r.endTime > :startTime
            )
            ORDER BY w.name
            """)
    List<Workspace> findAvailableWorkspaces(@Param("startTime") java.time.LocalDateTime startTime,
                                           @Param("endTime") java.time.LocalDateTime endTime);

    /**
     * Находит доступные рабочие места с минимальной вместимостью на указанный период
     * 
     * @param startTime время начала
     * @param endTime время окончания
     * @param minCapacity минимальная вместимость
     * @return список доступных рабочих мест
     */
    @Query("""
            SELECT DISTINCT w FROM Workspace w 
            WHERE w.isActive = true 
            AND w.capacity >= :minCapacity
            AND w.id NOT IN (
                SELECT DISTINCT r.workspace.id 
                FROM Reservation r 
                WHERE r.status = 'ACTIVE' 
                AND r.startTime < :endTime 
                AND r.endTime > :startTime
            )
            ORDER BY w.capacity DESC, w.name
            """)
    List<Workspace> findAvailableWorkspacesWithCapacity(@Param("startTime") java.time.LocalDateTime startTime,
                                                       @Param("endTime") java.time.LocalDateTime endTime,
                                                       @Param("minCapacity") Integer minCapacity);
} 