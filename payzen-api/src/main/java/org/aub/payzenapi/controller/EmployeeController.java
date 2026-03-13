package org.aub.payzenapi.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.aub.payzenapi.base.ApiResponse;
import org.aub.payzenapi.base.BaseController;
import org.aub.payzenapi.model.dto.request.EmployeeRequest;
import org.aub.payzenapi.model.dto.response.EmployeeResponse;
import org.aub.payzenapi.model.enums.EmployeeStatus;
import org.aub.payzenapi.service.EmployeeService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/employees")
@RequiredArgsConstructor
@Tag(name = "Employee Management", description = "APIs for managing employees")
public class EmployeeController extends BaseController {

    private final EmployeeService employeeService;

    @PostMapping
    @Operation(summary = "Create a new employee", description = "Creates a new employee record in the system")
    public ResponseEntity<ApiResponse<EmployeeResponse>> createEmployee(
            @Valid @RequestBody EmployeeRequest request) {
        EmployeeResponse employee = employeeService.createEmployee(request);
        return response("Employee created successfully", HttpStatus.CREATED, employee);
    }

    @GetMapping("/{employeeId}")
    @Operation(summary = "Get employee by ID", description = "Retrieves an employee by their unique identifier")
    public ResponseEntity<ApiResponse<EmployeeResponse>> getEmployeeById(
            @Parameter(description = "Employee unique identifier") @PathVariable UUID employeeId) {
        EmployeeResponse employee = employeeService.getEmployeeById(employeeId);
        return response("Employee retrieved successfully", employee);
    }

    @PutMapping("/{employeeId}")
    @Operation(summary = "Update employee", description = "Updates an existing employee's information")
    public ResponseEntity<ApiResponse<EmployeeResponse>> updateEmployee(
            @Parameter(description = "Employee unique identifier") @PathVariable UUID employeeId,
            @Valid @RequestBody EmployeeRequest request) {
        EmployeeResponse employee = employeeService.updateEmployee(employeeId, request);
        return response("Employee updated successfully", employee);
    }

    @DeleteMapping("/{employeeId}")
    @Operation(summary = "Delete employee", description = "Soft deletes an employee by changing status to TERMINATED")
    public ResponseEntity<ApiResponse<Object>> deleteEmployee(
            @Parameter(description = "Employee unique identifier") @PathVariable UUID employeeId) {
        employeeService.deleteEmployee(employeeId);
        return response("Employee deleted successfully");
    }

    @GetMapping
    @Operation(summary = "Get all employees", description = "Retrieves all employees with optional filtering and pagination")
    public ResponseEntity<ApiResponse<Page<EmployeeResponse>>> getAllEmployees(
            @Parameter(description = "Filter by employee name") @RequestParam(required = false) String name,
            @Parameter(description = "Filter by department") @RequestParam(required = false) String department,
            @Parameter(description = "Filter by employee status") @RequestParam(required = false) EmployeeStatus status,
            @Parameter(description = "Page number (0-based)") @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Page size") @RequestParam(defaultValue = "10") int size,
            @Parameter(description = "Sort field") @RequestParam(defaultValue = "createdAt") String sortBy,
            @Parameter(description = "Sort direction (asc/desc)") @RequestParam(defaultValue = "desc") String sortDir) {

        Sort sort = sortDir.equalsIgnoreCase("desc") ?
                Sort.by(sortBy).descending() : Sort.by(sortBy).ascending();
        Pageable pageable = PageRequest.of(page, size, sort);

        Page<EmployeeResponse> employees = employeeService.getAllEmployees(name, department, status, pageable);
        return response("Employees retrieved successfully", employees);
    }

    @GetMapping("/department/{department}")
    @Operation(summary = "Get employees by department", description = "Retrieves all employees in a specific department")
    public ResponseEntity<ApiResponse<List<EmployeeResponse>>> getEmployeesByDepartment(
            @Parameter(description = "Department name") @PathVariable String department) {
        List<EmployeeResponse> employees = employeeService.getEmployeesByDepartment(department);
        return response("Employees retrieved successfully", employees);
    }

    @GetMapping("/status/{status}")
    @Operation(summary = "Get employees by status", description = "Retrieves all employees with a specific status")
    public ResponseEntity<ApiResponse<List<EmployeeResponse>>> getEmployeesByStatus(
            @Parameter(description = "Employee status") @PathVariable EmployeeStatus status) {
        List<EmployeeResponse> employees = employeeService.getEmployeesByStatus(status);
        return response("Employees retrieved successfully", employees);
    }

    @PatchMapping("/{employeeId}/status")
    @Operation(summary = "Update employee status", description = "Updates the status of an employee")
    public ResponseEntity<ApiResponse<EmployeeResponse>> updateEmployeeStatus(
            @Parameter(description = "Employee unique identifier") @PathVariable UUID employeeId,
            @Parameter(description = "New employee status") @RequestParam EmployeeStatus status) {
        EmployeeResponse employee = employeeService.updateEmployeeStatus(employeeId, status);
        return response("Employee status updated successfully", employee);
    }

    @GetMapping("/count")
    @Operation(summary = "Get employee count by status", description = "Returns the count of employees for each status")
    public ResponseEntity<ApiResponse<Object>> getEmployeeCountByStatus() {
        var counts = Map.of(
                "active", employeeService.getEmployeeCountByStatus(EmployeeStatus.ACTIVE),
                "inactive", employeeService.getEmployeeCountByStatus(EmployeeStatus.INACTIVE),
                "terminated", employeeService.getEmployeeCountByStatus(EmployeeStatus.TERMINATED),
                "onLeave", employeeService.getEmployeeCountByStatus(EmployeeStatus.ON_LEAVE)
        );
        return response("Employee counts retrieved successfully", counts);
    }
}
