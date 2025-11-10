package net.alephdev.calendar.dto;

import java.time.LocalDate;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class ReleaseDto {
  private String version;

  private LocalDate releaseDate;

  private String description;

  private Integer sprintId;
}
