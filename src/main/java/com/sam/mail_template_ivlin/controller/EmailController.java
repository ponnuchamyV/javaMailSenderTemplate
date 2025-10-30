//package com.sam.mail_template_ivlin.controller;
//
//import com.sam.mail_template_ivlin.dto.EmailRequestDTO;
//import com.sam.mail_template_ivlin.service.EmailService;
//import jakarta.mail.MessagingException;
//import org.springframework.http.MediaType;
//import org.springframework.web.bind.annotation.*;
//import org.springframework.web.multipart.MultipartFile;
//
//@RestController
//@RequestMapping("api/mail")
//public class EmailController {
//
//    private final EmailService emailService;
//
//    public EmailController(EmailService emailService) {
//        this.emailService = emailService;
//    }
//
//    @PostMapping("/send")
//    public String sendMail(@RequestBody EmailRequestDTO request) {
//        return emailService.sendSimpleMail(request);
//    }
//
//    @PostMapping(value = "/send-with-attachments", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
//    public String sendMailWithAttachments(
//            @RequestParam("to") String to,
//            @RequestParam("subject") String subject,
//            @RequestParam(value = "body", required = false) String body,
//            @RequestParam(value = "attachments", required = false) MultipartFile[] attachments
//    ) throws MessagingException {
//        EmailRequestDTO dto = new EmailRequestDTO(to, subject, body, attachments);
//        return emailService.sendMailWithAttachments(dto);
//    }
//}























package com.sam.mail_template_ivlin.controller;

import com.sam.mail_template_ivlin.dto.EmailRequestDTO;
import com.sam.mail_template_ivlin.service.EmailService;
import jakarta.mail.MessagingException;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/mail")
public class EmailController {

    private final EmailService emailService;

    public EmailController(EmailService emailService) {
        this.emailService = emailService;
    }

    // Simple JSON send (no attachments)
    @PostMapping("/send")
    public String sendMail(@RequestBody EmailRequestDTO request) {
        return emailService.sendSimpleMail(request);
    }

    // Multipart endpoint: attachments + template support
    @PostMapping(value = "/send-with-attachments", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public String sendMailWithAttachments(
            @RequestParam(value = "to") String to,
            @RequestParam(value = "subject") String subject,
            @RequestParam(value = "body", required = false) String body,
            @RequestParam(value = "templateName", required = false) String templateName,
            @RequestParam(value = "templateVars", required = false) String templateVarsJson,
            @RequestParam(value = "isHtml", required = false, defaultValue = "false") boolean isHtml,
            @RequestParam(value = "attachments", required = false) MultipartFile[] attachments
    ) throws MessagingException {

        EmailRequestDTO dto = new EmailRequestDTO(to, subject, body, attachments, isHtml);

        // If templateName provided, use template method (this will override body if template found)
        if (templateName != null && !templateName.isBlank()) {
            return emailService.sendMailWithTemplate(dto, templateName, templateVarsJson);
        } else {
            return emailService.sendMailWithAttachments(dto);
        }
    }
}
