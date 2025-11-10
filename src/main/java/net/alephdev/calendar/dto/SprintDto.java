package net.alephdev.calendar.dto;

import java.time.LocalDate;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class SprintDto {
  private final String majorVersion;

  private final LocalDate startDate;

  private final LocalDate endDate;

  private final LocalDate regressionStart;

  private final LocalDate regressionEnd;

  private final Integer teamId;
}
