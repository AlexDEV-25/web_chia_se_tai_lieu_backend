package com.example.app.share;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Component;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class SendMail {

	private final JavaMailSender emailSender;

	@Value("${spring.mail.username}")
	private String FROM;

	public void sendEmail(String email, String activationCode) {
		String subject = "Kích hoạt tài khoản của bạn";

		String url = "http://localhost:5173/activate/" + email + "/" + activationCode;

		// HTML content
		String html = "<html><body>" + "<h2 style='color:#4A90E2'>Kích hoạt tài khoản</h2>"
				+ "<p>Vui lòng sử dụng mã sau:</p>" + "<h1 style='color:green'>" + activationCode + "</h1>"
				+ "<p>Bấm vào link để kích hoạt:</p>" + "<a href='" + url + "'>Nhấn vào đây để kích hoạt</a>"
				+ "</body></html>";

		sendHtmlEmail(FROM, email, subject, html);
	}

	private void sendHtmlEmail(String from, String to, String subject, String html) {
		try {
			MimeMessage message = emailSender.createMimeMessage();

			MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

			helper.setFrom(from);
			helper.setTo(to);
			helper.setSubject(subject);
			helper.setText(html, true); // true = cho phép HTML

			emailSender.send(message);

		} catch (MessagingException e) {
			throw new RuntimeException("Không thể gửi email", e);
		}
	}
}
