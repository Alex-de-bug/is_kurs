import {Team} from "./team";
import {Role} from "./role";

/**
 * Модель пользователя системы.
 *
 * Представляет пользователя с его личными данными, ролью и командой.
 * Пользователь может быть исполнителем задач и создавать новые задачи
 * в зависимости от прав доступа.
 *
 * @property login - Уникальный логин пользователя (идентификатор)
 * @property firstName - Имя пользователя (может быть null)
 * @property lastName - Фамилия пользователя (может быть null)
 * @property email - Email адрес пользователя (может быть null)
 * @property team - Команда, к которой принадлежит пользователь (может быть null)
 * @property role - Роль пользователя (определяет права доступа, может быть null)
 * @property canCreateTasks - Флаг, разрешающий создание задач (может быть null)
 */
export interface User {
  login: string;
  firstName: string | null;
  lastName: string | null;
  email: string | null;
  team: Team | null;
  role: Role | null;
  canCreateTasks: boolean | null;
}
