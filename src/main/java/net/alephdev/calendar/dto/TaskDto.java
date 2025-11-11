package net.alephdev.calendar.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import net.alephdev.calendar.models.Task;

@Getter
@AllArgsConstructor
@Schema(description = "DTO для создания и обновления задачи")
public class TaskDto {
  @Schema(description = "Название задачи", example = "Реализовать авторизацию")
  private String name;
  
  @Schema(description = "Количество story points", example = "5")
  private Integer storyPoints;
  
  @Schema(description = "Приоритет задачи", example = "HIGH")
  private Task.Priority priorityEnum;
}
