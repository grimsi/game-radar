package de.grimsi.gameradar.backend.controller;

import org.springframework.boot.web.servlet.error.ErrorController;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseStatus;

@Controller
public class FrontendController implements ErrorController {

    // Forward all GET requests that can not be mapped to a path in the backend to Angular
    @GetMapping(value = "/error")
    @ResponseStatus(HttpStatus.OK)
    public String error() {
        return "forward:/index.html";
    }
}
