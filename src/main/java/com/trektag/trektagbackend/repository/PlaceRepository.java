
package com.trektag.trektagbackend.repository;

import com.trektag.trektagbackend.model.Place;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PlaceRepository extends JpaRepository<Place, Long> {

    List<Place> findByApprovedTrue();

    List<Place> findByCategoryIgnoreCaseAndApprovedTrue(String category);

    List<Place> findByNameContainingIgnoreCaseAndApprovedTrue(String name);
}
