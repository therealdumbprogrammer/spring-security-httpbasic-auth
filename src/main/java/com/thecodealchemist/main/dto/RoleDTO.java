package com.thecodealchemist.main.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.Set;

@Getter
@Setter
public class RoleDTO {
    private String name;
    private Set<AuthorityDTO> authorities;
}
