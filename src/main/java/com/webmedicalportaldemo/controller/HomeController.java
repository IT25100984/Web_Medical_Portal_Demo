package com.webmedicalportaldemo.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HomeController {

    /**
     * Displays the public home page.
     *
     * The GlobalLayoutAdvice class automatically adds
     * "publicReviews" to the model.
     */
    @GetMapping({"/", "/index"})
    public String showWelcomePage() {
        return "index";
    }
}