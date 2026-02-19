package com.example.Medico.repository;

import com.example.Medico.model.Patient;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
// FIXED: Removed "<Patient>" after interface name.
// It should just extend JpaRepository<EntityName, ID_Type>
public interface PatientRepository extends JpaRepository<Patient, Long> {

    // You can add this later if you need to find patients by name
    // java.util.List<Patient> findByName(String name);
}