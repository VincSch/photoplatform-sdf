/*
 *
 * Copyright (C) 2014
 *
 */

package de.htw.sdf.photoplatform.webservice.controller;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import de.htw.sdf.photoplatform.exception.BadRequestException;
import de.htw.sdf.photoplatform.exception.NotFoundException;
import de.htw.sdf.photoplatform.exception.common.AbstractBaseException;
import de.htw.sdf.photoplatform.exception.common.ManagerException;
import de.htw.sdf.photoplatform.manager.UserManager;
import de.htw.sdf.photoplatform.persistence.model.User;
import de.htw.sdf.photoplatform.security.TokenUtils;
import de.htw.sdf.photoplatform.webservice.BaseAPIController;
import de.htw.sdf.photoplatform.webservice.Endpoints;
import de.htw.sdf.photoplatform.webservice.dto.PasswordResetDto;
import de.htw.sdf.photoplatform.webservice.dto.UserCredential;
import de.htw.sdf.photoplatform.webservice.dto.UserData;
import de.htw.sdf.photoplatform.webservice.dto.UserRegister;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.security.authentication.*;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.persistence.NoResultException;
import javax.validation.Valid;
import java.io.IOException;

/**
 * This controller generates the token that must be present in subsequent REST
 * invocations.
 *
 * @author <a href="mailto:philip@sorst.net">Philip W. Sorst</a>
 * @author <a href="mailto:josh@joshlong.com">Josh Long</a>
 */
@RestController
public class AuthenticationController extends BaseAPIController {

    @Resource
    @Qualifier(value = "myAuthManager")
    private AuthenticationManager authenticationManager;

    @Resource
    private UserDetailsService userDetailsService;

    @Resource
    private UserManager userManager;

    /**
     * Login user.
     *
     * @param userCredential the login for user
     * @return the user
     * @throws IOException the io exception
     */
    @RequestMapping(value = Endpoints.USER_LOGIN, method = RequestMethod.POST)
    public UserData login(@Valid @RequestBody UserCredential userCredential,
                          BindingResult bindingResult) throws IOException,
            AbstractBaseException {

        if (bindingResult.hasErrors()) {
            throw new BadRequestException("login rejected", bindingResult);
        }

        UsernamePasswordAuthenticationToken token = new UsernamePasswordAuthenticationToken(
                userCredential.getEmail(), userCredential.getPassword());

        Authentication authentication;

        try {
            authentication = authenticationManager.authenticate(token);
        } catch (DisabledException | LockedException ex) {
            // User is disabled or locked
            throw new BadRequestException(messages.getMessage("User.locked"));
        } catch (BadCredentialsException | NoResultException ex) {
            throw new BadRequestException(messages.getMessage("User.notFound"));
        }

        SecurityContextHolder.getContext().setAuthentication(authentication);

        User user = (User) this.userDetailsService
                .loadUserByUsername(userCredential.getEmail());
        user.setSecToken(TokenUtils.createToken(user));

        return new UserData(user);
    }

    /**
     * Register user.
     *
     * @param userRegister  user data for register
     * @param bindingResult the binding result
     * @throws Exception the exception
     */
    @RequestMapping(value = Endpoints.USER_REGISTER, method = RequestMethod.POST)
    public void register(@Valid @RequestBody final UserRegister userRegister,
                         BindingResult bindingResult) throws Exception {
        // Check if password match
        if (!userRegister.getPassword().equals(
                userRegister.getPasswordConfirm())) {
            bindingResult
                    .addError(new FieldError("register", "passwordConfirm",
                            messages.getMessage("Password.confirm")));
        }

        if (bindingResult.hasErrors()) {
            // User input errors
            log.info("-- register user fail: email = \""
                    + userRegister.getEmail() + "; password=\"**********\";");

            throw new BadRequestException("register", bindingResult);
        }

        try {
            // Try to register user
            userManager.registerUser(userRegister.getEmail(), userRegister.getFirstName(), userRegister.getLastName(),
                    userRegister.getPassword());
        } catch (ManagerException ex) {
            switch (ex.getCode()) {
                case AbstractBaseException.USER_EMAIL_EXISTS:
                    bindingResult.addError(new FieldError("register", "email",
                            messages.getMessage("Email.exists")));
                    break;

                default:
                    throw new RuntimeException("Unhandled error");
            }

            throw new BadRequestException("register", bindingResult);
        }
    }

    /**
     * Recipe by name.
     *
     * @param json the json object
     * @return the user
     */
    @RequestMapping(value = Endpoints.USER_PASSWORD_LOST, method = RequestMethod.POST)
    public void passwordLost(@RequestBody String json) throws IOException, NotFoundException, BadRequestException {
        ObjectMapper mapper = new ObjectMapper();
        JsonNode node = mapper.readTree(json);
        String email = mapper.convertValue(node.get("email"),
                String.class);

        try {
            userManager.passwordLost(email);
        } catch (ManagerException ex) {
            switch (ex.getCode()) {
                case ManagerException.NOT_FOUND:
                    throw new NotFoundException("user");

                default:
                    throw new BadRequestException("unhandled eception " + ex.getMessage());
            }
        }
    }

    /**
     * Reset password.
     */
    @RequestMapping(value = Endpoints.USER_PASSWORD_RESET, method = RequestMethod.POST)
    public void passwordReset(@Valid @RequestBody PasswordResetDto dto,
                              BindingResult bindingResult) throws IOException, NotFoundException, BadRequestException {

        // Check if new password match
        if (!dto.getNewPassword().equals(
                dto.getPasswordConfirm())) {
            bindingResult
                    .addError(new FieldError("register", "passwordConfirm",
                            messages.getMessage("Password.confirm")));
        }

        if (bindingResult.hasErrors()) {
            throw new BadRequestException("password reset", bindingResult);
        }

        try {
            userManager.passwordReset(dto.getPasswordResetToken(), dto.getNewPassword());
        } catch (ManagerException ex) {
            switch (ex.getCode()) {
                case ManagerException.NOT_FOUND:
                    throw new BadRequestException(messages.getMessage("Password.reset.notFound"));
                default:
                    throw new BadRequestException("Bad request");
            }
        }
    }

}
