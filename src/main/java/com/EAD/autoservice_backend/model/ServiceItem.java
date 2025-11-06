package com.EAD.autoservice_backend.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;
import java.util.Set;

@Getter
@Setter
@Entity
@Table(name = "services and modifications")
public class ServiceItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long serviceItemId;

    @Column(nullable = false, length = 255)
    private String serviceItemName;

    @Column(nullable = false, length = 255)
    @Enumerated(EnumType.STRING)
    private VehicleType vehicleType;

    @Column(nullable = false)
    private Integer requiredEmployeeCount;

    @Column(precision = 17, scale = 2, nullable = false)
    private BigDecimal serviceItemCost;

    @Column(name = "service_item_type", nullable = false)
    @Enumerated(EnumType.STRING)
    private ServiceItemType serviceItemType;

    @Column(name = "estimated_duration_minutes", nullable = false)
    private Integer estimatedDuration;

    @OneToMany(mappedBy = "serviceItem")
    @JsonIgnore  // Add this line to prevent circular reference
    private Set<AppointmentJob> bookedItems;

}