package org.aub.payzenapi.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.aub.payzenapi.base.ApiResponse;
import org.aub.payzenapi.base.BaseController;
import org.aub.payzenapi.model.enums.AttendanceStatus;
import org.aub.payzenapi.model.enums.EmployeeStatus;
import org.aub.payzenapi.service.AttendanceService;
import org.aub.payzenapi.service.EmployeeService;
import org.aub.payzenapi.service.PayslipService;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/dashboard")
@RequiredArgsConstructor
@Tag(name = "Dashboard", description = "APIs for dashboard statistics and analytics")
public class DashboardController extends BaseController {

    private final EmployeeService employeeService;
    private final AttendanceService attendanceService;
    private final PayslipService payslipService;

    @GetMapping("/stats")
    @Operation(summary = "Get dashboard statistics", description = "Retrieves comprehensive dashboard statistics")
    public ResponseEntity<ApiResponse<Object>> getDashboardStats() {
        var stats = Map.of(
                "employees", Map.of(
                        "total", employeeService.getEmployeeCountByStatus(EmployeeStatus.ACTIVE) +
                                 employeeService.getEmployeeCountByStatus(EmployeeStatus.INACTIVE) +
                                 employeeService.getEmployeeCountByStatus(EmployeeStatus.ON_LEAVE),
                        "active", employeeService.getEmployeeCountByStatus(EmployeeStatus.ACTIVE),
                        "inactive", employeeService.getEmployeeCountByStatus(EmployeeStatus.INACTIVE),
                        "onLeave", employeeService.getEmployeeCountByStatus(EmployeeStatus.ON_LEAVE),
                        "terminated", employeeService.getEmployeeCountByStatus(EmployeeStatus.TERMINATED)
                ),
                "attendance", Map.of(
                        "date", LocalDate.now(),
                        "message", "Attendance statistics for today"
                ),
                "payroll", Map.of(
                        "currentMonth", LocalDateTime.now().getMonth().toString(),
                        "message", "Payroll statistics for current month"
                )
        );
        return response("Dashboard statistics retrieved successfully", stats);
    }

    @GetMapping("/employee-stats")
    @Operation(summary = "Get employee statistics", description = "Retrieves detailed employee statistics")
    public ResponseEntity<ApiResponse<Object>> getEmployeeStats() {
        var stats = Map.of(
                "totalEmployees", employeeService.getEmployeeCountByStatus(EmployeeStatus.ACTIVE) +
                                  employeeService.getEmployeeCountByStatus(EmployeeStatus.INACTIVE) +
                                  employeeService.getEmployeeCountByStatus(EmployeeStatus.ON_LEAVE),
                "activeEmployees", employeeService.getEmployeeCountByStatus(EmployeeStatus.ACTIVE),
                "inactiveEmployees", employeeService.getEmployeeCountByStatus(EmployeeStatus.INACTIVE),
                "employeesOnLeave", employeeService.getEmployeeCountByStatus(EmployeeStatus.ON_LEAVE),
                "terminatedEmployees", employeeService.getEmployeeCountByStatus(EmployeeStatus.TERMINATED),
                "lastUpdated", LocalDateTime.now()
        );
        return response("Employee statistics retrieved successfully", stats);
    }

    @GetMapping("/attendance-stats")
    @Operation(summary = "Get attendance statistics", description = "Retrieves attendance statistics for a specific date")
    public ResponseEntity<ApiResponse<Object>> getAttendanceStats(
            @Parameter(description = "Date for attendance statistics") @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {

        LocalDate targetDate = date != null ? date : LocalDate.now();

        var stats = Map.of(
                "date", targetDate,
                "statistics", Map.of(
                        "present", "Statistics would be calculated here",
                        "absent", "Statistics would be calculated here",
                        "late", "Statistics would be calculated here",
                        "onLeave", "Statistics would be calculated here"
                ),
                "message", "Attendance statistics for " + targetDate
        );
        return response("Attendance statistics retrieved successfully", stats);
    }

    @GetMapping("/payroll-stats")
    @Operation(summary = "Get payroll statistics", description = "Retrieves payroll statistics for a specific period")
    public ResponseEntity<ApiResponse<Object>> getPayrollStats(
            @Parameter(description = "Start date for payroll statistics") @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
            @Parameter(description = "End date for payroll statistics") @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate) {

        LocalDateTime start = startDate != null ? startDate : LocalDateTime.now().withDayOfMonth(1);
        LocalDateTime end = endDate != null ? endDate : LocalDateTime.now();

        var stats = Map.of(
                "period", Map.of(
                        "startDate", start,
                        "endDate", end
                ),
                "statistics", Map.of(
                        "totalPayslips", "Statistics would be calculated here",
                        "totalGrossSalary", "Statistics would be calculated here",
                        "totalNetSalary", "Statistics would be calculated here",
                        "averageSalary", "Statistics would be calculated here"
                ),
                "message", "Payroll statistics for the specified period"
        );
        return response("Payroll statistics retrieved successfully", stats);
    }

    @GetMapping("/recent-activities")
    @Operation(summary = "Get recent activities", description = "Retrieves recent system activities")
    public ResponseEntity<ApiResponse<Object>> getRecentActivities(
            @Parameter(description = "Number of activities to retrieve") @RequestParam(defaultValue = "10") int limit) {

        var activities = Map.of(
                "activities", "Recent activities would be listed here",
                "limit", limit,
                "lastUpdated", LocalDateTime.now(),
                "message", "Recent activities retrieved successfully"
        );
        return response("Recent activities retrieved successfully", activities);
    }

    @GetMapping("/quick-stats")
    @Operation(summary = "Get quick statistics", description = "Retrieves quick overview statistics for dashboard widgets")
    public ResponseEntity<ApiResponse<Object>> getQuickStats() {
        var quickStats = Map.of(
                "todayAttendance", Map.of(
                        "present", "To be calculated",
                        "absent", "To be calculated",
                        "late", "To be calculated"
                ),
                "thisMonthPayroll", Map.of(
                        "processed", "To be calculated",
                        "pending", "To be calculated",
                        "totalAmount", "To be calculated"
                ),
                "employeeOverview", Map.of(
                        "total", employeeService.getEmployeeCountByStatus(EmployeeStatus.ACTIVE),
                        "newThisMonth", "To be calculated",
                        "birthdays", "To be calculated"
                ),
                "lastUpdated", LocalDateTime.now()
        );
        return response("Quick statistics retrieved successfully", quickStats);
    }
}
