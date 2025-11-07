package com.EAD.autoservice_backend.controller;

import com.EAD.autoservice_backend.dto.UserDTO;
import com.EAD.autoservice_backend.service.UserService;
import org.springframework.web.bind.annotation.*;
import java.util.List;

/**
 * REST controller for user-related operations.
 * Handles retrieval of user information.
 */
@RestController
@RequestMapping("/api/v1/users")
@CrossOrigin(origins = "*")
public class UsersController {

    private final UserService userService;

    public UsersController(UserService userService) {
        this.userService = userService;
    }

    /**
     * Retrieves basic information for all customers.
     *
     * @return list of customer DTOs containing email, phone number, and ID
     */
    @GetMapping("/customers")
    public List<UserDTO> getAllCustomerInfo() {
        return userService.getAllCustomersBasicInfo();
    }
}