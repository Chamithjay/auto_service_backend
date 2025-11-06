package com.EAD.autoservice_backend.dto;

import lombok.*;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ServiceAndModificationResponse {
    private List<ServiceItemDTO> services;
    private List<ServiceItemDTO> modifications;
}
