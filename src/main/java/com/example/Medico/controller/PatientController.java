package com.example.Medico.controller;

import com.example.Medico.model.Patient;
import com.example.Medico.repository.PatientRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/patients")
public class PatientController {

    @Autowired
    private PatientRepository patientRepo;

    // RECEPTIONIST: Register a new patient
    // Endpoint: POST /api/patients/register
    @PostMapping("/register")
    public Patient registerPatient(@RequestBody Patient patient) {
        if (patient.getHistoryBlob() == null) {
            patient.setHistoryBlob("New Patient Registered.");
        }
        return patientRepo.save(patient);
    }
}