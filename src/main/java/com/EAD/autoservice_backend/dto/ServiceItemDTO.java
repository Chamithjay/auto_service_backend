package com.EAD.autoservice_backend.dto;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ServiceItemDTO {
    private Long id;
    private String name;
    private String type; // SERVICE or MODIFICATION
}
