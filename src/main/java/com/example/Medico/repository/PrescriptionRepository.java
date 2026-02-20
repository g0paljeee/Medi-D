package com.example.Medico.repository; // <--- Corrected Package

import com.example.Medico.model.Patient;
import com.example.Medico.model.Prescription;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PrescriptionRepository extends JpaRepository<Prescription, Long> {
    List<Prescription> findByStatus(String status);
    List<Prescription> findByPatientId(Long patientId);
    List<Prescription> findByPatient(Patient patient);
}