/**
 * Модель риска проекта.
 *
 * Риск описывается вероятностью и оценкой потенциальных потерь.
 *
 * @property id - Уникальный идентификатор риска
 * @property description - Описание риска
 * @property probability - Вероятность наступления (0..1)
 * @property estimatedLoss - Оценка потерь при наступлении риска
 */
export interface Risk {
  id: number;
  description: string;
  probability: number;
  estimatedLoss: number;
}
