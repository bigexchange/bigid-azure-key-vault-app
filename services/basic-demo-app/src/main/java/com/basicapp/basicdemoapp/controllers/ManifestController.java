package com.basicapp.basicdemoapp.controllers;

import com.basicapp.basicdemoapp.services.ReadfileService;
import com.bigid.appinfrastructure.controllers.AbstractManifestController;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Controller;

import java.io.IOException;
import java.io.InputStream;

@Controller
public class ManifestController extends AbstractManifestController {
    Logger logger = LoggerFactory.getLogger(ManifestController.class);

    @Override
    public String getManifest() {
        try {
            ClassPathResource resource = new ClassPathResource("Manifest");
            InputStream inputStream = resource.getInputStream();
            return ReadfileService.readFileContentFromInputStream(inputStream);

        } catch (IOException ex){
            logger.error(ex.getMessage());
        }

        return "Unable to receive manifest!";
    }
}
