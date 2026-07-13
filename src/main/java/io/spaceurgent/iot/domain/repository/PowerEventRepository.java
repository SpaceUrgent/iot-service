package io.spaceurgent.iot.domain.repository;

import io.spaceurgent.iot.domain.model.PowerEvent;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface PowerEventRepository extends JpaRepository<PowerEvent, Long> {

    @Query(
        """
        from PowerEvent event
        where event.id < :id
        order by event.timestamp desc
        limit 1
        """
    )
    Optional<PowerEvent> findPreviousById(Long id);
}
