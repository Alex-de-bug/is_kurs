/**
 * Модель команды разработки.
 *
 * @property id - Уникальный идентификатор команды
 * @property name - Название команды
 * @property color - Цвет команды (для отображения в UI)
 * @property description - Описание команды
 * @property isActive - Флаг активности команды
 */
export interface Team {
  id: number;
  name: string;
  color: string | null;
  description: string | null;
  isActive: boolean;
}
