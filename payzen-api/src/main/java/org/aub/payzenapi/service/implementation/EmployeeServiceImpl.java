package org.aub.payzenapi.service.implementation;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aub.payzenapi.exception.DuplicateResourceException;
import org.aub.payzenapi.exception.ResourceNotFoundException;
import org.aub.payzenapi.model.dto.request.EmployeeRequest;
import org.aub.payzenapi.model.dto.response.EmployeeResponse;
import org.aub.payzenapi.model.entity.Employee;
import org.aub.payzenapi.model.enums.EmployeeStatus;
import org.aub.payzenapi.repository.EmployeeRepository;
import org.aub.payzenapi.service.EmployeeService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class EmployeeServiceImpl implements EmployeeService {

    private final EmployeeRepository employeeRepository;

    @Override
    public EmployeeResponse createEmployee(EmployeeRequest request) {
        log.info("Creating new employee with email: {}", request.getEmail());

        if (employeeRepository.existsByEmail(request.getEmail())) {
            throw new DuplicateResourceException("Employee with email " + request.getEmail() + " already exists");
        }

        Employee employee = Employee.builder()
                .employeeCode(generateEmployeeCode())
                .firstName(request.getFirstName())
                .lastName(request.getLastName())
                .email(request.getEmail())
                .phoneNumber(request.getPhoneNumber())
                .dateOfBirth(request.getDateOfBirth())
                .gender(request.getGender())
                .address(request.getAddress())
                .hireDate(request.getHireDate())
                .department(request.getDepartment())
                .position(request.getPosition())
                .baseSalary(request.getBaseSalary())
                .profileImageUrl(request.getProfileImageUrl())
                .status(EmployeeStatus.ACTIVE)
                .build();

        Employee savedEmployee = employeeRepository.save(employee);
        log.info("Employee created successfully with ID: {}", savedEmployee.getEmployeeId());

        return mapToResponse(savedEmployee);
    }

    @Override
    @Transactional(readOnly = true)
    public EmployeeResponse getEmployeeById(UUID employeeId) {
        log.info("Fetching employee with ID: {}", employeeId);

        Employee employee = employeeRepository.findById(employeeId)
                .orElseThrow(() -> new ResourceNotFoundException("Employee not found with ID: " + employeeId));

        return mapToResponse(employee);
    }

    @Override
    public EmployeeResponse updateEmployee(UUID employeeId, EmployeeRequest request) {
        log.info("Updating employee with ID: {}", employeeId);

        Employee employee = employeeRepository.findById(employeeId)
                .orElseThrow(() -> new ResourceNotFoundException("Employee not found with ID: " + employeeId));

        // Check if email is being changed and if it already exists
        if (!employee.getEmail().equals(request.getEmail()) &&
            employeeRepository.existsByEmail(request.getEmail())) {
            throw new DuplicateResourceException("Employee with email " + request.getEmail() + " already exists");
        }

        employee.setFirstName(request.getFirstName());
        employee.setLastName(request.getLastName());
        employee.setEmail(request.getEmail());
        employee.setPhoneNumber(request.getPhoneNumber());
        employee.setDateOfBirth(request.getDateOfBirth());
        employee.setGender(request.getGender());
        employee.setAddress(request.getAddress());
        employee.setDepartment(request.getDepartment());
        employee.setPosition(request.getPosition());
        employee.setBaseSalary(request.getBaseSalary());
        employee.setProfileImageUrl(request.getProfileImageUrl());

        Employee updatedEmployee = employeeRepository.save(employee);
        log.info("Employee updated successfully with ID: {}", updatedEmployee.getEmployeeId());

        return mapToResponse(updatedEmployee);
    }

    @Override
    public void deleteEmployee(UUID employeeId) {
        log.info("Deleting employee with ID: {}", employeeId);

        Employee employee = employeeRepository.findById(employeeId)
                .orElseThrow(() -> new ResourceNotFoundException("Employee not found with ID: " + employeeId));

        // Soft delete by changing status to TERMINATED
        employee.setStatus(EmployeeStatus.TERMINATED);
        employeeRepository.save(employee);

        log.info("Employee deleted successfully with ID: {}", employeeId);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<EmployeeResponse> getAllEmployees(String name, String department, EmployeeStatus status, Pageable pageable) {
        log.info("Fetching employees with filters - name: {}, department: {}, status: {}", name, department, status);

        Page<Employee> employees = employeeRepository.findEmployeesWithFilters(name, department, status, pageable);
        return employees.map(this::mapToResponse);
    }

    @Override
    @Transactional(readOnly = true)
    public List<EmployeeResponse> getEmployeesByDepartment(String department) {
        log.info("Fetching employees by department: {}", department);

        List<Employee> employees = employeeRepository.findByDepartment(department);
        return employees.stream().map(this::mapToResponse).toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<EmployeeResponse> getEmployeesByStatus(EmployeeStatus status) {
        log.info("Fetching employees by status: {}", status);

        List<Employee> employees = employeeRepository.findByStatus(status);
        return employees.stream().map(this::mapToResponse).toList();
    }

    @Override
    public EmployeeResponse updateEmployeeStatus(UUID employeeId, EmployeeStatus status) {
        log.info("Updating employee status for ID: {} to {}", employeeId, status);

        Employee employee = employeeRepository.findById(employeeId)
                .orElseThrow(() -> new ResourceNotFoundException("Employee not found with ID: " + employeeId));

        employee.setStatus(status);
        Employee updatedEmployee = employeeRepository.save(employee);

        log.info("Employee status updated successfully for ID: {}", employeeId);
        return mapToResponse(updatedEmployee);
    }

    @Override
    @Transactional(readOnly = true)
    public Long getEmployeeCountByStatus(EmployeeStatus status) {
        return employeeRepository.countByStatus(status);
    }

    private String generateEmployeeCode() {
        String prefix = "EMP";
        String timestamp = String.valueOf(System.currentTimeMillis()).substring(8);
        return prefix + timestamp;
    }

    private EmployeeResponse mapToResponse(Employee employee) {
        return EmployeeResponse.builder()
                .employeeId(employee.getEmployeeId())
                .employeeCode(employee.getEmployeeCode())
                .firstName(employee.getFirstName())
                .lastName(employee.getLastName())
                .email(employee.getEmail())
                .phoneNumber(employee.getPhoneNumber())
                .dateOfBirth(employee.getDateOfBirth())
                .gender(employee.getGender())
                .address(employee.getAddress())
                .hireDate(employee.getHireDate())
                .department(employee.getDepartment())
                .position(employee.getPosition())
                .baseSalary(employee.getBaseSalary())
                .status(employee.getStatus())
                .profileImageUrl(employee.getProfileImageUrl())
                .createdAt(employee.getCreatedAt())
                .updatedAt(employee.getUpdatedAt())
                .build();
    }
}
