/**
 * DTO для представления агрегированной информации по риску в топ‑10.
 *
 * @property riskId - Идентификатор риска
 * @property description - Описание риска
 * @property totalEstimatedLoss - Суммарные потенциальные потери
 */
export interface TopRiskDto {
  riskId: number;
  description: string;
  totalEstimatedLoss: number;
}
