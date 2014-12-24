/*
 *
 * Copyright (C) 2014
 *
 */

package de.htw.sdf.photoplatform.webservice.dto;

import org.hibernate.validator.constraints.NotEmpty;

import javax.validation.constraints.Size;

/**
 * Data Transfer Object.
 *
 * @author <a href="mailto:s0531603@htw-berlin.de">Daniil Tomilow</a>
 */
public class PasswordResetDto {

    @NotEmpty
    private String passwordResetToken;

    @NotEmpty
    @Size(min = 3, max = 60)
    private String newPassword;

    @NotEmpty
    @Size(min = 3, max = 60)
    private String passwordConfirm;

    /**
     * @return the password confirm token
     */
    public String getPasswordResetToken() {
        return passwordResetToken;
    }

    /**
     * @param passwordResetToken the password reset token to set
     */
    public void setPasswordResetToken(String passwordResetToken) {
        this.passwordResetToken = passwordResetToken;
    }

    /**
     * @return new password
     */
    public String getNewPassword() {
        return newPassword;
    }

    /**
     * @param newPassword the new password
     */
    public void setNewPassword(String newPassword) {
        this.newPassword = newPassword;
    }

    /**
     * @return the password confirm
     */
    public final String getPasswordConfirm() {
        return passwordConfirm;
    }

    /**
     * @param passwordConfirm the passwordConfirm to set
     */
    public final void setPasswordConfirm(final String passwordConfirm) {
        this.passwordConfirm = passwordConfirm;
    }

}
