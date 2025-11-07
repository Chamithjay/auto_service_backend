package com.EAD.autoservice_backend.service;

import com.EAD.autoservice_backend.dto.*;
import com.EAD.autoservice_backend.exception.AppointmentJobNotFoundException;
import com.EAD.autoservice_backend.exception.DetailsMissingException;
import com.EAD.autoservice_backend.model.*;
import com.EAD.autoservice_backend.repository.AppointmentJobRepository;
import io.micrometer.common.util.StringUtils;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Service class for managing appointment jobs.
 * Handles operations related to appointment jobs including retrieval, status updates, and job notes.
 */
@Service
public class AppointmentJobService {

    private final AppointmentJobRepository appointmentJobRepository;
    private final JobAssignmentService jobAssignmentService;

    /**
     * Constructs an AppointmentJobService with the required dependencies.
     *
     * @param appointmentJobRepository repository for appointment job operations
     * @param jobAssignmentService service for job assignment operations
     */
    public AppointmentJobService(AppointmentJobRepository appointmentJobRepository, JobAssignmentService jobAssignmentService) {
        this.appointmentJobRepository = appointmentJobRepository;
        this.jobAssignmentService = jobAssignmentService;
    }

    /**
     * Retrieves an appointment job by its ID with all related details.
     *
     * @param appointmentJobId the appointment job ID
     * @return the appointment job response with vehicle, service item, customer, and assignment details
     * @throws AppointmentJobNotFoundException if appointment job is not found
     * @throws DetailsMissingException if required related data is missing
     * @throws RuntimeException if an unexpected error occurs
     */
    public EmployeeAppointmentJobResponse getAppointmentJobById(Long appointmentJobId)  {
       try{
           AppointmentJob appointmentJob = appointmentJobRepository.findByIdWithDetails(appointmentJobId)
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
                   appointmentJob.getId(),
                   appointmentJob.getJobNote(),
                   appointmentJob.getAdditionalCost(),
                   appointmentJob.getItemStatus().name(),
                   vehicleDetails,
                   serviceItemDetails,
                   customerDetails,
                   jobAssignmentList
           );
       }catch (AppointmentJobNotFoundException |DetailsMissingException e){
           throw e;
        }
       catch (Exception e) {
           throw new RuntimeException("Failed to get Appointment Job: " + e.getMessage());
       }
    }

    /**
     * Updates the status of an appointment job.
     *
     * @param appointmentJobId the appointment job ID
     * @param jobStatus the new status to set
     * @return success message with the new status
     * @throws AppointmentJobNotFoundException if appointment job is not found
     * @throws IllegalArgumentException if job status is invalid
     * @throws RuntimeException if an unexpected error occurs
     */
    public String updateAppointmentJobStatus(Long appointmentJobId, String jobStatus) {
        AppointmentJob appointmentJob = appointmentJobRepository.findById(appointmentJobId)
                .orElseThrow(() -> new AppointmentJobNotFoundException("Appointment Job Not Found. Job ID: " + appointmentJobId));

        try {
            if (jobStatus == null) {
                throw new IllegalArgumentException();
            }
            AppointmentStatus newStatus = AppointmentStatus.valueOf(jobStatus.toUpperCase());
            appointmentJob.setItemStatus(newStatus);

            appointmentJobRepository.save(appointmentJob);

            return "Successfully updated Appointment Job to " + newStatus.name();

        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Invalid Job Status: " + jobStatus
                + ". Valid values are: "
                + java.util.Arrays.stream(AppointmentStatus.values())
                    .map(Enum::name)
                    .reduce((a, b) -> a + ", " + b).orElse(""));
        }catch (AppointmentJobNotFoundException e) {
            throw e;
        } catch (Exception e) {
            throw new RuntimeException("Failed to update Appointment Job Status: " + e.getMessage());
        }
    }

    /**
     * Saves a job note for an appointment job.
     * Appends the new note to existing notes if any.
     *
     * @param appointmentJobId the appointment job ID
     * @param employeeJobNoteRequest the job note request containing the note text
     * @return the updated appointment job response
     * @throws AppointmentJobNotFoundException if appointment job is not found
     * @throws IllegalArgumentException if job note is empty or blank
     * @throws RuntimeException if an unexpected error occurs
     */
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

            return getAppointmentJobById(appointmentJobId);

        }catch (AppointmentJobNotFoundException | IllegalArgumentException e){
            throw e;
        }catch (Exception e){
            throw new RuntimeException("Failed to save job note for Appointment Job: " + e.getMessage());
        }

    }
}
