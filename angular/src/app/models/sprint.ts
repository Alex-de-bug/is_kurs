import {Team} from "./team";

/**
 * Модель спринта в Agile методологии.
 *
 * Представляет временной промежуток (итерацию) в процессе разработки,
 * в течение которого команда работает над набором задач.
 *
 * Спринт имеет четко определенные даты начала и окончания,
 * а также период регрессионного тестирования.
 *
 * @property id - Уникальный идентификатор спринта
 * @property majorVersion - Версия спринта (например, "1.0.0", "2.1.5")
 * @property startDate - Дата начала спринта (ISO формат)
 * @property endDate - Дата окончания спринта (ISO формат)
 * @property regressionStart - Дата начала регрессионного тестирования (ISO формат)
 * @property regressionEnd - Дата окончания регрессионного тестирования (ISO формат)
 * @property team - Команда, работающая над спринтом
 */
export interface Sprint {
  id: number;
  majorVersion: string;
  startDate: string; // Date in ISO format
  endDate: string; // Date in ISO format
  regressionStart: string; // Date in ISO format
  regressionEnd: string; // Date in ISO format
  team: Team;
}
