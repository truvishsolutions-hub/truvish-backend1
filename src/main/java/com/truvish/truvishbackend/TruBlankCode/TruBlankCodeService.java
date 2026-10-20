package com.truvish.truvishbackend.TruBlankCode;

import com.truvish.truvishbackend.TruBlankCode.dto.ActivateBlankCodeRequest;
import com.truvish.truvishbackend.TruBlankCode.dto.AssignClientRequest;
import com.truvish.truvishbackend.TruBlankCode.dto.GenerateBlankCodeRequest;
import com.truvish.truvishbackend.TruBlankCode.dto.UpdateBlankCodeRequest;
import com.truvish.truvishbackend.TruBlankCode.response.TruBlankCodeResponse;
import com.truvish.truvishbackend.client.Client;
import com.truvish.truvishbackend.client.ClientRepository;
import com.truvish.truvishbackend.redemption.UserRedemption;
import com.truvish.truvishbackend.redemption.UserRedemptionRepository;
import com.truvish.truvishbackend.wallet.service.WalletTransactionService;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@Transactional
public class TruBlankCodeService {

    private final TruBlankCodeRepository repository;
    private final ClientRepository clientRepository;
    private final WalletTransactionService walletTransactionService;
    private final UserRedemptionRepository userRedemptionRepository;

    private static final SecureRandom RANDOM =
            new SecureRandom();

    private static final long SERIAL_BASE =
            324243145000L;

    private static final long DEFAULT_ADMIN_ID =
            1L;


    // =========================================================
    // CONSTRUCTOR
    // =========================================================

    public TruBlankCodeService(
            TruBlankCodeRepository repository,
            ClientRepository clientRepository,
            WalletTransactionService walletTransactionService,
            UserRedemptionRepository userRedemptionRepository
    ) {
        this.repository = repository;
        this.clientRepository = clientRepository;
        this.walletTransactionService = walletTransactionService;
        this.userRedemptionRepository = userRedemptionRepository;
    }


    // =========================================================
    // GENERATE BLANK CODES
    // =========================================================

    public List<TruBlankCodeResponse> generateCodes(
            GenerateBlankCodeRequest request,
            Long adminId
    ) {

        if (request == null) {
            throw new RuntimeException(
                    "Generate request cannot be null"
            );
        }

        if (request.getQuantity() == null) {
            throw new RuntimeException(
                    "Quantity is required"
            );
        }

        if (request.getQuantity() < 1) {
            throw new RuntimeException(
                    "Quantity must be at least 1"
            );
        }

        if (adminId == null) {
            throw new RuntimeException(
                    "adminId is required"
            );
        }

        int quantity =
                request.getQuantity();

        Long generationNumber =
                repository
                        .findTopByOrderByGenerationNumberDesc()
                        .map(code -> {

                            Long last =
                                    code.getGenerationNumber();

                            return last == null
                                    ? 1L
                                    : last + 1;
                        })
                        .orElse(1L);

        long nextSerial =
                getNextSerialNumber();

        LocalDateTime now =
                LocalDateTime.now();

        List<TruBlankCode> generatedCodes =
                new ArrayList<>();


        for (int i = 0; i < quantity; i++) {

            TruBlankCode code =
                    new TruBlankCode();


            // =================================================
            // BASIC
            // =================================================

            code.setGenerationNumber(
                    generationNumber
            );

            code.setSerialNumber(
                    String.valueOf(
                            nextSerial + i
                    )
            );

            code.setCodeNumber(
                    generateCodeNumber()
            );

            code.setReferenceNumber(
                    generateReferenceNumber()
            );


            // =================================================
            // STATUS
            // =================================================

            code.setStatus(
                    TruBlankCodeStatus.INACTIVE
            );


            // =================================================
            // REWARD
            // =================================================

            code.setDenomination(null);

            code.setValidityMonths(null);

            code.setExpiryDate(null);


            // =================================================
            // TRUCARD BRAND
            // =================================================

            code.setBrandNames(
                    new ArrayList<>()
            );

            code.setBrandCategory(null);

            code.setThemeName(null);

            code.setThemeImg(null);


            // =================================================
            // CLIENT
            // =================================================

            code.setClientId(null);

            code.setClientName(null);

            code.setClientImg(null);

            code.setClientBrand(
                    new ArrayList<>()
            );

            code.setClientCategory(null);

            code.setClientTheme(null);

            code.setClientThemeImg(null);


            // =================================================
            // BALANCE
            // =================================================

            code.setClientBalanceBeforeActivation(
                    null
            );

            code.setClientBalanceAfterActivation(
                    null
            );


            // =================================================
            // TRUPROMISE
            // =================================================

            resetTruPromise(code);


            // =================================================
            // AUDIT
            // =================================================

            code.setCreatedBy(adminId);

            code.setCreatedAt(now);

            code.setUpdatedAt(now);

            code.setActivatedAt(null);

            code.setActivatedBy(null);

            code.setRedeemedAt(null);


            generatedCodes.add(code);
        }


        List<TruBlankCode> savedCodes =
                repository.saveAll(
                        generatedCodes
                );


        return savedCodes
                .stream()
                .map(this::toResponse)
                .toList();
    }


    // =========================================================
    // ASSIGN CLIENT
    // =========================================================

    public TruBlankCodeResponse assignClient(
            Long id,
            AssignClientRequest request
    ) {

        validateId(id);

        if (request == null) {
            throw new RuntimeException(
                    "Assign client request cannot be null"
            );
        }

        if (request.getClientId() == null) {
            throw new RuntimeException(
                    "clientId is required"
            );
        }

        TruBlankCode code =
                repository
                        .findById(id)
                        .orElseThrow(() ->
                                new RuntimeException(
                                        "Blank code not found: "
                                                + id
                                )
                        );


        if (code.getStatus()
                != TruBlankCodeStatus.INACTIVE) {

            throw new RuntimeException(
                    "Only INACTIVE code can be assigned"
            );
        }


        Client client =
                clientRepository
                        .findById(
                                request.getClientId()
                        )
                        .orElseThrow(() ->
                                new RuntimeException(
                                        "Client not found: "
                                                + request.getClientId()
                                )
                        );


        // =====================================================
        // CLIENT SNAPSHOT
        // =====================================================

        code.setClientId(
                client.getId()
        );

        code.setClientName(
                cleanString(
                        client.getClientName()
                )
        );

        code.setClientImg(
                cleanString(
                        client.getLogoImg()
                )
        );


        // =====================================================
        // CLIENT BRAND
        // =====================================================

        List<String> clientBrands =
                request.getClientBrand();

        if (clientBrands == null) {
            clientBrands =
                    new ArrayList<>();
        }

        code.setClientBrand(
                new ArrayList<>(
                        clientBrands
                )
        );


        // =====================================================
        // CLIENT CATEGORY
        // =====================================================

        code.setClientCategory(
                cleanString(
                        request.getClientCategory()
                )
        );


        // =====================================================
        // CLIENT THEME
        // =====================================================

        code.setClientTheme(
                cleanString(
                        request.getClientTheme()
                )
        );

        code.setClientThemeImg(
                cleanString(
                        request.getClientThemeImg()
                )
        );


        code.setUpdatedAt(
                LocalDateTime.now()
        );


        return toResponse(
                repository.save(code)
        );
    }


    // =========================================================
    // UPDATE CODE
    // =========================================================

    public TruBlankCodeResponse updateCode(
            Long id,
            UpdateBlankCodeRequest request
    ) {

        validateId(id);

        if (request == null) {
            throw new RuntimeException(
                    "Update request cannot be null"
            );
        }

        TruBlankCode code =
                repository
                        .findById(id)
                        .orElseThrow(() ->
                                new RuntimeException(
                                        "Blank code not found: "
                                                + id
                                )
                        );


        if (code.getStatus()
                != TruBlankCodeStatus.INACTIVE) {

            throw new RuntimeException(
                    "Only INACTIVE code can be updated"
            );
        }


        // =====================================================
        // DENOMINATION
        // =====================================================

        if (request.getDenomination() != null) {

            Double value =
                    request.getDenomination();

            if (value <= 0) {
                throw new RuntimeException(
                        "Denomination must be greater than 0"
                );
            }

            if (value % 1 != 0) {
                throw new RuntimeException(
                        "Denomination must be a whole number"
                );
            }

            code.setDenomination(
                    value.longValue()
            );
        }


        // =====================================================
        // VALIDITY
        // =====================================================

        if (request.getValidityMonths() != null) {

            if (request.getValidityMonths() < 1) {
                throw new RuntimeException(
                        "Validity months must be greater than 0"
                );
            }

            code.setValidityMonths(
                    request.getValidityMonths()
            );
        }


        // =====================================================
        // TRUCARD BRAND
        // =====================================================

        if (request.getBrandNames() != null) {

            code.setBrandNames(
                    new ArrayList<>(
                            request.getBrandNames()
                    )
            );
        }


        if (request.getBrandCategory() != null) {

            code.setBrandCategory(
                    cleanString(
                            request.getBrandCategory()
                    )
            );
        }


        // =====================================================
        // THEME
        // =====================================================

        if (request.getThemeName() != null) {

            code.setThemeName(
                    cleanString(
                            request.getThemeName()
                    )
            );
        }


        if (request.getThemeImg() != null) {

            code.setThemeImg(
                    cleanString(
                            request.getThemeImg()
                    )
            );
        }


        // =====================================================
        // TRUPROMISE
        // The update request carries the percentage; the backend
        // calculates and persists the amount from denomination.
        // This endpoint only permits INACTIVE codes above.
        // =====================================================

        if (request.getTrupromisePercentage() != null) {
            BigDecimal percentage = request.getTrupromisePercentage()
                    .setScale(2, RoundingMode.HALF_UP);

            if (percentage.compareTo(BigDecimal.ZERO) < 0
                    || percentage.compareTo(new BigDecimal("100.00")) > 0) {
                throw new RuntimeException(
                        "TruPromise percentage must be between 0 and 100"
                );
            }

            if (code.getDenomination() == null || code.getDenomination() <= 0) {
                throw new RuntimeException(
                        "Set a valid denomination before configuring TruPromise"
                );
            }

            code.setTrupromisePercentage(percentage);

            applyTruPromiseOnActivation(
                    code,
                    BigDecimal.valueOf(code.getDenomination())
                            .setScale(2, RoundingMode.HALF_UP),
                    LocalDateTime.now()
            );
        }

        code.setUpdatedAt(
                LocalDateTime.now()
        );


        return toResponse(
                repository.save(code)
        );
    }


    // =========================================================
    // ACTIVATE CODE
    // =========================================================
    //
    // WALLET:
    //
    // Voucher ₹100
    // Client balance ₹1000
    //
    // Wallet debit = ₹100
    // Client balance = ₹900
    //
    // TruPromise 10%
    // Promise = ₹10
    //
    // IMPORTANT:
    // TruPromise ₹10 is NOT deducted separately.
    //
    // STATUS:
    // PROMISED
    //
    // =========================================================

    public TruBlankCodeResponse activateCode(
            Long id,
            ActivateBlankCodeRequest request
    ) {

        validateId(id);

        if (request == null) {
            throw new RuntimeException(
                    "Activation request cannot be null"
            );
        }

        if (request.getClientId() == null) {
            throw new RuntimeException(
                    "clientId is required"
            );
        }


        TruBlankCode code =
                repository
                        .findById(id)
                        .orElseThrow(() ->
                                new RuntimeException(
                                        "Blank code not found: "
                                                + id
                                )
                        );


        // =====================================================
        // RESOLVE ADMIN ID
        // =====================================================

        Long resolvedAdminId =
                request.getAdminId();

        if (resolvedAdminId == null) {
            resolvedAdminId =
                    code.getCreatedBy();
        }

        if (resolvedAdminId == null) {
            resolvedAdminId =
                    DEFAULT_ADMIN_ID;
        }


        // =====================================================
        // STATUS
        // =====================================================

        if (code.getStatus()
                != TruBlankCodeStatus.INACTIVE) {

            throw new RuntimeException(
                    "Only INACTIVE code can be activated"
            );
        }


        // =====================================================
        // ASSIGNED CLIENT CHECK
        // =====================================================

        if (code.getClientId() == null) {

            throw new RuntimeException(
                    "Client must be assigned before activation"
            );
        }


        if (!code.getClientId().equals(
                request.getClientId()
        )) {

            throw new RuntimeException(
                    "Activation client does not match assigned client"
            );
        }


        // =====================================================
        // DENOMINATION
        // =====================================================

        if (code.getDenomination() == null
                || code.getDenomination() < 1) {

            throw new RuntimeException(
                    "Denomination must be set and > 0 before activation"
            );
        }


        // =====================================================
        // VALIDITY
        // =====================================================

        if (code.getValidityMonths() == null
                || code.getValidityMonths() < 1) {

            throw new RuntimeException(
                    "Validity months must be set and > 0 before activation"
            );
        }


        // =====================================================
        // CLIENT
        // =====================================================

        Client client =
                clientRepository
                        .findById(
                                code.getClientId()
                        )
                        .orElseThrow(() ->
                                new RuntimeException(
                                        "Assigned client not found: "
                                                + code.getClientId()
                                )
                        );


        // =====================================================
        // BALANCE BEFORE
        // =====================================================

        BigDecimal balanceBefore =
                client.getBalance() != null
                        ? client.getBalance()
                        : BigDecimal.ZERO;

        balanceBefore =
                balanceBefore.setScale(
                        2,
                        RoundingMode.HALF_UP
                );


        BigDecimal denomination =
                BigDecimal.valueOf(
                        code.getDenomination()
                ).setScale(
                        2,
                        RoundingMode.HALF_UP
                );


        // =====================================================
        // BALANCE CHECK
        // =====================================================

        if (balanceBefore.compareTo(
                denomination
        ) < 0) {

            throw new RuntimeException(
                    "Insufficient client wallet balance. "
                            + "Available: "
                            + balanceBefore
                            + ", Required: "
                            + denomination
            );
        }


        // =====================================================
        // DEDUCT WALLET
        // =====================================================

        int updatedRows =
                clientRepository.deductBalance(
                        client.getId(),
                        denomination
                );


        if (updatedRows != 1) {

            throw new RuntimeException(
                    "Unable to deduct client wallet balance. "
                            + "Client balance may have changed."
            );
        }


        // =====================================================
        // WALLET TRANSACTION
        // =====================================================

        walletTransactionService.recordDebit(
                client.getId(),
                denomination,
                "TruBlankCode activation - "
                        + code.getCodeNumber(),
                "TRU_BLANK_CODE",
                String.valueOf(
                        code.getId()
                )
        );


        // =====================================================
        // BALANCE AFTER
        // =====================================================

        BigDecimal balanceAfter =
                balanceBefore
                        .subtract(denomination)
                        .setScale(
                                2,
                                RoundingMode.HALF_UP
                        );


        LocalDateTime activatedAt =
                LocalDateTime.now();


        // =====================================================
        // CLIENT SNAPSHOT
        // =====================================================

        code.setClientId(
                client.getId()
        );

        code.setClientName(
                cleanString(
                        client.getClientName()
                )
        );

        code.setClientImg(
                cleanString(
                        client.getLogoImg()
                )
        );


        // =====================================================
        // CLIENT BRAND
        // =====================================================

        List<String> clientBrands =
                request.getClientBrand();

        if (clientBrands != null
                && !clientBrands.isEmpty()) {

            code.setClientBrand(
                    new ArrayList<>(
                            clientBrands
                    )
            );

        } else if (
                code.getClientBrand() == null
        ) {

            code.setClientBrand(
                    new ArrayList<>()
            );
        }


        // =====================================================
        // CLIENT CATEGORY
        // =====================================================

        if (request.getClientCategory() != null) {

            code.setClientCategory(
                    cleanString(
                            request.getClientCategory()
                    )
            );
        }


        // =====================================================
        // CLIENT THEME
        // =====================================================

        if (request.getClientTheme() != null) {

            code.setClientTheme(
                    cleanString(
                            request.getClientTheme()
                    )
            );
        }


        if (request.getClientThemeImg() != null) {

            code.setClientThemeImg(
                    cleanString(
                            request.getClientThemeImg()
                    )
            );
        }


        // =====================================================
        // BALANCE SNAPSHOT
        // =====================================================

        code.setClientBalanceBeforeActivation(
                balanceBefore
        );

        code.setClientBalanceAfterActivation(
                balanceAfter
        );


        // =====================================================
        // ACTIVATION
        // =====================================================

        code.setActivatedAt(
                activatedAt
        );

        code.setActivatedBy(
                resolvedAdminId
        );

        code.setExpiryDate(
                activatedAt.plusMonths(
                        code.getValidityMonths()
                )
        );

        code.setStatus(
                TruBlankCodeStatus.ACTIVE
        );

        code.setUpdatedAt(
                activatedAt
        );


        // =====================================================
        // TRUPROMISE
        // =====================================================
        //
        // Promise is calculated from voucher denomination.
        //
        // Example:
        //
        // ₹100 × 10% = ₹10
        //
        // Wallet debit remains ₹100.
        //
        // Promise ₹10 is NOT deducted separately.
        //
        // =====================================================

        applyTruPromiseOnActivation(
                code,
                denomination,
                activatedAt
        );


        return toResponse(
                repository.save(code)
        );
    }


    // =========================================================
    // REDEEM
    // =========================================================
    //
    // ACTIVE
    //   ↓
    // REDEEMED
    //
    // PROMISED
    //   ↓
    // PAID
    //
    // No additional wallet debit.
    // =========================================================

    public TruBlankCodeResponse redeemCode(
            Long id
    ) {

        validateId(id);

        TruBlankCode code =
                repository
                        .findById(id)
                        .orElseThrow(() ->
                                new RuntimeException(
                                        "Blank code not found: "
                                                + id
                                )
                        );


        if (code.getStatus()
                != TruBlankCodeStatus.ACTIVE) {

            throw new RuntimeException(
                    "Only ACTIVE code can be redeemed"
            );
        }


        LocalDateTime now =
                LocalDateTime.now();


        if (isExpired(code, now)) {

            markExpired(
                    code,
                    now
            );

            throw new RuntimeException(
                    "Code has expired"
            );
        }


        code.setStatus(
                TruBlankCodeStatus.REDEEMED
        );

        code.setRedeemedAt(now);

        code.setUpdatedAt(now);


        // =====================================================
        // TRUPROMISE PAID
        // =====================================================

        markTruPromisePaid(
                code,
                now
        );


        return toResponse(
                repository.save(code)
        );
    }


    // =========================================================
    // DEACTIVATE
    // =========================================================

    public TruBlankCodeResponse deactivateCode(
            Long id
    ) {
        validateId(id);

        TruBlankCode code = repository.findByIdForUpdate(id)
                .orElseThrow(() -> new RuntimeException(
                        "Blank code not found: " + id));

        if (code.getStatus() == TruBlankCodeStatus.REDEEMED) {
            throw new RuntimeException("Redeemed code cannot be deactivated");
        }
        if (code.getStatus() == TruBlankCodeStatus.DEACTIVATED) {
            throw new RuntimeException("Blank code is already deactivated");
        }
        if (code.getStatus() == TruBlankCodeStatus.CANCELLED) {
            throw new RuntimeException("Cancelled code cannot be deactivated");
        }
        if (code.getStatus() != TruBlankCodeStatus.ACTIVE
                && code.getStatus() != TruBlankCodeStatus.EXPIRED) {
            throw new RuntimeException(
                    "Only ACTIVE or EXPIRED code can be deactivated");
        }

        LocalDateTime now = LocalDateTime.now();

        // Refund only the exact TruPromise amount saved on this physical code.
        // Never refund the full voucher denomination. The helper is idempotent
        // because it changes PROMISED to BackToWallet in this transaction.
        reverseTruPromiseToWallet(code, now);

        code.setStatus(TruBlankCodeStatus.DEACTIVATED);
        code.setUpdatedAt(now);

        return toResponse(repository.save(code));
    }


    // =========================================================
    // CANCEL
    // =========================================================
    public TruBlankCodeResponse cancelCode(
            Long id
    ) {

        validateId(id);

        TruBlankCode code =
                repository
                        .findById(id)
                        .orElseThrow(() ->
                                new RuntimeException(
                                        "Blank code not found: "
                                                + id
                                )
                        );


        if (code.getStatus()
                == TruBlankCodeStatus.CANCELLED) {

            throw new RuntimeException(
                    "Blank code is already cancelled"
            );
        }


        if (code.getStatus()
                == TruBlankCodeStatus.REDEEMED) {

            throw new RuntimeException(
                    "Redeemed code cannot be cancelled"
            );
        }


        LocalDateTime now =
                LocalDateTime.now();


        // =====================================================
        // TRUPROMISE REVERSAL
        // =====================================================

        reverseTruPromiseToWallet(
                code,
                now
        );


        code.setStatus(
                TruBlankCodeStatus.CANCELLED
        );

        code.setUpdatedAt(now);


        return toResponse(
                repository.save(code)
        );
    }


    // =========================================================
    // GET ALL
    // =========================================================

    @Transactional(readOnly = true)
    public Page<TruBlankCodeResponse> getAllCodes(
            Pageable pageable
    ) {

        refreshExpiredCodes();

        return repository
                .findAllByOrderByCreatedAtDesc(
                        pageable
                )
                .map(this::toResponse);
    }


    // =========================================================
    // GET BY CLIENT
    // =========================================================

    @Transactional(readOnly = true)
    public List<TruBlankCodeResponse> getCodesByClient(
            Long clientId
    ) {

        validateId(clientId);

        refreshExpiredCodes();

        return repository
                .findByClientIdOrderByCreatedAtDesc(
                        clientId
                )
                .stream()
                .map(this::toResponse)
                .toList();
    }


    @Transactional(readOnly = true)
    public Page<TruBlankCodeResponse> getCodesByClient(
            Long clientId,
            Pageable pageable
    ) {

        validateId(clientId);

        refreshExpiredCodes();

        return repository
                .findByClientIdOrderByCreatedAtDesc(
                        clientId,
                        pageable
                )
                .map(this::toResponse);
    }


    // =========================================================
    // GET BY ID
    // =========================================================

    @Transactional(readOnly = true)
    public TruBlankCodeResponse getById(
            Long id
    ) {

        validateId(id);

        TruBlankCode code =
                repository
                        .findById(id)
                        .orElseThrow(() ->
                                new RuntimeException(
                                        "Blank code not found: "
                                                + id
                                )
                        );

        refreshExpiredCode(code);

        return toResponse(code);
    }


    // =========================================================
    // GET BY SERIAL
    // =========================================================

    @Transactional(readOnly = true)
    public TruBlankCodeResponse getBySerial(
            String serialNumber
    ) {

        validateSearchValue(
                serialNumber,
                "Serial number"
        );

        String value =
                serialNumber.trim();

        TruBlankCode code =
                repository
                        .findBySerialNumber(value)
                        .orElseThrow(() ->
                                new RuntimeException(
                                        "Blank code not found for serial: "
                                                + value
                                )
                        );

        refreshExpiredCode(code);

        return toResponse(code);
    }


    // =========================================================
    // GET BY REFERENCE
    // =========================================================

    @Transactional(readOnly = true)
    public TruBlankCodeResponse getByReference(
            String referenceNumber
    ) {

        validateSearchValue(
                referenceNumber,
                "Reference number"
        );

        String value =
                referenceNumber.trim();

        TruBlankCode code =
                repository
                        .findByReferenceNumber(value)
                        .orElseThrow(() ->
                                new RuntimeException(
                                        "Blank code not found for reference: "
                                                + value
                                )
                        );

        refreshExpiredCode(code);

        return toResponse(code);
    }


    // =========================================================
    // GET BY CODE NUMBER
    // =========================================================

    @Transactional(readOnly = true)
    public TruBlankCodeResponse getByCodeNumber(
            String codeNumber
    ) {

        validateSearchValue(
                codeNumber,
                "Code number"
        );

        String value =
                codeNumber.trim();

        TruBlankCode code =
                repository
                        .findByCodeNumber(value)
                        .orElseThrow(() ->
                                new RuntimeException(
                                        "Blank code not found for code: "
                                                + value
                                )
                        );

        refreshExpiredCode(code);

        return toResponse(code);
    }


    // =========================================================
    // SEARCH
    // =========================================================

    @Transactional(readOnly = true)
    public TruBlankCodeResponse search(
            String value
    ) {

        validateSearchValue(
                value,
                "Search value"
        );

        String searchValue =
                value.trim();


        var byCode =
                repository.findByCodeNumber(
                        searchValue
                );

        if (byCode.isPresent()) {

            TruBlankCode code =
                    byCode.get();

            refreshExpiredCode(code);

            return toResponse(code);
        }


        var bySerial =
                repository.findBySerialNumber(
                        searchValue
                );

        if (bySerial.isPresent()) {

            TruBlankCode code =
                    bySerial.get();

            refreshExpiredCode(code);

            return toResponse(code);
        }


        var byReference =
                repository.findByReferenceNumber(
                        searchValue
                );

        if (byReference.isPresent()) {

            TruBlankCode code =
                    byReference.get();

            refreshExpiredCode(code);

            return toResponse(code);
        }


        throw new RuntimeException(
                "Blank code not found for: "
                        + searchValue
        );
    }


    // =========================================================
    // STATUS
    // =========================================================

    @Transactional(readOnly = true)
    public Page<TruBlankCodeResponse> getByStatus(
            TruBlankCodeStatus status,
            Pageable pageable
    ) {

        if (status == null) {
            throw new RuntimeException(
                    "Status is required"
            );
        }

        refreshExpiredCodes();

        return repository
                .findByStatusOrderByCreatedAtDesc(
                        status,
                        pageable
                )
                .map(this::toResponse);
    }


    // =========================================================
    // COUNTS
    // =========================================================

    @Transactional(readOnly = true)
    public long countInactive() {

        return repository.countByStatus(
                TruBlankCodeStatus.INACTIVE
        );
    }


    @Transactional(readOnly = true)
    public long countActive() {

        refreshExpiredCodes();

        return repository.countByStatus(
                TruBlankCodeStatus.ACTIVE
        );
    }


    @Transactional(readOnly = true)
    public long countRedeemed() {

        return repository.countByStatus(
                TruBlankCodeStatus.REDEEMED
        );
    }


    @Transactional(readOnly = true)
    public long countExpired() {

        refreshExpiredCodes();

        return repository.countByStatus(
                TruBlankCodeStatus.EXPIRED
        );
    }


    @Transactional(readOnly = true)
    public long countCancelled() {

        return repository.countByStatus(
                TruBlankCodeStatus.CANCELLED
        );
    }


    // =========================================================
    // CLIENT COUNTS
    // =========================================================

    @Transactional(readOnly = true)
    public long countClientCodes(
            Long clientId
    ) {

        validateId(clientId);

        return repository.countByClientId(
                clientId
        );
    }


    @Transactional(readOnly = true)
    public long countClientInactive(
            Long clientId
    ) {

        validateId(clientId);

        return repository.countByClientIdAndStatus(
                clientId,
                TruBlankCodeStatus.INACTIVE
        );
    }


    @Transactional(readOnly = true)
    public long countClientActive(
            Long clientId
    ) {

        validateId(clientId);

        refreshExpiredCodes();

        return repository.countClientActiveCodes(
                clientId,
                TruBlankCodeStatus.ACTIVE,
                LocalDateTime.now()
        );
    }


    @Transactional(readOnly = true)
    public long countClientRedeemed(
            Long clientId
    ) {

        validateId(clientId);

        return repository.countByClientIdAndStatus(
                clientId,
                TruBlankCodeStatus.REDEEMED
        );
    }


    @Transactional(readOnly = true)
    public long countClientExpired(
            Long clientId
    ) {

        validateId(clientId);

        refreshExpiredCodes();

        return repository.countClientExpiredCodes(
                clientId,
                TruBlankCodeStatus.EXPIRED,
                TruBlankCodeStatus.ACTIVE,
                LocalDateTime.now()
        );
    }


    // =========================================================
    // CLIENT VALUES
    // =========================================================

    @Transactional(readOnly = true)
    public BigDecimal clientIssuedValue(
            Long clientId
    ) {

        validateId(clientId);

        return safeAmount(
                repository.sumDenominationByClientId(
                        clientId
                )
        );
    }


    @Transactional(readOnly = true)
    public BigDecimal clientInactiveValue(
            Long clientId
    ) {

        validateId(clientId);

        return safeAmount(
                repository.sumDenominationByClientIdAndStatus(
                        clientId,
                        TruBlankCodeStatus.INACTIVE
                )
        );
    }


    @Transactional(readOnly = true)
    public BigDecimal clientActiveValue(
            Long clientId
    ) {

        validateId(clientId);

        refreshExpiredCodes();

        return safeAmount(
                repository.sumClientActiveValue(
                        clientId,
                        TruBlankCodeStatus.ACTIVE,
                        LocalDateTime.now()
                )
        );
    }


    @Transactional(readOnly = true)
    public BigDecimal clientRedeemedValue(
            Long clientId
    ) {

        validateId(clientId);

        return safeAmount(
                repository.sumDenominationByClientIdAndStatus(
                        clientId,
                        TruBlankCodeStatus.REDEEMED
                )
        );
    }


    @Transactional(readOnly = true)
    public BigDecimal clientExpiredValue(
            Long clientId
    ) {

        validateId(clientId);

        refreshExpiredCodes();

        return safeAmount(
                repository.sumClientExpiredValue(
                        clientId,
                        TruBlankCodeStatus.EXPIRED,
                        TruBlankCodeStatus.ACTIVE,
                        LocalDateTime.now()
                )
        );
    }


    // =========================================================
    // REFRESH EXPIRED
    // =========================================================

    @Transactional
    public int refreshExpiredCodes() {

        LocalDateTime now =
                LocalDateTime.now();

        List<TruBlankCode> expiredCodes =
                repository.findExpiredActiveCodes(
                        TruBlankCodeStatus.ACTIVE,
                        now
                );


        if (expiredCodes.isEmpty()) {
            return 0;
        }


        for (TruBlankCode code :
                expiredCodes) {

            // =================================================
            // TRUPROMISE
            // =================================================

            reverseTruPromiseToWallet(
                    code,
                    now
            );


            code.setStatus(
                    TruBlankCodeStatus.EXPIRED
            );

            code.setUpdatedAt(now);
        }


        repository.saveAll(
                expiredCodes
        );


        return expiredCodes.size();
    }


    // =========================================================
    // REFRESH SINGLE
    // =========================================================

    private void refreshExpiredCode(
            TruBlankCode code
    ) {

        if (code == null) {
            return;
        }


        LocalDateTime now =
                LocalDateTime.now();


        if (isExpired(code, now)) {

            markExpired(
                    code,
                    now
            );
        }
    }


    // =========================================================
    // IS EXPIRED
    // =========================================================

    private boolean isExpired(
            TruBlankCode code,
            LocalDateTime now
    ) {

        return code.getStatus()
                == TruBlankCodeStatus.ACTIVE

                && code.getExpiryDate() != null

                && !now.isBefore(
                code.getExpiryDate()
        );
    }


    // =========================================================
    // MARK EXPIRED
    // =========================================================

    private void markExpired(
            TruBlankCode code,
            LocalDateTime now
    ) {

        reverseTruPromiseToWallet(
                code,
                now
        );

        code.setStatus(
                TruBlankCodeStatus.EXPIRED
        );

        code.setUpdatedAt(now);

        repository.save(code);
    }


    // =========================================================
    // TRUPROMISE
    // APPLY ON ACTIVATION
    // =========================================================

    private void applyTruPromiseOnActivation(
            TruBlankCode code,
            BigDecimal voucherAmount,
            LocalDateTime now
    ) {

        if (code == null) {
            return;
        }


        BigDecimal percentage =
                code.getTrupromisePercentage();


        // =====================================================
        // NO PROMISE
        // =====================================================

        if (percentage == null
                || percentage.compareTo(
                BigDecimal.ZERO
        ) <= 0) {

            code.setTrupromisePercentage(
                    BigDecimal.ZERO.setScale(
                            2,
                            RoundingMode.HALF_UP
                    )
            );

            code.setTrupromiseAmount(
                    BigDecimal.ZERO.setScale(
                            2,
                            RoundingMode.HALF_UP
                    )
            );

            code.setTrupromiseStatus(null);

            code.setTrupromiseCreatedAt(null);

            code.setTrupromisePaidAt(null);

            code.setTrupromiseReversedAt(null);

            return;
        }


        // =====================================================
        // VALIDATE %
        // =====================================================

        percentage =
                percentage.setScale(
                        2,
                        RoundingMode.HALF_UP
                );


        if (percentage.compareTo(
                new BigDecimal("100")
        ) > 0) {

            throw new RuntimeException(
                    "TruPromise percentage cannot be greater than 100"
            );
        }


        // =====================================================
        // CALCULATE PROMISE
        // =====================================================

        BigDecimal promiseAmount =
                voucherAmount
                        .multiply(percentage)
                        .divide(
                                new BigDecimal("100"),
                                2,
                                RoundingMode.HALF_UP
                        );


        // =====================================================
        // SAVE PROMISE
        // =====================================================

        code.setTrupromisePercentage(
                percentage
        );

        code.setTrupromiseAmount(
                promiseAmount
        );

        code.setTrupromiseStatus(
                TruPromiseStatus.PROMISED
        );

        code.setTrupromiseCreatedAt(
                now
        );

        code.setTrupromisePaidAt(
                null
        );

        code.setTrupromiseReversedAt(
                null
        );
    }


    // =========================================================
    // TRUPROMISE
    // MARK PAID
    // =========================================================

    private void markTruPromisePaid(
            TruBlankCode code,
            LocalDateTime now
    ) {

        if (code == null) {
            return;
        }


        BigDecimal promiseAmount =
                code.getTrupromiseAmount();


        if (promiseAmount == null
                || promiseAmount.compareTo(
                BigDecimal.ZERO
        ) <= 0) {

            return;
        }


        if (code.getTrupromiseStatus() == TruPromiseStatus.PAID) {
            if (code.getTrupromisePaidAt() == null) {
                code.setTrupromisePaidAt(now);
            }
            return;
        }

        if (code.getTrupromiseStatus() != TruPromiseStatus.PROMISED) {
            return;
        }

        code.setTrupromiseStatus(TruPromiseStatus.PAID);
        if (code.getTrupromisePaidAt() == null) {
            code.setTrupromisePaidAt(now);
        }
    }


    // =========================================================
    // TRUPROMISE
    // BACK TO WALLET
    // =========================================================
    //
    // IMPORTANT:
    //
    // Only TruPromise amount is credited.
    //
    // Original voucher amount is NOT refunded.
    //
    // Example:
    //
    // Voucher = ₹100
    // Promise = ₹10
    //
    // Cancel / Expire:
    //
    // Wallet +₹10
    //
    // NOT +₹100
    //
    // =========================================================

    private void reverseTruPromiseToWallet(
            TruBlankCode code,
            LocalDateTime now
    ) {

        if (code == null) {
            return;
        }


        if (code.getClientId() == null) {
            return;
        }


        if (code.getTrupromiseStatus()
                != TruPromiseStatus.PROMISED) {

            return;
        }


        BigDecimal promiseAmount =
                code.getTrupromiseAmount();


        if (promiseAmount == null
                || promiseAmount.compareTo(
                BigDecimal.ZERO
        ) <= 0) {

            return;
        }


        BigDecimal amount =
                promiseAmount.setScale(
                        2,
                        RoundingMode.HALF_UP
                );


        // =====================================================
        // CREDIT PROMISE BACK TO WALLET
        // =====================================================

        walletTransactionService.recordCreditOnce(
                code.getClientId(),
                amount,
                "TruPromise back to wallet - "
                        + code.getCodeNumber(),
                "TRU_PROMISE",
                String.valueOf(code.getId())
        );


        // =====================================================
        // STATUS
        // =====================================================

        code.setTrupromiseStatus(
                TruPromiseStatus.BackToWallet
        );

        code.setTrupromiseReversedAt(
                now
        );
    }


    // =========================================================
    // RESET TRUPROMISE
    // =========================================================

    private void resetTruPromise(
            TruBlankCode code
    ) {

        code.setTrupromisePercentage(
                BigDecimal.ZERO.setScale(
                        2,
                        RoundingMode.HALF_UP
                )
        );

        code.setTrupromiseAmount(
                BigDecimal.ZERO.setScale(
                        2,
                        RoundingMode.HALF_UP
                )
        );

        code.setTrupromiseStatus(null);

        code.setTrupromiseCreatedAt(null);

        code.setTrupromisePaidAt(null);

        code.setTrupromiseReversedAt(null);
    }


    // =========================================================
    // NEXT SERIAL
    // =========================================================

    private long getNextSerialNumber() {

        return repository
                .findTopByOrderBySerialNumberDesc()
                .map(code -> {

                    String serial =
                            code.getSerialNumber();

                    if (serial == null
                            || serial.trim().isEmpty()) {

                        return SERIAL_BASE + 1;
                    }


                    try {

                        return Long.parseLong(
                                serial.trim()
                        ) + 1;

                    } catch (
                            NumberFormatException e
                    ) {

                        return SERIAL_BASE + 1;
                    }
                })
                .orElse(
                        SERIAL_BASE + 1
                );
    }


    // =========================================================
    // GENERATE CODE
    // =========================================================

    private String generateCodeNumber() {

        String code;

        do {

            code =
                    randomBlock(4)
                            + "-"
                            + randomBlock(4)
                            + "-"
                            + randomBlock(4);

        } while (
                repository.existsByCodeNumber(
                        code
                )
        );

        return code;
    }


    // =========================================================
    // RANDOM BLOCK
    // =========================================================

    private String randomBlock(
            int length
    ) {

        final String characters =
                "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";

        StringBuilder builder =
                new StringBuilder(length);


        for (int i = 0; i < length; i++) {

            builder.append(
                    characters.charAt(
                            RANDOM.nextInt(
                                    characters.length()
                            )
                    )
            );
        }


        return builder.toString();
    }


    // =========================================================
    // REFERENCE
    // =========================================================

    private String generateReferenceNumber() {

        String reference;

        do {

            String date =
                    LocalDateTime
                            .now()
                            .toLocalDate()
                            .toString()
                            .replace("-", "");


            reference =
                    "REF-"
                            + date
                            + "-"
                            + randomBlock(6);

        } while (
                repository.existsByReferenceNumber(
                        reference
                )
        );


        return reference;
    }


    // =========================================================
    // VALIDATE ID
    // =========================================================

    private void validateId(
            Long id
    ) {

        if (id == null) {

            throw new RuntimeException(
                    "Blank code ID is required"
            );
        }


        if (id < 1) {

            throw new RuntimeException(
                    "Blank code ID must be greater than 0"
            );
        }
    }


    // =========================================================
    // VALIDATE SEARCH
    // =========================================================

    private void validateSearchValue(
            String value,
            String fieldName
    ) {

        if (value == null
                || value.trim().isEmpty()) {

            throw new RuntimeException(
                    fieldName + " is required"
            );
        }
    }


    // =========================================================
    // CLEAN STRING
    // =========================================================

    private String cleanString(
            String value
    ) {

        return (
                value == null
                        || value.trim().isEmpty()
        )
                ? null
                : value.trim();
    }


    // =========================================================
    // SAFE AMOUNT
    // =========================================================

    private BigDecimal safeAmount(
            BigDecimal value
    ) {

        return value == null
                ? BigDecimal.ZERO.setScale(
                2,
                RoundingMode.HALF_UP
        )
                : value.setScale(
                2,
                RoundingMode.HALF_UP
        );
    }


    // =========================================================
    // RESPONSE
    // =========================================================

    private TruBlankCodeResponse toResponse(
            TruBlankCode code
    ) {

        TruBlankCodeResponse response =
                new TruBlankCodeResponse();


        // =====================================================
        // BASIC
        // =====================================================

        response.setId(
                code.getId()
        );

        response.setGenerationNumber(
                code.getGenerationNumber()
        );

        response.setCodeNumber(
                code.getCodeNumber()
        );

        response.setSerialNumber(
                code.getSerialNumber()
        );

        response.setReferenceNumber(
                code.getReferenceNumber()
        );


        // =====================================================
        // REWARD
        // =====================================================

        response.setDenomination(
                code.getDenomination()
        );

        response.setValidityMonths(
                code.getValidityMonths()
        );

        response.setExpiryDate(
                code.getExpiryDate()
        );


        // =====================================================
        // TRUCARD
        // =====================================================

        response.setBrandNames(
                code.getBrandNames() != null
                        ? new ArrayList<>(
                        code.getBrandNames()
                )
                        : new ArrayList<>()
        );

        response.setBrandCategory(
                code.getBrandCategory()
        );

        response.setThemeName(
                code.getThemeName()
        );

        response.setThemeImg(
                code.getThemeImg()
        );


        // =====================================================
        // STATUS
        // =====================================================

        response.setStatus(
                code.getStatus()
        );


        // =====================================================
        // LIFECYCLE
        // =====================================================

        response.setCreatedAt(
                code.getCreatedAt()
        );

        response.setUpdatedAt(
                code.getUpdatedAt()
        );

        response.setActivatedAt(
                code.getActivatedAt()
        );

        response.setRedeemedAt(
                code.getRedeemedAt()
        );

        response.setCreatedBy(
                code.getCreatedBy()
        );

        response.setActivatedBy(
                code.getActivatedBy()
        );


        // =====================================================
        // CLIENT
        // =====================================================

        response.setClientId(
                code.getClientId()
        );

        response.setClientName(
                code.getClientName()
        );

        response.setClientImg(
                code.getClientImg()
        );

        response.setClientBrand(
                code.getClientBrand() != null
                        ? new ArrayList<>(
                        code.getClientBrand()
                )
                        : new ArrayList<>()
        );

        response.setClientCategory(
                code.getClientCategory()
        );

        response.setClientTheme(
                code.getClientTheme()
        );

        response.setClientThemeImg(
                code.getClientThemeImg()
        );


        // =====================================================
        // BALANCE
        // =====================================================

        response.setClientBalanceBeforeActivation(
                code.getClientBalanceBeforeActivation()
        );

        response.setClientBalanceAfterActivation(
                code.getClientBalanceAfterActivation()
        );


        // =====================================================
        // TRUPROMISE
        // =====================================================

        response.setTrupromisePercentage(
                code.getTrupromisePercentage()
        );

        response.setTrupromiseAmount(
                code.getTrupromiseAmount()
        );

        response.setTrupromiseStatus(
                code.getTrupromiseStatus()
        );

        response.setTrupromiseCreatedAt(
                code.getTrupromiseCreatedAt()
        );

        response.setTrupromisePaidAt(
                code.getTrupromisePaidAt()
        );

        response.setTrupromiseReversedAt(
                code.getTrupromiseReversedAt()
        );


        // =====================================================
        // REDEEMED BY
        // =====================================================

        String redeemedBy = null;


        if (code.getStatus()
                == TruBlankCodeStatus.REDEEMED) {

            List<UserRedemption> redemptions =
                    userRedemptionRepository
                            .findByUserTruvishCodeOrderByUserBrandTimeTempDesc(
                                    code.getCodeNumber()
                            );


            if (!redemptions.isEmpty()) {

                redeemedBy =
                        redemptions
                                .get(0)
                                .getUserPhoneNumber();
            }
        }


        response.setRedeemedBy(
                redeemedBy
        );


        return response;
    }
}