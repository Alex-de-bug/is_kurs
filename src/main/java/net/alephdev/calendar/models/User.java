package net.alephdev.calendar.models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.ForeignKey;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Entity
@Table(name = "Users")
@NoArgsConstructor
@AllArgsConstructor
@Builder
@SuppressFBWarnings({"EI_EXPOSE_REP", "EI_EXPOSE_REP2"})
public class User {
  @Id private String login;

  @Column(nullable = false)
  @JsonIgnore
  private String password;

  private String firstName;

  private String lastName;

  private String email;

  @Transient private boolean canCreateTasks;

  @ManyToOne
  @JoinColumn(name = "team_id", foreignKey = @ForeignKey(name = "fk_users_team_id"))
  private Team team;

  @ManyToOne
  @JoinColumn(name = "role_id", foreignKey = @ForeignKey(name = "fk_users_role_id"))
  private Role role;
}
