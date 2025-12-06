package com.krillinator.spring_security.user.dto;

// TODO validation & Injection protection
public record CustomUserLoginDTO(
        String username,
        String password
) {
}
