package org.aub.payzenapi.service.implementation;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aub.payzenapi.exception.BadRequestException;
import org.aub.payzenapi.exception.DuplicateResourceException;
import org.aub.payzenapi.exception.ResourceNotFoundException;
import org.aub.payzenapi.model.dto.request.AttendanceRequest;
import org.aub.payzenapi.model.dto.response.AttendanceResponse;
import org.aub.payzenapi.model.entity.Attendance;
import org.aub.payzenapi.model.entity.Employee;
import org.aub.payzenapi.model.enums.AttendanceStatus;
import org.aub.payzenapi.repository.AttendanceRepository;
import org.aub.payzenapi.repository.EmployeeRepository;
import org.aub.payzenapi.service.AttendanceService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class AttendanceServiceImpl implements AttendanceService {

    private final AttendanceRepository attendanceRepository;
    private final EmployeeRepository employeeRepository;

    @Override
    public AttendanceResponse recordAttendance(AttendanceRequest request) {
        log.info("Recording attendance for employee: {}", request.getEmployeeId());

        Employee employee = employeeRepository.findById(request.getEmployeeId())
                .orElseThrow(() -> new ResourceNotFoundException("Employee not found with ID: " + request.getEmployeeId()));

        // Check if attendance already exists for this date
        if (attendanceRepository.findByEmployeeAndAttendanceDate(employee, request.getAttendanceDate()).isPresent()) {
            throw new DuplicateResourceException("Attendance already recorded for this date");
        }

        Attendance attendance = Attendance.builder()
                .employee(employee)
                .attendanceDate(request.getAttendanceDate())
                .checkInTime(request.getCheckInTime())
                .checkOutTime(request.getCheckOutTime())
                .breakDurationMinutes(request.getBreakDurationMinutes() != null ? request.getBreakDurationMinutes() : 0)
                .status(request.getStatus() != null ? request.getStatus() : AttendanceStatus.PRESENT)
                .notes(request.getNotes())
                .build();

        // Calculate total hours worked if both check-in and check-out times are provided
        if (attendance.getCheckInTime() != null && attendance.getCheckOutTime() != null) {
            attendance.setTotalHoursWorked(calculateHoursWorked(attendance));
        }

        Attendance savedAttendance = attendanceRepository.save(attendance);
        log.info("Attendance recorded successfully with ID: {}", savedAttendance.getAttendanceId());

        return mapToResponse(savedAttendance);
    }

    @Override
    @Transactional(readOnly = true)
    public AttendanceResponse getAttendanceById(UUID attendanceId) {
        log.info("Fetching attendance with ID: {}", attendanceId);

        Attendance attendance = attendanceRepository.findById(attendanceId)
                .orElseThrow(() -> new ResourceNotFoundException("Attendance not found with ID: " + attendanceId));

        return mapToResponse(attendance);
    }

    @Override
    public AttendanceResponse updateAttendance(UUID attendanceId, AttendanceRequest request) {
        log.info("Updating attendance with ID: {}", attendanceId);

        Attendance attendance = attendanceRepository.findById(attendanceId)
                .orElseThrow(() -> new ResourceNotFoundException("Attendance not found with ID: " + attendanceId));

        attendance.setCheckInTime(request.getCheckInTime());
        attendance.setCheckOutTime(request.getCheckOutTime());
        attendance.setBreakDurationMinutes(request.getBreakDurationMinutes() != null ? request.getBreakDurationMinutes() : 0);
        attendance.setStatus(request.getStatus() != null ? request.getStatus() : attendance.getStatus());
        attendance.setNotes(request.getNotes());

        // Recalculate total hours worked
        if (attendance.getCheckInTime() != null && attendance.getCheckOutTime() != null) {
            attendance.setTotalHoursWorked(calculateHoursWorked(attendance));
        }

        Attendance updatedAttendance = attendanceRepository.save(attendance);
        log.info("Attendance updated successfully with ID: {}", updatedAttendance.getAttendanceId());

        return mapToResponse(updatedAttendance);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<AttendanceResponse> getAttendanceRecords(UUID employeeId, LocalDate startDate,
                                                         LocalDate endDate, AttendanceStatus status,
                                                         Pageable pageable) {
        log.info("Fetching attendance records with filters");

        Page<Attendance> attendances = attendanceRepository.findAttendancesWithFilters(
                employeeId, startDate, endDate, status, pageable);

        return attendances.map(this::mapToResponse);
    }

    @Override
    public AttendanceResponse checkIn(UUID attendanceId) {
        log.info("Processing check-in for attendance ID: {}", attendanceId);

        Attendance attendance = attendanceRepository.findById(attendanceId)
                .orElseThrow(() -> new ResourceNotFoundException("Attendance not found with ID: " + attendanceId));

        if (attendance.getCheckInTime() != null) {
            throw new BadRequestException("Employee has already checked in");
        }

        attendance.setCheckInTime(LocalTime.now());
        attendance.setStatus(AttendanceStatus.PRESENT);

        Attendance updatedAttendance = attendanceRepository.save(attendance);
        log.info("Check-in processed successfully for attendance ID: {}", attendanceId);

        return mapToResponse(updatedAttendance);
    }

    @Override
    public AttendanceResponse checkOut(UUID attendanceId) {
        log.info("Processing check-out for attendance ID: {}", attendanceId);

        Attendance attendance = attendanceRepository.findById(attendanceId)
                .orElseThrow(() -> new ResourceNotFoundException("Attendance not found with ID: " + attendanceId));

        if (attendance.getCheckInTime() == null) {
            throw new BadRequestException("Employee must check in before checking out");
        }

        if (attendance.getCheckOutTime() != null) {
            throw new BadRequestException("Employee has already checked out");
        }

        attendance.setCheckOutTime(LocalTime.now());
        attendance.setTotalHoursWorked(calculateHoursWorked(attendance));

        Attendance updatedAttendance = attendanceRepository.save(attendance);
        log.info("Check-out processed successfully for attendance ID: {}", attendanceId);

        return mapToResponse(updatedAttendance);
    }

    @Override
    @Transactional(readOnly = true)
    public BigDecimal getTotalHoursWorkedByEmployee(UUID employeeId, LocalDate startDate, LocalDate endDate) {
        Employee employee = employeeRepository.findById(employeeId)
                .orElseThrow(() -> new ResourceNotFoundException("Employee not found with ID: " + employeeId));

        BigDecimal totalHours = attendanceRepository.getTotalHoursWorkedByEmployeeInPeriod(employee, startDate, endDate);
        return totalHours != null ? totalHours : BigDecimal.valueOf(0.0);
    }

    private BigDecimal calculateHoursWorked(Attendance attendance) {
        if (attendance.getCheckInTime() == null || attendance.getCheckOutTime() == null) {
            return BigDecimal.valueOf(0.0);
        }

        Duration duration = Duration.between(attendance.getCheckInTime(), attendance.getCheckOutTime());
        double totalMinutes = duration.toMinutes();

        // Subtract break duration
        totalMinutes -= attendance.getBreakDurationMinutes();

        return BigDecimal.valueOf(Math.max(0, totalMinutes / 60.0)); // Convert to hours and ensure non-negative
    }

    private AttendanceResponse mapToResponse(Attendance attendance) {
        return AttendanceResponse.builder()
                .attendanceId(attendance.getAttendanceId())
                .employeeId(attendance.getEmployee().getEmployeeId())
                .employeeName(attendance.getEmployee().getFirstName() + " " + attendance.getEmployee().getLastName())
                .attendanceDate(attendance.getAttendanceDate())
                .checkInTime(attendance.getCheckInTime())
                .checkOutTime(attendance.getCheckOutTime())
                .breakDurationMinutes(attendance.getBreakDurationMinutes())
                .totalHoursWorked(attendance.getTotalHoursWorked())
                .status(attendance.getStatus())
                .notes(attendance.getNotes())
                .createdAt(attendance.getCreatedAt())
                .build();
    }
}
