package net.alephdev.calendar.repository;

import java.util.List;
import net.alephdev.calendar.models.Release;
import net.alephdev.calendar.models.Sprint;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ReleaseRepository extends JpaRepository<Release, Integer> {
  List<Release> findAllBySprint(Sprint sprint, Sort sort);

  Page<Release> findAllBySprint_Id(Integer sprintId, Pageable pageable);
}
