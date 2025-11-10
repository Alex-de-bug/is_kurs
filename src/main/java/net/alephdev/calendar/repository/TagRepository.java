package net.alephdev.calendar.repository;

import java.util.Optional;
import net.alephdev.calendar.models.Tag;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TagRepository extends JpaRepository<Tag, Integer> {
  Optional<Tag> findByName(String name);
}
