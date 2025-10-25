package com.EAD.autoservice_backend.service;

import com.EAD.autoservice_backend.dto.AppointmentJobResponse;
import com.EAD.autoservice_backend.dto.ServiceItemResponse;
import com.EAD.autoservice_backend.dto.VehicleResponse;
import com.EAD.autoservice_backend.model.AppointmentJob;
import com.EAD.autoservice_backend.model.ServiceItem;
import com.EAD.autoservice_backend.model.Vehicle;
import com.EAD.autoservice_backend.repository.AppointmentJobRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class AppointmentJobService {

    private final AppointmentJobRepository appointmentJobRepository;

    public AppointmentJobService(AppointmentJobRepository appointmentJobRepository) {
        this.appointmentJobRepository = appointmentJobRepository;
    }

    public AppointmentJobResponse getAppointmentById(Long appointmentJobId) {

        AppointmentJob appointmentJob = appointmentJobRepository.findById(appointmentJobId)
                .orElseThrow(() -> new EntityNotFoundException("Appointment Job Not Found. Job ID: " + appointmentJobId));

        Vehicle vehicle = appointmentJob.getAppointment().getVehicle();
        ServiceItem serviceItem = appointmentJob.getServiceItem();

        VehicleResponse vehicleDetails = new VehicleResponse(
                vehicle.getVehicleId(),
                vehicle.getRegistrationNo(),
                vehicle.getVehicleType().name(),
                vehicle.getModel()
        );
        ServiceItemResponse serviceItemDetails = new ServiceItemResponse(
                serviceItem.getServiceItemId(),
                serviceItem.getServiceItemName()
        );

        return new AppointmentJobResponse(
                appointmentJob.getAppointmentJobId(),
                appointmentJob.getDescription(),
                appointmentJob.getAdditional_cost(),
                appointmentJob.getJobStatus().name(),
                vehicleDetails,
                serviceItemDetails
        );
    }
}
