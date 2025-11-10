package net.alephdev.calendar.repository;

import java.util.List;
import net.alephdev.calendar.models.Risk;
import net.alephdev.calendar.models.Task;
import net.alephdev.calendar.models.TaskRisk;
import net.alephdev.calendar.models.keys.TaskRiskId;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TaskRiskRepository extends JpaRepository<TaskRisk, TaskRiskId> {
  List<TaskRisk> findAllByTask(Task task);

  void deleteByTaskAndRisk(Task task, Risk risk);
}
