package net.alephdev.calendar.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDate;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
@Schema(description = "DTO для создания и обновления релиза")
public class ReleaseDto {
  @Schema(description = "Версия релиза", example = "1.2.0")
  private String version;
  
  @Schema(description = "Дата релиза", example = "2024-01-15")
  private LocalDate releaseDate;
  
  @Schema(description = "Описание релиза", example = "Добавлена поддержка темной темы")
  private String description;
  
  @Schema(description = "ID спринта", example = "1")
  private Integer sprintId;
}
