package com.EAD.autoservice_backend.dto;

import lombok.*;

@Setter
@Getter
@Data
@NoArgsConstructor
public class UserDTO {
    // getters and setters
    private Long customerId;
    private String email;
    private String mobile;

    public UserDTO(Long id, String email, String phoneNumber) {
        this.customerId = id;
        this.email = email;
        this.mobile = phoneNumber;
    }

}