package org.aub.payzenapi.service.implementation;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aub.payzenapi.exception.DuplicateResourceException;
import org.aub.payzenapi.exception.ResourceNotFoundException;
import org.aub.payzenapi.model.dto.response.PayslipResponse;
import org.aub.payzenapi.model.entity.Employee;
import org.aub.payzenapi.model.entity.Payslip;
import org.aub.payzenapi.model.enums.PayslipStatus;
import org.aub.payzenapi.repository.EmployeeRepository;
import org.aub.payzenapi.repository.PayslipRepository;
import org.aub.payzenapi.service.AttendanceService;
import org.aub.payzenapi.service.PayslipService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class PayslipServiceImpl implements PayslipService {

    private final PayslipRepository payslipRepository;
    private final EmployeeRepository employeeRepository;
    private final AttendanceService attendanceService;

    private static final BigDecimal STANDARD_HOURS_PER_MONTH = new BigDecimal("160"); // 8 hours * 20 working days
    private static final BigDecimal OVERTIME_MULTIPLIER = new BigDecimal("1.5");
    private static final BigDecimal TAX_RATE = new BigDecimal("0.10"); // 10% tax rate

    @Override
    public PayslipResponse generatePayslip(UUID employeeId, LocalDateTime payPeriodStart, LocalDateTime payPeriodEnd) {
        log.info("Generating payslip for employee: {} for period {} to {}", employeeId, payPeriodStart, payPeriodEnd);

        Employee employee = employeeRepository.findById(employeeId)
                .orElseThrow(() -> new ResourceNotFoundException("Employee not found with ID: " + employeeId));

        // Check if payslip already exists for this period
        if (payslipRepository.findByEmployeeAndPayPeriodStartAndPayPeriodEnd(employee, payPeriodStart, payPeriodEnd).isPresent()) {
            throw new DuplicateResourceException("Payslip already exists for this period");
        }

        // Calculate total hours worked during the period

        BigDecimal totalHours = attendanceService.getTotalHoursWorkedByEmployee(
                employeeId, payPeriodStart.toLocalDate(), payPeriodEnd.toLocalDate());
        BigDecimal hourlyRate = employee.getBaseSalary().divide(STANDARD_HOURS_PER_MONTH, 2, RoundingMode.HALF_UP);

        // Calculate overtime
        BigDecimal overtimeHours = totalHours.compareTo(STANDARD_HOURS_PER_MONTH) > 0 ?
                totalHours.subtract(STANDARD_HOURS_PER_MONTH) : BigDecimal.ZERO;
        BigDecimal overtimeRate = hourlyRate.multiply(OVERTIME_MULTIPLIER);
        BigDecimal overtimePay = overtimeHours.multiply(overtimeRate);

        // Calculate gross salary
        BigDecimal regularPay = STANDARD_HOURS_PER_MONTH.min(totalHours).multiply(hourlyRate);
        BigDecimal grossSalary = regularPay.add(overtimePay);

        // Calculate deductions
        BigDecimal taxDeduction = grossSalary.multiply(TAX_RATE);

        // Calculate net salary
        BigDecimal netSalary = grossSalary.subtract(taxDeduction);

        Payslip payslip = Payslip.builder()
                .employee(employee)
                .payPeriodStart(payPeriodStart)
                .payPeriodEnd(payPeriodEnd)
                .baseSalary(employee.getBaseSalary())
                .overtimeHours(overtimeHours)
                .overtimeRate(overtimeRate)
                .overtimePay(overtimePay)
                .allowances(BigDecimal.ZERO)
                .deductions(BigDecimal.ZERO)
                .taxDeduction(taxDeduction)
                .grossSalary(grossSalary)
                .netSalary(netSalary)
                .status(PayslipStatus.GENERATED)
                .build();

        Payslip savedPayslip = payslipRepository.save(payslip);
        log.info("Payslip generated successfully with ID: {}", savedPayslip.getPayslipId());

        return mapToResponse(savedPayslip);
    }

    @Override
    @Transactional(readOnly = true)
    public PayslipResponse getPayslipById(UUID payslipId) {
        log.info("Fetching payslip with ID: {}", payslipId);

        Payslip payslip = payslipRepository.findById(payslipId)
                .orElseThrow(() -> new ResourceNotFoundException("Payslip not found with ID: " + payslipId));

        return mapToResponse(payslip);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<PayslipResponse> getPayslips(UUID employeeId, LocalDateTime startDate,
                                             LocalDateTime endDate, PayslipStatus status,
                                             Pageable pageable) {
        log.info("Fetching payslips with filters");

        Page<Payslip> payslips = payslipRepository.findPayslipsWithFilters(
                employeeId, startDate, endDate, status, pageable);

        return payslips.map(this::mapToResponse);
    }

    @Override
    @Transactional(readOnly = true)
    public List<PayslipResponse> getPayslipsByEmployee(UUID employeeId) {
        log.info("Fetching payslips for employee: {}", employeeId);

        Employee employee = employeeRepository.findById(employeeId)
                .orElseThrow(() -> new ResourceNotFoundException("Employee not found with ID: " + employeeId));

        List<Payslip> payslips = payslipRepository.findByEmployee(employee);
        return payslips.stream().map(this::mapToResponse).toList();
    }

    @Override
    public PayslipResponse updatePayslipStatus(UUID payslipId, PayslipStatus status) {
        log.info("Updating payslip status for ID: {} to {}", payslipId, status);

        Payslip payslip = payslipRepository.findById(payslipId)
                .orElseThrow(() -> new ResourceNotFoundException("Payslip not found with ID: " + payslipId));

        payslip.setStatus(status);
        Payslip updatedPayslip = payslipRepository.save(payslip);

        log.info("Payslip status updated successfully for ID: {}", payslipId);
        return mapToResponse(updatedPayslip);
    }

    private PayslipResponse mapToResponse(Payslip payslip) {
        return PayslipResponse.builder()
                .payslipId(payslip.getPayslipId())
                .employeeId(payslip.getEmployee().getEmployeeId())
                .employeeName(payslip.getEmployee().getFirstName() + " " + payslip.getEmployee().getLastName())
                .payPeriodStart(payslip.getPayPeriodStart())
                .payPeriodEnd(payslip.getPayPeriodEnd())
                .baseSalary(payslip.getBaseSalary())
                .overtimeHours(payslip.getOvertimeHours())
                .overtimeRate(payslip.getOvertimeRate())
                .overtimePay(payslip.getOvertimePay())
                .allowances(payslip.getAllowances())
                .deductions(payslip.getDeductions())
                .taxDeduction(payslip.getTaxDeduction())
                .grossSalary(payslip.getGrossSalary())
                .netSalary(payslip.getNetSalary())
                .status(payslip.getStatus())
                .createdAt(payslip.getCreatedAt())
                .build();
    }
}
