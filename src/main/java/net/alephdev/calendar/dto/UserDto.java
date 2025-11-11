package net.alephdev.calendar.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
@Schema(description = "DTO для создания и обновления пользователя")
public class UserDto {
  @NotBlank(message = "Логин не должен быть пустой")
  @Size(min = 3, max = 50, message = "Логин должен быть от 3 до 50 символов")
  @Schema(
      description = "Уникальный логин пользователя",
      example = "john.doe",
      minLength = 3,
      maxLength = 50)
  private final String login;

  @NotBlank(message = "Почта не должна быть пустой")
  @Email(message = "Неверный формат почты")
  @Schema(description = "Email адрес пользователя", example = "john.doe@example.com")
  private final String email;

  @NotBlank(message = "Пароль не должен быть пустой")
  @Size(min = 6, message = "Пароль должен быть от 6 символов")
  @Schema(description = "Пароль пользователя", example = "password123", minLength = 6)
  private final String password;

  @NotBlank(message = "Имя не должно быть пустым")
  @Schema(description = "Имя пользователя", example = "Иван")
  private final String firstName;

  @NotBlank(message = "Фамилия не должна быть пустой")
  @Schema(description = "Фамилия пользователя", example = "Иванов")
  private final String lastName;
}
