package com.coworking.bookingservice.controller;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.coworking.bookingservice.dto.CreateReservationRequest;
import com.coworking.bookingservice.dto.ReservationDto;
import com.coworking.bookingservice.jwtUtils.UserPrincipal;
import com.coworking.bookingservice.service.ReservationService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;

/**
 * REST контроллер для работы с бронированиями
 *
 * Предоставляет API для создания, управления и отмены бронирований,
 * а также получения информации о бронированиях пользователей.
 */
@RestController
@RequestMapping("/reservations")
@Tag(name = "Бронирования", description = "API для работы с бронированиями")
public class ReservationController {

    private final ReservationService reservationService;

    public ReservationController(ReservationService reservationService) {
        this.reservationService = reservationService;
    }

    /**
     * Создает новое бронирование
     *
     * @param userId ID пользователя (из токена аутентификации)
     * @param request данные для создания бронирования
     * @return созданное бронирование
     */
    @PostMapping
    @Operation(summary = "Создать бронирование",
               description = "Создает новое бронирование рабочего места на указанный период времени")
    public ResponseEntity<ReservationDto> createReservation(
            @Parameter(description = "Данные для создания бронирования")
            @RequestBody CreateReservationRequest request) {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            UserPrincipal userPrincipal = (UserPrincipal) authentication.getPrincipal();
            ReservationDto createdReservation = reservationService.createReservation(userPrincipal.getUserId(), request);
            return ResponseEntity.status(HttpStatus.CREATED).body(createdReservation);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    /**
     * Получает бронирование по ID
     *
     * @param id ID бронирования
     * @return бронирование или 404 если не найдено
     */
    @GetMapping("/{id}")
    @Operation(summary = "Получить бронирование по ID", 
               description = "Возвращает информацию о конкретном бронировании")
    public ResponseEntity<ReservationDto> getReservationById(
            @Parameter(description = "ID бронирования", example = "1")
            @PathVariable Integer id) {
        return reservationService.getReservationById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    /**
     * Получает все бронирования пользователя
     *
     * @param userId ID пользователя (из токена аутентификации)
     * @return список бронирований пользователя
     */
    @GetMapping("/user")
    @Operation(summary = "Получить бронирования пользователя", 
               description = "Возвращает все бронирования конкретного пользователя")
    public ResponseEntity<List<ReservationDto>> getUserReservations() {
        try{
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            UserPrincipal userPrincipal = (UserPrincipal) authentication.getPrincipal();
            List<ReservationDto> reservations = reservationService.getUserReservations(userPrincipal.getUserId());
            return ResponseEntity.ok(reservations);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    /**
     * Получает активные бронирования пользователя
     *
     * @param userId ID пользователя (из токена аутентификации)
     * @return список активных бронирований
     */
    @GetMapping("/user/active")
    @Operation(summary = "Получить активные бронирования пользователя",
               description = "Возвращает только активные бронирования пользователя")
    public ResponseEntity<List<ReservationDto>> getUserActiveReservations() {
        try{
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            UserPrincipal userPrincipal = (UserPrincipal) authentication.getPrincipal();
            List<ReservationDto> reservations = reservationService.getUserActiveReservations(userPrincipal.getUserId());
            return ResponseEntity.ok(reservations);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    /**
     * Отменяет бронирование
     * 
     * @param id ID бронирования
     * @param userId ID пользователя (для проверки прав) (получаем из токена аутентификации)
     * @return 204 если успешно отменено
     */
    @DeleteMapping("/{id}")
    @Operation(summary = "Отменить бронирование", 
               description = "Отменяет активное бронирование (только владелец бронирования)")
    public ResponseEntity<Void> cancelReservation(
            @Parameter(description = "ID бронирования", example = "1")
            @PathVariable Integer id) {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            UserPrincipal userPrincipal = (UserPrincipal) authentication.getPrincipal();
            boolean cancelled = reservationService.cancelReservation(id, userPrincipal.getUserId());
            return cancelled ? ResponseEntity.noContent().build() : ResponseEntity.notFound().build();
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    /**
     * Обновляет время бронирования
     * 
     * @param id ID бронирования
     * @param userId ID пользователя (из токена аутентификации)
     * @param newStartTime новое время начала
     * @param newEndTime новое время окончания
     * @return обновленное бронирование
     */
    @PutMapping("/{id}/time")
    @Operation(summary = "Обновить время бронирования", 
               description = "Изменяет время активного бронирования (только владелец бронирования)")
    public ResponseEntity<ReservationDto> updateReservationTime(
            @Parameter(description = "ID бронирования", example = "1")
            @PathVariable Integer id,
            @Parameter(description = "Новое время начала", example = "2024-01-15 11:00:00")
            @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime newStartTime,
            @Parameter(description = "Новое время окончания", example = "2024-01-15 13:00:00")
            @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime newEndTime) {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            UserPrincipal userPrincipal = (UserPrincipal) authentication.getPrincipal();
            return reservationService.updateReservationTime(id, userPrincipal.getUserId(), newStartTime, newEndTime)
                    .map(ResponseEntity::ok)
                    .orElse(ResponseEntity.notFound().build());
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    /**
     * Получает бронирования рабочего места
     * 
     * @param workspaceId ID рабочего места
     * @return список бронирований
     */
    @GetMapping("/workspace/{workspaceId}")
    @Operation(summary = "Получить бронирования рабочего места", 
               description = "Возвращает все активные бронирования конкретного рабочего места")
    public ResponseEntity<List<ReservationDto>> getWorkspaceReservations(
            @Parameter(description = "ID рабочего места", example = "1")
            @PathVariable Integer workspaceId) {
        List<ReservationDto> reservations = reservationService.getWorkspaceReservations(workspaceId);
        return ResponseEntity.ok(reservations);
    }

    /**
     * Получает бронирования в указанном временном диапазоне
     * 
     * @param startTime время начала диапазона
     * @param endTime время окончания диапазона
     * @return список бронирований
     */
    @GetMapping("/range")
    @Operation(summary = "Получить бронирования в временном диапазоне", 
               description = "Возвращает все бронирования в указанном временном диапазоне")
    public ResponseEntity<List<ReservationDto>> getReservationsInTimeRange(
            @Parameter(description = "Время начала диапазона", example = "2024-01-15 00:00:00")
            @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime startTime,
            @Parameter(description = "Время окончания диапазона", example = "2024-01-15 23:59:59")
            @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime endTime) {
        try {
            List<ReservationDto> reservations = reservationService.getReservationsInTimeRange(startTime, endTime);
            return ResponseEntity.ok(reservations);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        }
    }
}