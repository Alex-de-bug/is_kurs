package net.alephdev.calendar.models.keys;

import java.io.Serializable;
import lombok.Data;

@Data
public class IdeaRiskId implements Serializable {
  private Integer idea;
  private Integer risk;
}
