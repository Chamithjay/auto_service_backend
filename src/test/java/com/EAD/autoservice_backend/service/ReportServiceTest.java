package com.EAD.autoservice_backend.service;

import com.EAD.autoservice_backend.dto.EmployeePerformance;
import com.EAD.autoservice_backend.dto.LeaveReport;
import com.EAD.autoservice_backend.dto.RevenueOverTime;
import com.EAD.autoservice_backend.dto.ServicePopularity;
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
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.never;

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
            @Override
            public String getEmployeeName() {
                return "Sam";
            }

            @Override
            public Long getTotalJobs() {
                return 12L;
            }

            @Override
            public Double getTotalHours() {
                return 48.0;
            }
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
    void getServicePopularity_emptyResult() {
        when(appointmentJobRepository.getServicePopularity(any(LocalDate.class), any(LocalDate.class)))
                .thenReturn(java.util.Collections.emptyList());

        var result = reportService.getServicePopularity(7);

        assertTrue(result.isEmpty());
        verify(appointmentJobRepository).getServicePopularity(any(LocalDate.class), any(LocalDate.class));
    }

    @Test
    void getServicePopularity_multipleServices() {
        List<ServicePopularity> expected = List.of(
                new ServicePopularity("Oil Change", 5L),
                new ServicePopularity("Brake Inspection", 3L),
                new ServicePopularity("Wheel Alignment", 2L));
        when(appointmentJobRepository.getServicePopularity(any(LocalDate.class), any(LocalDate.class)))
                .thenReturn(expected);

        var result = reportService.getServicePopularity(30);

        assertEquals(3, result.size());
        assertEquals("Oil Change", result.get(0).getServiceName());
        assertEquals(5L, result.get(0).getCount());
    }

    @Test
    void getRevenueOverTime_multipleEntries() {
        LocalDate today = LocalDate.now();
        List<RevenueOverTime> expected = List.of(
                new RevenueOverTime(today.minusDays(2), new BigDecimal("100.00")),
                new RevenueOverTime(today.minusDays(1), new BigDecimal("150.50")),
                new RevenueOverTime(today, new BigDecimal("200.75")));
        when(appointmentRepository.getRevenueOverTime(any(LocalDate.class), any(LocalDate.class)))
                .thenReturn(expected);

        var result = reportService.getRevenueOverTime(3);

        assertEquals(3, result.size());
        assertEquals(new BigDecimal("100.00"), result.get(0).getTotalRevenue());
        assertEquals(new BigDecimal("200.75"), result.get(2).getTotalRevenue());
    }

    @Test
    void getRevenueOverTime_emptyResult() {
        when(appointmentRepository.getRevenueOverTime(any(LocalDate.class), any(LocalDate.class)))
                .thenReturn(java.util.Collections.emptyList());

        var result = reportService.getRevenueOverTime(7);

        assertTrue(result.isEmpty());
    }

    @Test
    void getLeaveReport_emptyResult() {
        when(leaveRepository.getLeaveReport(any(LocalDate.class), any(LocalDate.class)))
                .thenReturn(java.util.Collections.emptyList());

        var result = reportService.getLeaveReport(30);

        assertTrue(result.isEmpty());
    }

    @Test
    void getLeaveReport_multipleLeaveTypes() {
        List<LeaveReport> expected = List.of(
                new LeaveReport(LeaveType.FULLDAY, 10L),
                new LeaveReport(LeaveType.HALFDAY_MORNING, 5L),
                new LeaveReport(LeaveType.HALFDAY_EVENING, 2L));
        when(leaveRepository.getLeaveReport(any(LocalDate.class), any(LocalDate.class)))
                .thenReturn(expected);

        var result = reportService.getLeaveReport(90);

        assertEquals(3, result.size());
        assertEquals(LeaveType.FULLDAY, result.get(0).getLeaveType());
        assertEquals(10L, result.get(0).getCount());
    }

    @Test
    void getEmployeePerformance_emptyResult() {
        when(jobAssignmentRepository.getEmployeePerformance(any(LocalDate.class), any(LocalDate.class)))
                .thenReturn(java.util.Collections.emptyList());

        var result = reportService.getEmployeePerformance(14);

        assertTrue(result.isEmpty());
    }

    @Test
    void getEmployeePerformance_multipleEmployees() {
        EmployeePerformance emp1 = new EmployeePerformance() {
            @Override
            public String getEmployeeName() {
                return "Alice";
            }

            @Override
            public Long getTotalJobs() {
                return 10L;
            }

            @Override
            public Double getTotalHours() {
                return 40.0;
            }
        };
        EmployeePerformance emp2 = new EmployeePerformance() {
            @Override
            public String getEmployeeName() {
                return "Bob";
            }

            @Override
            public Long getTotalJobs() {
                return 8L;
            }

            @Override
            public Double getTotalHours() {
                return 32.0;
            }
        };
        List<EmployeePerformance> expected = List.of(emp1, emp2);
        when(jobAssignmentRepository.getEmployeePerformance(any(LocalDate.class), any(LocalDate.class)))
                .thenReturn(expected);

        var result = reportService.getEmployeePerformance(30);

        assertEquals(2, result.size());
        assertEquals("Alice", result.get(0).getEmployeeName());
        assertEquals(10L, result.get(0).getTotalJobs());
    }

    @Test
    void getServicePopularity_boundaryRange_oneDay() {
        when(appointmentJobRepository.getServicePopularity(any(LocalDate.class), any(LocalDate.class)))
                .thenReturn(List.of(new ServicePopularity("Test", 1L)));

        var result = reportService.getServicePopularity(1);

        assertNotNull(result);
        verify(appointmentJobRepository).getServicePopularity(any(LocalDate.class), any(LocalDate.class));
    }

    @Test
    void getServicePopularity_boundaryRange_maxDays() {
        when(appointmentJobRepository.getServicePopularity(any(LocalDate.class), any(LocalDate.class)))
                .thenReturn(List.of(new ServicePopularity("Test", 100L)));

        var result = reportService.getServicePopularity(365);

        assertNotNull(result);
        assertEquals(100L, result.get(0).getCount());
    }

    @Test
    void getRevenueOverTime_largeRevenue() {
        LocalDate today = LocalDate.now();
        List<RevenueOverTime> expected = List.of(
                new RevenueOverTime(today, new BigDecimal("99999.99")));
        when(appointmentRepository.getRevenueOverTime(any(LocalDate.class), any(LocalDate.class)))
                .thenReturn(expected);

        var result = reportService.getRevenueOverTime(1);

        assertEquals(new BigDecimal("99999.99"), result.get(0).getTotalRevenue());
    }

    @Test
    void getRevenueOverTime_zeroRevenue() {
        LocalDate today = LocalDate.now();
        List<RevenueOverTime> expected = List.of(
                new RevenueOverTime(today, BigDecimal.ZERO));
        when(appointmentRepository.getRevenueOverTime(any(LocalDate.class), any(LocalDate.class)))
                .thenReturn(expected);

        var result = reportService.getRevenueOverTime(1);

        assertEquals(BigDecimal.ZERO, result.get(0).getTotalRevenue());
    }

    @Test
    void getLeaveReport_invalidRange_null() {
        assertThrows(BadRequestException.class, () -> reportService.getLeaveReport(null));
        verify(leaveRepository, never()).getLeaveReport(any(), any());
    }

    @Test
    void getLeaveReport_invalidRange_negative() {
        assertThrows(BadRequestException.class, () -> reportService.getLeaveReport(-5));
        verify(leaveRepository, never()).getLeaveReport(any(), any());
    }

    @Test
    void getLeaveReport_invalidRange_tooLarge() {
        assertThrows(BadRequestException.class, () -> reportService.getLeaveReport(400));
        verify(leaveRepository, never()).getLeaveReport(any(), any());
    }

    @Test
    void getEmployeePerformance_invalidRange_null() {
        assertThrows(BadRequestException.class, () -> reportService.getEmployeePerformance(null));
        verify(jobAssignmentRepository, never()).getEmployeePerformance(any(), any());
    }

    @Test
    void getEmployeePerformance_invalidRange_tooLarge() {
        assertThrows(BadRequestException.class, () -> reportService.getEmployeePerformance(400));
        verify(jobAssignmentRepository, never()).getEmployeePerformance(any(), any());
    }

    @Test
    void getRevenueOverTime_invalidRange_null() {
        assertThrows(BadRequestException.class, () -> reportService.getRevenueOverTime(null));
        verify(appointmentRepository, never()).getRevenueOverTime(any(), any());
    }

    @Test
    void getRevenueOverTime_invalidRange_negative() {
        assertThrows(BadRequestException.class, () -> reportService.getRevenueOverTime(-10));
        verify(appointmentRepository, never()).getRevenueOverTime(any(), any());
    }

    @Test
    void getRevenueOverTime_invalidRange_tooLarge() {
        assertThrows(BadRequestException.class, () -> reportService.getRevenueOverTime(500));
        verify(appointmentRepository, never()).getRevenueOverTime(any(), any());
    }
}
