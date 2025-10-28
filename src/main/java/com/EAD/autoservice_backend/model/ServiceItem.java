package com.EAD.autoservice_backend.model;

import jakarta.persistence.*;

import java.math.BigDecimal;
import java.util.Set;

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

    @Column(precision = 17, scale = 2)
    private BigDecimal serviceItemCost;

    @Column(name = "service_item_type", nullable = false)
    @Enumerated(EnumType.STRING)
    private ServiceItemType serviceItemType;

    @Column(name = "estimated_duration_minutes", nullable = false)
    private Integer estimatedDuration;

    @OneToMany(mappedBy = "serviceItem")
    private Set<AppointmentJob> bookedItems;

}
