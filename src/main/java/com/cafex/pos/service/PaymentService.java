package com.cafex.pos.service;

import com.cafex.pos.exception.ApiException;
import com.cafex.pos.exception.BadRequestException;
import com.cafex.pos.exception.ConflictException;
import com.cafex.pos.exception.ResourceNotFoundException;
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
                .orElseThrow(() -> new ResourceNotFoundException("Plan not found"));

        // Calculate expected amount
        BigDecimal baseAmount = plan.getPrice().multiply(BigDecimal.valueOf(request.getMonths()));
        BigDecimal discountPercentage = BigDecimal.valueOf(plan.getOffer_discount_percentage() != null ? plan.getOffer_discount_percentage() : 0);
        BigDecimal discountAmount = baseAmount.multiply(discountPercentage).divide(BigDecimal.valueOf(100));
        BigDecimal expectedAmount = baseAmount.subtract(discountAmount);

        // Validate calculated amount matches expected (with small tolerance for floating point precision)
        BigDecimal receivedAmount = BigDecimal.valueOf(request.getCalculatedAmount());
        BigDecimal difference = expectedAmount.subtract(receivedAmount).abs();
        if (difference.compareTo(BigDecimal.valueOf(0.01)) > 0) { // Allow 1 paise difference
            throw new BadRequestException("Amount mismatch: expected " + expectedAmount + ", received " + request.getCalculatedAmount());
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
}
