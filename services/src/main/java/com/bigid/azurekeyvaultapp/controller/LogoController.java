package com.bigid.azurekeyvaultapp.controller;

import com.bigid.appinfrastructure.controllers.AbstractLogoController;
import org.springframework.stereotype.Controller;

@Controller
public class LogoController extends AbstractLogoController {
    public String getSideBarIconPath() {
        return "azure-key-vault.png";
    }

    public String getIconPath() {
        return "azure-key-vault.png";

    }
}
