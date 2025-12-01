/**
 * DTO для создания или обновления риска.
 *
 * @property description - Описание риска
 * @property probability - Вероятность наступления (0..1)
 * @property estimatedLoss - Оценка потерь при наступлении риска
 */
export interface RiskDto {
    description: string;
    probability: number;
    estimatedLoss: number;
}
