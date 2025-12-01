package net.alephdev.calendar.models;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
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
import jakarta.persistence.Table;
import lombok.Data;

@Data
@Entity
@Table(name = "Idea")
@SuppressFBWarnings({"EI_EXPOSE_REP", "EI_EXPOSE_REP2"})
public class Idea {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Integer id;

  @Column(nullable = false)
  private String description;

  @ManyToOne
  @JoinColumn(name = "author_login", foreignKey = @ForeignKey(name = "fk_idea_author_login"))
  private User authorLogin;

  @Enumerated(EnumType.STRING)
  @Column(name = "status_enum_id", nullable = false)
  private Status statusEnumId;

  @ManyToOne
  @JoinColumn(name = "task_id", foreignKey = @ForeignKey(name = "fk_idea_task_id"))
  private Task task;

  public enum Status {
    PENDING,
    REJECTED,
    APPROVED;
  }
}
