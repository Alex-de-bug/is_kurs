package net.alephdev.calendar.models.keys;

import java.io.Serializable;
import lombok.Data;

@Data
public class RoleStatusId implements Serializable {
  private Integer role;
  private Integer status;
}
