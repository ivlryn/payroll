package org.aub.payzenapi.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.aub.payzenapi.base.ApiResponse;
import org.aub.payzenapi.base.BaseController;
import org.aub.payzenapi.model.dto.request.AttendanceRequest;
import org.aub.payzenapi.model.dto.response.AttendanceResponse;
import org.aub.payzenapi.model.enums.AttendanceStatus;
import org.aub.payzenapi.service.AttendanceService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/attendance")
@RequiredArgsConstructor
@Tag(name = "Attendance Management", description = "APIs for managing employee attendance")
public class AttendanceController extends BaseController {

    private final AttendanceService attendanceService;

    @PostMapping
    @Operation(summary = "Record attendance", description = "Records attendance for an employee")
    public ResponseEntity<ApiResponse<AttendanceResponse>> recordAttendance(
            @Valid @RequestBody AttendanceRequest request) {
        AttendanceResponse attendance = attendanceService.recordAttendance(request);
        return response("Attendance recorded successfully", HttpStatus.CREATED, attendance);
    }

    @GetMapping("/{attendanceId}")
    @Operation(summary = "Get attendance by ID", description = "Retrieves attendance record by its unique identifier")
    public ResponseEntity<ApiResponse<AttendanceResponse>> getAttendanceById(
            @Parameter(description = "Attendance unique identifier") @PathVariable UUID attendanceId) {
        AttendanceResponse attendance = attendanceService.getAttendanceById(attendanceId);
        return response("Attendance retrieved successfully", attendance);
    }

    @PutMapping("/{attendanceId}")
    @Operation(summary = "Update attendance", description = "Updates an existing attendance record")
    public ResponseEntity<ApiResponse<AttendanceResponse>> updateAttendance(
            @Parameter(description = "Attendance unique identifier") @PathVariable UUID attendanceId,
            @Valid @RequestBody AttendanceRequest request) {
        AttendanceResponse attendance = attendanceService.updateAttendance(attendanceId, request);
        return response("Attendance updated successfully", attendance);
    }

    @GetMapping
    @Operation(summary = "Get attendance records", description = "Retrieves attendance records with optional filtering and pagination")
    public ResponseEntity<ApiResponse<Page<AttendanceResponse>>> getAttendanceRecords(
            @Parameter(description = "Filter by employee ID") @RequestParam(required = false) UUID employeeId,
            @Parameter(description = "Filter by start date") @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @Parameter(description = "Filter by end date") @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
            @Parameter(description = "Filter by attendance status") @RequestParam(required = false) AttendanceStatus status,
            @Parameter(description = "Page number (0-based)") @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Page size") @RequestParam(defaultValue = "10") int size,
            @Parameter(description = "Sort field") @RequestParam(defaultValue = "attendanceDate") String sortBy,
            @Parameter(description = "Sort direction (asc/desc)") @RequestParam(defaultValue = "desc") String sortDir) {

        Sort sort = sortDir.equalsIgnoreCase("desc") ?
                Sort.by(sortBy).descending() : Sort.by(sortBy).ascending();
        Pageable pageable = PageRequest.of(page, size, sort);

        Page<AttendanceResponse> attendances = attendanceService.getAttendanceRecords(
                employeeId, startDate, endDate, status, pageable);
        return response("Attendance records retrieved successfully", attendances);
    }

    @PostMapping("/{attendanceId}/check-in")
    @Operation(summary = "Check in employee", description = "Records check-in time for an attendance record")
    public ResponseEntity<ApiResponse<AttendanceResponse>> checkIn(
            @Parameter(description = "Attendance unique identifier") @PathVariable UUID attendanceId) {
        AttendanceResponse attendance = attendanceService.checkIn(attendanceId);
        return response("Check-in recorded successfully", attendance);
    }

    @PostMapping("/{attendanceId}/check-out")
    @Operation(summary = "Check out employee", description = "Records check-out time and calculates total hours worked")
    public ResponseEntity<ApiResponse<AttendanceResponse>> checkOut(
            @Parameter(description = "Attendance unique identifier") @PathVariable UUID attendanceId) {
        AttendanceResponse attendance = attendanceService.checkOut(attendanceId);
        return response("Check-out recorded successfully", attendance);
    }

    @GetMapping("/employee/{employeeId}/hours")
    @Operation(summary = "Get total hours worked", description = "Calculates total hours worked by an employee in a date range")
    public ResponseEntity<ApiResponse<Object>> getTotalHoursWorked(
            @Parameter(description = "Employee unique identifier") @PathVariable UUID employeeId,
            @Parameter(description = "Start date") @RequestParam
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @Parameter(description = "End date") @RequestParam
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {

        BigDecimal totalHours = attendanceService.getTotalHoursWorkedByEmployee(employeeId, startDate, endDate);
        var result = Map.of(
                "employeeId", employeeId,
                "startDate", startDate,
                "endDate", endDate,
                "totalHours", totalHours
        );
        return response("Total hours calculated successfully", result);
    }

    @GetMapping("/today")
    @Operation(summary = "Get today's attendance", description = "Retrieves attendance records for today")
    public ResponseEntity<ApiResponse<Page<AttendanceResponse>>> getTodayAttendance(
            @Parameter(description = "Filter by attendance status") @RequestParam(required = false) AttendanceStatus status,
            @Parameter(description = "Page number (0-based)") @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Page size") @RequestParam(defaultValue = "20") int size) {

        LocalDate today = LocalDate.now();
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());

        Page<AttendanceResponse> attendances = attendanceService.getAttendanceRecords(
                null, today, today, status, pageable);
        return response("Today's attendance retrieved successfully", attendances);
    }
}
