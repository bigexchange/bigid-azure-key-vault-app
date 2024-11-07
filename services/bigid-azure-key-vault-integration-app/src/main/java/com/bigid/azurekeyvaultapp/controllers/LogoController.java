package com.bigid.azurekeyvaultapp.controllers;

import com.bigid.appinfrastructure.controllers.AbstractLogoController;
import org.springframework.stereotype.Controller;

@Controller
public class LogoController extends AbstractLogoController {
    public String getSideBarIconPath(){
        return "side-bar-icon.png";
    }
    public String getIconPath(){
        return "icon.png";

    }
}
