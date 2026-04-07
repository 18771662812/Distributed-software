package org.example.shop.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 支付成功消息
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class PaymentSuccessMessage {
    private Long orderId;
    private String paymentMethod;
    private Long payTime;
}
