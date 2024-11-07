package com.bigid.azurekeyvaultapp.controllers;

import com.bigid.appinfrastructure.controllers.AbstractLoggingController;
import org.springframework.stereotype.Controller;

@Controller
public class LogsController implements AbstractLoggingController {

    @Override
    public String getLogs() {
        return "1. log1\n" +
                "2. log2 \n" +
                "3. log3";
    }
}
