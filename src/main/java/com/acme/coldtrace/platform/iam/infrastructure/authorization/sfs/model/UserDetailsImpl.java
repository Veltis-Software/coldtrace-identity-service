package com.acme.coldtrace.platform.iam.infrastructure.authorization.sfs.model;

import com.acme.coldtrace.platform.iam.domain.model.aggregates.User;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;

/**
 * Spring Security user details backed by a ColdTrace user.
 *
 * @since 1.0
 */
@Getter
@EqualsAndHashCode
public class UserDetailsImpl implements UserDetails {
    private final String username;

    @JsonIgnore
    private final String password;

    private final boolean accountNonExpired;
    private final boolean accountNonLocked;
    private final boolean credentialsNonExpired;
    private final boolean enabled;
    private final Collection<? extends GrantedAuthority> authorities;

    public UserDetailsImpl(String username, String password, Collection<? extends GrantedAuthority> authorities) {
        this.username = username;
        this.password = password;
        this.authorities = authorities;
        this.accountNonExpired = true;
        this.accountNonLocked = true;
        this.credentialsNonExpired = true;
        this.enabled = true;
    }

    public static UserDetailsImpl build(User user, String roleName) {
        var authorities = List.of(new SimpleGrantedAuthority(roleName));
        return new UserDetailsImpl(user.getEmail(), user.getPasswordHash(), authorities);
    }
}
