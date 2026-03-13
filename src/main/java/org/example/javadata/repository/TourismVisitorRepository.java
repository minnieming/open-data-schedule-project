package org.example.javadata.repository;

import org.example.javadata.entity.TourismVisitorEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TourismVisitorRepository extends JpaRepository<TourismVisitorEntity, Long> {
}
