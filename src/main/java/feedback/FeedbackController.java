package feedback;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.mail.MailException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.mail.javamail.MimeMessagePreparator;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import javax.validation.ValidationException;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.Properties;

@RestController
@RequestMapping("/feedback")
public class FeedbackController {


    private EmailCfg emailCfg;

    public FeedbackController(EmailCfg emailCfg) {
        this.emailCfg = emailCfg;
    }

    @PostMapping
    public String sendFeedback(@RequestBody Feedback feedback,
                               BindingResult bindingResult) throws MessagingException, UnsupportedEncodingException {
        if (bindingResult.hasErrors()) {
            throw new ValidationException("Feedback is not valid");
        }
        // Create a mail sender
        JavaMailSenderImpl mailSender = new JavaMailSenderImpl();
        mailSender.setHost(this.emailCfg.getHost());
        mailSender.setPort(this.emailCfg.getPort());
        mailSender.setUsername(this.emailCfg.getUsername());
        mailSender.setPassword(this.emailCfg.getPassword());


        Properties props = mailSender.getJavaMailProperties();
        props.put("mail.transport.protocol", "smtp");
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.debug", "true");


        MimeMessage message = mailSender.createMimeMessage();
        message.setHeader("ContentXXX","Testing email!!!");
        MimeMessageHelper helper = new MimeMessageHelper(message);
        helper.setText("good morning bro !!!" + " sent by: " +  feedback.getEmail());
        helper.setTo(this.emailCfg.getUsername());
        helper.setFrom(feedback.getName() + "<"+feedback.getEmail()+">");
        helper.setSubject(feedback.getFeedback());
        try {
            mailSender.send(message);
            return "Successed!!!!";
        } catch (MailException ex) {
            //log it and go on
            System.err.println(ex.getMessage());
            return ex.getMessage();
        }
    }
}
