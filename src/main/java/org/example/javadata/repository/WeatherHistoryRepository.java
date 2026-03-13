package org.example.javadata.repository;

import org.example.javadata.entity.WeatherHistoryEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface WeatherHistoryRepository extends JpaRepository<WeatherHistoryEntity, Long> {

    @Query("SELECT MIN(e.id) FROM WeatherHistoryEntity e")
    Long findMinId();

    @Query("SELECT MAX(e.id) FROM WeatherHistoryEntity e")
    Long findMaxId();

    @Query("SELECT e.id FROM WeatherHistoryEntity e ORDER BY e.id")
    List<Long> findAllIds();

    @Modifying
    @Query("DELETE FROM WeatherHistoryEntity e WHERE e.id BETWEEN :startId AND :endId")
    void deleteByIdBetween(@Param("startId") long startId, @Param("endId") long endId);
}
