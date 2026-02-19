package com.example.Medico.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Patient {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    // This stores the medical history.
    // @Lob allows us to store large amounts of text.
    @Column(columnDefinition = "TEXT")
    private String historyBlob;
}