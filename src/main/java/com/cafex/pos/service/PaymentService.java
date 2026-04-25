package com.cafex.pos.service;

import com.cafex.pos.dto.PaymentOrderRequest;
import com.cafex.pos.dto.PaymentOrderResponse;
import com.cafex.pos.dto.SubscriptionPlansResponse;
import com.razorpay.RazorpayClient;
import com.razorpay.RazorpayException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class PaymentService {

    private final SubscriptionPlansService subscriptionPlansService;

    @Value("${razorpay.key.id}")
    private String razorpayKeyId;

    @Value("${razorpay.key.secret}")
    private String razorpayKeySecret;

    public PaymentOrderResponse createOrder(PaymentOrderRequest request) throws RazorpayException {
        // Get plan details
        SubscriptionPlansResponse plan = subscriptionPlansService.getSubscriptionPlanById(request.getPlanId())
                .orElseThrow(() -> new RuntimeException("Plan not found"));

        // Calculate expected amount
        int discountMonths = getDiscountMonths(request.getMonths());
        BigDecimal baseAmount = plan.getPrice().multiply(BigDecimal.valueOf(request.getMonths()));
        BigDecimal discountAmount = plan.getPrice().multiply(BigDecimal.valueOf(discountMonths));
        BigDecimal expectedAmount = baseAmount.subtract(discountAmount);

        // Validate calculated amount matches expected (with small tolerance for floating point precision)
        BigDecimal receivedAmount = BigDecimal.valueOf(request.getCalculatedAmount());
        BigDecimal difference = expectedAmount.subtract(receivedAmount).abs();
        if (difference.compareTo(BigDecimal.valueOf(0.01)) > 0) { // Allow 1 paise difference
            throw new RuntimeException("Amount mismatch: expected " + expectedAmount + ", received " + request.getCalculatedAmount());
        }

        // Create Razorpay order
        RazorpayClient razorpay = new RazorpayClient(razorpayKeyId, razorpayKeySecret);

        JSONObject orderRequest = new JSONObject();
        orderRequest.put("amount", (int) (request.getCalculatedAmount() * 100)); // Amount in paise
        orderRequest.put("currency", "INR");
        orderRequest.put("receipt", "sub_plan_" + request.getPlanId() + "_rest_" + request.getRestaurantId());
        orderRequest.put("payment_capture", 1);

        JSONObject notes = new JSONObject();
        notes.put("plan_id", request.getPlanId());
        notes.put("months", request.getMonths());
        notes.put("restaurant_id", request.getRestaurantId());
        orderRequest.put("notes", notes);

        com.razorpay.Order order = razorpay.orders.create(orderRequest);

        PaymentOrderResponse response = new PaymentOrderResponse();
        response.setOrderId(order.get("id"));
        response.setKeyId(razorpayKeyId);
        response.setAmount(request.getCalculatedAmount());

        return response;
    }

    private int getDiscountMonths(int months) {
        switch (months) {
            case 1: return 0;
            case 3: return 1;
            case 6: return 2;
            case 12: return 3;
            default: return 0;
        }
    }
}