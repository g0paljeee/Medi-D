package com.example.Medico.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

@Entity
@Data // Generates Getters, Setters, toString, etc.
@NoArgsConstructor
@AllArgsConstructor
public class Prescription {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne // Many prescriptions can belong to One Patient
    @JoinColumn(name = "patient_id")
    private Patient patient;

    // Stores Medicine ID (Key) and Quantity (Value)
    // This creates a separate table automatically to link meds to prescriptions
    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "prescription_medicines", joinColumns = @JoinColumn(name = "prescription_id"))
    @MapKeyColumn(name = "medicine_id")
    @Column(name = "quantity")
    private Map<Long, Integer> medicineQuantities;

    private String status; // "PENDING", "DISPENSED"
}