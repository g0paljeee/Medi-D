package com.example.Medico.service;

import com.example.Medico.model.Medicine;
import com.example.Medico.model.Prescription;
import com.example.Medico.repository.MedicineRepository;
import com.example.Medico.repository.PrescriptionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;

@Service
public class PharmacyService {

    @Autowired
    private MedicineRepository medicineRepo;

    @Autowired
    private PrescriptionRepository prescriptionRepo;

    // The "Rollback" Magic
    @Transactional(rollbackFor = Exception.class)
    public void dispenseMedicines(Long prescriptionId) throws Exception {

        Prescription prescription = prescriptionRepo.findById(prescriptionId)
                .orElseThrow(() -> new RuntimeException("Prescription not found"));

        if ("DISPENSED".equals(prescription.getStatus())) {
            throw new RuntimeException("Already dispensed!");
        }

        // ITERATE AND CHECK STOCK
        for (Map.Entry<Long, Integer> entry : prescription.getMedicineQuantities().entrySet()) {
            Long medId = entry.getKey();
            Integer qtyNeeded = entry.getValue();

            // PESSIMISTIC_WRITE lock ensures no one else edits stock while we check
            Medicine med = medicineRepo.findByIdWithLock(medId)
                    .orElseThrow(() -> new RuntimeException("Medicine ID " + medId + " not found"));

            if (med.getStock() < qtyNeeded) {
                // This triggers the ROLLBACK. No stock is deducted for ANY item.
                throw new RuntimeException("Transaction Failed: Insufficient stock for " + med.getName());
            }

            // If check passes, stage the update
            med.setStock(med.getStock() - qtyNeeded);
            medicineRepo.save(med);
        }

        prescription.setStatus("DISPENSED");
        prescriptionRepo.save(prescription);
    }
}