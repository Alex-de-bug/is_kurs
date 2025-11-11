package net.alephdev.calendar.dto.functional;

import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDate;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
@Schema(description = "Спринт с данными команды для представлений и фильтров")
public class SprintTeamDto {
  @Schema(description = "ID спринта", example = "42")
  private final Integer sprintId;

  @Schema(description = "Основная версия спринта", example = "1.5")
  private final String majorVersion;

  @Schema(description = "Дата начала спринта", example = "2024-03-01")
  private final LocalDate startDate;

  @Schema(description = "Дата окончания спринта", example = "2024-03-14")
  private final LocalDate endDate;

  @Schema(description = "Название команды", example = "Platform Team")
  private final String teamName;

  @Schema(description = "Цвет команды в HEX", example = "#4F46E5")
  private final String teamColor;

  public SprintTeamDto(SprintTeamProjection projection) {
    this(
        projection.getSprintId(),
        projection.getMajorVersion(),
        projection.getStartDate(),
        projection.getEndDate(),
        projection.getTeamName(),
        projection.getTeamColor());
  }
}
