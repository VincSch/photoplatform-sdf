/*
 *
 * Copyright (C) 2014
 *
 */

package de.htw.sdf.photoplatform.repository;

import de.htw.sdf.photoplatform.persistence.model.Role;
import de.htw.sdf.photoplatform.persistence.model.User;
import de.htw.sdf.photoplatform.repository.common.GenericDAO;
import org.springframework.security.core.userdetails.UserDetailsService;

import java.util.List;

/**
 * @author <a href="mailto:s0541962@htw-berlin.de">Vincent Schwarzer</a>
 */
public interface UserDAO extends UserDetailsService, GenericDAO<User> {

    /**
     * Find user by specified email.
     *
     * @param email the email
     * @return the user
     */
    User findByEmail(String email);

    /**
     * Returns all user by role.
     *
     * @param role role entity
     * @return all users by role
     */
    List<User> findByRole(final Role role);

    /**
     * Returns all enabled user between start and count. SET WHERE user.enable =
     * true. IF start and count contains 0, than return first user. IF start
     * contains 0 and count contains 1, than return first user.
     *
     * @param start index for first.
     * @param count index for last.
     * @return users
     */
    List<User> find(Integer start, Integer count);

    /**
     * Returns all user by role id.
     *
     * @param roleId role id
     * @return all users by role id
     */
    List<User> findByRoleId(final Long roleId);

    /**
     * Returns all not admin users.
     *
     * @return all not admin users
     */
    List<User> findAllNotAdminUsers();

    /**
     * Returns not admin users. If enabled is true, than all enabled not admin
     * users else otherwise.
     *
     * @param enabled true, false
     * @return not admin users
     */
    List<User> findByEnabled(boolean enabled);

    /**
     * Returns all users by role and enabled filter.
     *
     *
     * @param roleName
     * @param enabled true, false
     * @return not admin users
     */
    List<User> findByRoleAndEnabledFilter(final String roleName, boolean enabled);

    /**
     * Returns not admin users. If locked is true, than all not admin users
     * which account is not locked else otherwise
     *
     * @param locked true, false
     * @return not admin users
     */
    List<User> findByAccountLocked(boolean locked);

    /**
     * Returns user by its id.
     * Note this method fetch all user data.
     * To find only user, use findOne.
     *
     * @param id user id.
     * @return all user data.
     */
    User findById(Long id);

    /**
     * Return list of photographers to activate
     *
     * @return list of users
     */
    List<User> findPhotographersToActivate();

    /**
     * Find user by specified passwordResetToken.
     *
     * @param passwordResetToken the passwordResetToken
     * @return the user
     */
    User findByPasswordResetToken(final String passwordResetToken);

}
