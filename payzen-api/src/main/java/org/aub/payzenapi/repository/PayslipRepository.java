package org.aub.payzenapi.repository;

import org.aub.payzenapi.model.entity.Employee;
import org.aub.payzenapi.model.entity.Payslip;
import org.aub.payzenapi.model.enums.PayslipStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface PayslipRepository extends JpaRepository<Payslip, UUID> {

    List<Payslip> findByEmployee(Employee employee);

    List<Payslip> findByEmployeeAndStatus(Employee employee, PayslipStatus status);

    Optional<Payslip> findByEmployeeAndPayPeriodStartAndPayPeriodEnd(Employee employee,
                                                                     LocalDateTime payPeriodStart,
                                                                     LocalDateTime payPeriodEnd);

    @Query("SELECT p FROM Payslip p WHERE " +
           "(:employeeId IS NULL OR p.employee.employeeId = :employeeId) AND " +
           "(:startDate IS NULL OR p.payPeriodStart >= :startDate) AND " +
           "(:endDate IS NULL OR p.payPeriodEnd <= :endDate) AND " +
           "(:status IS NULL OR p.status = :status)")
    Page<Payslip> findPayslipsWithFilters(@Param("employeeId") UUID employeeId,
                                          @Param("startDate") LocalDateTime startDate,
                                          @Param("endDate") LocalDateTime endDate,
                                          @Param("status") PayslipStatus status,
                                          Pageable pageable);

    List<Payslip> findByStatus(PayslipStatus status);
}
