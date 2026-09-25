package com.simple.bank.udemy.notification.service;

import com.simple.bank.udemy.auth.entity.UserEntity;
import com.simple.bank.udemy.enums.NotificationType;
import com.simple.bank.udemy.notification.dto.NotificationDTO;
import com.simple.bank.udemy.notification.entity.NotificationEntity;
import com.simple.bank.udemy.notification.repository.NotificationRepository;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import java.nio.charset.StandardCharsets;

@Service
@Slf4j
@RequiredArgsConstructor
public class NotificationServiceImpl implements NotificationService {

    private final NotificationRepository notificationRepository;
    private final JavaMailSender mailSender;
    private final TemplateEngine templateEngine;

    @Override
    @Async
    public void sendEmail(NotificationDTO notificationDTO, UserEntity user) {
        try {
            MimeMessage mimeMessage = mailSender.createMimeMessage();

            MimeMessageHelper mimeMessageHelper = new MimeMessageHelper(
                    mimeMessage,
                    MimeMessageHelper.MULTIPART_MODE_MIXED_RELATED,
                    StandardCharsets.UTF_8.name()
            );

            mimeMessageHelper.setTo(notificationDTO.getRecipient());
            mimeMessageHelper.setSubject(notificationDTO.getSubject());

            if (notificationDTO.getTemplateName() != null) {
                Context context = new Context();
                context.setVariables(notificationDTO.getTemplateVariables());
                String htmlContent = templateEngine.process(notificationDTO.getTemplateName(), context);

                mimeMessageHelper.setText(htmlContent, true);
            } else {
                mimeMessageHelper.setText(notificationDTO.getBody(), true);
            }

            mailSender.send(mimeMessage);

//            NotificationEntity notificationEntity = NotificationEntity.builder()
//                    .recipient(notificationDTO.getRecipient())
//                    .subject(notificationDTO.getSubject())
//                    .body(notificationDTO.getBody())
//                    .type(NotificationType.EMAIL)
//                    .user(user)
//                    .build();
//
//            notificationRepository.save(notificationEntity);

        } catch (MessagingException e) {
            log.error(e.getMessage());
        }
    }
}
