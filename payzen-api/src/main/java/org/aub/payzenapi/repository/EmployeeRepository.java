package org.aub.payzenapi.repository;

import org.aub.payzenapi.model.entity.Employee;
import org.aub.payzenapi.model.enums.EmployeeStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface EmployeeRepository extends JpaRepository<Employee, UUID> {

    Optional<Employee> findByEmail(String email);

    Optional<Employee> findByEmployeeCode(String employeeCode);

    List<Employee> findByStatus(EmployeeStatus status);

    List<Employee> findByDepartment(String department);

    @Query("SELECT e FROM Employee e WHERE " +
           "(:name IS NULL OR LOWER(CONCAT(e.firstName, ' ', e.lastName)) LIKE LOWER(CONCAT('%', :name, '%'))) AND " +
           "(:department IS NULL OR LOWER(e.department) LIKE LOWER(CONCAT('%', :department, '%'))) AND " +
           "(:status IS NULL OR e.status = :status)")
    Page<Employee> findEmployeesWithFilters(@Param("name") String name,
                                            @Param("department") String department,
                                            @Param("status") EmployeeStatus status,
                                            Pageable pageable);

    @Query("SELECT COUNT(e) FROM Employee e WHERE e.status = :status")
    Long countByStatus(@Param("status") EmployeeStatus status);

    boolean existsByEmail(String email);

    boolean existsByEmployeeCode(String employeeCode);
}
