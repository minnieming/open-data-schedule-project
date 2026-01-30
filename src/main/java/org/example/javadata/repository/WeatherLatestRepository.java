package org.example.javadata.repository;

import org.example.javadata.entity.WeatherLatestEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface WeatherLatestRepository extends JpaRepository<WeatherLatestEntity, Long> {
    void deleteByRegionCode(String regionCode);
}
