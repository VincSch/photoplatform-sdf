package de.htw.sdf.photoplatform.manager.common;

import de.htw.sdf.photoplatform.persistence.model.User;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.MailException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Created by Vincent Schwarzer on 05.12.14.
 */
@Service
@Transactional
public class MailService extends DAOReferenceCollector {

    private Logger log = Logger.getLogger(this.getClass().getName());

    @Autowired
    private JavaMailSender mailSender;

    @Autowired
    private SimpleMailMessage templateMessage;

    /**
     * Send an email to a specific user with a subject and message
     *
     * @param user    send to user
     * @param subject subject
     * @param message message
     * @return true if mail was send successfully, false otherwise
     */
    public boolean sendMail(User user, String subject, String message) {
        SimpleMailMessage msg = new SimpleMailMessage(templateMessage);
        if (subject == null || message == null || user == null) {
            return false;
        }
        String email = user.getUsername();
        msg.setSubject(subject);
        msg.setTo(email);
        msg.setText(message);
        try {
            this.mailSender.send(msg);
        } catch (MailException ex) {
            log.info(ex.getMessage());
            return false;
        }
        return true;
    }
}
