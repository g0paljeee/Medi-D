package com.example.Medico.dto;

import lombok.Data;

@Data
public class DoctorViewDTO {
    private Long patientId;
    private String patientName;
    private String historyBlob; // This is the sensitive field only Doctors see
}