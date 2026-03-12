package com.github.nathilang.task_tracker.mapper;

import java.util.List;

import org.mapstruct.Mapper;
import com.github.nathilang.task_tracker.dto.CreateTaskDTO;
import com.github.nathilang.task_tracker.dto.TaskDTO;
import com.github.nathilang.task_tracker.dto.UpdateTaskDTO;
import com.github.nathilang.task_tracker.model.Task;

@Mapper(componentModel = "spring")
public interface TaskMapper {

    Task toEntity(CreateTaskDTO dto);

    Task toEntity(UpdateTaskDTO dto);

    TaskDTO toDTO(Task task);

    List<TaskDTO> toDTOList(List<Task> tasks);
}
