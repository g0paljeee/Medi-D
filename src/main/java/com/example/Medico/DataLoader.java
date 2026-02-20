package com.example.Medico;

import com.example.Medico.model.*;
import com.example.Medico.repository.*;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Map;

@Component
public class DataLoader implements CommandLineRunner {

    private final MedicineRepository medicineRepo;
    private final PatientRepository patientRepo;
    private final PrescriptionRepository prescriptionRepo;
    private final AppointmentRepository appointmentRepo;
    private final DoctorRepository doctorRepo;
    private final UserRepository userRepo;
    private final PasswordEncoder passwordEncoder;

    public DataLoader(MedicineRepository medicineRepo,
                      PatientRepository patientRepo,
                      PrescriptionRepository prescriptionRepo,
                      AppointmentRepository appointmentRepo,
                      DoctorRepository doctorRepo,
                      UserRepository userRepo,
                      PasswordEncoder passwordEncoder) {
        this.medicineRepo = medicineRepo;
        this.patientRepo = patientRepo;
        this.prescriptionRepo = prescriptionRepo;
        this.appointmentRepo = appointmentRepo;
        this.doctorRepo = doctorRepo;
        this.userRepo = userRepo;
        this.passwordEncoder = passwordEncoder;
    }

    private void createUserIfMissing(String username, String email, String rawPassword, String role, String fullName) {
        if (userRepo.existsByUsername(username)) {
            return;
        }

        User user = new User();
        user.setUsername(username);
        user.setEmail(email);
        user.setPassword(passwordEncoder.encode(rawPassword));
        user.setRole(role);
        user.setFullName(fullName);
        user.setActive(true);
        userRepo.save(user);
    }

    @Override
    public void run(String... args) {
        System.out.println("⏳ Loading Test Data...");

        // 1. Create Test Users with hashed passwords (if missing)
        createUserIfMissing("receptionist", "receptionist@medico.com", "recep123", "RECEPTIONIST", "Priya Singh");
        createUserIfMissing("doctor", "doctor@medico.com", "doctor123", "DOCTOR", "Dr. Rajesh Verma");
        createUserIfMissing("pharmacist", "pharmacist@medico.com", "pharma123", "PHARMACIST", "Kavya Sharma");
        createUserIfMissing("admin", "admin@medico.com", "admin123", "ADMIN", "System Administrator");

        // 2. Create Medicines
        Medicine paracetamol = new Medicine(null, "Paracetamol", 50, LocalDate.now().plusYears(1));
        Medicine aspirin = new Medicine(null, "Aspirin", 5, LocalDate.now().plusYears(1));

        medicineRepo.save(paracetamol);
        medicineRepo.save(aspirin);

        // 3. Create Patients
        Patient patient1 = new Patient(null, "Munna Kumar", "Medical History: Type 2 Diabetes, Allergic to Penicillin");
        Patient patient2 = new Patient(null, "Sangeeta Sharma", "Medical History: Hypertension");
        patientRepo.save(patient1);
        patientRepo.save(patient2);

        // 4. Create a Prescription for Munna Kumar
        Prescription p1 = new Prescription();
        p1.setPatient(patient1);
        p1.setMedicineQuantities(Map.of(aspirin.getId(), 2));
        p1.setStatus("PENDING");
        prescriptionRepo.save(p1);

        // 5. Create Doctors
        Doctor doc1 = new Doctor(null, "Dr. Rajesh Verma", "General Physician");
        Doctor doc2 = new Doctor(null, "Dr. Meera Iyer", "Pediatrics");
        doctorRepo.save(doc1);
        doctorRepo.save(doc2);

        // 6. Create an Appointment
        Appointment appt = new Appointment();
        appt.setDoctorId(doc1.getId());
        appt.setPatient(patient1);
        appt.setAppointmentTime(LocalDateTime.now().plusDays(1));
        appt.setStatus("BOOKED");
        appointmentRepo.save(appt);

        System.out.println("✅ Test Data Loaded Successfully!");
        System.out.println("   - Users created: receptionist, doctor, pharmacist, admin");
    }
}
