package com.example.Medico.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class StaffSummaryDTO {
    private Long id;
    private String username;
    private String email;
    private String role;
    private String fullName;
    private Boolean active;
}
