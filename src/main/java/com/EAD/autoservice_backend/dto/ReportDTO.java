package com.EAD.autoservice_backend.dto;

// This is a generic DTO to hold the results of our report queries.
// It will hold a name (like "Oil Change") and a number (like "10").
public record ReportDTO(
        String label, // The name of the item
        Long value    // The count or sum
) {}