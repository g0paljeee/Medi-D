package com.example.Medico.controller;

import com.example.Medico.dto.PharmacistViewDTO;
import com.example.Medico.repository.PrescriptionRepository;
import com.example.Medico.service.PharmacyService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/pharmacy")
public class PharmacyController {

    @Autowired
    private PharmacyService pharmacyService;

    @Autowired
    private PrescriptionRepository prescriptionRepo;
    @Autowired
    private com.example.Medico.repository.MedicineRepository medicineRepo;

    // Constraint 2: Only return safe data (Privacy Firewall)
    @GetMapping("/queue")
    public List<PharmacistViewDTO> getPendingPrescriptions() {
        return prescriptionRepo.findByStatus("PENDING").stream().map(p -> {
            PharmacistViewDTO dto = new PharmacistViewDTO();
            dto.setPrescriptionId(p.getId());

            // Null check is good practice in case a patient was deleted but prescription remains
            if (p.getPatient() != null) {
                dto.setPatientName(p.getPatient().getName());
            } else {
                dto.setPatientName("Unknown Patient");
            }

            // Map the medicine quantities map (ID -> Qty) to (Name -> Qty)
            if (p.getMedicineQuantities() != null) {
                java.util.Map<String, Integer> mapped = new java.util.HashMap<>();
                for (java.util.Map.Entry<Long, Integer> e : p.getMedicineQuantities().entrySet()) {
                    Long medId = e.getKey();
                    Integer qty = e.getValue();
                    String name = medicineRepo.findById(medId).map(m -> m.getName()).orElse("Medicine-" + medId);
                    mapped.put(name, qty);
                }
                dto.setMedicinesToDispense(mapped);
            }

            dto.setStatus(p.getStatus());

            // CRITICAL: We are intentionally NOT mapping p.getPatient().getHistoryBlob()
            return dto;
        }).collect(Collectors.toList());
    }

    @PostMapping("/dispense/{id}")
    public ResponseEntity<String> dispense(@PathVariable Long id) {
        try {
            pharmacyService.dispenseMedicines(id);
            return ResponseEntity.ok("Dispensed Successfully");
        } catch (Exception e) {
            // This catches the "Insufficient Stock" exception from the Service
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}