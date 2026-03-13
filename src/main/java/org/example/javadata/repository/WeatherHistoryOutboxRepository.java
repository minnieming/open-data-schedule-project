package org.example.javadata.repository;

import org.example.javadata.entity.WeatherHistoryOutboxEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface WeatherHistoryOutboxRepository extends JpaRepository<WeatherHistoryOutboxEntity, Long> {

    List<WeatherHistoryOutboxEntity> findByIdInAndStatus(List<Long> ids, String status);
}
