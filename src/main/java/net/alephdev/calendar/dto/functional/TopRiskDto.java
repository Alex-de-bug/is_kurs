package net.alephdev.calendar.dto.functional;

import io.swagger.v3.oas.annotations.media.Schema;
import java.math.BigDecimal;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
@Schema(description = "Агрегация потерь по риску для топ-10 отчета")
public class TopRiskDto {
  @Schema(description = "ID риска", example = "7")
  private final Integer riskId;

  @Schema(
      description = "Описание риска",
      example = "Падение производительности при высокой нагрузке")
  private final String description;

  @Schema(description = "Суммарные ожидаемые потери", example = "15234.50")
  private final BigDecimal totalEstimatedLoss;

  public TopRiskDto(TopRiskProjection projection) {
    this(projection.getRiskId(), projection.getDescription(), projection.getTotalEstimatedLoss());
  }
}
