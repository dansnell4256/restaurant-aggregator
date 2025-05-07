package com.catalogue.controller.admin;

import com.catalogue.dto.ApiResponse;
import com.catalogue.util.SampleDataLoader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Controller to reset sample data for development and testing environments.
 * This controller is only available in dev and test profiles.
 */
@RestController
@RequestMapping("/api/v1/admin")
@Profile({"dev", "test"})
public class DataResetController {

    private static final Logger LOGGER = LoggerFactory.getLogger(DataResetController.class);

    @Autowired
    private SampleDataLoader sampleDataLoader;

    /**
     * Reset sample data by clearing existing data and reloading from the JSON file.
     *
     * @return Response indicating success or failure
     */
    @PostMapping("/reset-test-data")
    public ResponseEntity<ApiResponse<Void>> resetTestData() {
        LOGGER.info("Received request to reset sample data");

        ResponseEntity<ApiResponse<Void>> response;

        try {
            sampleDataLoader.run(new String[0]);
            response = ResponseEntity.ok(ApiResponse.success("Sample data reset successfully", null));
        } catch (Exception e) {
            LOGGER.error("Error resetting sample data", e);
            response = ResponseEntity.internalServerError()
                    .body(ApiResponse.error(500, "Error resetting sample data: " + e.getMessage()));
        }

        return response;
    }
}
