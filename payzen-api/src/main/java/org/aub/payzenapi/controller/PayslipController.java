package org.aub.payzenapi.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.aub.payzenapi.base.ApiResponse;
import org.aub.payzenapi.base.BaseController;
import org.aub.payzenapi.model.dto.response.PayslipResponse;
import org.aub.payzenapi.model.enums.PayslipStatus;
import org.aub.payzenapi.service.PayslipService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/payslips")
@RequiredArgsConstructor
@Tag(name = "Payslip Management", description = "APIs for managing employee payslips and salary calculations")
public class PayslipController extends BaseController {

    private final PayslipService payslipService;

    @PostMapping("/generate/{employeeId}")
    @Operation(summary = "Generate payslip", description = "Generates a payslip for an employee for a specific period")
    public ResponseEntity<ApiResponse<PayslipResponse>> generatePayslip(
            @Parameter(description = "Employee unique identifier") @PathVariable UUID employeeId,
            @Parameter(description = "Pay period start date and time") @RequestParam
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime payPeriodStart,
            @Parameter(description = "Pay period end date and time") @RequestParam
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime payPeriodEnd) {

        PayslipResponse payslip = payslipService.generatePayslip(employeeId, payPeriodStart, payPeriodEnd);
        return response("Payslip generated successfully", HttpStatus.CREATED, payslip);
    }

    @GetMapping("/{payslipId}")
    @Operation(summary = "Get payslip by ID", description = "Retrieves a payslip by its unique identifier")
    public ResponseEntity<ApiResponse<PayslipResponse>> getPayslipById(
            @Parameter(description = "Payslip unique identifier") @PathVariable UUID payslipId) {
        PayslipResponse payslip = payslipService.getPayslipById(payslipId);
        return response("Payslip retrieved successfully", payslip);
    }

    @GetMapping
    @Operation(summary = "Get payslips", description = "Retrieves payslips with optional filtering and pagination")
    public ResponseEntity<ApiResponse<Page<PayslipResponse>>> getPayslips(
            @Parameter(description = "Filter by employee ID") @RequestParam(required = false) UUID employeeId,
            @Parameter(description = "Filter by start date") @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
            @Parameter(description = "Filter by end date") @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate,
            @Parameter(description = "Filter by payslip status") @RequestParam(required = false) PayslipStatus status,
            @Parameter(description = "Page number (0-based)") @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Page size") @RequestParam(defaultValue = "10") int size,
            @Parameter(description = "Sort field") @RequestParam(defaultValue = "createdAt") String sortBy,
            @Parameter(description = "Sort direction (asc/desc)") @RequestParam(defaultValue = "desc") String sortDir) {

        Sort sort = sortDir.equalsIgnoreCase("desc") ?
                Sort.by(sortBy).descending() : Sort.by(sortBy).ascending();
        Pageable pageable = PageRequest.of(page, size, sort);

        Page<PayslipResponse> payslips = payslipService.getPayslips(employeeId, startDate, endDate, status, pageable);
        return response("Payslips retrieved successfully", payslips);
    }

    @GetMapping("/employee/{employeeId}")
    @Operation(summary = "Get employee payslips", description = "Retrieves all payslips for a specific employee")
    public ResponseEntity<ApiResponse<List<PayslipResponse>>> getEmployeePayslips(
            @Parameter(description = "Employee unique identifier") @PathVariable UUID employeeId) {
        List<PayslipResponse> payslips = payslipService.getPayslipsByEmployee(employeeId);
        return response("Employee payslips retrieved successfully", payslips);
    }

    @PatchMapping("/{payslipId}/status")
    @Operation(summary = "Update payslip status", description = "Updates the status of a payslip")
    public ResponseEntity<ApiResponse<PayslipResponse>> updatePayslipStatus(
            @Parameter(description = "Payslip unique identifier") @PathVariable UUID payslipId,
            @Parameter(description = "New payslip status") @RequestParam PayslipStatus status) {
        PayslipResponse payslip = payslipService.updatePayslipStatus(payslipId, status);
        return response("Payslip status updated successfully", payslip);
    }

    @PostMapping("/generate-bulk")
    @Operation(summary = "Generate bulk payslips", description = "Generates payslips for all active employees for a specific period")
    public ResponseEntity<ApiResponse<Object>> generateBulkPayslips(
            @Parameter(description = "Pay period start date and time") @RequestParam
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime payPeriodStart,
            @Parameter(description = "Pay period end date and time") @RequestParam
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime payPeriodEnd,
            @Parameter(description = "Department filter (optional)") @RequestParam(required = false) String department) {

        // This would be implemented in the service layer
        var result = Map.of(
                "message", "Bulk payslip generation initiated",
                "payPeriodStart", payPeriodStart,
                "payPeriodEnd", payPeriodEnd,
                "department", department != null ? department : "All departments"
        );
        return response("Bulk payslip generation initiated successfully", result);
    }

    @GetMapping("/summary")
    @Operation(summary = "Get payslip summary", description = "Retrieves payslip summary statistics")
    public ResponseEntity<ApiResponse<Object>> getPayslipSummary(
            @Parameter(description = "Filter by start date") @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
            @Parameter(description = "Filter by end date") @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate) {

        // This would be implemented in the service layer
        var summary = Map.of(
                "totalPayslips", 0,
                "totalGrossSalary", 0.0,
                "totalNetSalary", 0.0,
                "totalTaxDeductions", 0.0,
                "averageSalary", 0.0
        );
        return response("Payslip summary retrieved successfully", summary);
    }
}
