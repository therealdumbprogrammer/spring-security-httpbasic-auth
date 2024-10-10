package com.thecodealchemist.main.service;

import com.thecodealchemist.main.dto.AuthorityDTO;
import com.thecodealchemist.main.dto.RoleDTO;
import com.thecodealchemist.main.dto.UserDTO;
import com.thecodealchemist.main.entity.Authority;
import com.thecodealchemist.main.entity.Role;
import com.thecodealchemist.main.entity.User;
import com.thecodealchemist.main.model.AuthenticatedUser;
import com.thecodealchemist.main.repository.AuthorityRepository;
import com.thecodealchemist.main.repository.RolesRepository;
import com.thecodealchemist.main.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;


@Service
public class UserService implements UserDetailsService {
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RolesRepository rolesRepository;

    @Autowired
    private AuthorityRepository authorityRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    public User register(UserDTO userDTO) {
        System.out.println("Request body: " + userDTO);

        User u = new User();
        u.setEmail(userDTO.getEmail());
        u.setUsername(userDTO.getUsername());
        u.setPassword(passwordEncoder.encode(userDTO.getPassword()));

        Set<Role> roles = userDTO.getRoles().stream().map(roleDTO -> {
            //Single role
            Role role = getOrCreateRole(roleDTO);

            //multiple authorities
            Set<Authority> authorities = getAuthoritiesFromRequest(roleDTO);

            role.setAuthorities(authorities);

            return role;
        }).collect(Collectors.toSet());

        u.setRoles(roles);

        return userRepository.save(u);
    }

    private Set<Authority> getAuthoritiesFromRequest(RoleDTO roleDTO) {
        return roleDTO.getAuthorities().stream().map(authorityDTO -> {
            return getOrCreateAuthority(authorityDTO);
        }).collect(Collectors.toSet());
    }

    private Authority getOrCreateAuthority(AuthorityDTO authorityDTO) {
        return authorityRepository.findByName(authorityDTO.getName()).orElseGet(() -> {
            Authority auth = new Authority();
            auth.setName(authorityDTO.getName());
            return authorityRepository.save(auth);
        });
    }

    private Role getOrCreateRole(RoleDTO roleDTO) {
        return rolesRepository.findByName(roleDTO.getName()).orElseGet(() -> {
            Role r = new Role();
            r.setName(roleDTO.getName());
            return rolesRepository.save(r);
        });
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User u = userRepository.findByUsername(username);

        //fetch the authorities
        List<GrantedAuthority> authorities = u.getRoles().stream()
                .flatMap(role -> role.getAuthorities().stream().map(authority -> new SimpleGrantedAuthority(role.getName()+"_"+authority.getName()))).collect(Collectors.toList());

        System.out.println("authorities:::::::::" + authorities);

        //fetch the role
        authorities.addAll(u.getRoles().stream().map(r -> new SimpleGrantedAuthority("ROLE_" + r.getName())).collect(Collectors.toList()));
        System.out.println("Role:::::::::" + authorities);

        return new AuthenticatedUser(u, authorities);
    }
}
