package net.alephdev.calendar.dto.functional;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
@Schema(description = "Суммарные story points пользователя по спринту")
public class UserStoryPointsDto {
  @Schema(description = "Логин пользователя", example = "john.doe")
  private final String userLogin;

  @Schema(description = "Суммарные story points", example = "21")
  private final Long totalStoryPoints;

  public UserStoryPointsDto(UserStoryPointsProjection projection) {
    this(projection.getUserLogin(), projection.getTotalStoryPoints());
  }
}
