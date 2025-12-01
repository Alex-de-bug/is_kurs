/**
 * Модель роли пользователя.
 *
 * Определяет набор прав и зону ответственности пользователя.
 *
 * @property id - Уникальный идентификатор роли
 * @property name - Название роли
 * @property responsibilities - Описание обязанностей и зоны ответственности
 */
export interface Role {
  id: number;
  name: string;
  responsibilities: string | null;
}
