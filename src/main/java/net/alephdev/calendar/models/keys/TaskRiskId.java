package net.alephdev.calendar.models.keys;

import java.io.Serializable;
import lombok.Data;

@Data
public class TaskRiskId implements Serializable {
  private Integer task;
  private Integer risk;
}
