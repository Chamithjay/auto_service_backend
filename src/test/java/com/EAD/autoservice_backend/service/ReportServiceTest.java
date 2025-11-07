package com.EAD.autoservice_backend.service;

import com.EAD.autoservice_backend.dto.reports.EmployeePerformance;
import com.EAD.autoservice_backend.dto.reports.LeaveReport;
import com.EAD.autoservice_backend.dto.reports.RevenueOverTime;
import com.EAD.autoservice_backend.dto.reports.ServicePopularity;
import com.EAD.autoservice_backend.exception.BadRequestException;
import com.EAD.autoservice_backend.model.LeaveType;
import com.EAD.autoservice_backend.repository.AppointmentJobRepository;
import com.EAD.autoservice_backend.repository.AppointmentRepository;
import com.EAD.autoservice_backend.repository.JobAssignmentRepository;
import com.EAD.autoservice_backend.repository.LeaveRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ReportServiceTest {

    @Mock
    private AppointmentJobRepository appointmentJobRepository;

    @Mock
    private JobAssignmentRepository jobAssignmentRepository;

    @Mock
    private AppointmentRepository appointmentRepository;

    @Mock
    private LeaveRepository leaveRepository;

    @InjectMocks
    private ReportService reportService;

    @Test
    void getServicePopularity_returnsRepositoryResult_andComputesRange() {
        int range = 7;
        List<ServicePopularity> expected = List.of(new ServicePopularity("Oil Change", 5L));
        when(appointmentJobRepository.getServicePopularity(any(LocalDate.class), any(LocalDate.class)))
                .thenReturn(expected);

        var result = reportService.getServicePopularity(range);
        assertEquals(expected, result);

        ArgumentCaptor<LocalDate> startCaptor = ArgumentCaptor.forClass(LocalDate.class);
        ArgumentCaptor<LocalDate> endCaptor = ArgumentCaptor.forClass(LocalDate.class);
        verify(appointmentJobRepository, times(1)).getServicePopularity(startCaptor.capture(), endCaptor.capture());
        LocalDate passedEnd = endCaptor.getValue();
        LocalDate passedStart = startCaptor.getValue();
        assertEquals(passedEnd.minusDays(range), passedStart);
        assertEquals(LocalDate.now(), passedEnd);
    }

    @Test
    void getEmployeePerformance_returnsRepositoryResult_andComputesRange() {
        int range = 30;
        EmployeePerformance ep = new EmployeePerformance() {
            @Override public String getEmployeeName() { return "Sam"; }
            @Override public Long getTotalJobs() { return 12L; }
            @Override public Double getTotalHours() { return 48.0; }
        };
        List<EmployeePerformance> expected = List.of(ep);
        when(jobAssignmentRepository.getEmployeePerformance(any(LocalDate.class), any(LocalDate.class)))
                .thenReturn(expected);

        var result = reportService.getEmployeePerformance(range);
        assertEquals(expected, result);

        ArgumentCaptor<LocalDate> startCaptor = ArgumentCaptor.forClass(LocalDate.class);
        ArgumentCaptor<LocalDate> endCaptor = ArgumentCaptor.forClass(LocalDate.class);
        verify(jobAssignmentRepository, times(1)).getEmployeePerformance(startCaptor.capture(), endCaptor.capture());
        LocalDate passedEnd = endCaptor.getValue();
        LocalDate passedStart = startCaptor.getValue();
        assertEquals(passedEnd.minusDays(range), passedStart);
        assertEquals(LocalDate.now(), passedEnd);
    }

    @Test
    void getRevenueOverTime_returnsRepositoryResult_andComputesRange() {
        int range = 14;
        RevenueOverTime r1 = new RevenueOverTime(LocalDate.now().minusDays(1), new BigDecimal("150.00"));
        List<RevenueOverTime> expected = List.of(r1);
        when(appointmentRepository.getRevenueOverTime(any(LocalDate.class), any(LocalDate.class)))
                .thenReturn(expected);

        var result = reportService.getRevenueOverTime(range);
        assertEquals(expected, result);

        ArgumentCaptor<LocalDate> startCaptor = ArgumentCaptor.forClass(LocalDate.class);
        ArgumentCaptor<LocalDate> endCaptor = ArgumentCaptor.forClass(LocalDate.class);
        verify(appointmentRepository, times(1)).getRevenueOverTime(startCaptor.capture(), endCaptor.capture());
        LocalDate passedEnd = endCaptor.getValue();
        LocalDate passedStart = startCaptor.getValue();
        assertEquals(passedEnd.minusDays(range), passedStart);
        assertEquals(LocalDate.now(), passedEnd);
    }

    @Test
    void getLeaveReport_returnsRepositoryResult_andComputesRange() {
        int range = 3;
        LeaveReport lr = new LeaveReport(LeaveType.FULLDAY, 2L);
        List<LeaveReport> expected = List.of(lr);
        when(leaveRepository.getLeaveReport(any(LocalDate.class), any(LocalDate.class)))
                .thenReturn(expected);

        var result = reportService.getLeaveReport(range);
        assertEquals(expected, result);

        ArgumentCaptor<LocalDate> startCaptor = ArgumentCaptor.forClass(LocalDate.class);
        ArgumentCaptor<LocalDate> endCaptor = ArgumentCaptor.forClass(LocalDate.class);
        verify(leaveRepository, times(1)).getLeaveReport(startCaptor.capture(), endCaptor.capture());
        LocalDate passedEnd = endCaptor.getValue();
        LocalDate passedStart = startCaptor.getValue();
        assertEquals(passedEnd.minusDays(range), passedStart);
        assertEquals(LocalDate.now(), passedEnd);
    }

    @Test
    void getServicePopularity_invalidRange_negative() {
        assertThrows(BadRequestException.class, () -> reportService.getServicePopularity(-1));
    }

    @Test
    void getServicePopularity_invalidRange_null() {
        assertThrows(BadRequestException.class, () -> reportService.getServicePopularity(null));
    }

    @Test
    void getServicePopularity_invalidRange_tooLarge() {
        assertThrows(BadRequestException.class, () -> reportService.getServicePopularity(366));
    }

    @Test
    void getEmployeePerformance_invalidRange_negative() {
        assertThrows(BadRequestException.class, () -> reportService.getEmployeePerformance(-1));
    }

    @Test
    void getEmployeePerformance_invalidRange_null() {
        assertThrows(BadRequestException.class, () -> reportService.getEmployeePerformance(null));
    }

    @Test
    void getEmployeePerformance_invalidRange_tooLarge() {
        assertThrows(BadRequestException.class, () -> reportService.getEmployeePerformance(366));
    }

    @Test
    void getRevenueOverTime_invalidRange_negative() {
        assertThrows(BadRequestException.class, () -> reportService.getRevenueOverTime(-1));
    }

    @Test
    void getRevenueOverTime_invalidRange_null() {
        assertThrows(BadRequestException.class, () -> reportService.getRevenueOverTime(null));
    }

    @Test
    void getRevenueOverTime_invalidRange_tooLarge() {
        assertThrows(BadRequestException.class, () -> reportService.getRevenueOverTime(366));
    }

    @Test
    void getLeaveReport_invalidRange_negative() {
        assertThrows(BadRequestException.class, () -> reportService.getLeaveReport(-1));
    }

    @Test
    void getLeaveReport_invalidRange_null() {
        assertThrows(BadRequestException.class, () -> reportService.getLeaveReport(null));
    }

    @Test
    void getLeaveReport_invalidRange_tooLarge() {
        assertThrows(BadRequestException.class, () -> reportService.getLeaveReport(366));
    }
}
