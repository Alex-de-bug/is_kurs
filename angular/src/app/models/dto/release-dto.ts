/**
 * DTO для создания или обновления релиза.
 *
 * @property version - Версия релиза (например, 1.0.0)
 * @property releaseDate - Дата релиза в ISO формате
 * @property description - Описание релиза
 * @property sprintId - Идентификатор спринта, к которому относится релиз
 */
export interface ReleaseDto {
  version: string;
  releaseDate: string;
  description: string;
  sprintId: number;
}
