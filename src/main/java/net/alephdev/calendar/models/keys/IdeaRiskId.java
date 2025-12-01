package net.alephdev.calendar.models.keys;

import java.io.Serializable;
import lombok.Data;

@Data
public class IdeaRiskId implements Serializable {
  private static final long serialVersionUID = 1L;
  private Integer idea;
  private Integer risk;
}
