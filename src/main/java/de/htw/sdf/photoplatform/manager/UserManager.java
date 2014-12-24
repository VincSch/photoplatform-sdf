/*
 *
 * Copyright (C) 2014
 *
 */

package de.htw.sdf.photoplatform.manager;

import de.htw.sdf.photoplatform.exception.common.ManagerException;
import de.htw.sdf.photoplatform.persistence.model.User;

import java.util.List;

/**
 * Interface defining business methods for users.
 *
 * @author <a href="mailto:s0541962@htw-berlin.de">Vincent Schwarzer</a>
 */
public interface UserManager {

    /**
     * Register user.
     *
     * @param email     the email
     * @param firstName the first name
     * @param lastName the last name
     * @param password  the password
     *
     * @throws ManagerException the exception
     */
    void registerUser(final String email,
                      String firstName, String lastName, final String password)
            throws ManagerException;

    /**
     * Sends password recovery to the specified email.
     *
     * @param email the email
     */
    void passwordLost(final String email) throws ManagerException;

    /**
     * Reset user password
     *
     * @param passwordResetToken the password reset token
     * @param newPassword        the new password
     */
    void passwordReset(String passwordResetToken, final String newPassword) throws ManagerException;

    /**
     * Update user data.
     *
     * @param entity User you want to update
     * @return the updated User
     */
    User update(final User entity);

    /**
     * delete a User.
     *
     * @param entity user to be deleted.
     */
    void delete(final User entity);

    /**
     * find User by id. Note. This method fetch all user data. To find only
     * user, use findOne.
     *
     * @param id User id
     * @return User class
     */
    User findById(final long id);

    /**
     * find all Users.
     *
     * @return a list of all Users.
     */
    List<User> findAll();

    /**
     * delete all Users.
     */
    void deleteAll();

    /**
     * find a user by its unique name.
     *
     * @param name unique name.
     * @return the user entity.
     */
    User findByName(String name);

    /**
     * activates a photograph user account.
     *
     * @param id photograph user id.
     * @return user.
     */
    User enablePhotographer(long id) throws ManagerException;

    /**
     * grants the user admin rights.
     *
     * @param id user id.
     * @return user.
     */
    User makeAdmin(long id);

    /**
     * locks the user for the system
     *
     * @param id user id.
     * @return user.
     */
    User lockUser(long id);

    /**
     * unlocks the user for the system
     *
     * @param id user id.
     * @return user.
     */
    User unlockUser(long id);

    /**
     * Returns all user between start and count. IF start and count contains 0,
     * than return first user. IF start contains 0 and count contains 1, than
     * return first user.
     *
     * @param start index for first.
     * @param count index for last.
     * @return users
     */
    List<User> find(Integer start, Integer count);

    /**
     * Returns all photographs, that should be activated.
     *
     * @return users with role photograph.
     */
    List<User> getPhotographersToActivate();

    /**
     * Returns all photographs, that should be activated.
     *
     * @param roleName the role name
     * @param enabled  if true, enable
     * @return users.
     */
    List<User> getByRoleAndEnabled(String roleName, boolean enabled);

    /**
     * Become a photographer
     *
     * @param userId
     * @param company  the company
     * @param phone    the phone number
     * @param homepage the homepage
     * @param paypalID the paypalID
     * @param iban     the iban
     * @param swift    the swift
     * @return user who wants to become photographer
     */
    boolean becomePhotographer(long userId, final String company, final String phone, final String homepage,
                               final String paypalID, final String iban, final String swift) throws ManagerException;

    /**
     * Check user admin role.
     *
     * @param user user.
     * @return true if user has role admin.
     */
    Boolean isUserAdmin(User user);

    /**
     * Check user photographer role.
     *
     * @param user user.
     * @return true if user has role photographer.
     */
    Boolean isUserPhotographer(User user);

    /**
     * Returns true, if user has the role.
     *
     * @param user     user.
     * @param roleName role name.
     * @return true or false.
     */
    Boolean isRoleIncluded(User user, String roleName);

}
