package com.EAD.autoservice_backend.repository;

import com.EAD.autoservice_backend.model.JobAssignment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface JobAssignmentRepository extends JpaRepository<JobAssignment,Long> {


    @Query("SELECT j FROM JobAssignment j WHERE j.appointmentJob.appointmentJobId = :appointmentJobId")
    List<JobAssignment> findByAppointmentJobId(long appointmentJobId);
}
