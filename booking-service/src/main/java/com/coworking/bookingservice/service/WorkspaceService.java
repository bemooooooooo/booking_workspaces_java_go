package com.coworking.bookingservice.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.coworking.bookingservice.dto.WorkspaceDto;
import com.coworking.bookingservice.entity.Workspace;
import com.coworking.bookingservice.repository.WorkspaceRepository;

/**
 * Сервис для работы с рабочими местами
 * 
 * Предоставляет бизнес-логику для управления рабочими местами,
 * включая поиск доступных мест на определенное время.
 */
@Service
@Transactional
public class WorkspaceService {

    private final WorkspaceRepository workspaceRepository;

    public WorkspaceService(WorkspaceRepository workspaceRepository) {
        this.workspaceRepository = workspaceRepository;
    }

    /**
     * Получает все активные рабочие места
     * 
     * @return список активных рабочих мест
     */
    @Transactional(readOnly = true)
    public List<WorkspaceDto> getAllActiveWorkspaces() {
        return workspaceRepository.findByIsActiveTrue()
                .stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    /**
     * Получает рабочее место по ID
     * 
     * @param id ID рабочего места
     * @return Optional с рабочим местом
     */
    @Transactional(readOnly = true)
    public Optional<WorkspaceDto> getWorkspaceById(Integer id) {
        return workspaceRepository.findById(id)
                .map(this::convertToDto);
    }

    /**
     * Получает доступные рабочие места на указанный период времени
     * 
     * @param startTime время начала
     * @param endTime время окончания
     * @return список доступных рабочих мест
     */
    @Transactional(readOnly = true)
    public List<WorkspaceDto> getAvailableWorkspaces(LocalDateTime startTime, LocalDateTime endTime) {
        validateTimeRange(startTime, endTime);
        
        return workspaceRepository.findAvailableWorkspaces(startTime, endTime)
                .stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    /**
     * Получает доступные рабочие места с минимальной вместимостью
     * 
     * @param startTime время начала
     * @param endTime время окончания
     * @param minCapacity минимальная вместимость
     * @return список доступных рабочих мест
     */
    @Transactional(readOnly = true)
    public List<WorkspaceDto> getAvailableWorkspacesWithCapacity(LocalDateTime startTime, 
                                                                LocalDateTime endTime, 
                                                                Integer minCapacity) {
        validateTimeRange(startTime, endTime);
        validateCapacity(minCapacity);
        
        return workspaceRepository.findAvailableWorkspacesWithCapacity(startTime, endTime, minCapacity)
                .stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    /**
     * Создает новое рабочее место
     * 
     * @param workspaceDto данные рабочего места
     * @return созданное рабочее место
     */
    public WorkspaceDto createWorkspace(WorkspaceDto workspaceDto) {
        Workspace workspace = new Workspace();
        workspace.setName(workspaceDto.getName());
        workspace.setDescription(workspaceDto.getDescription());
        workspace.setCapacity(workspaceDto.getCapacity());
        workspace.setIsActive(true);
        
        Workspace savedWorkspace = workspaceRepository.save(workspace);
        return convertToDto(savedWorkspace);
    }

    /**
     * Обновляет рабочее место
     * 
     * @param id ID рабочего места
     * @param workspaceDto новые данные
     * @return обновленное рабочее место
     */
    public Optional<WorkspaceDto> updateWorkspace(Integer id, WorkspaceDto workspaceDto) {
        return workspaceRepository.findById(id)
                .map(workspace -> {
                    workspace.setName(workspaceDto.getName());
                    workspace.setDescription(workspaceDto.getDescription());
                    workspace.setCapacity(workspaceDto.getCapacity());
                    workspace.setIsActive(workspaceDto.getIsActive());
                    return convertToDto(workspaceRepository.save(workspace));
                });
    }

    /**
     * Деактивирует рабочее место
     * 
     * @param id ID рабочего места
     * @return true если рабочее место было деактивировано
     */
    public boolean deactivateWorkspace(Integer id) {
        return workspaceRepository.findById(id)
                .map(workspace -> {
                    workspace.setIsActive(false);
                    workspaceRepository.save(workspace);
                    return true;
                })
                .orElse(false);
    }

    /**
     * Проверяет, доступно ли рабочее место на указанный период
     * 
     * @param workspaceId ID рабочего места
     * @param startTime время начала
     * @param endTime время окончания
     * @return true если место доступно
     */
    @Transactional(readOnly = true)
    public boolean isWorkspaceAvailable(Integer workspaceId, LocalDateTime startTime, LocalDateTime endTime) {
        validateTimeRange(startTime, endTime);
        
        List<Workspace> availableWorkspaces = workspaceRepository.findAvailableWorkspaces(startTime, endTime);
        return availableWorkspaces.stream()
                .anyMatch(workspace -> workspace.getId().equals(workspaceId));
    }

    /**
     * Конвертирует сущность в DTO
     * 
     * @param workspace сущность рабочего места
     * @return DTO рабочего места
     */
    private WorkspaceDto convertToDto(Workspace workspace) {
        return new WorkspaceDto(
                workspace.getId(),
                workspace.getName(),
                workspace.getDescription(),
                workspace.getCapacity(),
                workspace.getIsActive(),
                workspace.getCreatedAt()
        );
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
    }

    /**
     * Валидирует вместимость
     * 
     * @param capacity вместимость
     * @throws IllegalArgumentException если вместимость некорректная
     */
    private void validateCapacity(Integer capacity) {
        if (capacity == null || capacity < 1) {
            throw new IllegalArgumentException("Вместимость должна быть не менее 1");
        }
    }
} 