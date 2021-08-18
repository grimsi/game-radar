package de.grimsi.gameradar.backend.controller;

import de.grimsi.gameradar.backend.api.InfoApi;
import de.grimsi.gameradar.backend.service.ApplicationManagementService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class InfoApiController implements InfoApi {

    @Autowired
    private ApplicationManagementService applicationManagementService;

    @Override
    public ResponseEntity<Boolean> isSetup() {
        return ResponseEntity.ok(applicationManagementService.isApplicationSetupComplete());
    }


    @Override
    public ResponseEntity<String> version() {
        return ResponseEntity.ok(applicationManagementService.getApplicationVersion());
    }
}
