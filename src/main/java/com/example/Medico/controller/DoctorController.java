package com.example.Medico.controller;

import com.example.Medico.model.Doctor;
import com.example.Medico.repository.DoctorRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/doctors")
public class DoctorController {

    @Autowired
    private DoctorRepository doctorRepository;

    // 1. List all doctors (public - for appointment booking)
    @GetMapping
    public ResponseEntity<?> getAllDoctors() {
        try {
            List<Doctor> doctors = doctorRepository.findAll();
            return ResponseEntity.ok(Map.of(
                    "doctors", doctors,
                    "count", doctors.size()
            ));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Failed to fetch doctors"));
        }
    }

    // 2. Get doctor by ID
    @GetMapping("/{id}")
    public ResponseEntity<?> getDoctorById(@PathVariable Long id) {
        try {
            Optional<Doctor> doctor = doctorRepository.findById(id);
            if (doctor.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(Map.of("error", "Doctor not found"));
            }
            return ResponseEntity.ok(doctor.get());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Failed to fetch doctor"));
        }
    }

    // 3. Create new doctor (ADMIN only)
    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> createDoctor(@RequestBody Map<String, Object> request) {
        try {
            // Validate input
            if (!request.containsKey("name") || !request.containsKey("specialization")) {
                return ResponseEntity.badRequest()
                        .body(Map.of("error", "Missing required fields: name, specialization"));
            }

            String name = request.get("name").toString();
            String specialization = request.get("specialization").toString();

            // Validate name is not empty
            if (name.trim().isEmpty()) {
                return ResponseEntity.badRequest()
                        .body(Map.of("error", "Doctor name cannot be empty"));
            }

            // Create doctor
            Doctor doctor = new Doctor();
            doctor.setName(name);
            doctor.setSpecialization(specialization);

            Doctor saved = doctorRepository.save(doctor);

            return ResponseEntity.status(HttpStatus.CREATED).body(Map.of(
                    "message", "Doctor created successfully",
                    "doctorId", saved.getId(),
                    "name", saved.getName(),
                    "specialization", saved.getSpecialization()
            ));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Failed to create doctor: " + e.getMessage()));
        }
    }

    // 4. Update doctor (ADMIN only)
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> updateDoctor(@PathVariable Long id, @RequestBody Map<String, Object> request) {
        try {
            Optional<Doctor> doctorOpt = doctorRepository.findById(id);
            if (doctorOpt.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(Map.of("error", "Doctor not found"));
            }

            Doctor doctor = doctorOpt.get();

            // Update name if provided
            if (request.containsKey("name")) {
                doctor.setName(request.get("name").toString());
            }

            // Update specialization if provided
            if (request.containsKey("specialization")) {
                doctor.setSpecialization(request.get("specialization").toString());
            }

            Doctor updated = doctorRepository.save(doctor);

            return ResponseEntity.ok(Map.of(
                    "message", "Doctor updated successfully",
                    "doctorId", updated.getId(),
                    "name", updated.getName(),
                    "specialization", updated.getSpecialization()
            ));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Failed to update doctor"));
        }
    }

    // 5. Delete doctor (ADMIN only)
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> deleteDoctor(@PathVariable Long id) {
        try {
            Optional<Doctor> doctor = doctorRepository.findById(id);
            if (doctor.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(Map.of("error", "Doctor not found"));
            }

            doctorRepository.deleteById(id);

            return ResponseEntity.ok(Map.of(
                    "message", "Doctor deleted successfully",
                    "doctorId", id
            ));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Failed to delete doctor"));
        }
    }
}
