package com.EAD.autoservice_backend.service;

import com.EAD.autoservice_backend.dto.CustomerProfileResponse;
import com.EAD.autoservice_backend.dto.UpdateProfileRequest;
import com.EAD.autoservice_backend.exception.ResourceNotFoundException;
import com.EAD.autoservice_backend.exception.UserAlreadyExistsException;
import com.EAD.autoservice_backend.model.Customer;
import com.EAD.autoservice_backend.repository.CustomerRepository;
import com.EAD.autoservice_backend.util.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Service for customer profile operations
 */
@Service
@Transactional
public class CustomerProfileService {

    private final CustomerRepository customerRepository;
    private final JwtUtil jwtUtil;
    private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    @Autowired
    public CustomerProfileService(CustomerRepository customerRepository, JwtUtil jwtUtil) {
        this.customerRepository = customerRepository;
        this.jwtUtil = jwtUtil;
    }

    /**
     * Get customer profile by username
     */
    public CustomerProfileResponse getCustomerProfile(String username) {
        Customer customer = customerRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("Customer not found with username: " + username));

        return mapToProfileResponse(customer);
    }

    /**
     * Update customer profile
     */
    public CustomerProfileResponse updateCustomerProfile(String username, UpdateProfileRequest request) {
        Customer customer = customerRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("Customer not found with username: " + username));

        boolean usernameChanged = false;
        
        // Check if username is being changed and if new username already exists
        if (request.getUsername() != null && !request.getUsername().equals(customer.getUsername())) {
            if (customerRepository.existsByUsernameAndIdNot(request.getUsername(), customer.getId())) {
                throw new UserAlreadyExistsException("Username '" + request.getUsername() + "' is already taken");
            }
            customer.setUsername(request.getUsername());
            usernameChanged = true;
        }

        // Check if email is being changed and if new email already exists
        if (request.getEmail() != null && !request.getEmail().equals(customer.getEmail())) {
            if (customerRepository.existsByEmailAndIdNot(request.getEmail(), customer.getId())) {
                throw new UserAlreadyExistsException("Email '" + request.getEmail() + "' is already registered");
            }
            customer.setEmail(request.getEmail());
        }

        // Update phone number if provided
        if (request.getPhoneNumber() != null) {
            customer.setPhoneNumber(request.getPhoneNumber());
        }

        customer.setUpdatedAt(LocalDateTime.now());
        Customer updatedCustomer = customerRepository.save(customer);

        // Generate new token if username was changed
        CustomerProfileResponse response = mapToProfileResponse(updatedCustomer);
        if (usernameChanged) {
            String newToken = jwtUtil.generateToken(
                updatedCustomer.getUsername(), 
                updatedCustomer.getId(), 
                updatedCustomer.getEmail(), 
                "CUSTOMER"
            );
            response.setToken(newToken);
        }
        
        return response;
    }

    /**
     * Map Customer entity to CustomerProfileResponse DTO
     */
    private CustomerProfileResponse mapToProfileResponse(Customer customer) {
        return new CustomerProfileResponse(
                customer.getId(),
                customer.getUsername(),
                customer.getEmail(),
                customer.getPhoneNumber(),
                customer.getCreatedAt().format(DATE_TIME_FORMATTER),
                customer.getUpdatedAt().format(DATE_TIME_FORMATTER)
        );
    }
}