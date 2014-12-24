/*
 *
 * Copyright (C) 2014
 *
 */

package de.htw.sdf.photoplatform.manager.impl;

import de.htw.sdf.photoplatform.common.Messages;
import de.htw.sdf.photoplatform.exception.common.AbstractBaseException;
import de.htw.sdf.photoplatform.exception.common.ManagerException;
import de.htw.sdf.photoplatform.manager.UserManager;
import de.htw.sdf.photoplatform.manager.common.MailService;
import de.htw.sdf.photoplatform.persistence.model.PhotographerData;
import de.htw.sdf.photoplatform.persistence.model.Role;
import de.htw.sdf.photoplatform.persistence.model.User;
import de.htw.sdf.photoplatform.persistence.model.UserRole;
import de.htw.sdf.photoplatform.repository.RoleDAO;
import de.htw.sdf.photoplatform.repository.UserDAO;
import de.htw.sdf.photoplatform.repository.UserRoleDAO;
import org.apache.log4j.Logger;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import org.springframework.web.util.UriComponents;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * business methods for users.
 *
 * @author <a href="mailto:s0541962@htw-berlin.de">Vincent Schwarzer</a>
 */
@Service
@Transactional
public class UserManagerImpl implements UserManager {

    @Resource
    private UserDAO userDAO;

    @Resource
    private RoleDAO roleDAO;

    @Resource
    private UserRoleDAO userRoleDAO;

    @Resource
    private MailService mailService;

    @Resource
    private Messages messages;

    /**
     * {@inheritDoc}
     */
    @Override
    public void registerUser(final String email,
                             final String firstName, final String lastName,
                             final String password) throws ManagerException {

        checkUser(email, password);

        User user = new User();
        // email is username!
        user.setUsername(email);
        user.setFirstName(firstName);
        user.setLastName(lastName);
        user.setPassword(new BCryptPasswordEncoder().encode(password));

        // Enable user
        user.setAccountNonLocked(true);
        user.setEnabled(true);

        userDAO.create(user);

        UserRole userRole = new UserRole();
        userRole.setUser(user);

        Role role = roleDAO.findByName(Role.CUSTOMER);
        if (role == null) {
            throw new RuntimeException("User role = " + Role.CUSTOMER
                    + " does not exists.");
        }

        userRole.setRole(role);
        userRoleDAO.create(userRole);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void passwordLost(final String email) throws ManagerException {
        User user = userDAO.findByEmail(email);

        if (user == null) {
            throw new ManagerException(ManagerException.NOT_FOUND);
        }

        // Generate new password
        Date now = new Date();
        // Generate some random shit
        String userAndDate = String.valueOf(user.toString()) + now;
        String randomToken = new BCryptPasswordEncoder().encode(userAndDate);

        user.setPasswordResetToken(randomToken);

        UriComponents uriComponents =
                ServletUriComponentsBuilder.fromCurrentContextPath().path("/profile/password/reset")
                        .queryParam("token", randomToken).build();

        String resetPasswordUrl = uriComponents.toUriString();

        String subject = messages.getMessage("Password.lost.subject");
        String message = messages.getMessage("Password.lost.message", user.getFirstName(), resetPasswordUrl);

        // Send message to user
        if (!mailService.sendMail(user, subject, message)) {
            // TODO this is Internal Server Error Exception
            throw new ManagerException(ManagerException.BAD_REQUEST);
        }
    }


    /**
     * {@inheritDoc}
     */
    @Override
    public void passwordReset(String passwordResetToken,
                              final String newPassword) throws ManagerException {
        User user = userDAO.findByPasswordResetToken(passwordResetToken);

        if (user == null) {
            throw new ManagerException(ManagerException.NOT_FOUND);
        }

        user.setPassword(new BCryptPasswordEncoder().encode(newPassword));

        // Remove the old tokens
        user.setPasswordResetToken(null);
    }

    /**
     * Check is email or username already exists.
     *
     * @param email    the email
     * @param password the password
     * @throws ManagerException the exception
     */
    private void checkUser(final String email, final String password)
            throws ManagerException {
        if (email == null || password == null) {
            throw new RuntimeException("This should not happen");
        }

        // check if email already in use
        User user = userDAO.findByEmail(email);
        if (user != null) {
            throw new ManagerException(AbstractBaseException.USER_EMAIL_EXISTS);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public User update(User entity) {
        return userDAO.update(entity);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void delete(User entity) {
        userDAO.delete(entity);

    }

    /**
     * {@inheritDoc}
     */
    @Override
    public User findById(final long id) {
        return userDAO.findOne(id);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<User> findAll() {
        return userDAO.findAll();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void deleteAll() {
        userDAO.deleteAll();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public User findByName(String username) {
        return userDAO.findByEmail(username);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<User> find(Integer start, Integer count) {
        return userDAO.find(start, count);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<User> getPhotographersToActivate() {
        return userDAO.findByRoleAndEnabledFilter(Role.BECOME_PHOTOGRAPHER, true /* enabled */);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<User> getByRoleAndEnabled(String roleName, boolean enabled) {
        return userDAO.findByRoleAndEnabledFilter(roleName, enabled);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public User enablePhotographer(long userId) throws ManagerException {
        User user = userDAO.findOne(userId);
        if (user == null) {
            throw new ManagerException(AbstractBaseException.NOT_FOUND);
        }

        if (user.isBecomePhotographer() && user.getPhotographerData() != null) {
            removeRole(user, Role.BECOME_PHOTOGRAPHER);
            addRole(user, Role.PHOTOGRAPHER);
        }

        return user;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public User makeAdmin(long id) {
        User user = userDAO.findById(id);
        return addRole(user, Role.ADMIN);
    }

    /**
     * Add role to user
     *
     * @param user     the user id
     * @param roleName the role name
     * @return the user wih new role
     */
    private User addRole(User user, final String roleName) {
        if (!user.isRoleIncluded(roleName)) {
            Role role = roleDAO.findByName(roleName);

            UserRole userRole = new UserRole();
            userRole.setRole(role);
            userRole.setUser(user);
            userRoleDAO.create(userRole);
            List<UserRole> userRoles = user.getUserRoles();
            userRoles.add(userRole);
            user.setUserRoles(userRoles);
        }

        return user;
    }

    /**
     * Remove role from user
     *
     * @param user     the user id
     * @param roleName the role name
     * @return the user wih new role
     */
    private User removeRole(User user, final String roleName) {
        if (user.isRoleIncluded(roleName)) {
            List<UserRole> userRoles = new ArrayList<>();
            for (UserRole userRole : user.getUserRoles()) {
                if (userRole.getRole().getName().equals(roleName)) {
                    userRoleDAO.delete(userRole);
                } else {
                    userRoles.add(userRole);
                }
            }

            user.setUserRoles(userRoles);
        }

        return user;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public User lockUser(long id) {
        User user = userDAO.findOne(id);
        user.setAccountNonLocked(false);
        userDAO.update(user);
        return user;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public User unlockUser(long id) {
        User user = userDAO.findOne(id);
        user.setAccountNonLocked(true);
        userDAO.update(user);
        return user;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean becomePhotographer(long userId, final String company, final String phone,
                                      final String homepage, final String paypalID, final String iban,
                                      final String swift) throws ManagerException {
        User user = userDAO.findById(userId);
        if (user == null) {
            throw new ManagerException(AbstractBaseException.NOT_FOUND);
        }

        if (user.isBecomePhotographer()) {
            // User already want to be a photographer
            return true;
        }

        PhotographerData data = new PhotographerData();
        data.setCompany(company);
        data.setHomepage(homepage);
        data.setIban(iban);
        data.setSwift(swift);
        data.setPhone(phone);
        data.setPaypalID(paypalID);

        user.setPhotographerData(data);

        // User want to become photographer
        addRole(user, Role.BECOME_PHOTOGRAPHER);

        return true;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Boolean isUserAdmin(User user) {
        return isRoleIncluded(user, Role.ADMIN);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Boolean isUserPhotographer(User user) {
        return isRoleIncluded(user, Role.PHOTOGRAPHER);
    }

    /**
     * {@inheritDoc}
     */
    public Boolean isRoleIncluded(User user, String roleName) {
        for (UserRole userRole : user.getUserRoles()) {
            if (userRole.getRole().getName().equals(roleName)) {
                return true;
            }
        }
        return false;
    }
}
