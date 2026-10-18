package io.spaceurgent.iot.domain.repository;

import io.quarkus.hibernate.orm.panache.PanacheRepository;
import io.spaceurgent.iot.domain.model.PowerEvent;
import jakarta.enterprise.context.ApplicationScoped;

import java.util.Optional;

@ApplicationScoped
public class PowerEventRepository implements PanacheRepository<PowerEvent> {

    public Optional<PowerEvent> findPreviousById(Long id) {
        return find("id < ?1 order by timestamp desc", id).firstResultOptional();
    }
}
