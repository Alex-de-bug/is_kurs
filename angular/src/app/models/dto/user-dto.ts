/**
 * DTO для создания или обновления пользователя.
 *
 * @property login - Логин пользователя
 * @property email - Email пользователя
 * @property password - Пароль (может быть null при обновлении без смены пароля)
 * @property firstName - Имя
 * @property lastName - Фамилия
 */
export interface UserDto {
  login: string;
  email: string;
  password: string | null;
  firstName: string;
  lastName: string;
}
