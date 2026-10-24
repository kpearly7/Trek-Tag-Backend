
package com.trektag.trektagbackend.controller;

import com.trektag.trektagbackend.model.Place;
import com.trektag.trektagbackend.service.PlaceService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/places")
@CrossOrigin(origins = {
    "http://localhost:5173",
    "http://localhost:5174"
})
public class PlaceController {

    private final PlaceService placeService;

    public PlaceController(PlaceService placeService) {
        this.placeService = placeService;
    }

    @GetMapping
    public List<Place> getPlaces(
            @RequestParam(required = false) String search,
            @RequestParam(required = false) String category) {

        if (search != null && !search.isBlank()) {
            return placeService.searchPlaces(search);
        }

        if (category != null && !category.isBlank()) {
            return placeService.getPlacesByCategory(category);
        }

        return placeService.getAllApprovedPlaces();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Place> getPlaceById(@PathVariable Long id) {
        return placeService.getPlaceById(id)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<Place> submitPlace(@RequestBody Place place) {
        Place savedPlace = placeService.submitPlace(place);
        return ResponseEntity.status(HttpStatus.CREATED).body(savedPlace);
    }
}
