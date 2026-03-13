package org.aub.payzenapi.model.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.aub.payzenapi.model.enums.PayslipStatus;
import org.hibernate.annotations.CreationTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "payslips")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Payslip {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "payslip_id")
    private UUID payslipId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "employee_id", nullable = false)
    private Employee employee;

    @Column(name = "pay_period_start", nullable = false)
    private LocalDateTime payPeriodStart;

    @Column(name = "pay_period_end", nullable = false)
    private LocalDateTime payPeriodEnd;

    @Column(name = "base_salary", nullable = false)
    private BigDecimal baseSalary;

    @Column(name = "overtime_hours")
    @Builder.Default
    private BigDecimal overtimeHours = BigDecimal.ZERO;

    @Column(name = "overtime_rate")
    @Builder.Default
    private BigDecimal overtimeRate = BigDecimal.ZERO;

    @Column(name = "overtime_pay")
    @Builder.Default
    private BigDecimal overtimePay = BigDecimal.ZERO;

    @Column(name = "allowances")
    @Builder.Default
    private BigDecimal allowances = BigDecimal.ZERO;

    @Column(name = "deductions")
    @Builder.Default
    private BigDecimal deductions = BigDecimal.ZERO;

    @Column(name = "tax_deduction")
    @Builder.Default
    private BigDecimal taxDeduction = BigDecimal.ZERO;

    @Column(name = "gross_salary", nullable = false)
    private BigDecimal grossSalary;

    @Column(name = "net_salary", nullable = false)
    private BigDecimal netSalary;

    @Enumerated(EnumType.STRING)
    @Column(name = "status")
    @Builder.Default
    private PayslipStatus status = PayslipStatus.DRAFT;

    @CreationTimestamp
    @Column(name = "created_at")
    private LocalDateTime createdAt;
}
