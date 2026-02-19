package com.example.Medico.dto;

import lombok.Data;
import java.util.Map;

@Data
public class PharmacistViewDTO {
    private Long prescriptionId;
    private String patientName;

    // Key = Medicine ID, Value = Quantity
    // (Ideally, you would fetch names here, but for MVP, IDs are okay)
    private Map<Long, Integer> medicinesToDispense;

    private String status;
}