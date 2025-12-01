/**
 * Модель тега задачи.
 *
 * @property id - Уникальный идентификатор тега
 * @property name - Название тега
 * @property description - Описание тега
 */
export interface Tag {
  id: number;
  name: string;
  description: string | null;
}
