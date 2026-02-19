package com.example.Medico.dto;

import lombok.Data;
import java.util.Map;

@Data
public class PharmacistViewDTO {
    private Long prescriptionId;
    private String patientName;

    // Key = Medicine Name, Value = Quantity
    private Map<String, Integer> medicinesToDispense;

    private String status;
}