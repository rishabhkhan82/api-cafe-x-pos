package com.cafex.pos.service;

import com.cafex.pos.dto.LoyaltyTransactionsPageResponse;
import com.cafex.pos.dto.LoyaltyTransactionsRequest;
import com.cafex.pos.dto.LoyaltyTransactionsResponse;
import com.cafex.pos.entity.Customer;
import com.cafex.pos.entity.LoyaltyPrograms;
import com.cafex.pos.entity.LoyaltyTransactions;
import com.cafex.pos.entity.Restaurant;
import com.cafex.pos.repository.CustomerRepository;
import com.cafex.pos.repository.LoyaltyProgramsRepository;
import com.cafex.pos.repository.LoyaltyTransactionsRepository;
import com.cafex.pos.repository.RestaurantRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.cafex.pos.exception.ApiException;
import com.cafex.pos.exception.BadRequestException;
import com.cafex.pos.exception.ConflictException;
import com.cafex.pos.exception.ResourceNotFoundException;
import jakarta.persistence.criteria.Predicate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class LoyaltyTransactionsServiceImpl implements LoyaltyTransactionsService {

    private final LoyaltyTransactionsRepository loyaltyTransactionsRepository;
    private final CustomerRepository customerRepository;
    private final RestaurantRepository restaurantRepository;
    private final LoyaltyProgramsRepository loyaltyProgramsRepository;

    @Override
    public LoyaltyTransactionsResponse createTransaction(LoyaltyTransactionsRequest request) {
        String transactionId = request.getTransactionId();
        if (transactionId == null || transactionId.trim().isEmpty()) {
            String prefix = "TXN";
            List<String> existingIds = loyaltyTransactionsRepository.findAll().stream()
                    .map(LoyaltyTransactions::getTransactionId)
                    .filter(id -> id != null && id.startsWith(prefix))
                    .collect(Collectors.toList());
            int maxNum = existingIds.stream()
                    .map(id -> id.substring(prefix.length()))
                    .filter(s -> s.matches("\\d+"))
                    .mapToInt(Integer::parseInt)
                    .max()
                    .orElse(0);
            transactionId = prefix + String.format("%03d", maxNum + 1);
        }

        log.info("Creating loyalty transaction: {}", transactionId);

        if (loyaltyTransactionsRepository.existsByTransactionId(transactionId)) {
            throw new ConflictException("Transaction ID already exists: " + transactionId);
        }

        Customer customer = customerRepository.findById(request.getCustomerId())
                .orElseThrow(() -> new ResourceNotFoundException("Customer not found with ID: " + request.getCustomerId()));
        Restaurant restaurant = restaurantRepository.findById(request.getRestaurantId())
                .orElseThrow(() -> new ResourceNotFoundException("Restaurant not found with ID: " + request.getRestaurantId()));

        LoyaltyTransactions transaction = new LoyaltyTransactions();
        transaction.setTransactionId(transactionId);
        transaction.setCustomer(customer);
        transaction.setRestaurant(restaurant);
        transaction.setTransactionType(request.getTransactionType());
        transaction.setPoints(request.getPoints());
        transaction.setBalanceBefore(request.getBalanceBefore());
        transaction.setBalanceAfter(request.getBalanceAfter());
        transaction.setOrderId(request.getOrderId());
        transaction.setInvoiceId(request.getInvoiceId());
        transaction.setOfferId(request.getOfferId());
        transaction.setDescription(request.getDescription());
        transaction.setReference(request.getReference());
        transaction.setExpiryDate(request.getExpiryDate());
        transaction.setEarnedFrom(request.getEarnedFrom());
        transaction.setRedeemedFor(request.getRedeemedFor());
        transaction.setProcessedBy(request.getProcessedBy());
        transaction.setProcessedAt(request.getProcessedAt());
        transaction.setApprovalRequired(request.getApprovalRequired());
        transaction.setApprovedBy(request.getApprovedBy());
        transaction.setApprovedAt(request.getApprovedAt());
        transaction.setReversalTransactionId(request.getReversalTransactionId());
        transaction.setIsReversal(request.getIsReversal());
        transaction.setNotes(request.getNotes());
        transaction.setCreatedAt(request.getCreatedAt() != null ? request.getCreatedAt() : LocalDateTime.now());

        LoyaltyTransactions savedTransaction = loyaltyTransactionsRepository.save(transaction);
        log.info("Loyalty transaction created successfully with ID: {}", savedTransaction.getId());

        LoyaltyPrograms program = loyaltyProgramsRepository.findByCustomerId(request.getCustomerId())
                .orElse(null);
        if (program != null) {
            int points = request.getPoints() != null ? request.getPoints() : 0;
            if ("EARNED".equalsIgnoreCase(request.getTransactionType())) {
                program.setPointsBalance(program.getPointsBalance() + points);
                program.setTotalPointsEarned(program.getTotalPointsEarned() + points);
            } else if ("REDEEMED".equalsIgnoreCase(request.getTransactionType())) {
                program.setPointsBalance(program.getPointsBalance() - points);
                program.setTotalPointsRedeemed(program.getTotalPointsRedeemed() + points);
            }
            program.setLastActivityDate(LocalDateTime.now());
            loyaltyProgramsRepository.save(program);
        }

        return convertToResponse(savedTransaction);
    }

    @Override
    public LoyaltyTransactionsResponse updateTransaction(Long id, LoyaltyTransactionsRequest request) {
        log.info("Updating loyalty transaction with ID: {}", id);

        LoyaltyTransactions existingTransaction = loyaltyTransactionsRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Loyalty transaction not found with ID: " + id));

        String transactionId = request.getTransactionId();
        if (transactionId == null || transactionId.trim().isEmpty()) {
            throw new BadRequestException("Transaction ID is required for update");
        }

        if (!existingTransaction.getTransactionId().equals(transactionId) &&
                loyaltyTransactionsRepository.existsByTransactionId(transactionId)) {
            throw new ConflictException("Transaction ID already exists: " + transactionId);
        }

        Customer customer = customerRepository.findById(request.getCustomerId())
                .orElseThrow(() -> new ResourceNotFoundException("Customer not found with ID: " + request.getCustomerId()));
        Restaurant restaurant = restaurantRepository.findById(request.getRestaurantId())
                .orElseThrow(() -> new ResourceNotFoundException("Restaurant not found with ID: " + request.getRestaurantId()));

        existingTransaction.setTransactionId(transactionId);
        existingTransaction.setCustomer(customer);
        existingTransaction.setRestaurant(restaurant);
        existingTransaction.setTransactionType(request.getTransactionType());
        existingTransaction.setPoints(request.getPoints());
        existingTransaction.setBalanceBefore(request.getBalanceBefore());
        existingTransaction.setBalanceAfter(request.getBalanceAfter());
        existingTransaction.setOrderId(request.getOrderId());
        existingTransaction.setInvoiceId(request.getInvoiceId());
        existingTransaction.setOfferId(request.getOfferId());
        existingTransaction.setDescription(request.getDescription());
        existingTransaction.setReference(request.getReference());
        existingTransaction.setExpiryDate(request.getExpiryDate());
        existingTransaction.setEarnedFrom(request.getEarnedFrom());
        existingTransaction.setRedeemedFor(request.getRedeemedFor());
        existingTransaction.setProcessedBy(request.getProcessedBy());
        existingTransaction.setProcessedAt(request.getProcessedAt());
        existingTransaction.setApprovalRequired(request.getApprovalRequired());
        existingTransaction.setApprovedBy(request.getApprovedBy());
        existingTransaction.setApprovedAt(request.getApprovedAt());
        existingTransaction.setReversalTransactionId(request.getReversalTransactionId());
        existingTransaction.setIsReversal(request.getIsReversal());
        existingTransaction.setNotes(request.getNotes());

        LoyaltyTransactions updatedTransaction = loyaltyTransactionsRepository.save(existingTransaction);
        log.info("Loyalty transaction updated successfully with ID: {}", updatedTransaction.getId());

        return convertToResponse(updatedTransaction);
    }

    @Override
    public LoyaltyTransactionsPageResponse getTransactionsWithFilters(String customerId, String restaurantId, String transactionType, int page, int size) {
        log.info("Fetching loyalty transactions with filters - customerId: {}, restaurantId: {}, transactionType: {}, page: {}, size: {}",
                customerId, restaurantId, transactionType, page, size);

        Pageable pageable = PageRequest.of(Math.max(0, page - 1), size);

        Specification<LoyaltyTransactions> spec = (root, query, criteriaBuilder) -> {
            Predicate predicate = criteriaBuilder.conjunction();

            if (customerId != null && !customerId.trim().isEmpty()) {
                try {
                    Long cid = Long.parseLong(customerId);
                    predicate = criteriaBuilder.and(predicate, criteriaBuilder.equal(root.get("customer").get("id"), cid));
                } catch (NumberFormatException e) {
                    // invalid, ignore
                }
            }

            if (restaurantId != null && !restaurantId.trim().isEmpty()) {
                try {
                    Long rid = Long.parseLong(restaurantId);
                    predicate = criteriaBuilder.and(predicate, criteriaBuilder.equal(root.get("restaurant").get("id"), rid));
                } catch (NumberFormatException e) {
                    // invalid, ignore
                }
            }

            if (transactionType != null && !transactionType.trim().isEmpty()) {
                predicate = criteriaBuilder.and(predicate, criteriaBuilder.equal(root.get("transactionType"), transactionType));
            }

            return predicate;
        };

        Page<LoyaltyTransactions> transactionPage = loyaltyTransactionsRepository.findAll(spec, pageable);

        List<LoyaltyTransactionsResponse> content = transactionPage.getContent().stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());

        return new LoyaltyTransactionsPageResponse(
                content,
                transactionPage.getNumber() + 1,
                transactionPage.getTotalPages(),
                transactionPage.getTotalElements()
        );
    }

    @Override
    public Optional<LoyaltyTransactionsResponse> getTransactionById(Long id) {
        log.info("Fetching loyalty transaction by ID: {}", id);
        return loyaltyTransactionsRepository.findById(id)
                .map(this::convertToResponse);
    }

    @Override
    public void deleteTransaction(Long id) {
        log.info("Deleting loyalty transaction with ID: {}", id);

        LoyaltyTransactions transaction = loyaltyTransactionsRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Loyalty transaction not found with ID: " + id));

        loyaltyTransactionsRepository.deleteById(id);
        log.info("Loyalty transaction deleted successfully with ID: {}", id);
    }

    private LoyaltyTransactionsResponse convertToResponse(LoyaltyTransactions transaction) {
        LoyaltyTransactionsResponse response = new LoyaltyTransactionsResponse();
        response.setId(transaction.getId());
        response.setTransactionId(transaction.getTransactionId());
        response.setCustomerId(transaction.getCustomer() != null ? transaction.getCustomer().getId() : null);
        response.setRestaurantId(transaction.getRestaurant() != null ? transaction.getRestaurant().getId() : null);
        response.setTransactionType(transaction.getTransactionType());
        response.setPoints(transaction.getPoints());
        response.setBalanceBefore(transaction.getBalanceBefore());
        response.setBalanceAfter(transaction.getBalanceAfter());
        response.setOrderId(transaction.getOrderId());
        response.setInvoiceId(transaction.getInvoiceId());
        response.setOfferId(transaction.getOfferId());
        response.setDescription(transaction.getDescription());
        response.setReference(transaction.getReference());
        response.setExpiryDate(transaction.getExpiryDate());
        response.setEarnedFrom(transaction.getEarnedFrom());
        response.setRedeemedFor(transaction.getRedeemedFor());
        response.setProcessedBy(transaction.getProcessedBy());
        response.setProcessedAt(transaction.getProcessedAt());
        response.setApprovalRequired(transaction.getApprovalRequired());
        response.setApprovedBy(transaction.getApprovedBy());
        response.setApprovedAt(transaction.getApprovedAt());
        response.setReversalTransactionId(transaction.getReversalTransactionId());
        response.setIsReversal(transaction.getIsReversal());
        response.setNotes(transaction.getNotes());
        response.setCreatedAt(transaction.getCreatedAt());
        return response;
    }
}
