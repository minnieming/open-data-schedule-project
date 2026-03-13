package org.example.javadata.repository;

import org.example.javadata.entity.AirQualityEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AirQualityRepository extends JpaRepository<AirQualityEntity, Long> {
}
