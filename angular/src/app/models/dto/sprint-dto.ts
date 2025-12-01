/**
 * DTO для создания или обновления спринта.
 *
 * @property majorVersion - Версия спринта (например, 1.0.0)
 * @property startDate - Дата начала спринта (ISO формат)
 * @property endDate - Дата окончания спринта (ISO формат)
 * @property regressionStart - Дата начала регрессионного тестирования (ISO формат)
 * @property regressionEnd - Дата окончания регрессионного тестирования (ISO формат)
 * @property teamId - Идентификатор команды, которая ведёт спринт
 */
export interface SprintDto {
    majorVersion: string;
    startDate: string;
    endDate: string;
    regressionStart: string;
    regressionEnd: string;
    teamId: number;
}
