package org.aub.payzenapi.model.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.aub.payzenapi.model.enums.AttendanceStatus;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class AttendanceResponse {
    private UUID attendanceId;
    private UUID employeeId;
    private String employeeName;
    private LocalDate attendanceDate;
    private LocalTime checkInTime;
    private LocalTime checkOutTime;
    private Integer breakDurationMinutes;
    private BigDecimal totalHoursWorked;
    private AttendanceStatus status;
    private String notes;
    private LocalDateTime createdAt;
}
