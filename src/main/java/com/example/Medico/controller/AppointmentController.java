package com.example.Medico.controller;

import com.example.Medico.model.Appointment;
import com.example.Medico.model.Patient;
import com.example.Medico.repository.AppointmentRepository;
import com.example.Medico.repository.PatientRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/appointments") // <--- CHANGED: Shared Endpoint
public class AppointmentController {

    @Autowired
    private AppointmentRepository appointmentRepo;

    @Autowired
    private PatientRepository patientRepo; // <--- ADDED: To validate patients

    // 1. View All Appointments (Dashboard for Admin/Receptionist)
    @GetMapping
    public List<Appointment> getAllAppointments() {
        return appointmentRepo.findAll();
    }

    // 2. Book a Slot (Receptionist Action)
    // Constraint: Must link to a valid Patient
    @PostMapping("/book")
    public ResponseEntity<String> bookAppointment(@RequestBody Appointment appointment) {
        // Validation: Does the patient exist?
        // We check if the patient object is null OR if the ID provided doesn't exist
        if (appointment.getPatient() == null || appointment.getPatient().getId() == null) {
            return ResponseEntity.badRequest().body("Error: Patient ID is required.");
        }

        if (!patientRepo.existsById(appointment.getPatient().getId())) {
            return ResponseEntity.badRequest().body("Error: Patient ID not found in database.");
        }

        // Logic: Force status to "BOOKED"
        appointment.setStatus("BOOKED");
        appointmentRepo.save(appointment);

        return ResponseEntity.ok("Appointment Booked Successfully for Patient ID: " + appointment.getPatient().getId());
    }

    // 3. Mark Unavailable (Doctor Action)
    // Requirement: "Doctors can mark slots as Unavailable"
    @PostMapping("/block")
    public ResponseEntity<String> blockSlot(@RequestBody Appointment appointment) {
        // Logic: Force status to "UNAVAILABLE"
        // We don't need a patient for a blocked slot
        appointment.setStatus("UNAVAILABLE");

        appointmentRepo.save(appointment);
        return ResponseEntity.ok("Slot marked as Unavailable for Doctor ID: " + appointment.getDoctorId());
    }
}