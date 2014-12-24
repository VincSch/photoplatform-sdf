/*
 *
 * Copyright (C) 2014
 *
 */

package de.htw.sdf.photoplatform.persistence.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import de.htw.sdf.photoplatform.persistence.AbstractBaseAuditEntity;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import javax.persistence.*;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Entity class for a user representing the corresponding database table.
 *
 * @author <a href="mailto:s0541962@htw-berlin.de">Vincent Schwarzer</a>
 */
@Entity
@Table(name = "SYS_USER")
public class User extends AbstractBaseAuditEntity implements UserDetails {

    private static final long serialVersionUID = 3719799602561353931L;

    // Used as username
    @Column(name = "EMAIL", unique = true, nullable = false)
    private String email;

    @Column(name = "FIRST_NAME", nullable = false)
    private String firstName;

    @Column(name = "LAST_NAME", nullable = false)
    private String lastName;

    @Column(name = "PASSWORD", nullable = false)
    @JsonIgnore
    private String password;

    @Embedded
    @Column(nullable = true)
    private PhotographerData photographerData;

    @Transient
    private String secToken;

    @OneToMany(fetch = FetchType.EAGER, mappedBy = "user")
    private List<UserRole> userRoles;

    @Column(name = "IS_ACCOUNT_NON_LOCKED")
    private boolean accountNonLocked;

    @Column(name = "IS_ENABLED")
    private boolean enabled;

    @Column(name = "LOST_PASSWORD_TOKEN", nullable = true)
    private String passwordResetToken;

    /**
     * User constructor.
     */
    public User() {
        super();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        Set<String> roles = new HashSet<>();
        if (userRoles == null) {
            return null;
        }

        for (UserRole userRole : userRoles) {
            roles.add(userRole.getRole().getName());
        }

        Set<GrantedAuthority> authorities = new HashSet<>();
        for (String role : roles) {
            authorities.add(new SimpleGrantedAuthority(role));
        }

        return authorities;
    }

    /**
     * @param roleName the role
     * @return true if role is included
     */
    @Transient
    public boolean isRoleIncluded(final String roleName) {
        List<UserRole> roles = getUserRoles();
        if (roles == null) {
            return false;
        }

        for (UserRole userRole : getUserRoles()) {
            if (userRole.getRole().getName().equals(roleName)) {
                return true;
            }
        }
        return false;
    }

    /**
     * {@inheritDoc}
     */
    @Transient
    public boolean isAdmin() {
        return isRoleIncluded(Role.ADMIN);
    }

    /**
     * {@inheritDoc}
     */
    @Transient
    public boolean isPhotographer() {
        return isRoleIncluded(Role.PHOTOGRAPHER);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getUsername() {
        return email;
    }

    /**
     * Set username as email.
     *
     * @param email username
     */
    public void setUsername(String email) {
        this.email = email;
    }

    /**
     * @return the first name
     */
    public String getFirstName() {
        return firstName;
    }

    /**
     * @param firstName the first name to set
     */
    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    /**
     * @return the last name
     */
    public String getLastName() {
        return lastName;
    }

    /**
     * @param lastName the last name to set
     */
    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getPassword() {
        return password;
    }

    /**
     * Set password.
     *
     * @param password the password to set
     */
    public void setPassword(String password) {
        this.password = password;
    }

    /**
     * Returns token.
     *
     * @return the sec toke
     */
    public String getSecToken() {
        return secToken;
    }

    /**
     * Set token.
     *
     * @param secToken the sec token to set
     */
    public void setSecToken(String secToken) {
        this.secToken = secToken;
    }

    /**
     * Returns user roles.
     *
     * @return the user roles
     */
    public List<UserRole> getUserRoles() {
        return userRoles;
    }

    /**
     * Set user roles.
     *
     * @param userRoles
     */
    public void setUserRoles(List<UserRole> userRoles) {
        this.userRoles = userRoles;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isAccountNonLocked() {
        return accountNonLocked;
    }

    /**
     * Set locked value. If true than locked, else non locked
     *
     * @param isAccountNonLocked the account non locked to set
     */
    public void setAccountNonLocked(boolean isAccountNonLocked) {
        this.accountNonLocked = isAccountNonLocked;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isEnabled() {
        return enabled;
    }

    /**
     * @param isEnabled the enable to set
     */
    public void setEnabled(boolean isEnabled) {
        this.enabled = isEnabled;
    }

    /**
     * Return true if customer want to become a photographer
     *
     * @return true if want to become photographer
     */
    public boolean isBecomePhotographer() {
        return isRoleIncluded(Role.BECOME_PHOTOGRAPHER);
    }

    /**
     * @return photographer data
     */
    public PhotographerData getPhotographerData() {
        return photographerData;
    }

    /**
     * @param photographerData the photographer data to set
     */
    public void setPhotographerData(PhotographerData photographerData) {
        this.photographerData = photographerData;
    }

    /**
     * @return the lost password token
     */
    public String getPasswordResetToken() {
        return passwordResetToken;
    }

    /**
     * @param passwordResetToken the lost password token
     */
    public void setPasswordResetToken(String passwordResetToken) {
        this.passwordResetToken = passwordResetToken;
    }

}
