package net.alephdev.calendar.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDate;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
@Schema(description = "DTO для создания и обновления спринта")
public class SprintDto {
  @Schema(description = "Основная версия спринта", example = "1.0")
  private final String majorVersion;
  
  @Schema(description = "Дата начала спринта", example = "2024-01-01")
  private final LocalDate startDate;
  
  @Schema(description = "Дата окончания спринта", example = "2024-01-15")
  private final LocalDate endDate;
  
  @Schema(description = "Дата начала регрессионного тестирования", example = "2024-01-10")
  private final LocalDate regressionStart;
  
  @Schema(description = "Дата окончания регрессионного тестирования", example = "2024-01-14")
  private final LocalDate regressionEnd;
  
  @Schema(description = "ID команды, ответственной за спринт", example = "1")
  private final Integer teamId;
}
