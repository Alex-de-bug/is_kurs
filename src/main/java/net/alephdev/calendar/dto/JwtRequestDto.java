package net.alephdev.calendar.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
@Schema(description = "DTO для запроса аутентификации")
public class JwtRequestDto {
  @NotBlank(message = "Имя не должно быть пустое")
  @Size(min = 3, max = 50, message = "Имя должно быть от 3 до 50 символов")
  @Schema(description = "Логин пользователя", example = "john.doe", minLength = 3, maxLength = 50)
  private final String username;

  @NotBlank(message = "Пароль не должен быть пустой")
  @Size(min = 6, max = 100, message = "Пароль должен быть от 6 до 100 символов")
  @Schema(
      description = "Пароль пользователя",
      example = "password123",
      minLength = 6,
      maxLength = 100)
  private final String password;
}
