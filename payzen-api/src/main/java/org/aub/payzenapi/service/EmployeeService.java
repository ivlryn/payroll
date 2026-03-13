package org.aub.payzenapi.service;

import org.aub.payzenapi.model.dto.request.EmployeeRequest;
import org.aub.payzenapi.model.dto.response.EmployeeResponse;
import org.aub.payzenapi.model.enums.EmployeeStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.UUID;

public interface EmployeeService {

    EmployeeResponse createEmployee(EmployeeRequest request);

    EmployeeResponse getEmployeeById(UUID employeeId);

    EmployeeResponse updateEmployee(UUID employeeId, EmployeeRequest request);

    void deleteEmployee(UUID employeeId);

    Page<EmployeeResponse> getAllEmployees(String name, String department, EmployeeStatus status, Pageable pageable);

    List<EmployeeResponse> getEmployeesByDepartment(String department);

    List<EmployeeResponse> getEmployeesByStatus(EmployeeStatus status);

    EmployeeResponse updateEmployeeStatus(UUID employeeId, EmployeeStatus status);

    Long getEmployeeCountByStatus(EmployeeStatus status);
}
