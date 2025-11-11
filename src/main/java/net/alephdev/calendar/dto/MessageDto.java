package net.alephdev.calendar.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
@Schema(description = "DTO для сообщений об ошибках и уведомлений")
public class MessageDto {
  @Schema(description = "Текст сообщения", example = "Операция выполнена успешно")
  private String message;
}
