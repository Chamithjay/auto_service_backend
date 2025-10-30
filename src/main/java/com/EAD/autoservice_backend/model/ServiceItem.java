package com.EAD.autoservice_backend.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.AllArgsConstructor;

import java.math.BigDecimal;
import java.util.Set;

@Entity
@Table(name = "services_and_modifications") // ✅ fixed invalid table name (no spaces)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ServiceItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long serviceItemId;

    @Column(nullable = false, length = 255)
    private String serviceItemName;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 255)
    private VehicleType vehicleType;

    @Column(nullable = false)
    private Integer requiredEmployeeCount;

    @Column(precision = 17, scale = 2)
    private BigDecimal serviceItemCost;

    @Enumerated(EnumType.STRING)
    @Column(name = "service_item_type", nullable = false)
    private ServiceItemType serviceItemType;

    @Column(name = "estimated_duration_minutes", nullable = false)
    private Integer estimatedDuration;

    @OneToMany(mappedBy = "serviceItem")
    private Set<AppointmentJob> bookedItems;
}
