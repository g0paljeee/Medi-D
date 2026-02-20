package com.example.Medico.controller;

import com.example.Medico.model.Appointment;
import com.example.Medico.model.Doctor;
import com.example.Medico.model.Patient;
import com.example.Medico.repository.AppointmentRepository;
import com.example.Medico.repository.DoctorRepository;
import com.example.Medico.repository.PatientRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/appointments") 
public class AppointmentController {

    @Autowired
    private AppointmentRepository appointmentRepo;

    @Autowired
    private PatientRepository patientRepo;

    @Autowired
    private DoctorRepository doctorRepo;

    // 1. View All Appointments 
    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'RECEPTIONIST', 'DOCTOR')")
    public ResponseEntity<?> getAllAppointments() {
        try {
            List<Appointment> appointments = appointmentRepo.findAll();
            return ResponseEntity.ok(appointments);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Failed to fetch appointments: " + e.getMessage()));
        }
    }

    // 2. Get Appointments by Patient 
    @GetMapping("/patient/{patientId}")
    @PreAuthorize("hasAnyRole('DOCTOR', 'RECEPTIONIST')")
    public ResponseEntity<?> getAppointmentsByPatient(@PathVariable Long patientId) {
        try {
            Optional<Patient> patient = patientRepo.findById(patientId);
            if (patient.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(Map.of("error", "Patient not found"));
            }
            
            List<Appointment> appointments = appointmentRepo.findAll().stream()
                    .filter(a -> a.getPatient() != null && a.getPatient().getId().equals(patientId))
                    .toList();
            
            return ResponseEntity.ok(appointments);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Failed to fetch appointments"));
        }
    }

    // 3. Get Appointments by Doctor
    @GetMapping("/doctor/{doctorId}")
    @PreAuthorize("hasAnyRole('DOCTOR', 'RECEPTIONIST')")
    public ResponseEntity<?> getAppointmentsByDoctor(@PathVariable Long doctorId) {
        try {
            Optional<Doctor> doctor = doctorRepo.findById(doctorId);
            if (doctor.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(Map.of("error", "Doctor not found"));
            }
            
            List<Appointment> appointments = appointmentRepo.findAll().stream()
                    .filter(a -> a.getDoctorId().equals(doctorId))
                    .toList();
            
            return ResponseEntity.ok(appointments);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Failed to fetch appointments"));
        }
    }

    // 4. Book Appointment (Receptionist Action)
    @PostMapping("/book")
    @PreAuthorize("hasAnyRole('RECEPTIONIST', 'ADMIN')")
    public ResponseEntity<?> bookAppointment(@RequestBody Map<String, Object> request) {
        try {
            // Validate input
            if (!request.containsKey("patientId") || !request.containsKey("doctorId") || !request.containsKey("appointmentTime")) {
                return ResponseEntity.badRequest()
                        .body(Map.of("error", "Missing required fields: patientId, doctorId, appointmentTime"));
            }
            
            Long patientId = Long.parseLong(request.get("patientId").toString());
            Long doctorId = Long.parseLong(request.get("doctorId").toString());
            LocalDateTime appointmentTime = LocalDateTime.parse(request.get("appointmentTime").toString());
            
            // Validate patient exists
            Optional<Patient> patient = patientRepo.findById(patientId);
            if (patient.isEmpty()) {
                return ResponseEntity.badRequest()
                        .body(Map.of("error", "Patient not found with ID: " + patientId));
            }
            
            // Validate doctor exists
            Optional<Doctor> doctor = doctorRepo.findById(doctorId);
            if (doctor.isEmpty()) {
                return ResponseEntity.badRequest()
                        .body(Map.of("error", "Doctor not found with ID: " + doctorId));
            }
            
            // Check for conflicts
            boolean conflict = appointmentRepo.findAll().stream()
                    .anyMatch(a -> a.getDoctorId().equals(doctorId) && 
                               a.getAppointmentTime().equals(appointmentTime) &&
                               a.getStatus().equals("BOOKED"));
            
            if (conflict) {
                return ResponseEntity.status(HttpStatus.CONFLICT)
                        .body(Map.of("error", "Time slot already booked for this doctor"));
            }
            
            // Create appointment
            Appointment appointment = new Appointment();
            appointment.setDoctorId(doctorId);
            appointment.setPatient(patient.get());
            appointment.setAppointmentTime(appointmentTime);
            appointment.setStatus("BOOKED");
            
            Appointment saved = appointmentRepo.save(appointment);
            
            return ResponseEntity.status(HttpStatus.CREATED).body(Map.of(
                    "message", "Appointment booked successfully",
                    "appointmentId", saved.getId(),
                    "patientName", patient.get().getName(),
                    "doctorName", doctor.get().getName(),
                    "appointmentTime", appointmentTime,
                    "status", "BOOKED"
            ));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Failed to book appointment: " + e.getMessage()));
        }
    }

    // 5. Cancel Appointment
    @PutMapping("/{appointmentId}/cancel")
    @PreAuthorize("hasAnyRole('RECEPTIONIST', 'DOCTOR', 'ADMIN')")
    public ResponseEntity<?> cancelAppointment(@PathVariable Long appointmentId) {
        try {
            Optional<Appointment> appointment = appointmentRepo.findById(appointmentId);
            if (appointment.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(Map.of("error", "Appointment not found"));
            }
            
            Appointment appt = appointment.get();
            appt.setStatus("CANCELLED");
            appointmentRepo.save(appt);
            
            return ResponseEntity.ok(Map.of(
                    "message", "Appointment cancelled successfully",
                    "appointmentId", appointmentId,
                    "status", "CANCELLED"
            ));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Failed to cancel appointment"));
        }
    }

    // 6. Block Time Slot (Doctor Action)
    @PostMapping("/block")
    @PreAuthorize("hasRole('DOCTOR')")
    public ResponseEntity<?> blockSlot(@RequestBody Map<String, Object> request) {
        try {
            if (!request.containsKey("doctorId") || !request.containsKey("appointmentTime")) {
                return ResponseEntity.badRequest()
                        .body(Map.of("error", "Missing required fields: doctorId, appointmentTime"));
            }
            
            Long doctorId = Long.parseLong(request.get("doctorId").toString());
            LocalDateTime appointmentTime = LocalDateTime.parse(request.get("appointmentTime").toString());
            
            // Validate doctor exists
            Optional<Doctor> doctor = doctorRepo.findById(doctorId);
            if (doctor.isEmpty()) {
                return ResponseEntity.badRequest()
                        .body(Map.of("error", "Doctor not found"));
            }
            
            // Create blocked slot
            Appointment blocked = new Appointment();
            blocked.setDoctorId(doctorId);
            blocked.setAppointmentTime(appointmentTime);
            blocked.setStatus("BLOCK");
            
            Appointment saved = appointmentRepo.save(blocked);
            
            return ResponseEntity.status(HttpStatus.CREATED).body(Map.of(
                    "message", "Time slot blocked successfully",
                    "appointmentId", saved.getId(),
                    "doctorName", doctor.get().getName(),
                    "blockedTime", appointmentTime,
                    "status", "BLOCK"
            ));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Failed to block slot: " + e.getMessage()));
        }
    }

    // 7. Get Available Doctors for Booking
    @GetMapping("/available-doctors")
    @PreAuthorize("hasRole('RECEPTIONIST')")
    public ResponseEntity<?> getAvailableDoctors() {
        try {
            List<Doctor> doctors = doctorRepo.findAll();
            return ResponseEntity.ok(Map.of(
                    "doctors", doctors,
                    "count", doctors.size()
            ));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Failed to fetch doctors"));
        }
    }
}
