package com.EAD.autoservice_backend.service;

import com.EAD.autoservice_backend.dto.UserDTO;
import com.EAD.autoservice_backend.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserService userService;

    private UserDTO user1;
    private UserDTO user2;

    @BeforeEach
    void setUp() {
        user1 = new UserDTO(1L, "john@example.com", "1234567890");
        user2 = new UserDTO(2L, "jane@example.com", "9876543210");
    }

    @Test
    void testGetAllCustomersBasicInfo() {
       
        when(userRepository.findAllCustomersBasicInfo()).thenReturn(Arrays.asList(user1, user2));

        
        List<UserDTO> result = userService.getAllCustomersBasicInfo();

        assertEquals(2, result.size());
        assertEquals(1L, result.get(0).getCustomerId());
        assertEquals("john@example.com", result.get(0).getEmail());
        assertEquals("1234567890", result.get(0).getMobile());

        assertEquals(2L, result.get(1).getCustomerId());
        assertEquals("jane@example.com", result.get(1).getEmail());
        assertEquals("9876543210", result.get(1).getMobile());
    }
}
