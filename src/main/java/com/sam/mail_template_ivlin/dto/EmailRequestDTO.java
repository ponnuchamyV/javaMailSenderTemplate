//package com.sam.mail_template_ivlin.dto;
//
//import lombok.AllArgsConstructor;
//import lombok.Data;
//import lombok.NoArgsConstructor;
//import org.springframework.web.multipart.MultipartFile;
//
//@Data
//@NoArgsConstructor
//@AllArgsConstructor
//public class EmailRequestDTO {
//    private String to;
//    private String subject;
//    private String body;
//    private MultipartFile[] attachments;
//}








package com.sam.mail_template_ivlin.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.web.multipart.MultipartFile;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class EmailRequestDTO {
    private String to;
    private String subject;
    private String body;
    private MultipartFile[] attachments;
    private boolean isHtml; // set to true when sending HTML/template
}
