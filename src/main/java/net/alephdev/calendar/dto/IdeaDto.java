package net.alephdev.calendar.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "DTO для создания и обновления идеи")
public class IdeaDto {
  @Schema(description = "Описание идеи", example = "Добавить темную тему в приложение")
  private String description;
}
