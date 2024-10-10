package com.thecodealchemist.main.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.Set;

/**
 * {
 *     "username": "john_doe",
 *     "password": "password123",
 *     "email": "john.doe@example.com",
 *     "roles": [
 *         {
 *             "name": "ADMIN",
 *             "authorities": [
 *                 {
 *                     "name": "READ_PERM"
 *                 },
 *                 {
 *                     "name": "WRITE_PERM"
 *                 },
 *                 {
 *                     "name": "DELETE_PERM"
 *                 }
 *             ]
 *         }
 *     ]
 * }
 */

@Getter
@Setter
public class UserDTO {
    private String username;
    private String password;
    private String email;
    private Set<RoleDTO> roles;
}
