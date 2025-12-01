/**
 * Модель статуса задачи.
 *
 * @property id - Уникальный идентификатор статуса
 * @property name - Название статуса
 * @property description - Описание статуса
 */
export interface Status {
  id: number;
  name: string;
  description: string | null;
}
