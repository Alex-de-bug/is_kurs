package net.alephdev.calendar.repository;

import java.util.Optional;
import net.alephdev.calendar.models.Status;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface StatusRepository extends JpaRepository<Status, Integer> {
  Optional<Status> findByName(String name);
}
