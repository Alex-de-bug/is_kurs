/**
 * DTO для представления спринта вместе с информацией о команде.
 *
 * Используется в календаре спринтов.
 *
 * @property sprintId - Идентификатор спринта
 * @property majorVersion - Версия спринта
 * @property startDate - Дата начала спринта (ISO формат)
 * @property endDate - Дата окончания спринта (ISO формат)
 * @property teamName - Название команды
 * @property teamColor - Цвет команды
 */
export interface SprintTeamDto {
  sprintId: number;
  majorVersion: string;
  startDate: string;
  endDate: string;
  teamName: string;
  teamColor: string;
}
