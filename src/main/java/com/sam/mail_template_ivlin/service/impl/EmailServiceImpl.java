//package com.sam.mail_template_ivlin.service.impl;
//
//import com.sam.mail_template_ivlin.dto.EmailRequestDTO;
//import com.sam.mail_template_ivlin.service.EmailService;
//import jakarta.mail.MessagingException;
//import jakarta.mail.internet.MimeMessage;
//import org.springframework.core.io.ByteArrayResource;
//import org.springframework.mail.SimpleMailMessage;
//import org.springframework.mail.javamail.JavaMailSender;
//import org.springframework.mail.javamail.MimeMessageHelper;
//import org.springframework.stereotype.Service;
//import org.springframework.web.multipart.MultipartFile;
//
//@Service
//public class EmailServiceImpl implements EmailService {
//
//    private final JavaMailSender mailSender;
//    private final String FROM = "samvjaathish@gmail.com";
//
//    public EmailServiceImpl(JavaMailSender mailSender) {
//        this.mailSender = mailSender;
//    }
//
//    @Override
//    public String sendSimpleMail(EmailRequestDTO request) {
//        try {
//            SimpleMailMessage msg = new SimpleMailMessage();
//            msg.setFrom(FROM);
//            msg.setTo(request.getTo());
//            msg.setSubject(request.getSubject());
//            msg.setText(request.getBody() == null ? "" : request.getBody());
//            mailSender.send(msg);
//            return "Mail sent successfully!";
//        } catch (Exception e) {
//            return "Error: " + e.getMessage();
//        }
//    }
//
//    @Override
//    public String sendMailWithAttachments(EmailRequestDTO request) throws MessagingException {
//        try {
//            MimeMessage mime = mailSender.createMimeMessage();
//            MimeMessageHelper helper = new MimeMessageHelper(mime, true);
//            helper.setFrom(FROM);
//            helper.setTo(request.getTo());
//            helper.setSubject(request.getSubject());
//            helper.setText(request.getBody() == null ? "" : request.getBody());
//
//            MultipartFile[] files = request.getAttachments();
//            if (files != null) {
//                for (MultipartFile file : files) {
//                    if (file != null && !file.isEmpty()) {
//                        helper.addAttachment(file.getOriginalFilename(),
//                                new ByteArrayResource(file.getBytes()));
//                    }
//                }
//            }
//
//            mailSender.send(mime);
//            return "Mail with attachments sent successfully!";
//        } catch (Exception e) {
//            if (e instanceof MessagingException) throw (MessagingException) e;
//            throw new MessagingException("Failed to send mail: " + e.getMessage(), e);
//        }
//    }
//}




























package com.sam.mail_template_ivlin.service.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sam.mail_template_ivlin.dto.EmailRequestDTO;
import com.sam.mail_template_ivlin.service.EmailService;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.Map;

@Service
public class EmailServiceImpl implements EmailService {

    private final JavaMailSender mailSender;
    private final String FROM = "samvjaathish@gmail.com";
    private final ObjectMapper mapper = new ObjectMapper();

    public EmailServiceImpl(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    @Override
    public String sendSimpleMail(EmailRequestDTO request) {
        try {
            SimpleMailMessage msg = new SimpleMailMessage();
            msg.setFrom(FROM);
            msg.setTo(request.getTo());
            msg.setSubject(request.getSubject());
            msg.setText(request.getBody() == null ? "" : request.getBody());
            mailSender.send(msg);
            return "Mail sent successfully!";
        } catch (Exception e) {
            return "Error: " + e.getMessage();
        }
    }

    @Override
    public String sendMailWithAttachments(EmailRequestDTO request) throws MessagingException {
        try {
            MimeMessage mime = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(mime, true);
            helper.setFrom(FROM);
            helper.setTo(request.getTo());
            helper.setSubject(request.getSubject());

            // Use isHtml flag
            String body = request.getBody() == null ? "" : request.getBody();
            helper.setText(body, request.isHtml());

            MultipartFile[] files = request.getAttachments();
            if (files != null) {
                for (MultipartFile file : files) {
                    if (file != null && !file.isEmpty()) {
                        helper.addAttachment(file.getOriginalFilename(),
                                new ByteArrayResource(file.getBytes()));
                    }
                }
            }

            mailSender.send(mime);
            return "Mail with attachments sent successfully!";
        } catch (Exception e) {
            if (e instanceof MessagingException) throw (MessagingException) e;
            throw new MessagingException("Failed to send mail: " + e.getMessage(), e);
        }
    }

    @Override
    public String sendMailWithTemplate(EmailRequestDTO request, String templateName, String templateVarsJson) throws MessagingException {
        try {
            // load template if requested
            String finalBody = request.getBody();
            if (templateName != null && !templateName.isBlank()) {
                String template = loadTemplateFromClasspath(templateName);
                Map<String, String> vars = parseVarsJsonToMap(templateVarsJson);
                finalBody = applyTemplateVariables(template, vars);
                request.setHtml(trueIfTemplateUsed(request)); // ensure HTML
            }

            MimeMessage mime = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(mime, true);
            helper.setFrom(FROM);
            helper.setTo(request.getTo());
            helper.setSubject(request.getSubject());

            helper.setText(finalBody == null ? "" : finalBody, request.isHtml());

            MultipartFile[] files = request.getAttachments();
            if (files != null) {
                for (MultipartFile file : files) {
                    if (file != null && !file.isEmpty()) {
                        helper.addAttachment(file.getOriginalFilename(), new ByteArrayResource(file.getBytes()));
                    }
                }
            }

            mailSender.send(mime);
            return "Mail with template sent successfully!";
        } catch (Exception e) {
            if (e instanceof MessagingException) throw (MessagingException) e;
            throw new MessagingException("Failed to send template mail: " + e.getMessage(), e);
        }
    }

    // --- helper methods ---

    private String loadTemplateFromClasspath(String templateName) {
        try (InputStream in = getClass().getResourceAsStream("/templates/" + templateName)) {
            if (in == null) return null;
            return new String(in.readAllBytes(), StandardCharsets.UTF_8);
        } catch (Exception e) {
            return null;
        }
    }

    private Map<String, String> parseVarsJsonToMap(String varsJson) {
        try {
            if (varsJson == null || varsJson.isBlank()) return Map.of();
            // read as Map<String,String>
            return mapper.readValue(varsJson, Map.class);
        } catch (Exception e) {
            return Map.of();
        }
    }

    private String applyTemplateVariables(String template, Map<String, String> vars) {
        if (template == null) return "";
        if (vars == null || vars.isEmpty()) return template;
        String result = template;
        for (Map.Entry<String, String> e : vars.entrySet()) {
            String placeholder = "{{" + e.getKey() + "}}";
            String value = e.getValue() == null ? "" : e.getValue();
            result = result.replace(placeholder, value);
        }
        return result;
    }

    // small helper to set HTML if template exists; keep original isHtml true if already true
    private boolean trueIfTemplateUsed(EmailRequestDTO req) {
        return true;
    }
}
