package com.example.flippedclass.service;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Service
public class EmailService {

    @Autowired
    private JavaMailSender mailSender;

    @Async // Đánh dấu chạy ngầm Background, giúp API phản hồi tức thì dưới 0.1 giây!
    public void sendResetPasswordEmail(String toEmail, String resetToken) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            helper.setTo(toEmail);
            helper.setSubject("[FlippedClass] Yêu Cầu Khôi Phục Mật Khẩu Tài Khoản");

            // URL dẫn đến trang web Frontend đổi mật khẩu thực tế
            String resetLink = "http://localhost:3000/reset-password?token=" + resetToken;

            // Giao diện email HTML siêu đẳng cấp doanh nghiệp
            String htmlContent = "<div style=\"font-family: 'Helvetica Neue', Helvetica, Arial, sans-serif; max-width: 600px; margin: 0 auto; padding: 20px; border: 1px solid #e1e8ed; border-radius: 12px; background-color: #ffffff;\">"
                    + "  <div style=\"text-align: center; margin-bottom: 30px;\">"
                    + "    <h2 style=\"color: #f15a24; margin: 0; font-size: 28px; font-weight: 700;\">FLIPPED CLASSROOM</h2>"
                    + "    <p style=\"color: #657786; font-size: 14px; margin-top: 5px;\">Hệ thống Học tập Đảo ngược thông minh</p>"
                    + "  </div>"
                    + "  <div style=\"padding: 20px 10px; border-top: 1px solid #e1e8ed; border-bottom: 1px solid #e1e8ed;\">"
                    + "    <p style=\"font-size: 16px; color: #14171a; line-height: 1.6;\">Chào bạn,</p>"
                    + "    <p style=\"font-size: 16px; color: #14171a; line-height: 1.6;\">Bạn vừa gửi yêu cầu đặt lại mật khẩu cho tài khoản Flipped Classroom của mình. Vui lòng nhấn vào nút bấm nổi bật dưới đây để thiết lập mật khẩu mới:</p>"
                    + "    <div style=\"text-align: center; margin: 35px 0;\">"
                    + "      <a href=\"" + resetLink + "\" style=\"background-color: #f15a24; color: #ffffff; padding: 14px 30px; font-size: 16px; font-weight: bold; text-decoration: none; border-radius: 8px; display: inline-block; box-shadow: 0 4px 6px rgba(241, 90, 36, 0.2); transition: all 0.2s;\">Đặt lại mật khẩu</a>"
                    + "    </div>"
                    + "    <p style=\"font-size: 14px; color: #ff3333; line-height: 1.6; font-weight: 500;\">⚠️ Lưu ý: Đường liên kết này chỉ có hiệu lực trong vòng 15 phút. Nếu bạn không gửi yêu cầu này, vui lòng bỏ qua email này một cách an toàn.</p>"
                    + "  </div>"
                    + "  <div style=\"text-align: center; margin-top: 30px; color: #657786; font-size: 12px;\">"
                    + "    <p>Đây là email tự động từ hệ thống, vui lòng không trả lời thư này.</p>"
                    + "    <p>&copy; 2026 Flipped Classroom Team. All rights reserved.</p>"
                    + "  </div>"
                    + "</div>";

            helper.setText(htmlContent, true); // true = gửi định dạng HTML
            mailSender.send(message);

        } catch (MessagingException e) {
            throw new RuntimeException("Error: Failed to send reset password email: " + e.getMessage());
        }
    }
}
