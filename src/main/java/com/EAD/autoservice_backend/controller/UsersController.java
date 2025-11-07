package com.EAD.autoservice_backend.controller;

import com.EAD.autoservice_backend.dto.UserDTO;
import com.EAD.autoservice_backend.service.UserService;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/users")
@CrossOrigin(origins = "*")
public class UsersController {

    private final UserService userService;

    public UsersController(UserService userService) {
        this.userService = userService;
    }

    //  GET all customer emails, phone numbers, and IDs
    @GetMapping("/customers")
    public List<UserDTO> getAllCustomerInfo() {
        return userService.getAllCustomersBasicInfo();
    }
}
