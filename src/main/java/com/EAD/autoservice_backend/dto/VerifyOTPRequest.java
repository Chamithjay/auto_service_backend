package com.EAD.autoservice_backend.dto;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class VerifyOTPRequest {
    private String email;
    private String otp;

}
