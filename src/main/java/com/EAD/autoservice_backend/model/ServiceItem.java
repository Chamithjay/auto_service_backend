package com.EAD.autoservice_backend.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;
import java.util.Set;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "service_items")
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
    @JsonIgnore  // Add this line to prevent circular reference
    private Set<AppointmentJob> bookedItems;
}
