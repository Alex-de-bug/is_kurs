package net.alephdev.calendar.models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.ForeignKey;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import java.util.List;
import lombok.Data;

@Data
@Entity
@Table(name = "Task")
public class Task {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Integer id;

  @Column(nullable = false)
  private String name;

  private Integer storyPoints;

  @ManyToOne
  @JoinColumn(name = "implementer", foreignKey = @ForeignKey(name = "fk_task_implementer"))
  private User implementer;

  @ManyToOne
  @JoinColumn(name = "sprint_id", foreignKey = @ForeignKey(name = "fk_task_sprint_id"))
  private Sprint sprint;

  @ManyToOne
  @JoinColumn(
      name = "status_id",
      foreignKey = @ForeignKey(name = "fk_task_status_id"),
      nullable = false)
  private Status status;

  @Enumerated(EnumType.STRING)
  @Column(nullable = false)
  private Priority priorityEnum;

  @ManyToOne
  @JoinColumn(name = "created_by", foreignKey = @ForeignKey(name = "fk_task_created_by"))
  private User createdBy;

  @OneToMany(mappedBy = "task")
  @JsonIgnore
  private List<TaskTag> taskTags;

  public enum Priority {
    LOW,
    MEDIUM,
    CRITICAL
  }
}
