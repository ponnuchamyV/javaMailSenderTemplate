//package com.sam.mail_template_ivlin.service;
//
//import com.sam.mail_template_ivlin.dto.EmailRequestDTO;
//import jakarta.mail.MessagingException;
//
//public interface EmailService {
//    String sendSimpleMail(EmailRequestDTO request);
//    String sendMailWithAttachments(EmailRequestDTO request) throws MessagingException;
//}






package com.sam.mail_template_ivlin.service;

import com.sam.mail_template_ivlin.dto.EmailRequestDTO;
import jakarta.mail.MessagingException;

public interface EmailService {
    String sendSimpleMail(EmailRequestDTO request);
    String sendMailWithAttachments(EmailRequestDTO request) throws MessagingException;
    String sendMailWithTemplate(EmailRequestDTO request, String templateName, String templateVarsJson) throws MessagingException;
}
