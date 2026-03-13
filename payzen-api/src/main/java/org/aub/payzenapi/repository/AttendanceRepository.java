package org.aub.payzenapi.repository;

import org.aub.payzenapi.model.entity.Attendance;
import org.aub.payzenapi.model.entity.Employee;
import org.aub.payzenapi.model.enums.AttendanceStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface AttendanceRepository extends JpaRepository<Attendance, UUID> {

    List<Attendance> findByEmployeeAndAttendanceDateBetween(Employee employee, LocalDate startDate, LocalDate endDate);

    Optional<Attendance> findByEmployeeAndAttendanceDate(Employee employee, LocalDate attendanceDate);

    List<Attendance> findByAttendanceDateAndStatus(LocalDate attendanceDate, AttendanceStatus status);

    @Query("SELECT a FROM Attendance a WHERE " +
           "(:employeeId IS NULL OR a.employee.employeeId = :employeeId) AND " +
           "(:startDate IS NULL OR a.attendanceDate >= :startDate) AND " +
           "(:endDate IS NULL OR a.attendanceDate <= :endDate) AND " +
           "(:status IS NULL OR a.status = :status)")
    Page<Attendance> findAttendancesWithFilters(@Param("employeeId") UUID employeeId,
                                                @Param("startDate") LocalDate startDate,
                                                @Param("endDate") LocalDate endDate,
                                                @Param("status") AttendanceStatus status,
                                                Pageable pageable);

    @Query("SELECT SUM(a.totalHoursWorked) FROM Attendance a WHERE a.employee = :employee AND " +
           "a.attendanceDate BETWEEN :startDate AND :endDate AND a.status = 'PRESENT'")
    BigDecimal getTotalHoursWorkedByEmployeeInPeriod(@Param("employee") Employee employee,
                                                     @Param("startDate") LocalDate startDate,
                                                     @Param("endDate") LocalDate endDate);
}
