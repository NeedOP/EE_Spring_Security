package com.krillinator.spring_security;

import com.krillinator.spring_security.user.CustomUser;
import com.krillinator.spring_security.user.CustomUserDetails;
import com.krillinator.spring_security.user.authority.UserRole;
import com.krillinator.spring_security.security.jwt.JwtUtils;
import org.springframework.amqp.rabbit.annotation.EnableRabbit;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.util.Set;

@EnableRabbit
@SpringBootApplication
public class SpringSecurityApplication {

	public static void main(String[] args) {
		SpringApplication.run(SpringSecurityApplication.class, args);

		System.out.println(
				UserRole.GUEST.getRoleName()	// ROLE_GUEST
		);

		System.out.println(
				UserRole.USER.getUserPermissions() // PERMISSIONS
		);

		System.out.println(
				UserRole.ADMIN.getUserPermissions()
		);

		System.out.println(
				UserRole.GUEST.getUserAuthorities() + " \n " + // GUEST - AUTHORITY
				UserRole.USER.getUserAuthorities() + " \n " + // USER - AUTHORITY incl. perms.
				UserRole.ADMIN.getUserAuthorities()			 // ADMIN - AUTHORITY incl. perms.
		);

		CustomUser benny = new CustomUser(
				"",
				"",
				true,
				true,
				true,
				true,
				Set.of(UserRole.USER, UserRole.ADMIN)
		);
		/*
			CustomUserDetails customUserDetails = new CustomUserDetails(benny);
			System.out.println("getAuthorities: " + customUserDetails.getAuthorities());

			JwtUtils jwtUtils = new JwtUtils();

			// Generate the token
			String token = jwtUtils.generateJwtToken(benny);
			System.out.println("Generated JWT:\n" + token);

			// Extract the roles
			Set<UserRole> extractedRoles = jwtUtils.getRolesFromJwtToken(token);
			System.out.println("Extracted roles: " + extractedRoles);

		 */
	}

}
