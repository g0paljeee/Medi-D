package com.example.Medico.repository;

import com.example.Medico.model.Appointment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface AppointmentRepository extends JpaRepository<Appointment, Long> {

    // Find all appointments for a specific doctor
    List<Appointment> findByDoctorId(Long doctorId);

    // Find available slots (Module A logic)
    List<Appointment> findByStatus(String status);
}