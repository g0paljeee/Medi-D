package com.example.Medico.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
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
    
    @Column(name = "created_at", nullable = true, updatable = false)
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        if (createdAt == null) {
            createdAt = LocalDateTime.now();
        }
    }
    
    // Getters and setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Patient getPatient() { return patient; }
    public void setPatient(Patient patient) { this.patient = patient; }
    public Map<Long, Integer> getMedicineQuantities() { return medicineQuantities; }
    public void setMedicineQuantities(Map<Long, Integer> medicineQuantities) { this.medicineQuantities = medicineQuantities; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}