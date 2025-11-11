package net.alephdev.calendar.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import net.alephdev.calendar.models.User;

@Getter
@AllArgsConstructor
@Schema(description = "DTO для ответа аутентификации")
public class JwtResponseDto {
  @Schema(
      description = "JWT токен для аутентификации",
      example = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...")
  private final String token;

  @Schema(description = "Информация о пользователе")
  private final User user;
}
