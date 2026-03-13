package org.aub.payzenapi.service;

public interface EmailSenderService {
    void sendEmail(String toEmail, String otp);
}
