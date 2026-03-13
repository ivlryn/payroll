package org.aub.payzenapi.service;

import org.aub.payzenapi.model.dto.request.AttendanceRequest;
import org.aub.payzenapi.model.dto.response.AttendanceResponse;
import org.aub.payzenapi.model.enums.AttendanceStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

public interface AttendanceService {

    AttendanceResponse recordAttendance(AttendanceRequest request);

    AttendanceResponse getAttendanceById(UUID attendanceId);

    AttendanceResponse updateAttendance(UUID attendanceId, AttendanceRequest request);

    Page<AttendanceResponse> getAttendanceRecords(UUID employeeId, LocalDate startDate,
                                                  LocalDate endDate, AttendanceStatus status,
                                                  Pageable pageable);

    AttendanceResponse checkIn(UUID attendanceId);

    AttendanceResponse checkOut(UUID attendanceId);

    BigDecimal getTotalHoursWorkedByEmployee(UUID employeeId, LocalDate startDate, LocalDate endDate);
}
