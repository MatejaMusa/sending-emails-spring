package com.personalproject.userservice.service.impl;

import com.personalproject.userservice.service.EmailService;
import com.personalproject.userservice.utils.EmailUtils;
import jakarta.activation.DataHandler;
import jakarta.activation.DataSource;
import jakarta.activation.FileDataSource;
import jakarta.mail.BodyPart;
import jakarta.mail.internet.MimeBodyPart;
import jakarta.mail.internet.MimeMessage;
import jakarta.mail.internet.MimeMultipart;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.FileSystemResource;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import java.io.File;

import static com.personalproject.userservice.utils.EmailUtils.getVerificationUrl;

@Service
@RequiredArgsConstructor
public class EmailServiceImpl implements EmailService {

    @Value("${spring.mail.verify.host}")
    private String host;
    @Value("${spring.mail.username}")
    private String fromEmail;
    private final JavaMailSender emailSender;
    private final TemplateEngine templateEngine;

    @Override
    @Async
    public void sendSimpleMailMessage(String name, String to, String token) {
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setSubject("New User Account Verification");
            message.setFrom(fromEmail);
            message.setTo(to);
            message.setText(EmailUtils.getEmailMessage(name,host,token));
            emailSender.send(message);
        }catch (Exception e){
            System.out.println(e.getMessage());
            throw new RuntimeException(e.getMessage());
        }
    }

    @Override
    @Async
    public void sendMimeMessageWithAttachments(String name, String to, String token) {
        try {
            MimeMessage message = getMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
            helper.setPriority(1);
            helper.setSubject("New User Account Verification");
            helper.setFrom(fromEmail);
            helper.setTo(to);
            helper.setText(EmailUtils.getEmailMessage(name,host,token));
            //Add attachments
            FileSystemResource image = new FileSystemResource(new File("C:\\Users\\Mateja\\Desktop\\user-service\\src\\main\\java\\com\\personalproject\\userservice\\images\\cloud-and-thunder.png"));
            helper.addAttachment(image.getFilename(), image);
            emailSender.send(message);
        }catch (Exception e){
            System.out.println(e.getMessage());
            throw new RuntimeException(e.getMessage());
        }
    }

    private MimeMessage getMimeMessage(){
       return emailSender.createMimeMessage();
    }

    @Override
    @Async
    public void sendMimeMessageWithEmbeddedFiles(String name, String to, String token) {
        try {
            MimeMessage message = getMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
            helper.setPriority(1);
            helper.setSubject("New User Account Verification");
            helper.setFrom(fromEmail);
            helper.setTo(to);
            helper.setText(EmailUtils.getEmailMessage(name,host,token));
            //Add attachments
            FileSystemResource image = new FileSystemResource(new File("C:\\Users\\Mateja\\Desktop\\user-service\\src\\main\\java\\com\\personalproject\\userservice\\images\\cloud-and-thunder.png"));
            FileSystemResource file = new FileSystemResource(new File("C:\\Users\\Mateja\\Desktop\\user-service\\src\\main\\java\\com\\personalproject\\userservice\\images\\Doc2.2.docx"));
            helper.addInline(getContentId(image.getFilename()), image);
            helper.addInline(getContentId(file.getFilename()), file);
            emailSender.send(message);
        }catch (Exception e){
            System.out.println(e.getMessage());
            throw new RuntimeException(e.getMessage());
        }
    }
    private String getContentId(String fileName) {
        return "<" + fileName + ">";
    }

    @Override
    @Async
    public void sendHtmlEmail(String name, String to, String token) {
        try {
            Context context = new Context();
            context.setVariable("name", name);
            context.setVariable("url", getVerificationUrl(host, token));
            String text = templateEngine.process("emailtemplate", context);
            MimeMessage message = getMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
            helper.setPriority(1);
            helper.setSubject("New User Account Verification");
            helper.setFrom(fromEmail);
            helper.setTo(to);
            helper.setText(text,true);
//            FileSystemResource image = new FileSystemResource(new File("C:\\Users\\Mateja\\Desktop\\user-service\\src\\main\\java\\com\\personalproject\\userservice\\images\\cloud-and-thunder.png"));
//            FileSystemResource file = new FileSystemResource(new File("C:\\Users\\Mateja\\Desktop\\user-service\\src\\main\\java\\com\\personalproject\\userservice\\images\\Doc2.2.docx"));
//            helper.addInline(getContentId(image.getFilename()), image);
//            helper.addInline(getContentId(file.getFilename()), file);
            emailSender.send(message);
        }catch (Exception e){
            System.out.println(e.getMessage());
            throw new RuntimeException(e.getMessage());
        }
    }

    @Override
    @Async
    public void sendHtmlEmailWithEmbeddedFiles(String name, String to, String token) {
        try {
            MimeMessage message = getMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
            helper.setPriority(1);
            helper.setSubject("New User Account Verification");
            helper.setFrom(fromEmail);
            helper.setTo(to);
            //helper.setText(text,true);
            Context context = new Context();
            context.setVariable("name", name);
            context.setVariable("url", getVerificationUrl(host, token));
            String text = templateEngine.process("emailtemplate", context);

            //Add HTML email body
            MimeMultipart mimeMultipart = new MimeMultipart("related");
            BodyPart messageBodyPart = new MimeBodyPart();
            messageBodyPart.setContent(text, "text/html");
            mimeMultipart.addBodyPart(messageBodyPart);

            //Add images to the email body
            messageBodyPart = new MimeBodyPart();
            DataSource dataSource = new FileDataSource("C:\\Users\\Mateja\\Desktop\\user-service\\src\\main\\java\\com\\personalproject\\userservice\\images\\cloud-and-thunder.png");
            messageBodyPart.setDataHandler(new DataHandler(dataSource));
            messageBodyPart.setHeader("Content-ID", "image");
            mimeMultipart.addBodyPart(messageBodyPart);

            message.setContent(mimeMultipart);

            emailSender.send(message);
        }catch (Exception e){
            System.out.println(e.getMessage());
            throw new RuntimeException(e.getMessage());
        }
    }
}
