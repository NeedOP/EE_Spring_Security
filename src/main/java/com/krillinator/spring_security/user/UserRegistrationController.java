package com.krillinator.spring_security.user;

import com.krillinator.spring_security.config.RabbitConfig;
import com.krillinator.spring_security.user.authority.UserRole;
import com.krillinator.spring_security.user.dto.CustomUserCreationDTO;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

@RestController
@RequestMapping("/api")
public class UserRegistrationController {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());
    private final CustomUserRepository customUserRepository;
    private final PasswordEncoder passwordEncoder;
    private final AmqpTemplate amqpTemplate;

    @Autowired
    public UserRegistrationController(CustomUserRepository customUserRepository,
                                      PasswordEncoder passwordEncoder,
                                      AmqpTemplate amqpTemplate) {
        this.customUserRepository = customUserRepository;
        this.passwordEncoder = passwordEncoder;
        this.amqpTemplate = amqpTemplate;
    }

    @PostMapping("/register")
    public ResponseEntity<?> registerUser(@Valid @RequestBody CustomUserCreationDTO dto,
                                          BindingResult bindingResult) {

        logger.info("Registration attempt for username: {}", dto.username());

        // Validation errors
        if (bindingResult.hasErrors()) {
            Map<String, String> errors = new HashMap<>();
            bindingResult.getFieldErrors().forEach(error ->
                    errors.put(error.getField(), error.getDefaultMessage())
            );
            return ResponseEntity.badRequest().body(errors);
        }

        try {
            // Create new user with default USER role
            CustomUser newUser = new CustomUser(
                    dto.username(),
                    passwordEncoder.encode(dto.password()),
                    true, // account non-expired
                    true, // account non-locked
                    true, // credentials non-expired
                    false, // enabled - set to false initially for email verification
                    Set.of(UserRole.USER) // Default role
            );

            customUserRepository.save(newUser);
            logger.info("User registered successfully: {}", dto.username());

            // Send registration email via RabbitMQ
            sendRegistrationEmail(newUser);

            Map<String, Object> response = new HashMap<>();
            response.put("message", "User registered successfully. Please check your email to verify your account.");
            response.put("username", newUser.getUsername());
            response.put("userId", newUser.getId());
            response.put("enabled", false);

            return ResponseEntity.status(HttpStatus.CREATED).body(response);

        } catch (DataIntegrityViolationException e) {
            logger.error("Username already exists: {}", dto.username());
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body(Map.of("error", "Username already exists"));
        } catch (Exception e) {
            logger.error("Registration failed: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Registration failed"));
        }
    }

    private void sendRegistrationEmail(CustomUser user) {
        try {
            String message = String.format(
                    "Registration email for user: %s (ID: %s). Account is pending activation.",
                    user.getUsername(),
                    user.getId()
            );

            amqpTemplate.convertAndSend(
                    RabbitConfig.EXCHANGE_NAME,
                    RabbitConfig.ROUTING_KEY,
                    message
            );

            logger.info("Registration email sent via RabbitMQ for user: {}", user.getUsername());
        } catch (Exception e) {
            logger.error("Failed to send registration email: {}", e.getMessage());
        }
    }

    // For testing purposes - get all users (should be admin only in production)
    @GetMapping("/users")
    public ResponseEntity<?> getAllUsers() {
        return ResponseEntity.ok(customUserRepository.findAll());
    }
}