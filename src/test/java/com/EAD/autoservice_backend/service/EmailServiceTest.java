package com.EAD.autoservice_backend.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mail.MailSendException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class EmailServiceTest {

    @Mock
    private JavaMailSender mailSender;

    @InjectMocks
    private EmailService emailService;

    @Test
    void sendOTPEmail_success_sendsExpectedMessage() {
        String to = "user@example.com";
        String otp = "123456";

        emailService.sendOTPEmail(to, otp);

        ArgumentCaptor<SimpleMailMessage> captor = ArgumentCaptor.forClass(SimpleMailMessage.class);
        verify(mailSender, times(1)).send(captor.capture());

        SimpleMailMessage msg = captor.getValue();
        assertNotNull(msg);
        assertArrayEquals(new String[]{to}, msg.getTo());
        assertEquals("Password Reset OTP - AutoService", msg.getSubject());
        assertNotNull(msg.getText());
        assertTrue(msg.getText().contains(otp));
    }

    @Test
    void sendOTPEmail_failure_wrapsException() {
        doThrow(new MailSendException("SMTP error"))
                .when(mailSender).send(any(SimpleMailMessage.class));

        RuntimeException ex = assertThrows(RuntimeException.class,
                () -> emailService.sendOTPEmail("user@example.com", "000000"));

        assertTrue(ex.getMessage().startsWith("Failed to send email: "));
    }
}
