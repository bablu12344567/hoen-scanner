package com.skyscanner;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.dropwizard.core.Application;
import io.dropwizard.core.setup.Bootstrap;
import io.dropwizard.core.setup.Environment;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

public class HoenScannerApplication extends Application<HoenScannerConfiguration> {

    public static void main(final String[] args) throws Exception {
        new HoenScannerApplication().run(args);
    }

    @Override
    public String getName() {
        return "hoen-scanner";
    }

    @Override
    public void initialize(final Bootstrap<HoenScannerConfiguration> bootstrap) {

    }

    @Override
    public void run(final HoenScannerConfiguration configuration,
                    final Environment environment) {

        ObjectMapper objectMapper = environment.getObjectMapper();

        List<SearchResult> searchResults = new ArrayList<>();

        searchResults.addAll(
                loadResults(objectMapper, "hotels.json", "hotel")
        );

        searchResults.addAll(
                loadResults(objectMapper, "rental_cars.json", "rental_car")
        );

        environment.jersey().register(
                new SearchResource(searchResults)
        );
    }

    private List<SearchResult> loadResults(
            ObjectMapper objectMapper,
            String fileName,
            String kind) {

        try {
            InputStream inputStream =
                    getClass().getClassLoader().getResourceAsStream(fileName);

            if (inputStream == null) {
                throw new RuntimeException(
                        "Could not find resource: " + fileName
                );
            }

            List<SearchResult> results = objectMapper.readValue(
                    inputStream,
                    new TypeReference<List<SearchResult>>() {}
            );

            for (SearchResult result : results) {
                result.setKind(kind);
            }

            return results;

        } catch (Exception e) {
            throw new RuntimeException(
                    "Unable to load " + fileName,
                    e
            );
        }
    }
}