package com.EAD.autoservice_backend.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class UserDTO {
    private Long customerId;
    private String email;
    private String mobile;

    public UserDTO(Long id, String email, String phoneNumber) {
        this.customerId = id;
        this.email = email;
        this.mobile = phoneNumber;
    }

    // getters and setters
    public Long getCustomerId() { return customerId; }
    public void setCustomerId(Long customerId) { this.customerId = customerId; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getMobile() { return mobile; }
    public void setMobile(String mobile) { this.mobile = mobile; }
}
