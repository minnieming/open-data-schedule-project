package org.example.javadata.repository;

import org.example.javadata.entity.WeatherHistoryEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface WeatherHistoryRepository extends JpaRepository<WeatherHistoryEntity, Long> {
}
