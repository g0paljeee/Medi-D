package com.example.Medico;

import com.example.Medico.model.Appointment;
import com.example.Medico.model.Medicine;
import com.example.Medico.model.Patient;
import com.example.Medico.model.Prescription;
import com.example.Medico.repository.AppointmentRepository;
import com.example.Medico.repository.MedicineRepository;
import com.example.Medico.repository.PatientRepository;
import com.example.Medico.repository.PrescriptionRepository;
import com.example.Medico.model.Doctor;
import com.example.Medico.repository.DoctorRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Map;

@Component
public class DataLoader implements CommandLineRunner {

    private final MedicineRepository medicineRepo;
    private final PatientRepository patientRepo;
    private final PrescriptionRepository prescriptionRepo;
    private final AppointmentRepository appointmentRepo; // 1. Added field here
    private final DoctorRepository doctorRepo;

    // 2. Updated Constructor to inject AppointmentRepository
    public DataLoader(MedicineRepository medicineRepo,
                      PatientRepository patientRepo,
                      PrescriptionRepository prescriptionRepo,
                      AppointmentRepository appointmentRepo,
                      DoctorRepository doctorRepo) {
        this.medicineRepo = medicineRepo;
        this.patientRepo = patientRepo;
        this.prescriptionRepo = prescriptionRepo;
        this.appointmentRepo = appointmentRepo; // 3. Assigned it here
        this.doctorRepo = doctorRepo;
    }

    @Override
    public void run(String... args) throws Exception {
        System.out.println("⏳ Loading Test Data...");

        // 1. Create Medicines
        Medicine paracetamol = new Medicine(null, "Paracetamol", 50, LocalDate.now().plusYears(1));
        Medicine aspirin = new Medicine(null, "Aspirin", 5, LocalDate.now().plusYears(1)); // Low Stock!

        medicineRepo.save(paracetamol);
        medicineRepo.save(aspirin);

        // 2. Create Patients (Indian names)
        Patient patient1 = new Patient(null, "Munna Kumar", "Medical History: Type 2 Diabetes, Allergic to Penicillin");
        Patient patient2 = new Patient(null, "Sangeeta Sharma", "Medical History: Hypertension");
        patientRepo.save(patient1);
        patientRepo.save(patient2);

        // 3. Create a Prescription for Munna Kumar
        Prescription p1 = new Prescription();
        p1.setPatient(patient1);
        p1.setMedicineQuantities(Map.of(aspirin.getId(), 2)); // Needs 2 Aspirin (Stock is 5)
        p1.setStatus("PENDING");
        prescriptionRepo.save(p1);

        // 4. Create a Test Appointment (Module A)
        // No need to declare 'appointmentRepo = null' here because we injected it above!
        // 4. Create Doctors and an appointment
        Doctor doc1 = new Doctor(null, "Dr. Rajesh Verma", "General Physician");
        Doctor doc2 = new Doctor(null, "Dr. Meera Iyer", "Pediatrics");
        doctorRepo.save(doc1);
        doctorRepo.save(doc2);

        Appointment appt = new Appointment();
        appt.setDoctorId(doc1.getId());
        appt.setPatient(patient1); // Link to Munna Kumar
        appt.setAppointmentTime(LocalDateTime.now().plusDays(1)); // Tomorrow
        appt.setStatus("BOOKED");
        appointmentRepo.save(appt);

        System.out.println("✅ Test Data Loaded Successfully!");
        System.out.println("   - Patient: " + patient1.getName());
        System.out.println("   - Medicine: Aspirin (Stock: 5)");
        System.out.println("   - Prescription: Pending (Needs 2 Aspirin)");
        System.out.println("   - Appointment: Booked for Doctor: " + doc1.getName());
    }
}