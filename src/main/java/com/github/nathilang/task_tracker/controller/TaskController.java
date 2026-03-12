package com.github.nathilang.task_tracker.controller;

import com.github.nathilang.task_tracker.dto.CreateTaskDTO;
import com.github.nathilang.task_tracker.dto.TaskDTO;
import com.github.nathilang.task_tracker.dto.UpdateTaskDTO;
import com.github.nathilang.task_tracker.mapper.TaskMapper;
import com.github.nathilang.task_tracker.model.Task;
import com.github.nathilang.task_tracker.service.TaskService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import jakarta.validation.Valid;

import java.util.List;

@RestController
@RequestMapping("/tasks")
public class TaskController {

    private final TaskService service;
    @Autowired
    private TaskMapper mapper;

    public TaskController(TaskService service) {
        this.service = service;
    }

    @PostMapping
    public TaskDTO createTask(@Valid @RequestBody CreateTaskDTO dto) {
        Task task = mapper.toEntity(dto);
        Task saved = service.createTask(task);
        return mapper.toDTO(saved);
    }

    @GetMapping
    public List<TaskDTO> getAllTasks() {
        return mapper.toDTOList(service.getAllTasks());
    }

    @GetMapping("/{id}")
    public TaskDTO getTask(@PathVariable Long id) {
        System.out.println(service.getTask(id));
        return mapper.toDTO(service.getTask(id));
    }

    @PutMapping("/{id}")
    public TaskDTO updateTask(@PathVariable Long id, @Valid @RequestBody UpdateTaskDTO dto) {
        Task task = mapper.toEntity(dto);
        Task saved = service.updateTask(id, task);
        return mapper.toDTO(saved);
    }

    @DeleteMapping("/{id}")
    public void deleteTask(@PathVariable Long id) {
        service.deleteTask(id);
    }

    @ExceptionHandler(RuntimeException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public String handleNotFound(RuntimeException ex) {
        return ex.getMessage();
    }
}
