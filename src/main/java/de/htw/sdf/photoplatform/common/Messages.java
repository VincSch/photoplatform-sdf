/*
 *
 * Copyright (C) 2014
 *
 */

package de.htw.sdf.photoplatform.common;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Component;

import java.util.Locale;

/**
 * Message Bundle.
 *
 * @author <a href="mailto:s0531603@htw-berlin.de">Daniil Tomilow</a>
 */
@Component
public class Messages {
    private final Locale defaultLocale = new Locale("de", "DE");

    @Autowired
    private MessageSource messageSource;

    /**
     * Return message by specified code.
     *
     * @param code the code
     * @return the message
     */
    public String getMessage(String code) {
        return getMessage(code, null);
    }

    /**
     * Return message by specified code.
     *
     * @param code the code
     * @param args Array of arguments that will be filled in for params within the message (params
     *             look like "{0}", "{1,date}", "{2,time}" within a message),
     * @return the message
     */
    public String getMessage(String code, String ... args) {
        return messageSource.getMessage(code, args, defaultLocale);
    }

}
