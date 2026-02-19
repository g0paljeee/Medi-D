package com.example.Medico.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Appointment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Requirement: "doctor_id"
    // Since we don't have a specific 'Doctor' table (just roles), we store the ID directly.
    private Long doctorId;

    // Requirement: "patient_id"
    // We link this to the existing Patient table
    @ManyToOne
    @JoinColumn(name = "patient_id")
    private Patient patient;

    // Essential for "Module A": Tracking when the slot is
    private LocalDateTime appointmentTime;

    // "AVAILABLE", "BOOKED", "CANCELLED"
    private String status;
}