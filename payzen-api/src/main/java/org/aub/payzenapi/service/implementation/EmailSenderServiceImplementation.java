package org.aub.payzenapi.service.implementation;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.aub.payzenapi.service.EmailSenderService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ClassPathResource;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

@Service
@RequiredArgsConstructor
public class EmailSenderServiceImplementation implements EmailSenderService {
    private final JavaMailSender javaMailSender;
    private final TemplateEngine templateEngine;

    @Value("${spring.mail.username}")
    private String fromEmail;

    @SneakyThrows
    @Override
    public void sendEmail(String toEmail, String otp) {
        Context context = new Context();
        context.setVariable("otp", otp);
        String process = templateEngine.process("index", context);
        MimeMessage mimeMessage = javaMailSender.createMimeMessage();
        MimeMessageHelper mimeMessageHelper = new MimeMessageHelper(mimeMessage, true);
        mimeMessageHelper.setFrom(fromEmail);
        mimeMessageHelper.setSubject("Email verification OTP code");
        mimeMessageHelper.setText(process, true);
        ClassPathResource image = new ClassPathResource("images/payzen-logo.png");
        mimeMessageHelper.addInline("logo", image);
        mimeMessageHelper.setTo(toEmail);

        javaMailSender.send(mimeMessage);
    }

}
