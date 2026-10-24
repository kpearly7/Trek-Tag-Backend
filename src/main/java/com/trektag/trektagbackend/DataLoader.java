
package com.trektag.trektagbackend;

import com.trektag.trektagbackend.model.Place;
import com.trektag.trektagbackend.repository.PlaceRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class DataLoader {

    @Bean
    CommandLineRunner loadPlaces(PlaceRepository repository) {
        return args -> {
            if (repository.count() == 0) {
                repository.save(new Place(
                    "Hidden Waterfall Trail",
                    "A peaceful nature escape surrounded by greenery.",
                    "Telangana, India",
                    "Nature",
                    "https://images.unsplash.com/photo-1432405972618-c60b0225b8f9",
                    true,
                    true
                ));

                repository.save(new Place(
                    "Old City Food Corner",
                    "Discover local flavours and traditional street food.",
                    "Hyderabad, Telangana",
                    "Food",
                    "https://images.unsplash.com/photo-1555939594-58d7cb561ad1",
                    false,
                    true
                ));

                repository.save(new Place(
                    "Village Artisan Studio",
                    "Explore handmade crafts and traditional artistry.",
                    "Telangana, India",
                    "Artisans",
                    "https://images.unsplash.com/photo-1452860606245-08befc0ff44b",
                    true,
                    true
                ));

                System.out.println("Trek & Tag sample destinations loaded!");
            }
        };
    }
}
