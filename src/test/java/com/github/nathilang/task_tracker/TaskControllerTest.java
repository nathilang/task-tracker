package com.github.nathilang.task_tracker;

import com.github.nathilang.task_tracker.controller.TaskController;
import com.github.nathilang.task_tracker.dto.CreateTaskDTO;
import com.github.nathilang.task_tracker.dto.TaskDTO;
import com.github.nathilang.task_tracker.dto.UpdateTaskDTO;
import com.github.nathilang.task_tracker.mapper.TaskMapper;
import com.github.nathilang.task_tracker.model.Task;
import com.github.nathilang.task_tracker.service.TaskService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.util.List;

@WebMvcTest(TaskController.class)
class TaskControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @SuppressWarnings("removal")
    @MockBean
    private TaskService service;
    @SuppressWarnings("removal")
    @MockBean
    private TaskMapper mapper;

    @Test
    void testGetTaskById() throws Exception {
        Task entity = new Task("Task 1", "Desc", false);
        TaskDTO dto = new TaskDTO();
        dto.setId(1L);
        dto.setTitle("Task 1");
        dto.setDescription("Desc");
        dto.setCompleted(false);

        when(service.getTask(1L)).thenReturn(entity);
        when(mapper.toDTO(entity)).thenReturn(dto);

        mockMvc.perform(get("/tasks/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.title").value("Task 1"))
                .andExpect(jsonPath("$.description").value("Desc"))
                .andExpect(jsonPath("$.completed").value(false));
    }

    @Test
    void testGetAllTasks() throws Exception {
        Task t1 = new Task("Task 1", "Desc", false);
        Task t2 = new Task("Task 2", "Desc", false);
        TaskDTO dto1 = new TaskDTO();
        dto1.setId(1L);
        dto1.setTitle("Task 1");
        dto1.setDescription("Desc");
        dto1.setCompleted(false);

        TaskDTO dto2 = new TaskDTO();
        dto2.setId(2L);
        dto2.setTitle("Task 2");
        dto2.setDescription("Desc");
        dto2.setCompleted(false);

        when(service.getAllTasks()).thenReturn(List.of(t1, t2));
        when(mapper.toDTOList(List.of(t1, t2))).thenReturn(List.of(dto1, dto2));

        mockMvc.perform(get("/tasks"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[1].id").value(2))
                .andExpect(jsonPath("$[0].title").value("Task 1"))
                .andExpect(jsonPath("$[1].title").value("Task 2"))
                .andExpect(jsonPath("$[0].description").value("Desc"))
                .andExpect(jsonPath("$[1].description").value("Desc"))
                .andExpect(jsonPath("$[0].completed").value(false))
                .andExpect(jsonPath("$[1].completed").value(false));
    }

    @Test
    void testCreateTask() throws Exception {
        Task entity = new Task("New Task", "Desc", false);

        TaskDTO dto = new TaskDTO();
        dto.setId(1L);
        dto.setTitle("New Task");
        dto.setDescription("Desc");
        dto.setCompleted(false);

        CreateTaskDTO cdto = new CreateTaskDTO();
        cdto.setTitle("New Task");
        cdto.setDescription("Desc");
        cdto.setCompleted(false);

        when(mapper.toEntity(cdto)).thenReturn(entity);
        when(service.createTask(any(Task.class))).thenReturn(entity);
        when(mapper.toDTO(entity)).thenReturn(dto);

        mockMvc.perform(post("/tasks")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"title\":\"New Task\",\"description\":\"Desc\",\"completed\":false}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value("New Task"))
                .andExpect(jsonPath("$.description").value("Desc"))
                .andExpect(jsonPath("$.completed").value(false));
    }

    @Test
    void testUpdateTask() throws Exception {
        Task entity = new Task("Updated", "Desc", true);

        TaskDTO dto = new TaskDTO();
        dto.setId(1L);
        dto.setTitle("Updated");
        dto.setDescription("Desc");
        dto.setCompleted(true);

        UpdateTaskDTO udto = new UpdateTaskDTO();
        udto.setTitle("Updated");
        udto.setDescription("Desc");
        udto.setCompleted(true);

        when(mapper.toEntity(udto)).thenReturn(entity);
        when(service.updateTask(1L, entity)).thenReturn(entity);
        when(mapper.toDTO(entity)).thenReturn(dto);

        mockMvc.perform(put("/tasks/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"title\":\"Updated\",\"description\":\"Desc\",\"completed\":true}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value("Updated"))
                .andExpect(jsonPath("$.description").value("Desc"))
                .andExpect(jsonPath("$.completed").value(true));
    }

    @Test
    void testDeleteTask() throws Exception {
        doNothing().when(service).deleteTask(1L);

        mockMvc.perform(delete("/tasks/1"))
                .andExpect(status().isOk());
    }

    @Test
    void testDeleteTaskNotFound() throws Exception {
        doThrow(new RuntimeException("Task not found"))
                .when(service).deleteTask(99L);

        mockMvc.perform(delete("/tasks/99"))
                .andExpect(status().isNotFound());
    }

    @Test
    void testCreateTaskMissingTitle() throws Exception {
        mockMvc.perform(post("/tasks")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"description\":\"Desc\",\"completed\":false}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.title").value("Title is required"));
    }

    @Test
    void testCreateTaskDescriptionTooLong() throws Exception {
        String longDesc = "a".repeat(300);
        mockMvc.perform(post("/tasks")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"title\":\"Task\",\"description\":\"" + longDesc + "\",\"completed\":false}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.description").value("Description must be at most 255 characters"));
    }

}
