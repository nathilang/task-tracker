package com.github.nathilang.task_tracker;

import com.github.nathilang.task_tracker.model.Task;
import com.github.nathilang.task_tracker.repository.TaskRepository;
import com.github.nathilang.task_tracker.service.TaskService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class TaskServiceTest {

    private TaskRepository repository;
    private TaskService service;

    @BeforeEach
    void setUp() {
        repository = Mockito.mock(TaskRepository.class);
        service = new TaskService(repository);
    }

    @Test
    void testGetTaskById() {
        Task task = new Task();
        task.setId(1L);
        task.setTitle("Test Task");

        when(repository.findById(1L)).thenReturn(Optional.of(task));

        Task result = service.getTask(1L);

        assertNotNull(result);
        assertEquals("Test Task", result.getTitle());
        verify(repository, times(1)).findById(1L);
    }

    @Test
    void testGetTaskNotFound() {
        when(repository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> service.getTask(99L));
    }

    @Test
    void testCreateTask() {
        Task task = new Task();
        task.setTitle("New Task");

        when(repository.save(task)).thenReturn(task);

        Task result = service.createTask(task);

        assertNotNull(result);
        assertEquals("New Task", result.getTitle());
        verify(repository, times(1)).save(task);
    }

}
