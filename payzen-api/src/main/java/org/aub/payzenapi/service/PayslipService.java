package org.aub.payzenapi.service;

import org.aub.payzenapi.model.dto.response.PayslipResponse;
import org.aub.payzenapi.model.enums.PayslipStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public interface PayslipService {

    PayslipResponse generatePayslip(UUID employeeId, LocalDateTime payPeriodStart, LocalDateTime payPeriodEnd);

    PayslipResponse getPayslipById(UUID payslipId);

    Page<PayslipResponse> getPayslips(UUID employeeId, LocalDateTime startDate,
                                      LocalDateTime endDate, PayslipStatus status,
                                      Pageable pageable);

    List<PayslipResponse> getPayslipsByEmployee(UUID employeeId);

    PayslipResponse updatePayslipStatus(UUID payslipId, PayslipStatus status);
}
