package com.acme.coldtrace.platform.iam.infrastructure.authorization.sfs.services;

import com.acme.coldtrace.platform.iam.domain.repositories.RoleRepository;
import com.acme.coldtrace.platform.iam.domain.repositories.UserRepository;
import com.acme.coldtrace.platform.iam.infrastructure.authorization.sfs.model.UserDetailsImpl;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

/**
 * Provides ColdTrace users to Spring Security.
 *
 * @since 1.0
 */
@Service(value = "defaultUserDetailsService")
public class UserDetailsServiceImpl implements UserDetailsService {
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;

    public UserDetailsServiceImpl(UserRepository userRepository, RoleRepository roleRepository) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        var user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with email: " + email));
        var roleName = roleRepository.findById(user.getRoleId())
                .map(role -> role.getName().toUpperCase().replace('-', '_'))
                .orElse("USER");
        return UserDetailsImpl.build(user, roleName);
    }
}
