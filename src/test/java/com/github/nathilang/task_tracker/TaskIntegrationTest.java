package com.github.nathilang.task_tracker;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import com.github.nathilang.task_tracker.model.Task;
import com.github.nathilang.task_tracker.repository.TaskRepository;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class TaskIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private TaskRepository repository;

    @BeforeEach
    void setup() {
        repository.deleteAll();
    }

    // This test verifies:
    // JSON → Java object
    // Validation
    // Controller
    // Service
    // Repository
    // Database write
    // Everything working together.
    @Test
    void testCreateTaskIntegration() throws Exception {
        mockMvc.perform(post("/tasks")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                        {
                            "title": "Integration Task",
                            "description": "Testing",
                            "completed": false
                        }
                        """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value("Integration Task"));

        assertEquals(1, repository.count());
    }

    // This verifies:
    // DB read
    // Service
    // Controller
    // JSON response
    @Test
    void testGetTaskByIdIntegration() throws Exception {
        // Task saved = repository.save(new Task("Test", "Desc", false));
        Task t = new Task("Test", "Desc", false);
        System.out.println("Before save: " + t);
        Task saved = repository.save(t);
        System.out.println("After save: " + saved);
        Task find = repository.findById(saved.getId())
                .orElseThrow(() -> new RuntimeException("Task not found with id: " + saved.getId()));
        System.out.println("Find save: " + find);
        mockMvc.perform(get("/tasks/" + saved.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value("Test"));
    }

    @Test
    void testDeleteTaskIntegration() throws Exception {
        Task saved = repository.save(new Task("Delete Me", "Desc", false));

        mockMvc.perform(delete("/tasks/" + saved.getId()))
                .andExpect(status().isOk());

        assertEquals(0, repository.count());
    }

    @Test
    void testDeleteTaskNotFoundIntegration() throws Exception {
        mockMvc.perform(delete("/tasks/999"))
                .andExpect(status().isNotFound());
    }

}
