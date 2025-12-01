package net.alephdev.calendar.models;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import jakarta.persistence.Entity;
import jakarta.persistence.ForeignKey;
import jakarta.persistence.Id;
import jakarta.persistence.IdClass;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.Data;
import net.alephdev.calendar.models.keys.TaskTagId;

@Data
@Entity
@IdClass(TaskTagId.class)
@SuppressFBWarnings({"EI_EXPOSE_REP", "EI_EXPOSE_REP2"})
public class TaskTag {
  @Id
  @ManyToOne
  @JoinColumn(name = "task_id", foreignKey = @ForeignKey(name = "fk_task_tag_task_id"))
  private Task task;

  @Id
  @ManyToOne
  @JoinColumn(name = "tag_id", foreignKey = @ForeignKey(name = "fk_task_tag_tag_id"))
  private Tag tag;
}
