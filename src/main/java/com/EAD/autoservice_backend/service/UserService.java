package com.EAD.autoservice_backend.service;

import com.EAD.autoservice_backend.dto.UserDTO;
import com.EAD.autoservice_backend.repository.UserRepository;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class UserService {

    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public List<UserDTO> getAllCustomersBasicInfo() {
        return userRepository.findAllCustomersBasicInfo();
    }
}
