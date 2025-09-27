package com.EAD.autoservice_backend.repository;

import com.EAD.autoservice_backend.dto.ReportDTO;
import com.EAD.autoservice_backend.model.AppointmentJob;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AppointmentJobRepository extends JpaRepository<AppointmentJob, Long> {

    // This is your data science query!
    // It groups all the "AppointmentJob" entries by the service name,
    // counts how many are in each group, and returns a list of ReportDTOs.
    @Query("SELECT new com.EAD.autoservice_backend.dto.ReportDTO(s.serviceItemName, COUNT(aj)) " +
            "FROM AppointmentJob aj " +
            "JOIN aj.serviceItem s " +
            "GROUP BY s.serviceItemName " +
            "ORDER BY COUNT(aj) DESC")
    List<ReportDTO> getServicePopularityReport();

    // We can add more reports here later
}