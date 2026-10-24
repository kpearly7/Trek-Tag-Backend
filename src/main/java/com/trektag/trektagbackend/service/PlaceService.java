
package com.trektag.trektagbackend.service;

import com.trektag.trektagbackend.model.Place;
import com.trektag.trektagbackend.repository.PlaceRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class PlaceService {

    private final PlaceRepository placeRepository;

    public PlaceService(PlaceRepository placeRepository) {
        this.placeRepository = placeRepository;
    }

    public List<Place> getAllApprovedPlaces() {
        return placeRepository.findByApprovedTrue();
    }

    public List<Place> searchPlaces(String keyword) {
        return placeRepository
                .findByNameContainingIgnoreCaseAndApprovedTrue(keyword);
    }

    public List<Place> getPlacesByCategory(String category) {
        return placeRepository
                .findByCategoryIgnoreCaseAndApprovedTrue(category);
    }

    public Optional<Place> getPlaceById(Long id) {
        return placeRepository.findById(id)
                .filter(Place::isApproved);
    }

    public Place submitPlace(Place place) {
        place.setApproved(false);
        return placeRepository.save(place);
    }
}
