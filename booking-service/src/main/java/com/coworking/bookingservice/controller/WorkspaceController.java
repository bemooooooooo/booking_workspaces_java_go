package com.coworking.bookingservice.controller;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.coworking.bookingservice.dto.WorkspaceDto;
import com.coworking.bookingservice.service.WorkspaceService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;

/**
 * REST контроллер для работы с рабочими местами
 * 
 * Предоставляет API для получения информации о рабочих местах,
 * поиска доступных мест и административного управления.
 */
@RestController
@RequestMapping("/workspaces")
@Tag(name = "Рабочие места", description = "API для работы с рабочими местами")
public class WorkspaceController {

    private final WorkspaceService workspaceService;

    public WorkspaceController(WorkspaceService workspaceService) {
        this.workspaceService = workspaceService;
    }

    /**
     * Получает все активные рабочие места
     * 
     * @return список активных рабочих мест
     */
    @GetMapping
    @Operation(summary = "Получить все активные рабочие места", 
               description = "Возвращает список всех активных рабочих мест в коворкинге")
    public ResponseEntity<List<WorkspaceDto>> getAllWorkspaces() {
        List<WorkspaceDto> workspaces = workspaceService.getAllActiveWorkspaces();
        return ResponseEntity.ok(workspaces);
    }

    /**
     * Получает рабочее место по ID
     * 
     * @param id ID рабочего места
     * @return рабочее место или 404 если не найдено
     */
    @GetMapping("/{id}")
    @Operation(summary = "Получить рабочее место по ID", 
               description = "Возвращает информацию о конкретном рабочем месте")
    public ResponseEntity<WorkspaceDto> getWorkspaceById(
            @Parameter(description = "ID рабочего места", example = "1")
            @PathVariable Integer id) {
        return workspaceService.getWorkspaceById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    /**
     * Получает доступные рабочие места на указанный период времени
     * 
     * @param startTime время начала
     * @param endTime время окончания
     * @return список доступных рабочих мест
     */
    @GetMapping("/available")
    @Operation(summary = "Найти доступные рабочие места", 
               description = "Возвращает список рабочих мест, доступных для бронирования на указанный период")
    public ResponseEntity<List<WorkspaceDto>> getAvailableWorkspaces(
            @Parameter(description = "Время начала", example = "2024-01-15 10:00:00")
            @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime startTime,
            @Parameter(description = "Время окончания", example = "2024-01-15 12:00:00")
            @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime endTime) {
        try {
            List<WorkspaceDto> availableWorkspaces = workspaceService.getAvailableWorkspaces(startTime, endTime);
            return ResponseEntity.ok(availableWorkspaces);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    /**
     * Получает доступные рабочие места с минимальной вместимостью
     * 
     * @param startTime время начала
     * @param endTime время окончания
     * @param minCapacity минимальная вместимость
     * @return список доступных рабочих мест
     */
    @GetMapping("/available/capacity")
    @Operation(summary = "Найти доступные рабочие места с минимальной вместимостью", 
               description = "Возвращает список рабочих мест с указанной или большей вместимостью")
    public ResponseEntity<List<WorkspaceDto>> getAvailableWorkspacesWithCapacity(
            @Parameter(description = "Время начала", example = "2024-01-15 10:00:00")
            @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime startTime,
            @Parameter(description = "Время окончания", example = "2024-01-15 12:00:00")
            @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime endTime,
            @Parameter(description = "Минимальная вместимость", example = "5")
            @RequestParam Integer minCapacity) {
        try {
            List<WorkspaceDto> availableWorkspaces = workspaceService.getAvailableWorkspacesWithCapacity(
                    startTime, endTime, minCapacity);
            return ResponseEntity.ok(availableWorkspaces);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    /**
     * Создает новое рабочее место (только для администраторов)
     * 
     * @param workspaceDto данные рабочего места
     * @return созданное рабочее место
     */
    @PostMapping
    @PreAuthorize("hasRole('admin')")
    @Operation(summary = "Создать новое рабочее место", 
               description = "Создает новое рабочее место в коворкинге (требуются права администратора)")
    public ResponseEntity<WorkspaceDto> createWorkspace(
            @Parameter(description = "Данные рабочего места")
            @RequestBody WorkspaceDto workspaceDto) {
        try {
            WorkspaceDto createdWorkspace = workspaceService.createWorkspace(workspaceDto);
            return ResponseEntity.status(HttpStatus.CREATED).body(createdWorkspace);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    /**
     * Обновляет рабочее место (только для администраторов)
     * 
     * @param id ID рабочего места
     * @param workspaceDto новые данные
     * @return обновленное рабочее место
     */
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Обновить рабочее место", 
               description = "Обновляет информацию о рабочем месте (требуются права администратора)")
    public ResponseEntity<WorkspaceDto> updateWorkspace(
            @Parameter(description = "ID рабочего места", example = "1")
            @PathVariable Integer id,
            @Parameter(description = "Новые данные рабочего места")
            @RequestBody WorkspaceDto workspaceDto) {
        return workspaceService.updateWorkspace(id, workspaceDto)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    /**
     * Деактивирует рабочее место (только для администраторов)
     * 
     * @param id ID рабочего места
     * @return 204 если успешно деактивировано
     */
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Деактивировать рабочее место", 
               description = "Деактивирует рабочее место (требуются права администратора)")
    public ResponseEntity<Void> deactivateWorkspace(
            @Parameter(description = "ID рабочего места", example = "1")
            @PathVariable Integer id) {
        boolean deactivated = workspaceService.deactivateWorkspace(id);
        return deactivated ? ResponseEntity.noContent().build() : ResponseEntity.notFound().build();
    }
} 