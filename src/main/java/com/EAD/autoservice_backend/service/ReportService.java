package com.EAD.autoservice_backend.service;

import com.EAD.autoservice_backend.dto.ReportDTO;
import com.EAD.autoservice_backend.repository.AppointmentJobRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ReportService {

    @Autowired
    private AppointmentJobRepository appointmentJobRepository;

    public List<ReportDTO> getServicePopularity() {
        return appointmentJobRepository.getServicePopularityReport();
    }

    // You will add more methods here, like getEmployeePerformance()
}