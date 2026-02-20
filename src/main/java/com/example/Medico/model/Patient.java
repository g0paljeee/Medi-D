package com.example.Medico.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import jakarta.validation.constraints.NotBlank;
import java.time.LocalDateTime;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Patient {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Patient name is required")
    private String name;

    private String email;
    private String phone;
    private String address;

    // This stores the medical history.
    // @Lob allows us to store large amounts of text.
    @Column(columnDefinition = "TEXT")
    private String medicalHistory;

    @Column(name = "created_at", nullable = true, updatable = false)
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        if (createdAt == null) {
            createdAt = LocalDateTime.now();
        }
    }

    // Legacy field name support
    public String getHistoryBlob() {
        return medicalHistory;
    }

    public void setHistoryBlob(String historyBlob) {
        this.medicalHistory = historyBlob;
    }
}