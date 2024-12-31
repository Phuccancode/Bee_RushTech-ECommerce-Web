package com.project.bee_rushtech.configs;

import com.project.bee_rushtech.utils.VNPayUtil;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

import java.text.SimpleDateFormat;
import java.util.*;

@Getter
@Configuration
public class VNPAYConfig {
    @Value("${payment.vnPay.url}")
    private String vnp_PayUrl;

    @Value("${payment.vnPay.returnUrl}")
    private String vnp_ReturnUrl;

    @Value("${payment.vnPay.tmnCode}")
    private String vnp_TmnCode;

    @Value("${payment.vnPay.secretKey}")
    private String secretKey;

    @Value("${payment.vnPay.version}")
    private String vnp_Version;

    @Value("${payment.vnPay.command}")
    private String vnp_Command;

    @Value("${payment.vnPay.orderType}")
    private String orderType;

    public Map<String, String> getVNPayConfig() {
        Map<String, String> vnpParamsMap = new HashMap<>();
        vnpParamsMap.put("vnp_Version", this.vnp_Version); // Phiên bản API
        vnpParamsMap.put("vnp_Command", this.vnp_Command); // Loại API
        vnpParamsMap.put("vnp_TmnCode", this.vnp_TmnCode); // Mã website
        vnpParamsMap.put("vnp_CurrCode", "VND"); // Loại tiền tệ
        vnpParamsMap.put("vnp_TxnRef", VNPayUtil.getRandomNumber(8)); // Mã giao dịch
        vnpParamsMap.put("vnp_OrderInfo", "Thanh toan don hang:" + VNPayUtil.getRandomNumber(8)); // Thông tin đơn hàng
        vnpParamsMap.put("vnp_OrderType", this.orderType); // Loại đơn hàng
        vnpParamsMap.put("vnp_Locale", "vn"); // Ngôn ngữ
        vnpParamsMap.put("vnp_ReturnUrl", this.vnp_ReturnUrl); // URL trả về
        Calendar calendar = Calendar.getInstance(TimeZone.getTimeZone("Etc/GMT+7")); // Multiple time zone
        SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMddHHmmss"); // Format date
        String vnpCreateDate = formatter.format(calendar.getTime()); // Thời gian tạo giao dịch
        vnpParamsMap.put("vnp_CreateDate", vnpCreateDate); // Thời gian tạo giao dịch
        calendar.add(Calendar.MINUTE, 15);
        String vnp_ExpireDate = formatter.format(calendar.getTime()); // Thời gian hết hạn giao dịch
        vnpParamsMap.put("vnp_ExpireDate", vnp_ExpireDate);
        return vnpParamsMap;
    }
}