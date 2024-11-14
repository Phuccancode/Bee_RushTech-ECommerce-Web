package com.project.bee_rushtech.controllers;

import com.project.bee_rushtech.dtos.PaymentDTO;
import com.project.bee_rushtech.responses.ResponseObject;
import com.project.bee_rushtech.services.PaymentService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("${api.prefix}/payment")
@RequiredArgsConstructor

// Thông tin tài khoản dùng để test nhé
//Ngân hàng	NCB
//Số thẻ	9704198526191432198
//Tên chủ thẻ	NGUYEN VAN A
//Ngày phát hành	07/15
//Mật khẩu OTP	123456
public class PaymentController {
    private final PaymentService paymentService;
    @GetMapping("/vn-pay")
    public ResponseObject<PaymentDTO.VNPayResponse> pay(HttpServletRequest request) {
        return new ResponseObject<>(HttpStatus.OK, "Success", paymentService.createVnPayPayment(request));
    }
    @GetMapping("/vn-pay-callback")
    public ResponseObject<PaymentDTO.VNPayResponse> payCallbackHandler(HttpServletRequest request) {
        String status = request.getParameter("vnp_ResponseCode");
        if (status.equals("00")) {
            return new ResponseObject<>(HttpStatus.OK, "Success", new PaymentDTO.VNPayResponse("00", "Success", ""));
        } else {
            return new ResponseObject<>(HttpStatus.BAD_REQUEST, "Failed", null);
        }
    }
}
