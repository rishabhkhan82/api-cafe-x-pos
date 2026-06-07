package com.cafex.pos.service;

import com.cafex.pos.dto.LoyaltyProgramPageResponse;
import com.cafex.pos.dto.LoyaltyProgramRequest;
import com.cafex.pos.dto.LoyaltyProgramResponse;
import com.cafex.pos.entity.Customer;
import com.cafex.pos.entity.LoyaltyPrograms;
import com.cafex.pos.repository.CustomerRepository;
import com.cafex.pos.repository.LoyaltyProgramsRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import jakarta.persistence.criteria.Predicate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class LoyaltyProgramsServiceImpl implements LoyaltyProgramsService {

    private final LoyaltyProgramsRepository loyaltyProgramsRepository;
    private final CustomerRepository customerRepository;

    @Override
    public LoyaltyProgramResponse createProgram(LoyaltyProgramRequest request) {
        String programId = request.getProgramId();
        if (programId == null || programId.trim().isEmpty()) {
            String prefix = "LOYAL";
            List<String> existingIds = loyaltyProgramsRepository.findAll().stream()
                    .map(LoyaltyPrograms::getProgramId)
                    .filter(id -> id != null && id.startsWith(prefix))
                    .collect(Collectors.toList());
            int maxNum = existingIds.stream()
                    .map(id -> id.substring(prefix.length()))
                    .filter(s -> s.matches("\\d+"))
                    .mapToInt(Integer::parseInt)
                    .max()
                    .orElse(0);
            programId = prefix + String.format("%03d", maxNum + 1);
        }

        log.info("Saving new loyalty program: {}", programId);

        if (loyaltyProgramsRepository.existsByProgramId(programId)) {
            throw new RuntimeException("Program ID already exists: " + programId);
        }

        Customer customer = customerRepository.findById(request.getCustomerId())
                .orElseThrow(() -> new RuntimeException("Customer not found with ID: " + request.getCustomerId()));

        LoyaltyPrograms program = new LoyaltyPrograms();
        program.setProgramId(programId);
        program.setCustomer(customer);
        program.setProgramName(request.getProgramName());
        program.setPointsBalance(request.getPointsBalance() != null ? request.getPointsBalance() : 0);
        program.setTotalPointsEarned(request.getTotalPointsEarned() != null ? request.getTotalPointsEarned() : 0);
        program.setTotalPointsRedeemed(request.getTotalPointsRedeemed() != null ? request.getTotalPointsRedeemed() : 0);
        program.setTier(request.getTier());
        program.setTierExpiryDate(request.getTierExpiryDate());
        program.setLastActivityDate(request.getLastActivityDate());
        program.setIsActive(request.getIsActive() != null ? request.getIsActive() : true);
        program.setCreatedAt(request.getCreatedAt() != null ? request.getCreatedAt() : LocalDateTime.now());
        program.setCreatedBy(request.getCreatedBy());
        program.setUpdatedAt(LocalDateTime.now());
        program.setUpdatedBy(request.getUpdatedBy());

        LoyaltyPrograms savedProgram = loyaltyProgramsRepository.save(program);
        log.info("Loyalty program saved successfully with ID: {}", savedProgram.getId());

        return convertToResponse(savedProgram);
    }

    @Override
    public LoyaltyProgramResponse updateProgram(Long id, LoyaltyProgramRequest request) {
        log.info("Updating loyalty program with ID: {}", id);

        LoyaltyPrograms existingProgram = loyaltyProgramsRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Loyalty program not found with ID: " + id));

        String programId = request.getProgramId();
        if (programId == null || programId.trim().isEmpty()) {
            throw new RuntimeException("Program ID is required for update");
        }

        if (!existingProgram.getProgramId().equals(programId) &&
                loyaltyProgramsRepository.existsByProgramId(programId)) {
            throw new RuntimeException("Program ID already exists: " + programId);
        }

        existingProgram.setProgramId(programId);
        existingProgram.setProgramName(request.getProgramName());
        existingProgram.setPointsBalance(request.getPointsBalance() != null ? request.getPointsBalance() : 0);
        existingProgram.setTotalPointsEarned(request.getTotalPointsEarned() != null ? request.getTotalPointsEarned() : 0);
        existingProgram.setTotalPointsRedeemed(request.getTotalPointsRedeemed() != null ? request.getTotalPointsRedeemed() : 0);
        existingProgram.setTier(request.getTier());
        existingProgram.setTierExpiryDate(request.getTierExpiryDate());
        existingProgram.setLastActivityDate(request.getLastActivityDate());
        existingProgram.setIsActive(request.getIsActive() != null ? request.getIsActive() : true);
        existingProgram.setUpdatedAt(LocalDateTime.now());
        existingProgram.setCreatedBy(request.getCreatedBy());
        existingProgram.setUpdatedBy(request.getUpdatedBy());

        LoyaltyPrograms updatedProgram = loyaltyProgramsRepository.save(existingProgram);
        log.info("Loyalty program updated successfully with ID: {}", updatedProgram.getId());

        return convertToResponse(updatedProgram);
    }

    @Override
    public LoyaltyProgramPageResponse getProgramsWithFilters(String customerId, String isActive, int page, int size) {
        log.info("Fetching loyalty programs with filters - customerId: {}, isActive: {}, page: {}, size: {}",
                customerId, isActive, page, size);

        Pageable pageable = PageRequest.of(Math.max(0, page - 1), size);

        Specification<LoyaltyPrograms> spec = (root, query, criteriaBuilder) -> {
            Predicate predicate = criteriaBuilder.conjunction();

            if (customerId != null && !customerId.trim().isEmpty()) {
                try {
                    Long cid = Long.parseLong(customerId);
                    predicate = criteriaBuilder.and(predicate, criteriaBuilder.equal(root.get("customer").get("id"), cid));
                } catch (NumberFormatException e) {
                    // invalid, ignore
                }
            }

            if (isActive != null && !isActive.trim().isEmpty()) {
                Boolean active = "true".equals(isActive);
                predicate = criteriaBuilder.and(predicate, criteriaBuilder.equal(root.get("isActive"), active));
            }

            return predicate;
        };

        Page<LoyaltyPrograms> programPage = loyaltyProgramsRepository.findAll(spec, pageable);

        List<LoyaltyProgramResponse> content = programPage.getContent().stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());

        return new LoyaltyProgramPageResponse(
                content,
                programPage.getNumber() + 1,
                programPage.getTotalPages(),
                programPage.getTotalElements()
        );
    }

    @Override
    public Optional<LoyaltyProgramResponse> getProgramById(Long id) {
        log.info("Fetching loyalty program by ID: {}", id);
        return loyaltyProgramsRepository.findById(id)
                .map(this::convertToResponse);
    }

    @Override
    public void deleteProgram(Long id) {
        log.info("Deleting loyalty program with ID: {}", id);

        LoyaltyPrograms program = loyaltyProgramsRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Loyalty program not found with ID: " + id));

        loyaltyProgramsRepository.deleteById(id);
        log.info("Loyalty program deleted successfully with ID: {}", id);
    }

    private LoyaltyProgramResponse convertToResponse(LoyaltyPrograms program) {
        LoyaltyProgramResponse response = new LoyaltyProgramResponse();
        response.setId(program.getId());
        response.setProgramId(program.getProgramId());
        response.setCustomerId(program.getCustomer() != null ? program.getCustomer().getId() : null);
        response.setCustomerName(program.getCustomer() != null ? program.getCustomer().getName() : null);
        response.setProgramName(program.getProgramName());
        response.setPointsBalance(program.getPointsBalance());
        response.setTotalPointsEarned(program.getTotalPointsEarned());
        response.setTotalPointsRedeemed(program.getTotalPointsRedeemed());
        response.setTier(program.getTier());
        response.setTierExpiryDate(program.getTierExpiryDate());
        response.setLastActivityDate(program.getLastActivityDate());
        response.setIsActive(program.getIsActive());
        response.setCreatedAt(program.getCreatedAt());
        response.setUpdatedAt(program.getUpdatedAt());
        response.setCreatedBy(program.getCreatedBy());
        response.setUpdatedBy(program.getUpdatedBy());
        return response;
    }
}
