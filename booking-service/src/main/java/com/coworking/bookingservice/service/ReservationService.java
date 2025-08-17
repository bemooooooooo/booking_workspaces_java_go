package com.coworking.bookingservice.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.coworking.bookingservice.dto.CreateReservationRequest;
import com.coworking.bookingservice.dto.ReservationDto;
import com.coworking.bookingservice.entity.Reservation;
import com.coworking.bookingservice.entity.ReservationStatus;
import com.coworking.bookingservice.entity.Workspace;
import com.coworking.bookingservice.repository.ReservationRepository;
import com.coworking.bookingservice.repository.WorkspaceRepository;

/**
 * Сервис для работы с бронированиями
 * 
 * Предоставляет бизнес-логику для создания, управления и отмены бронирований,
 * включая проверки на пересечение временных интервалов.
 */
@Service
@Transactional
public class ReservationService {

    private final ReservationRepository reservationRepository;
    private final WorkspaceRepository workspaceRepository;

    public ReservationService(ReservationRepository reservationRepository, 
                            WorkspaceRepository workspaceRepository) {
        this.reservationRepository = reservationRepository;
        this.workspaceRepository = workspaceRepository;
    }

    /**
     * Создает новое бронирование
     * 
     * @param userId ID пользователя
     * @param request данные для создания бронирования
     * @return созданное бронирование
     * @throws IllegalArgumentException если место недоступно или данные некорректны
     */
    public ReservationDto createReservation(Integer userId, CreateReservationRequest request) {
        validateCreateRequest(request);
        
        Workspace workspace = workspaceRepository.findById(request.getWorkspaceId())
                .orElseThrow(() -> new IllegalArgumentException("Рабочее место не найдено"));
        
        if (!workspace.getIsActive()) {
            throw new IllegalArgumentException("Рабочее место неактивно");
        }
        
        // Проверяем доступность места
        List<Reservation> overlappingReservations = reservationRepository.findOverlappingReservations(
                request.getWorkspaceId(), request.getStartTime(), request.getEndTime(), null);
        
        if (!overlappingReservations.isEmpty()) {
            throw new IllegalArgumentException("Место уже забронировано на указанное время");
        }
        
        Reservation reservation = new Reservation();
        reservation.setUserId(userId);
        reservation.setWorkspace(workspace);
        reservation.setStartTime(request.getStartTime());
        reservation.setEndTime(request.getEndTime());
        reservation.setStatus(ReservationStatus.ACTIVE);
        
        Reservation savedReservation = reservationRepository.save(reservation);
        return convertToDto(savedReservation);
    }

    /**
     * Получает бронирование по ID
     * 
     * @param id ID бронирования
     * @return Optional с бронированием
     */
    @Transactional(readOnly = true)
    public Optional<ReservationDto> getReservationById(Integer id) {
        return reservationRepository.findByIdWithWorkspace(id)
                .map(this::convertToDto);
    }

    /**
     * Получает все бронирования пользователя
     * 
     * @param userId ID пользователя
     * @return список бронирований пользователя
     */
    @Transactional(readOnly = true)
    public List<ReservationDto> getUserReservations(Integer userId) {
        return reservationRepository.findByUserIdOrderByStartTimeDesc(userId)
                .stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    /**
     * Получает активные бронирования пользователя
     * 
     * @param userId ID пользователя
     * @return список активных бронирований
     */
    @Transactional(readOnly = true)
    public List<ReservationDto> getUserActiveReservations(Integer userId) {
        return reservationRepository.findByUserIdAndStatusOrderByStartTimeDesc(userId, ReservationStatus.ACTIVE)
                .stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    /**
     * Отменяет бронирование
     * 
     * @param reservationId ID бронирования
     * @param userId ID пользователя (для проверки прав)
     * @return true если бронирование было отменено
     */
    public boolean cancelReservation(Integer reservationId, Integer userId) {
        return reservationRepository.findById(reservationId)
                .map(reservation -> {
                    if (!reservation.getUserId().equals(userId)) {
                        throw new IllegalArgumentException("Нет прав для отмены этого бронирования");
                    }
                    if (!reservation.isActive()) {
                        throw new IllegalArgumentException("Бронирование уже отменено или завершено");
                    }
                    reservation.setStatus(ReservationStatus.CANCELLED);
                    reservationRepository.save(reservation);
                    return true;
                })
                .orElse(false);
    }

    /**
     * Обновляет время бронирования
     * 
     * @param reservationId ID бронирования
     * @param userId ID пользователя
     * @param newStartTime новое время начала
     * @param newEndTime новое время окончания
     * @return обновленное бронирование
     */
    public Optional<ReservationDto> updateReservationTime(Integer reservationId, Integer userId,
                                                         LocalDateTime newStartTime, LocalDateTime newEndTime) {
        validateTimeRange(newStartTime, newEndTime);
        
        return reservationRepository.findById(reservationId)
                .map(reservation -> {
                    if (!reservation.getUserId().equals(userId)) {
                        throw new IllegalArgumentException("Нет прав для изменения этого бронирования");
                    }
                    if (!reservation.isActive()) {
                        throw new IllegalArgumentException("Нельзя изменить отмененное или завершенное бронирование");
                    }
                    
                    // Проверяем доступность места на новое время
                    List<Reservation> overlappingReservations = reservationRepository.findOverlappingReservations(
                            reservation.getWorkspace().getId(), newStartTime, newEndTime, reservationId);
                    
                    if (!overlappingReservations.isEmpty()) {
                        throw new IllegalArgumentException("Место уже забронировано на новое время");
                    }
                    
                    reservation.setStartTime(newStartTime);
                    reservation.setEndTime(newEndTime);
                    
                    Reservation savedReservation = reservationRepository.save(reservation);
                    return convertToDto(savedReservation);
                });
    }

    /**
     * Получает бронирования рабочего места
     * 
     * @param workspaceId ID рабочего места
     * @return список бронирований
     */
    @Transactional(readOnly = true)
    public List<ReservationDto> getWorkspaceReservations(Integer workspaceId) {
        return reservationRepository.findByWorkspaceIdAndStatusOrderByStartTime(workspaceId, ReservationStatus.ACTIVE)
                .stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    /**
     * Получает бронирования в указанном временном диапазоне
     * 
     * @param startTime время начала диапазона
     * @param endTime время окончания диапазона
     * @return список бронирований
     */
    @Transactional(readOnly = true)
    public List<ReservationDto> getReservationsInTimeRange(LocalDateTime startTime, LocalDateTime endTime) {
        validateTimeRange(startTime, endTime);
        
        return reservationRepository.findReservationsInTimeRange(startTime, endTime)
                .stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    /**
     * Конвертирует сущность в DTO
     * 
     * @param reservation сущность бронирования
     * @return DTO бронирования
     */
    private ReservationDto convertToDto(Reservation reservation) {
        ReservationDto dto = new ReservationDto();
        dto.setId(reservation.getId());
        dto.setUserId(reservation.getUserId());
        dto.setWorkspaceId(reservation.getWorkspace().getId());
        dto.setWorkspaceName(reservation.getWorkspace().getName());
        dto.setStartTime(reservation.getStartTime());
        dto.setEndTime(reservation.getEndTime());
        dto.setStatus(com.coworking.bookingservice.dto.ReservationStatus.valueOf(reservation.getStatus().name()));
        dto.setCreatedAt(reservation.getCreatedAt());
        dto.setUpdatedAt(reservation.getUpdatedAt());
        return dto;
    }

    /**
     * Валидирует запрос на создание бронирования
     * 
     * @param request запрос
     * @throws IllegalArgumentException если данные некорректны
     */
    private void validateCreateRequest(CreateReservationRequest request) {
        if (request.getWorkspaceId() == null) {
            throw new IllegalArgumentException("ID рабочего места обязателен");
        }
        validateTimeRange(request.getStartTime(), request.getEndTime());
    }

    /**
     * Валидирует временной диапазон
     * 
     * @param startTime время начала
     * @param endTime время окончания
     * @throws IllegalArgumentException если диапазон некорректный
     */
    private void validateTimeRange(LocalDateTime startTime, LocalDateTime endTime) {
        if (startTime == null || endTime == null) {
            throw new IllegalArgumentException("Время начала и окончания не может быть null");
        }
        if (startTime.isAfter(endTime)) {
            throw new IllegalArgumentException("Время начала не может быть позже времени окончания");
        }
        if (startTime.isBefore(LocalDateTime.now())) {
            throw new IllegalArgumentException("Время начала не может быть в прошлом");
        }
        if (startTime.isEqual(endTime)) {
            throw new IllegalArgumentException("Время начала и окончания не может быть одинаковым");
        }
    }
} 