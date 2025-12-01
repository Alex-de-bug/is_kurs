import {Sprint} from "./sprint";

/**
 * Модель релиза продукта.
 *
 * Связан со спринтом и содержит версию и дату релиза.
 *
 * @property id - Уникальный идентификатор релиза
 * @property version - Версия релиза (например, 1.0.0)
 * @property releaseDate - Дата релиза в ISO формате
 * @property description - Описание релиза
 * @property sprint - Спринт, к которому относится релиз
 */
export interface Release {
  id: number;
  version: string;
  releaseDate: string; // Date in ISO format
  description: string | null;
  sprint: Sprint | null;
}
