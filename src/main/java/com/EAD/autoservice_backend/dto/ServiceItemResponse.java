package com.EAD.autoservice_backend.dto;

import lombok.Value;

@Value
public class ServiceItemResponse {
    private final Long serviceItemId;
    private final String serviceItemName;
}
