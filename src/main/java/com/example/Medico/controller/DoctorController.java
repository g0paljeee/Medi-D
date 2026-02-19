package com.example.Medico.controller;

import com.example.Medico.dto.DoctorViewDTO;
import com.example.Medico.model.Medicine;
import com.example.Medico.model.Patient;
import com.example.Medico.model.Prescription;
import com.example.Medico.repository.MedicineRepository;
import com.example.Medico.repository.PatientRepository;
import com.example.Medico.repository.PrescriptionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/doctor")
public class DoctorController {

    @Autowired private PatientRepository patientRepo;
    @Autowired private MedicineRepository medicineRepo;
    @Autowired private PrescriptionRepository prescriptionRepo;

    // 1. "Live DB Fetch" - Doctor needs to see what meds are available
    @GetMapping("/medicines")
    public List<Medicine> getAvailableMedicines() {
        return medicineRepo.findAll();
    }

    // 2. View Patient History (PRIVACY: ALLOWED for Doctor)
    @GetMapping("/patient/{id}")
    public ResponseEntity<DoctorViewDTO> getPatientDetails(@PathVariable Long id) {
        Patient patient = patientRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("Patient not found"));

        DoctorViewDTO dto = new DoctorViewDTO();
        dto.setPatientId(patient.getId());
        dto.setPatientName(patient.getName());
        dto.setHistoryBlob(patient.getHistoryBlob()); // <--- Doctor sees this!

        return ResponseEntity.ok(dto);
    }

    // 3. Create Prescription & Diagnosis
    @PostMapping("/prescribe")
    public ResponseEntity<String> createPrescription(@RequestParam Long patientId,
                                                     @RequestParam String diagnosis,
                                                     @RequestBody Map<Long, Integer> meds) {

        // A. Update Patient History with new Diagnosis
        Patient patient = patientRepo.findById(patientId)
                .orElseThrow(() -> new RuntimeException("Patient not found"));

        String newHistory = patient.getHistoryBlob() + " | New Diagnosis: " + diagnosis;
        patient.setHistoryBlob(newHistory);
        patientRepo.save(patient);

        // B. Create Prescription
        Prescription prescription = new Prescription();
        prescription.setPatient(patient);
        prescription.setMedicineQuantities(meds); // The "JSON" list of meds
        prescription.setStatus("PENDING"); // Ready for Pharmacist

        prescriptionRepo.save(prescription);

        return ResponseEntity.ok("Prescription Sent to Pharmacy. Diagnosis Saved.");
    }
}