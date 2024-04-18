package org.book.commerce.bookcommerce.service.exception;

import jakarta.mail.Message;
import jakarta.mail.MessagingException;
import jakarta.mail.Session;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.task.TaskSchedulingProperties;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class MailService {

    private final JavaMailSender emailSender;
    private Session session;

    public boolean sendEmail(String toEmail,String title, String key) throws MessagingException {
        MimeMessage emailForm = createEmailForm(toEmail,title,key);
        try{
            emailSender.send(emailForm);
        } catch (RuntimeException e){
//            log.debug("MailService.sendEmail exception occur toEmail: {}, " +
//                    "title: {}, text: {}", toEmail, title, text);
//            throw new BusinessLogicException(ExceptionCode.UNABLE_TO_SEND_EMAIL);
            throw new RuntimeException();
        }
        return true;
    }

    private MimeMessage createEmailForm(String toEmail,String title,String key) throws MessagingException {
        MimeMessage message = new MimeMessage(session);
        message.addRecipients(Message.RecipientType.TO,toEmail);
        message.setSubject(title);
        message.setContent("<h1>[이메일 인증]</h1> <p>아래 링크를 클릭하시면 이메일 인증이 완료됩니다.</p> " +
                "<a href='http://localhost:8080/auth/signup/email-verifications?key="+key+"' target='_blenk'>이메일 인증 확인</a>", "text/html;charset=euc-kr"
        );

        return message;
    }
}



