package com.example.Medico.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HomeController {

    @GetMapping("/")
    public String home() {
        return "<h1>🏥 MediCo Backend is Running!</h1>" +
                "<p>Use the following endpoints:</p>" +
                "<ul>" +
                "<li><a href='/api/pharmacy/queue'>/api/pharmacy/queue</a> (Pharmacist View)</li>" +
                "<li><a href='/api/doctor/medicines'>/api/doctor/medicines</a> (Medicine Stock)</li>" +
                "</ul>";
    }
}