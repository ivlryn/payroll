package org.aub.payzenapi.model.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.aub.payzenapi.model.enums.PayslipStatus;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class PayslipResponse {
    private UUID payslipId;
    private UUID employeeId;
    private String employeeName;
    private LocalDateTime payPeriodStart;
    private LocalDateTime payPeriodEnd;
    private BigDecimal baseSalary;
    private BigDecimal overtimeHours;
    private BigDecimal overtimeRate;
    private BigDecimal overtimePay;
    private BigDecimal allowances;
    private BigDecimal deductions;
    private BigDecimal taxDeduction;
    private BigDecimal grossSalary;
    private BigDecimal netSalary;
    private PayslipStatus status;
    private LocalDateTime createdAt;
}
