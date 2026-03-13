package org.example.javadata.repository;

import org.example.javadata.entity.HolidayEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface HolidayRepository extends JpaRepository<HolidayEntity, Long> {
    void deleteByLocdate(String locdate);
}
