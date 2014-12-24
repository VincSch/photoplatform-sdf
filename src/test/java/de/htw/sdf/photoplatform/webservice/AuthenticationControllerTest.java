/*
 * Copyright (C) 2014 photoplatform-webservice-angular
 *
 */

package de.htw.sdf.photoplatform.webservice;

import com.dumbster.smtp.SimpleSmtpServer;
import de.htw.sdf.photoplatform.common.BaseAPITester;
import de.htw.sdf.photoplatform.persistence.model.User;
import de.htw.sdf.photoplatform.webservice.dto.PasswordResetDto;
import de.htw.sdf.photoplatform.webservice.dto.UserCredential;
import de.htw.sdf.photoplatform.webservice.dto.UserRegister;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MvcResult;

import java.util.HashMap;
import java.util.Map;

import static org.hamcrest.Matchers.notNullValue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Test for user loginAsAdmin register user
 *
 * @author <a href="mailto:s0531603@htw-berlin.de">Daniil Tomilow</a>
 */
public class AuthenticationControllerTest extends BaseAPITester {//BaseTester {

    @Before
    public void setUp() throws Exception {
        initAPITest();
    }

    @Test
    public void testRegisterAndLoginUser() throws Exception {
        String email = "test@test.de";
        String password = "password";

        UserRegister userRegister = new UserRegister();

        userRegister.setEmail(email);
        userRegister.setFirstName("valid");
        userRegister.setLastName("user");
        userRegister.setPassword(password);
        userRegister.setPasswordConfirm(password);

        // Register User
        mockMvc.perform(
                post("/api/user/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(userRegister))
                        .accept(MediaType.APPLICATION_JSON)).andExpect(
                status().isOk());

        // Login user
        UserCredential userCredential = new UserCredential();
        userCredential.setEmail(email);
        userCredential.setPassword(password);

        mockMvc.perform(
                post("/api/user/login").contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(userCredential))
                        .accept(MediaType.APPLICATION_JSON)).andExpect(
                status().isOk());
    }

    /**
     * creates a new user register it then login to check normal login behaviour
     * again. Afterwards the expected test to check the locked user can't login
     * after he is locked. Set the user Enabled and the login will work.
     *
     * @throws Exception
     */
    @Test
    public void testLockedUser() throws Exception {
        String email = "testLocked@test.de";
        String firstName = "Test";
        String lastName = "Test";
        String password = "password";

        UserRegister userRegister = new UserRegister();
        userRegister.setEmail(email);
        userRegister.setFirstName(firstName);
        userRegister.setLastName(lastName);
        userRegister.setFirstName(email);
        userRegister.setPassword(password);
        userRegister.setPasswordConfirm(password);

        // Register User
        mockMvc.perform(
                post("/api/user/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(userRegister))
                        .accept(MediaType.APPLICATION_JSON)).andExpect(
                status().isOk());

        UserCredential userCredential = new UserCredential();
        userCredential.setEmail(email);
        userCredential.setPassword(password);

        mockMvc.perform(
                post("/api/user/login").contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(userCredential))
                        .accept(MediaType.APPLICATION_JSON)).andExpect(
                status().isOk());

        User user = userDAO.findByEmail(email);
        user.setAccountNonLocked(false);
        userDAO.update(user);

        mockMvc.perform(
                post("/api/user/login").contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(userCredential))
                        .accept(MediaType.APPLICATION_JSON)).andExpect(
                status().isBadRequest());

        user.setAccountNonLocked(true);
        userDAO.update(user);

        mockMvc.perform(
                post("/api/user/login").contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(userCredential))
                        .accept(MediaType.APPLICATION_JSON)).andExpect(
                status().isOk());
    }

    @Test
    public void testRegisterUserWithInvalidEmail() throws Exception {
        UserRegister userRegister = new UserRegister();
        userRegister.setEmail("invalidemail.de");
        userRegister.setPassword("1234");
        userRegister.setPasswordConfirm("1234");

        mockMvc.perform(
                post("/api/user/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(userRegister))
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errors.email", notNullValue()));
    }

    @Test
    public void testRegisterUserConfirmPasswordFail() throws Exception {
        UserRegister userRegister = new UserRegister();
        userRegister.setFirstName("valid");
        userRegister.setLastName("user");

        userRegister.setEmail("valid@mail.de");
        userRegister.setPassword("1234");
        userRegister.setPasswordConfirm("1wsd234");

        MvcResult result = mockMvc
                .perform(
                        post("/api/user/register")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(
                                        mapper.writeValueAsString(userRegister))
                                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errors.passwordConfirm", notNullValue()))
                .andReturn();

        System.out.println(result.getResponse().getContentAsString());
    }

    @Test
    public void testPasswordLostAndReset() throws Exception {

        final String email = "valid@mail.de";
        final String lostPassword = "123";
        final String newPassword = "321";

        UserRegister userRegister = new UserRegister();
        userRegister.setFirstName("valid");
        userRegister.setLastName("user");

        userRegister.setEmail(email);

        // i will lost this password...
        userRegister.setPassword(lostPassword);
        userRegister.setPasswordConfirm(lostPassword);

        // 1. Register user
        mockMvc
                .perform(
                        post("/api/user/register")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(
                                        mapper.writeValueAsString(userRegister))
                                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        User user = userDAO.findByEmail(email);

        String oldPassword = user.getPassword();

        Assert.assertTrue(user != null);
        Assert.assertTrue(user.getPasswordResetToken() == null);

        Map<String, String> map = new HashMap<>();
        map.put("email", email);

        // Start email server
        SimpleSmtpServer server = SimpleSmtpServer.start(1313);

        // 2. Oh boy ... forgot my password oO
        mockMvc
                .perform(
                        post("/api" + Endpoints.USER_PASSWORD_LOST)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(
                                        mapper.writeValueAsString(map))
                                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        // Check if email is here
        Assert.assertEquals(true, server.getReceivedEmailSize() == 1);
        // TODO parse email

        // reload user from DB
        user = userDAO.findByEmail(email);

        Assert.assertTrue(user.getPasswordResetToken() != null);

        String passwordResetToken = user.getPasswordResetToken();

        PasswordResetDto dto = new PasswordResetDto();
        dto.setPasswordResetToken(passwordResetToken);
        dto.setNewPassword(newPassword);
        dto.setPasswordConfirm(newPassword);

        // 3. set new password
        mockMvc
                .perform(
                        post("/api" + Endpoints.USER_PASSWORD_RESET)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(
                                        mapper.writeValueAsString(dto))
                                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        // reload user from DB
        user = userDAO.findByEmail(email);

        Assert.assertTrue(user.getPasswordResetToken() == null);
        // Old password should be changed
        Assert.assertTrue(!user.getPassword().equals(oldPassword));

        // 4. Login user
        UserCredential userCredential = new UserCredential();
        userCredential.setEmail(email);
        userCredential.setPassword(newPassword);

        mockMvc.perform(
                post("/api/user/login").contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(userCredential))
                        .accept(MediaType.APPLICATION_JSON)).andExpect(
                status().isOk());

        server.stop();
    }

    @After
    public void tearDown() throws Exception {
        cancel();
    }
}
