package com.project.bee_rushtech.services;

import java.util.List;
import java.util.Random;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.project.bee_rushtech.models.Email;
import com.project.bee_rushtech.models.Order;
import com.project.bee_rushtech.models.OrderDetail;
import com.project.bee_rushtech.models.User;
import com.project.bee_rushtech.utils.SecurityUtil;

import jakarta.mail.internet.MimeMessage;
import jakarta.servlet.http.HttpServletRequest;

import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;

@Service
public class EmailService {
    @Autowired
    private JavaMailSender javaMailSender;

    @Autowired
    private SecurityUtil securityUtil;

    @Autowired
    private UserService userService;

    @Autowired
    private OrderDetailService orderDetailService;

    public void sendEmail(Email email) {
        MimeMessage message = javaMailSender.createMimeMessage();
        try {
            MimeMessageHelper helper = new MimeMessageHelper(message, true);
            helper.setFrom("beerushtech@gmail.com");
            helper.setTo(email.getToEmail());
            helper.setSubject(email.getSubject());
            helper.setText(email.getBody());
            javaMailSender.send(message);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void handleSendMail(HttpServletRequest request, Order order) throws Exception {
        String token = request.getHeader("Authorization").substring(7);
        Long userId = securityUtil.getUserFromToken(token).getId();
        User user = userService.findById(userId);
        String toEmail = user.getEmail();
        List<OrderDetail> orderDetails = orderDetailService.findByOrderId(order.getId());

        StringBuilder result = new StringBuilder();
        result.append("The rented product:\n");
        for (int i = 0; i < orderDetails.size(); i++) {
            OrderDetail detail = orderDetails.get(i);

            Long days = detail.getTimeRenting() / 24;
            Long hours = detail.getTimeRenting() % 24;

            // Định dạng chuỗi kết quả
            String timeRenting = String.format("%d Days, %d hours", days, hours);

            result.append(String.format("%d. Product: %s - Quantity: %d - Time Renting: %d hour\n",
                    i + 1,
                    detail.getProduct().getName(),
                    detail.getNumberOfProducts(),
                    timeRenting));
        }

        String subject = "[BeeRushTech] Order Confirmation";
        String body = "Dear "
                + user.getFullName() + ",\n\n"
                + "Thank you for trusting and using our services. Your order number [number] has been confirmed and is being prepared. You can review the order details below:\n\n"
                + "INFORMATION ABOUT ORDER" + order.getId() + "\n"
                + result.toString()
                + "Total Price: " + order.getTotalMoney().longValue() + "\n"
                + "Payment Method: " + order.getPaymentMethod() + "\n"
                + "Shipping Number" + order.getTrackingNumber() + "\n\n"
                + "Delivery Address: " + order.getShippingAddress() + "\n\n"
                + "To invoke your right to change any information, please contact us at least 24 hours in advance.\n"
                + "Best,\n" + "Customer Service at Bee RushTech\n\n"
                + "Attention:\n"
                + "Before picking up your package, please check that it has not been damaged or tampered with. If the package is damaged, and you are afraid that the parcel may have been opened before it was delivered to you, remember to create a ticket with the courier. Only this way can we determine the guilty party and, if applicable, return your money or arrange another shipment.\n"
                + "During the rental period, if any damage occurs to the equipment, the renter is responsible for covering the entire repair cost. In the event that the equipment cannot be repaired, the renter must compensate for the cost of purchasing the equipment. Additionally, the renter must compensate for any economic loss resulting from the equipment being unavailable for rental.\n\n"
                + "Please contact us in the following ways:\n"
                + "Email:beerushtech@gmail.com\n"
                + "Phone: 0123456789\n"
                + "Showroom: 268, Ly Thuong Kiet, Ward 14, District 10, HCM City.\n";

        Email email = new Email();
        email.setToEmail(toEmail);
        email.setSubject(subject);
        email.setBody(body);
        sendEmail(email);
    }
}