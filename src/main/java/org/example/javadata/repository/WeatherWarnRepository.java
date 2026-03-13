package org.example.javadata.repository;

import org.example.javadata.entity.WeatherWarnEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface WeatherWarnRepository extends JpaRepository<WeatherWarnEntity, Long> {
}
