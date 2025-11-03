package com.EAD.autoservice_backend.service;

import com.EAD.autoservice_backend.dto.*;
import com.EAD.autoservice_backend.exception.AppointmentJobNotFoundException;
import com.EAD.autoservice_backend.exception.DetailsMissingException;
import com.EAD.autoservice_backend.model.*;
import com.EAD.autoservice_backend.repository.AppointmentJobRepository;
import io.micrometer.common.util.StringUtils;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AppointmentJobService {

    private final AppointmentJobRepository appointmentJobRepository;
    private final JobAssignmentService jobAssignmentService;


    public AppointmentJobService(AppointmentJobRepository appointmentJobRepository, JobAssignmentService jobAssignmentService) {
        this.appointmentJobRepository = appointmentJobRepository;
        this.jobAssignmentService = jobAssignmentService;
    }


    // Get appointment job by appointment job id.
    public EmployeeAppointmentJobResponse getAppointmentById(Long appointmentJobId) {

       try{
           AppointmentJob appointmentJob = appointmentJobRepository.findById(appointmentJobId)
                   .orElseThrow(() -> new AppointmentJobNotFoundException("Appointment Job Not Found. Job ID: " + appointmentJobId));

           Appointment appointment = appointmentJob.getAppointment();
           if (appointment == null) {
               throw new DetailsMissingException("Appointment not found for Appointment Job ID: " + appointmentJobId);
           }
           Vehicle vehicle = appointment.getVehicle();
           if (vehicle == null) {
               throw new DetailsMissingException("Vehicle not found for Appointment ID: " + appointment.getAppointmentId());
           }
           ServiceItem serviceItem = appointmentJob.getServiceItem();
           if (serviceItem == null) {
               throw new DetailsMissingException("Service Item not found for Appointment Job ID: " + appointmentJobId);
           }
           Customer customer = vehicle.getCustomer();
           if (customer == null) {
               throw new DetailsMissingException("Customer details not found for the vehicle: " + vehicle.getVehicleId());
           }


           List<EmployeeJobAssignmentResponse> jobAssignmentList = jobAssignmentService.getJobAssignmentListByAppointmentJobId(appointmentJobId);

           EmployeeVehicleResponse vehicleDetails = new EmployeeVehicleResponse(
                   vehicle.getRegistrationNo(),
                   vehicle.getVehicleType().name(),
                   vehicle.getModel()
           );
           EmployeeServiceItemResponse serviceItemDetails = new EmployeeServiceItemResponse(
                   serviceItem.getServiceItemId(),
                   serviceItem.getServiceItemName(),
                   serviceItem.getEstimatedDuration()
           );
           EmployeeCustomerDetailsResponse customerDetails = new EmployeeCustomerDetailsResponse(
                   customer.getUsername(),
                   customer.getPhoneNumber(),
                   customer.getEmail()
           );

           return new EmployeeAppointmentJobResponse(
                   appointmentJob.getAppointmentJobId(),
                   appointmentJob.getJobNote(),
                   appointmentJob.getAdditional_cost(),
                   appointmentJob.getJobStatus().name(),
                   vehicleDetails,
                   serviceItemDetails,
                   customerDetails,
                   jobAssignmentList
           );
       } catch (DetailsMissingException | AppointmentJobNotFoundException e) {
           throw new RuntimeException(e);
       } catch (Exception e) {
           throw new RuntimeException("Failed to get Appointment Job: " + e.getMessage(), e);
       }
    }

    // Update appointment job status.
    public String updateAppointmentJobStatus(Long appointmentJobId, String jobStatus) {
        AppointmentJob appointmentJob = appointmentJobRepository.findById(appointmentJobId)
                .orElseThrow(() -> new AppointmentJobNotFoundException("Appointment Job Not Found. Job ID: " + appointmentJobId));

        try {
            if (jobStatus == null) {
                throw new IllegalArgumentException("Job Status cannot be empty");
            }
            Status newStatus = Status.valueOf(jobStatus.toUpperCase());
            appointmentJob.setJobStatus(newStatus);

            appointmentJobRepository.save(appointmentJob);

            return "Successfully updated Appointment Job to " + newStatus.name();

        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Invalid Job Status: " + jobStatus
                + ". Valid values are: "
                + java.util.Arrays.stream(Status.values())
                    .map(Enum::name)
                    .reduce((a, b) -> a + ", " + b).orElse(""));
        }catch (AppointmentJobNotFoundException e) {
            throw new RuntimeException(e.getMessage());
        } catch (Exception e) {
            throw new RuntimeException("Failed to update Appointment Job Status: " + e.getMessage());
        }

    }

    // Save job note for an appointment job.
    public EmployeeAppointmentJobResponse saveJobNoteForAppointmentJob(Long appointmentJobId, EmployeeJobNoteRequest employeeJobNoteRequest) {
        try {
            AppointmentJob appointmentJob = appointmentJobRepository.findById(appointmentJobId)
                    .orElseThrow(() -> new AppointmentJobNotFoundException("Appointment Job Not Found. Job ID: " + appointmentJobId));

            if (StringUtils.isBlank(employeeJobNoteRequest.getJobNote())) {
                throw new IllegalArgumentException("Job note cannot be empty or blank");
            }

            String existingNote = appointmentJob.getJobNote();
            if (existingNote == null) {
                appointmentJob.setJobNote(employeeJobNoteRequest.getJobNote() + ".");
            } else {
                String updatedNote = existingNote + "\n" + employeeJobNoteRequest.getJobNote() + ".";
                appointmentJob.setJobNote(updatedNote);
            }
            appointmentJobRepository.save(appointmentJob);

            return getAppointmentById(appointmentJobId);

        }catch (AppointmentJobNotFoundException | IllegalArgumentException e){
            throw new RuntimeException(e.getMessage());
        }catch (Exception e){
            throw new RuntimeException("Failed to save job note for Appointment Job: " + e.getMessage());
        }

    }
}
