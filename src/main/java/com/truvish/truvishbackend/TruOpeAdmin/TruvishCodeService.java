package com.truvish.truvishbackend.TruOpeAdmin;

import com.truvish.truvishbackend.TruBlankCode.TruBlankCode;
import com.truvish.truvishbackend.TruBlankCode.TruBlankCodeRepository;
import com.truvish.truvishbackend.TruBlankCode.TruBlankCodeStatus;
import com.truvish.truvishbackend.client.Client;
import com.truvish.truvishbackend.client.ClientRepository;
import com.truvish.truvishbackend.redemption.UserRedemption;
import com.truvish.truvishbackend.redemption.UserRedemptionRepository;
import com.truvish.truvishbackend.wallet.dto.CreateWalletTxnRequest;
import com.truvish.truvishbackend.wallet.service.WalletTransactionService;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;

@Service
public class TruvishCodeService {

    private final TruvishCodeRepository repo;
    private final UserRedemptionRepository userRepo;
    private final WalletTransactionService walletService;
    private final ClientRepository clientRepo;
    private final TruBlankCodeRepository blankCodeRepo;

    // =========================================================
    // CONSTRUCTOR
    // =========================================================

    public TruvishCodeService(
            TruvishCodeRepository repo,
            UserRedemptionRepository userRepo,
            WalletTransactionService walletService,
            ClientRepository clientRepo,
            TruBlankCodeRepository blankCodeRepo
    ) {
        this.repo = repo;
        this.userRepo = userRepo;
        this.walletService = walletService;
        this.clientRepo = clientRepo;
        this.blankCodeRepo = blankCodeRepo;
    }

    // =========================================================
    // GENERATE DIGITAL CODE
    // =========================================================

    private String generateCode() {

        String uuid = UUID.randomUUID()
                .toString()
                .replace("-", "")
                .toUpperCase();

        return String.format(
                "%s-%s-%s",
                uuid.substring(0, 4),
                uuid.substring(4, 8),
                uuid.substring(8, 12)
        );
    }

    // =========================================================
    // ENSURE UNIQUE DIGITAL CODE
    // =========================================================

    private String generateUniqueCode() {

        String code;

        do {
            code = generateCode();
        } while (repo.existsByTruvishIdCodeNumber(code));

        return code;
    }

    // =========================================================
    // GENERATE SERIAL NUMBER
    // =========================================================

    private String generateSerialNumber() {

        String serialNumber;

        do {
            serialNumber = String.valueOf(
                    ThreadLocalRandom.current().nextLong(
                            100_000_000_000L,
                            1_000_000_000_000L
                    )
            );
        } while (repo.existsBySerialNumber(serialNumber));

        return serialNumber;
    }

    // =========================================================
    // GENERATE REFERENCE NUMBER
    // =========================================================

    private String generateReferenceNumber() {

        String referenceNumber;

        do {
            referenceNumber = UUID.randomUUID()
                    .toString()
                    .replace("-", "")
                    .toUpperCase()
                    .substring(0, 22);

        } while (repo.existsByReferenceNumber(referenceNumber));

        return referenceNumber;
    }

    // =========================================================
    // CREATE CODE
    // =========================================================

    @Transactional
    public TruvishCode create(TruvishCode code) {

        if (code == null) {
            throw new RuntimeException("Code request required");
        }

        LocalDateTime now = LocalDateTime.now();

        code.setTruvishCodeTimestamp(now);

        if (code.getTruvishIdCodeNumber() == null
                || code.getTruvishIdCodeNumber().trim().isEmpty()) {

            code.setTruvishIdCodeNumber(
                    generateUniqueCode()
            );
        }

        if (code.getSerialNumber() == null
                || code.getSerialNumber().trim().isEmpty()) {

            code.setSerialNumber(
                    generateSerialNumber()
            );
        }

        if (code.getReferenceNumber() == null
                || code.getReferenceNumber().trim().isEmpty()) {

            code.setReferenceNumber(
                    generateReferenceNumber()
            );
        }

        Long value = code.getTruvishCodeValue();

        if (value != null && value > 0) {

            code.setTruvishCodeStatus(
                    VoucherStatus.ACTIVE
            );

        } else {

            code.setTruvishCodeStatus(
                    VoucherStatus.INACTIVE
            );
        }

        if (code.getOriginalCodeValue() == null) {

            code.setOriginalCodeValue(
                    code.getTruvishCodeValue()
            );
        }

        return repo.save(code);
    }

    // =========================================================
    // TRUPROMISE CALCULATION
    // =========================================================

    private BigDecimal calculateTruPromiseAmount(
            BigDecimal voucherAmount,
            BigDecimal percentage
    ) {

        if (voucherAmount == null
                || voucherAmount.compareTo(BigDecimal.ZERO) <= 0) {

            return BigDecimal.ZERO.setScale(
                    2,
                    RoundingMode.HALF_UP
            );
        }

        if (percentage == null
                || percentage.compareTo(BigDecimal.ZERO) <= 0) {

            return BigDecimal.ZERO.setScale(
                    2,
                    RoundingMode.HALF_UP
            );
        }

        if (percentage.compareTo(
                BigDecimal.valueOf(100)
        ) > 0) {

            throw new RuntimeException(
                    "TruPromise percentage cannot be greater than 100"
            );
        }

        return voucherAmount
                .multiply(percentage)
                .divide(
                        BigDecimal.valueOf(100),
                        2,
                        RoundingMode.HALF_UP
                );
    }

    // =========================================================
    // INITIALIZE TRUPROMISE
    // =========================================================

    private void initializeTruPromise(
            TruvishCode code,
            BigDecimal percentage,
            BigDecimal voucherAmount,
            LocalDateTime now
    ) {

        if (percentage == null
                || percentage.compareTo(BigDecimal.ZERO) <= 0) {

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

        BigDecimal normalizedPercentage =
                percentage.setScale(
                        2,
                        RoundingMode.HALF_UP
                );

        BigDecimal promiseAmount =
                calculateTruPromiseAmount(
                        voucherAmount,
                        normalizedPercentage
                );

        code.setTrupromisePercentage(
                normalizedPercentage
        );

        code.setTrupromiseAmount(
                promiseAmount
        );

        code.setTrupromiseStatus(
                TruPromiseStatus.PROMISED
        );

        code.setTrupromiseCreatedAt(now);
        code.setTrupromisePaidAt(null);
        code.setTrupromiseReversedAt(null);
    }

    // =========================================================
    // GENERATE ORDER VOUCHERS
    // =========================================================

    @Transactional
    public List<TruvishCode> generateOrderVouchers(
            CodeAssignmentDto dto
    ) {

        if (dto == null) {
            throw new RuntimeException(
                    "Request required"
            );
        }

        // =====================================================
        // CLIENT ID
        // =====================================================

        if (dto.getClientId() == null
                || dto.getClientId() <= 0) {

            throw new RuntimeException(
                    "ClientId required"
            );
        }

        // =====================================================
        // VOUCHER VALUE
        // =====================================================

        if (dto.getTruvishCodeValue() == null
                || dto.getTruvishCodeValue() <= 0) {

            throw new RuntimeException(
                    "Voucher value required"
            );
        }

        // =====================================================
        // QUANTITY
        // =====================================================

        Integer quantity = dto.getQuantity();

        if (quantity == null) {
            quantity = 1;
        }

        if (quantity < 1) {

            throw new RuntimeException(
                    "Quantity must be at least 1"
            );
        }

        // =====================================================
        // VALIDITY
        // =====================================================

        if (dto.getValidity() == null
                || dto.getValidity() <= 0) {

            throw new RuntimeException(
                    "Validity is required"
            );
        }

        // =====================================================
        // CLIENT
        // =====================================================

        Client client = clientRepo
                .findById(dto.getClientId())
                .orElseThrow(
                        () -> new RuntimeException(
                                "Client not found: "
                                        + dto.getClientId()
                        )
                );

        // =====================================================
        // VOUCHER VALUE
        // =====================================================

        BigDecimal voucherAmount =
                BigDecimal.valueOf(
                        dto.getTruvishCodeValue()
                );

        BigDecimal totalVoucherValue =
                voucherAmount.multiply(
                        BigDecimal.valueOf(quantity)
                );

        // =====================================================
        // TRUPROMISE PERCENTAGE
        // =====================================================

        BigDecimal promisePercentage =
                dto.getTrupromisePercentage();

        if (promisePercentage == null) {
            promisePercentage = BigDecimal.ZERO;
        }

        if (promisePercentage.compareTo(
                BigDecimal.ZERO
        ) < 0
                || promisePercentage.compareTo(
                BigDecimal.valueOf(100)
        ) > 0) {

            throw new RuntimeException(
                    "Invalid TruPromise percentage"
            );
        }

        promisePercentage =
                promisePercentage.setScale(
                        2,
                        RoundingMode.HALF_UP
                );

        // =====================================================
        // TOTAL TRUPROMISE
        // =====================================================

        BigDecimal promisePerVoucher =
                calculateTruPromiseAmount(
                        voucherAmount,
                        promisePercentage
                );

        BigDecimal totalTruPromise =
                promisePerVoucher
                        .multiply(
                                BigDecimal.valueOf(quantity)
                        )
                        .setScale(
                                2,
                                RoundingMode.HALF_UP
                        );

        // =====================================================
        // ACTUAL WALLET DEBIT
        // =====================================================
        //
        // IMPORTANT:
        //
        // Voucher = ₹100
        // TruPromise = ₹10
        //
        // Wallet Debit = ₹100
        //
        // NOT ₹90.
        //
        // TruPromise is only a future promise.
        // It is NOT deducted from the voucher debit.
        //
        // =====================================================

        BigDecimal walletDebit =
                totalVoucherValue.setScale(
                        2,
                        RoundingMode.HALF_UP
                );

        // =====================================================
        // CLIENT BALANCE
        // =====================================================

        BigDecimal balance =
                client.getBalance() == null
                        ? BigDecimal.ZERO
                        : client.getBalance();

        if (balance.compareTo(
                walletDebit
        ) < 0) {

            throw new RuntimeException(
                    "Insufficient Balance. Required: "
                            + walletDebit
                            + ", Available: "
                            + balance
            );
        }

        // =====================================================
        // CREATE DIGITAL VOUCHERS
        // =====================================================

        List<TruvishCode> savedCodes =
                new ArrayList<>();

        for (int i = 0; i < quantity; i++) {

            TruvishCode code =
                    new TruvishCode();

            LocalDateTime now =
                    LocalDateTime.now();

            // DIGITAL CODE
            code.setTruvishIdCodeNumber(
                    generateUniqueCode()
            );

            // SERIAL
            code.setSerialNumber(
                    generateSerialNumber()
            );

            // REFERENCE
            code.setReferenceNumber(
                    generateReferenceNumber()
            );

            // CLIENT
            code.setClientId(
                    client.getId()
            );

            code.setClientName(
                    client.getClientName()
            );

            code.setClientImg(
                    client.getLogoImg()
            );

            // VALUE
            code.setTruvishCodeValue(
                    dto.getTruvishCodeValue()
            );

            code.setOriginalCodeValue(
                    dto.getTruvishCodeValue()
            );

            // PARTIAL REDEEM
            code.setTruvishIdIsPartialRedeemAllowed(
                    true
            );

            // THEME
            code.setClientTheme(
                    dto.getClientTheme()
            );

            code.setClientThemeImg(
                    dto.getClientThemeImg()
            );

            // BRAND
            code.setClientBrand(
                    dto.getClientBrand()
            );

            // CATEGORY
            code.setClientCategory(
                    dto.getClientCategory()
            );

            // VALIDITY
            code.setValidity(
                    dto.getValidity()
            );

            // TIMESTAMP
            code.setTruvishCodeTimestamp(
                    now
            );

            // STATUS
            code.setTruvishCodeStatus(
                    VoucherStatus.ACTIVE
            );

            // CLOSED REASON
            code.setCodeClosedReason(null);

            // TRUPROMISE
            initializeTruPromise(
                    code,
                    promisePercentage,
                    voucherAmount,
                    now
            );

            // SAVE
            TruvishCode saved =
                    repo.save(code);

            savedCodes.add(saved);
        }

        // =====================================================
        // WALLET DEBIT
        // =====================================================

        if (walletDebit.compareTo(
                BigDecimal.ZERO
        ) > 0) {

            CreateWalletTxnRequest walletReq =
                    new CreateWalletTxnRequest();

            walletReq.setAmount(
                    walletDebit
            );

            walletReq.setType(
                    "DEBIT"
            );

            walletReq.setDescription(
                    "Voucher Debit"
            );

            walletReq.setReferenceType(
                    "VOUCHER"
            );

            walletReq.setReferenceId(
                    UUID.randomUUID().toString()
            );

            walletService.create(
                    client.getId(),
                    walletReq
            );
        }

        return savedCodes;
    }

    // =========================================================
    // UPDATE CLIENT
    // =========================================================

    @Transactional
    public List<TruvishCode> updateClient(
            CodeAssignmentDto dto
    ) {

        return generateOrderVouchers(dto);
    }

    // =========================================================
    // VERIFY CODE
    // =========================================================

    @Transactional(readOnly = true)
    public VerifyCodeResponse verifyCode(
            String codeNumber
    ) {

        if (codeNumber == null
                || codeNumber.trim().isEmpty()) {

            throw new RuntimeException(
                    "Code required"
            );
        }

        String finalCode =
                codeNumber
                        .trim()
                        .toUpperCase();

        // =====================================================
        // DIGITAL CODE
        // =====================================================

        var normalCode =
                repo.findByTruvishIdCodeNumber(
                        finalCode
                );

        if (normalCode.isPresent()) {

            TruvishCode code =
                    normalCode.get();

            Long currentBalance =
                    code.getTruvishCodeValue();

            if (currentBalance == null) {
                currentBalance = 0L;
            }

            VoucherStatus currentStatus =
                    code.getTruvishCodeStatus();

            // =================================================
            // STATUS MUST BE ACTIVE
            // =================================================

            if (currentStatus != VoucherStatus.ACTIVE) {

                if (currentStatus == VoucherStatus.EXPIRED) {

                    throw new RuntimeException(
                            "Code is expired and cannot be verified"
                    );
                }

                if (currentStatus == VoucherStatus.CANCELLED) {

                    throw new RuntimeException(
                            "Code is cancelled and cannot be verified"
                    );
                }

                if (currentStatus == VoucherStatus.REDEEMED) {

                    throw new RuntimeException(
                            "Code has already been redeemed"
                    );
                }

                if (currentStatus == VoucherStatus.INACTIVE) {

                    throw new RuntimeException(
                            "Code is inactive and cannot be verified"
                    );
                }

                throw new RuntimeException(
                        "Code is not active and cannot be verified"
                );
            }

            // =================================================
            // EXPIRY CHECK
            // =================================================

            if (code.getTruvishCodeTimestamp() != null
                    && code.getValidity() != null
                    && code.getTruvishCodeTimestamp()
                    .plusMonths(code.getValidity())
                    .isBefore(LocalDateTime.now())) {

                throw new RuntimeException(
                        "Code has expired and cannot be verified"
                );
            }

            // =================================================
            // FULLY REDEEMED
            // =================================================

            if (currentBalance <= 0) {

                throw new RuntimeException(
                        "Code has already been redeemed"
                );
            }

            // =================================================
            // ACTIVE
            // =================================================

            return new VerifyCodeResponse(
                    finalCode,
                    VoucherStatus.ACTIVE.name(),
                    null,
                    currentBalance,
                    code.getClientImg(),
                    code.getValidity(),
                    code.getClientThemeImg(),
                    code.getClientBrand(),
                    code.getClientCategory()
            );
        }

        // =====================================================
        // PHYSICAL TRUCARD
        // =====================================================

        TruBlankCode blankCode =
                blankCodeRepo
                        .findByCodeNumber(
                                finalCode
                        )
                        .orElseThrow(
                                () -> new RuntimeException(
                                        "Invalid Code"
                                )
                        );

        // =====================================================
        // STATUS
        // =====================================================

        if (blankCode.getStatus()
                != TruBlankCodeStatus.ACTIVE) {

            throw new RuntimeException(
                    "This code is not active yet. "
                            + "Please contact the administrator."
            );
        }

        // =====================================================
        // DENOMINATION
        // =====================================================

        if (blankCode.getDenomination() == null
                || blankCode.getDenomination() <= 0) {

            throw new RuntimeException(
                    "Code value is not configured"
            );
        }

        // =====================================================
        // VALIDITY
        // =====================================================

        if (blankCode.getValidityMonths() == null
                || blankCode.getValidityMonths() <= 0) {

            throw new RuntimeException(
                    "Code validity is not configured"
            );
        }

        // =====================================================
        // EXPIRY
        // =====================================================

        if (blankCode.getExpiryDate() != null
                && blankCode.getExpiryDate()
                .isBefore(LocalDateTime.now())) {

            throw new RuntimeException(
                    "This code has expired"
            );
        }

        // =====================================================
        // CLIENT IMAGE
        // =====================================================

        String clientImg =
                blankCode.getClientImg();

        // =====================================================
        // RETURN TRUCARD
        // =====================================================

        return new VerifyCodeResponse(
                finalCode,
                blankCode.getStatus().name(),
                null,
                blankCode.getDenomination(),
                clientImg,
                blankCode.getValidityMonths(),
                blankCode.getThemeImg(),
                blankCode.getBrandNames(),
                blankCode.getBrandCategory() == null
                        ? new ArrayList<>()
                        : List.of(
                        blankCode.getBrandCategory()
                )
        );
    }

    // =========================================================
    // HISTORY BY CLIENT NAME
    // =========================================================

    public List<ClientHistoryItem> history(
            String clientName,
            int page,
            int size
    ) {

        List<TruvishCode> codes =
                repo.findByClientNameIgnoreCaseOrderByTruvishCodeTimestampDesc(
                        clientName
                );

        List<ClientHistoryItem> items =
                buildHistoryItems(codes);

        return paginate(
                items,
                page,
                size
        );
    }

    // =========================================================
    // HISTORY BY CLIENT ID
    // =========================================================

    public List<ClientHistoryItem> historyByClientId(
            Long clientId,
            int page,
            int size
    ) {

        if (clientId == null
                || clientId <= 0) {

            throw new RuntimeException(
                    "Invalid clientId"
            );
        }

        clientRepo.findById(clientId)
                .orElseThrow(
                        () -> new RuntimeException(
                                "Client not found: "
                                        + clientId
                        )
                );

        List<TruvishCode> clientCodes =
                repo.findByClientIdOrderByTruvishCodeTimestampDesc(
                        clientId
                );

        List<ClientHistoryItem> items =
                buildHistoryItems(
                        clientCodes
                );

        // Include assigned physical TruBlankCode cards in the same Client-ID history.
        for (TruBlankCode physical : blankCodeRepo.findByClientIdOrderByCreatedAtDesc(clientId)) {
            LocalDateTime eventTime = physical.getUpdatedAt() != null
                    ? physical.getUpdatedAt() : physical.getCreatedAt();
            String status = physical.getStatus() == null ? "UNKNOWN" : physical.getStatus().name();
            String eventType = status;
            String message = "Physical TruCard status: " + status;
            ClientHistoryItem item = new ClientHistoryItem(
                    eventTime,
                    physical.getCodeNumber(),
                    physical.getDenomination(),
                    physical.getDenomination(),
                    message,
                    eventType,
                    physical.getValidityMonths(),
                    physical.getExpiryDate(),
                    "-",
                    "-",
                    physical.getCreatedAt(),
                    null,
                    physical.getTrupromiseAmount(),
                    physical.getTrupromisePercentage(),
                    physical.getTrupromiseStatus() == null ? null : physical.getTrupromiseStatus().name()
            );
            item.setCodeType("PHYSICAL");
            item.setSerialNumber(physical.getSerialNumber());
            item.setReferenceNumber(physical.getReferenceNumber());
            item.setTrupromiseCreatedAt(physical.getTrupromiseCreatedAt());
            item.setTrupromisePaidAt(physical.getTrupromisePaidAt());
            item.setTrupromiseReversedAt(physical.getTrupromiseReversedAt());
            items.add(item);
        }

        items.sort(Comparator.comparing(
                ClientHistoryItem::getEventTime,
                Comparator.nullsLast(Comparator.reverseOrder())
        ));

        return paginate(
                items,
                page,
                size
        );
    }

    // =========================================================
    // BUILD HISTORY
    // =========================================================

    private List<ClientHistoryItem> buildHistoryItems(
            List<TruvishCode> codes
    ) {

        List<ClientHistoryItem> items =
                new ArrayList<>();

        List<UserRedemption> allRedemptions =
                userRepo.findAll();

        for (TruvishCode code : codes) {

            Long originalAmount =
                    code.getOriginalCodeValue() != null
                            ? code.getOriginalCodeValue()
                            : code.getTruvishCodeValue();

            LocalDateTime expiryDate = null;

            if (code.getTruvishCodeTimestamp() != null
                    && code.getValidity() != null) {

                expiryDate =
                        code.getTruvishCodeTimestamp()
                                .plusMonths(
                                        code.getValidity()
                                );
            }

            // =================================================
            // VOUCHER STATUS
            // =================================================

            String historyStatus =
                    code.getTruvishCodeStatus() == null
                            ? null
                            : code.getTruvishCodeStatus().name();

            // =================================================
            // EVENT TYPE
            // =================================================

            String historyEventType =
                    "CODE_ASSIGNED";

            String historyMessage =
                    "Code assigned";

            String closeReason =
                    code.getCodeClosedReason();

            // =================================================
            // ADMIN DEACTIVATED
            // =================================================

            if (code.getTruvishCodeStatus()
                    == VoucherStatus.DEACTIVATED) {

                historyEventType = "DEACTIVATED";
                historyMessage = "Code deactivated";

            } else if (code.getTruvishCodeStatus()
                    == VoucherStatus.EXPIRED) {

                if ("ADMIN_DEACTIVATED".equalsIgnoreCase(
                        closeReason
                )) {

                    historyEventType =
                            "DEACTIVATED";

                    historyMessage =
                            "Code deactivated";

                } else {

                    historyEventType =
                            "EXPIRED";

                    historyMessage =
                            "Code expired";
                }

            } else if (code.getTruvishCodeStatus()
                    == VoucherStatus.REDEEMED) {

                historyEventType =
                        "REDEEMED";

                historyMessage =
                        "Code redeemed";

            } else if (code.getTruvishCodeStatus()
                    == VoucherStatus.CANCELLED) {

                historyEventType =
                        "CANCELLED";

                historyMessage =
                        "Code cancelled";

            } else if (code.getTruvishCodeStatus()
                    == VoucherStatus.INACTIVE) {

                historyEventType =
                        "INACTIVE";

                historyMessage =
                        "Code inactive";
            }

            // =================================================
            // CODE STATUS HISTORY
            // =================================================

            items.add(
                    new ClientHistoryItem(
                            code.getTruvishCodeTimestamp(),
                            code.getTruvishIdCodeNumber(),
                            originalAmount,
                            code.getTruvishCodeValue(),

                            // MESSAGE
                            historyMessage,

                            // EVENT TYPE
                            historyEventType,

                            code.getValidity(),
                            expiryDate,
                            "-",
                            "-",
                            code.getTruvishCodeTimestamp(),
                            null,

                            // TRUPROMISE
                            code.getTrupromiseAmount(),
                            code.getTrupromisePercentage(),

                            // VOUCHER STATUS
                            historyStatus
                    )
            );

            // =================================================
            // REDEMPTIONS
            // =================================================

            List<UserRedemption> redemptions =
                    allRedemptions
                            .stream()
                            .filter(
                                    redemption ->
                                            redemption
                                                    .getUserTruvishCode()
                                                    != null
                                                    && redemption
                                                    .getUserTruvishCode()
                                                    .equals(
                                                            code.getTruvishIdCodeNumber()
                                                    )
                            )
                            .sorted(
                                    Comparator.comparing(
                                            UserRedemption
                                                    ::getUserBrandTimeTemp,
                                            Comparator.nullsLast(
                                                    Comparator.naturalOrder()
                                            )
                                    )
                            )
                            .toList();

            for (
                    UserRedemption redemption
                    : redemptions
            ) {

                String redemptionTruPromiseStatus =
                        code.getTrupromiseStatus() == null
                                ? null
                                : code.getTrupromiseStatus().name();

                items.add(
                        new ClientHistoryItem(
                                redemption.getUserBrandTimeTemp(),
                                redemption.getUserTruvishCode(),
                                redemption.getUserBrandValue(),
                                redemption.getAfterBalance(),
                                redemption.getHistoryMessage(),
                                redemption.getRedeemStatus(),
                                code.getValidity(),
                                expiryDate,
                                redemption.getUserBrandName(),
                                redemption.getUserPhoneNumber(),
                                code.getTruvishCodeTimestamp(),
                                redemption.getUserBrandTimeTemp(),

                                // TRUPROMISE
                                code.getTrupromiseAmount(),
                                code.getTrupromisePercentage(),

                                // TRUPROMISE STATUS
                                redemptionTruPromiseStatus
                        )
                );
            }
        }

        // Digital generator records should be explicitly labelled in the API.
        for (ClientHistoryItem item : items) {
            if (item.getCodeType() == null || item.getCodeType().isBlank()) {
                item.setCodeType("DIGITAL");
            }
        }

        // =====================================================
        // NEWEST FIRST
        // =====================================================

        items.sort(
                Comparator.comparing(
                        ClientHistoryItem::getEventTime,
                        Comparator.nullsLast(
                                Comparator.reverseOrder()
                        )
                )
        );

        return items;
    }

    // =========================================================
    // PAGINATION
    // =========================================================

    private List<ClientHistoryItem> paginate(
            List<ClientHistoryItem> items,
            int page,
            int size
    ) {

        if (items.isEmpty()) {
            return items;
        }

        if (page < 0) {
            page = 0;
        }

        if (size <= 0) {
            size = 20;
        }

        int fromIndex =
                page * size;

        if (fromIndex >= items.size()) {
            return new ArrayList<>();
        }

        int toIndex =
                Math.min(
                        fromIndex + size,
                        items.size()
                );

        return new ArrayList<>(
                items.subList(
                        fromIndex,
                        toIndex
                )
        );
    }

    // =========================================================
    // GENERATE ORDER
    // =========================================================

    @Transactional
    public List<TruvishCode> generateOrderVouchers(
            OrderVoucherRequest request
    ) {

        if (request == null) {
            throw new RuntimeException(
                    "Order request required"
            );
        }

        // =====================================================
        // CLIENT ID
        // =====================================================

        if (request.getClientId() == null
                || request.getClientId() <= 0) {

            throw new RuntimeException(
                    "ClientId required"
            );
        }

        // =====================================================
        // ITEMS
        // =====================================================

        if (request.getItems() == null
                || request.getItems().isEmpty()) {

            throw new RuntimeException(
                    "At least one denomination item is required"
            );
        }

        // =====================================================
        // VALIDITY
        // =====================================================

        if (request.getValidityMonths() == null
                || request.getValidityMonths() <= 0) {

            throw new RuntimeException(
                    "Validity months must be at least 1"
            );
        }

        // =====================================================
        // THEME
        // =====================================================

        if (request.getThemeName() == null
                || request.getThemeName()
                .trim()
                .isEmpty()) {

            throw new RuntimeException(
                    "Theme name is required"
            );
        }

        if (request.getThemeImg() == null
                || request.getThemeImg()
                .trim()
                .isEmpty()) {

            throw new RuntimeException(
                    "Theme image is required"
            );
        }

        // =====================================================
        // CLIENT
        // =====================================================

        Client client =
                clientRepo
                        .findById(
                                request.getClientId()
                        )
                        .orElseThrow(
                                () -> new RuntimeException(
                                        "Client not found: "
                                                + request.getClientId()
                                )
                        );

        List<TruvishCode> allCodes =
                new ArrayList<>();

        // =====================================================
        // GENERATE EACH DENOMINATION
        // =====================================================

        for (
                OrderVoucherRequest.DenomItem item
                : request.getItems()
        ) {

            if (item == null) {

                throw new RuntimeException(
                        "Invalid denomination item"
                );
            }

            if (item.getDenomination() == null
                    || item.getDenomination() <= 0) {

                throw new RuntimeException(
                        "Denomination must be positive"
                );
            }

            if (item.getQuantity() == null
                    || item.getQuantity() < 1) {

                throw new RuntimeException(
                        "Quantity must be at least 1"
                );
            }

            // =================================================
            // INTERNAL DTO
            // =================================================

            CodeAssignmentDto dto =
                    new CodeAssignmentDto();

            dto.setClientId(
                    request.getClientId()
            );

            dto.setClientName(
                    client.getClientName()
            );

            dto.setClientImg(
                    client.getLogoImg()
            );

            dto.setTruvishCodeValue(
                    item.getDenomination()
            );

            dto.setQuantity(
                    item.getQuantity()
            );

            dto.setValidity(
                    request.getValidityMonths()
            );

            dto.setClientTheme(
                    request.getThemeName()
            );

            dto.setClientThemeImg(
                    request.getThemeImg()
            );

            dto.setClientBrand(
                    request.getClientBrand()
            );

            dto.setClientCategory(
                    request.getClientCategory()
            );

            dto.setTrupromisePercentage(
                    request.getTrupromisePercentage()
            );

            // =================================================
            // GENERATE
            // =================================================

            List<TruvishCode> generatedCodes =
                    generateOrderVouchers(
                            dto
                    );

            allCodes.addAll(
                    generatedCodes
            );
        }

        return allCodes;
    }

    // =========================================================
    // CONFIGURE PHYSICAL TRUCARD
    // =========================================================

    @Transactional
    public TruBlankCode configureBlankCode(
            Long blankCodeId,
            Long clientId,
            Long denomination,
            Integer validityMonths
    ) {

        if (blankCodeId == null
                || blankCodeId <= 0) {

            throw new RuntimeException(
                    "Blank code id required"
            );
        }

        if (clientId == null
                || clientId <= 0) {

            throw new RuntimeException(
                    "ClientId required"
            );
        }

        if (denomination == null
                || denomination <= 0) {

            throw new RuntimeException(
                    "Denomination required"
            );
        }

        if (validityMonths == null
                || validityMonths <= 0) {

            throw new RuntimeException(
                    "Validity months required"
            );
        }

        TruBlankCode blankCode =
                blankCodeRepo
                        .findById(blankCodeId)
                        .orElseThrow(
                                () -> new RuntimeException(
                                        "TruBlankCode not found: "
                                                + blankCodeId
                                )
                        );

        Client client =
                clientRepo
                        .findById(clientId)
                        .orElseThrow(
                                () -> new RuntimeException(
                                        "Client not found: "
                                                + clientId
                                )
                        );

        blankCode.setClientId(
                client.getId()
        );

        blankCode.setClientName(
                client.getClientName()
        );

        blankCode.setClientImg(
                client.getLogoImg()
        );

        blankCode.setDenomination(
                denomination
        );

        blankCode.setValidityMonths(
                validityMonths
        );

        blankCode.setStatus(
                TruBlankCodeStatus.INACTIVE
        );

        blankCode.setUpdatedAt(
                LocalDateTime.now()
        );

        return blankCodeRepo.save(
                blankCode
        );
    }

    // =========================================================
    // ACTIVATE PHYSICAL TRUCARD
    // =========================================================

    @Transactional
    public TruBlankCode activateBlankCode(
            Long blankCodeId,
            Long clientId,
            Long denomination,
            Integer validityMonths
    ) {

        if (blankCodeId == null
                || blankCodeId <= 0) {

            throw new RuntimeException(
                    "Blank code id required"
            );
        }

        if (clientId == null
                || clientId <= 0) {

            throw new RuntimeException(
                    "ClientId required"
            );
        }

        if (denomination == null
                || denomination <= 0) {

            throw new RuntimeException(
                    "Denomination required"
            );
        }

        if (validityMonths == null
                || validityMonths <= 0) {

            throw new RuntimeException(
                    "Validity months required"
            );
        }

        TruBlankCode blankCode =
                blankCodeRepo
                        .findById(blankCodeId)
                        .orElseThrow(
                                () -> new RuntimeException(
                                        "TruBlankCode not found: "
                                                + blankCodeId
                                )
                        );

        if (blankCode.getStatus()
                == TruBlankCodeStatus.ACTIVE) {

            throw new RuntimeException(
                    "This TruCard is already active"
            );
        }

        Client client =
                clientRepo
                        .findById(clientId)
                        .orElseThrow(
                                () -> new RuntimeException(
                                        "Client not found: "
                                                + clientId
                                )
                        );

        LocalDateTime activationTime =
                LocalDateTime.now();

        LocalDateTime expiryDate =
                activationTime.plusMonths(
                        validityMonths
                );

        BigDecimal balanceBefore =
                client.getBalance() == null
                        ? BigDecimal.ZERO
                        : client.getBalance();

        BigDecimal cardAmount =
                BigDecimal.valueOf(
                        denomination
                );

        if (balanceBefore.compareTo(
                cardAmount
        ) < 0) {

            throw new RuntimeException(
                    "Insufficient Client Balance. "
                            + "Required: "
                            + cardAmount
                            + ", Available: "
                            + balanceBefore
            );
        }

        BigDecimal balanceAfter =
                balanceBefore.subtract(
                        cardAmount
                );

        blankCode.setClientId(
                client.getId()
        );

        blankCode.setClientName(
                client.getClientName()
        );

        blankCode.setClientImg(
                client.getLogoImg()
        );

        blankCode.setDenomination(
                denomination
        );

        blankCode.setValidityMonths(
                validityMonths
        );

        blankCode.setExpiryDate(
                expiryDate
        );

        blankCode.setClientBalanceBeforeActivation(
                balanceBefore
        );

        blankCode.setClientBalanceAfterActivation(
                balanceAfter
        );

        blankCode.setActivatedAt(
                activationTime
        );

        blankCode.setStatus(
                TruBlankCodeStatus.ACTIVE
        );

        blankCode.setUpdatedAt(
                activationTime
        );

        client.setBalance(
                balanceAfter
        );

        clientRepo.save(
                client
        );

        TruBlankCode savedCode =
                blankCodeRepo.save(
                        blankCode
                );

        CreateWalletTxnRequest walletReq =
                new CreateWalletTxnRequest();

        walletReq.setAmount(
                cardAmount
        );

        walletReq.setType(
                "DEBIT"
        );

        walletReq.setDescription(
                "TruCard Debited"
        );

        walletReq.setReferenceType(
                "TRUCARD"
        );

        walletReq.setReferenceId(
                String.valueOf(
                        savedCode.getId()
                )
        );

        walletService.create(
                clientId,
                walletReq
        );

        return savedCode;
    }

    // =========================================================
    // PAY TRUPROMISE
    // =========================================================

    @Transactional
    public TruvishCode payTruPromise(
            Long codeId
    ) {

        if (codeId == null
                || codeId <= 0) {

            throw new RuntimeException(
                    "Code id required"
            );
        }

        TruvishCode code =
                repo.findByIdForUpdate(codeId)
                        .orElseThrow(
                                () -> new RuntimeException(
                                        "TruvishCode not found: "
                                                + codeId
                                )
                        );

        // =====================================================
        // ALREADY PAID
        // =====================================================

        if (code.getTrupromiseStatus() == TruPromiseStatus.PAID) {
            if (code.getTrupromisePaidAt() == null) {
                code.setTrupromisePaidAt(LocalDateTime.now());
                return repo.save(code);
            }
            return code;
        }

        // =====================================================
        // ALREADY REVERSED
        // =====================================================

        if (code.getTrupromiseStatus()
                == TruPromiseStatus.REVERSED) {

            throw new RuntimeException(
                    "TruPromise has already been returned to wallet"
            );
        }

        // =====================================================
        // NO PROMISE
        // =====================================================

        if (code.getTrupromiseAmount() == null
                || code.getTrupromiseAmount()
                .compareTo(BigDecimal.ZERO) <= 0) {

            throw new RuntimeException(
                    "No TruPromise amount available"
            );
        }

        // =====================================================
        // MUST BE PROMISED
        // =====================================================

        if (code.getTrupromiseStatus()
                != TruPromiseStatus.PROMISED) {

            throw new RuntimeException(
                    "TruPromise is not in PROMISED status"
            );
        }

        // =====================================================
        // PAY
        // =====================================================

        code.setTrupromiseStatus(
                TruPromiseStatus.PAID
        );

        code.setTrupromisePaidAt(
                LocalDateTime.now()
        );

        // IMPORTANT:
        // No wallet CREDIT here.
        // Voucher amount was already fully debited
        // when voucher was generated.

        return repo.save(code);
    }

    // =========================================================
    // REVERSE TRUPROMISE
    // =========================================================

    @Transactional
    public TruvishCode reverseTruPromise(
            Long codeId
    ) {

        if (codeId == null
                || codeId <= 0) {

            throw new RuntimeException(
                    "Code id required"
            );
        }

        TruvishCode code =
                repo.findByIdForUpdate(codeId)
                        .orElseThrow(
                                () -> new RuntimeException(
                                        "TruvishCode not found: "
                                                + codeId
                                )
                        );

        reverseTruPromiseIfRequired(
                code,
                "ADMIN_DEACTIVATED"
        );

        return repo.save(code);
    }

    // =========================================================
    // EXPIRE CODE
    // =========================================================

    @Transactional
    public TruvishCode expireCode(
            Long codeId
    ) {

        if (codeId == null
                || codeId <= 0) {

            throw new RuntimeException(
                    "Code id required"
            );
        }

        TruvishCode code =
                repo.findByIdForUpdate(codeId)
                        .orElseThrow(
                                () -> new RuntimeException(
                                        "TruvishCode not found: "
                                                + codeId
                                )
                        );

        // =====================================================
        // ALREADY EXPIRED
        // =====================================================

        if (code.getTruvishCodeStatus()
                == VoucherStatus.EXPIRED) {

            String existingReason =
                    code.getCodeClosedReason();

            if ("ADMIN_DEACTIVATED".equalsIgnoreCase(
                    existingReason
            )) {

                reverseTruPromiseIfRequired(
                        code,
                        "ADMIN_DEACTIVATED"
                );

            } else {

                reverseTruPromiseIfRequired(
                        code,
                        "NORMAL_EXPIRY"
                );
            }

            return repo.save(code);
        }

        // =====================================================
        // REDEEMED
        // =====================================================

        if (code.getTruvishCodeStatus()
                == VoucherStatus.REDEEMED) {

            throw new RuntimeException(
                    "Redeemed code cannot be expired"
            );
        }

        // =====================================================
        // CANCELLED
        // =====================================================

        if (code.getTruvishCodeStatus()
                == VoucherStatus.CANCELLED) {

            throw new RuntimeException(
                    "Cancelled code cannot be normally expired"
            );
        }

        // =====================================================
        // EXPIRE
        // =====================================================

        code.setTruvishCodeStatus(
                VoucherStatus.EXPIRED
        );

        code.setCodeClosedReason(
                "NORMAL_EXPIRY"
        );

        // =====================================================
        // REVERSE PROMISE
        // =====================================================

        reverseTruPromiseIfRequired(
                code,
                "NORMAL_EXPIRY"
        );

        return repo.save(code);
    }

    // =========================================================
    // ADMIN DEACTIVATE CODE
    // =========================================================

    @Transactional
    public TruvishCode deactivateCode(
            Long codeId
    ) {

        if (codeId == null
                || codeId <= 0) {

            throw new RuntimeException(
                    "Code id required"
            );
        }

        TruvishCode code =
                repo.findByIdForUpdate(codeId)
                        .orElseThrow(
                                () -> new RuntimeException(
                                        "TruvishCode not found: "
                                                + codeId
                                )
                        );

        // =====================================================
        // REDEEMED
        // =====================================================

        if (code.getTruvishCodeStatus()
                == VoucherStatus.REDEEMED) {

            throw new RuntimeException(
                    "Redeemed code cannot be deactivated"
            );
        }

        // =====================================================
        // CANCELLED
        // =====================================================

        if (code.getTruvishCodeStatus()
                == VoucherStatus.CANCELLED) {

            reverseTruPromiseIfRequired(
                    code,
                    "ADMIN_DEACTIVATED"
            );

            return repo.save(code);
        }

        // =====================================================
        // ALREADY EXPIRED
        // =====================================================

        if (code.getTruvishCodeStatus()
                == VoucherStatus.EXPIRED) {

            code.setCodeClosedReason(
                    "ADMIN_DEACTIVATED"
            );
            code.setTruvishCodeStatus(VoucherStatus.DEACTIVATED);

            reverseTruPromiseIfRequired(
                    code,
                    "ADMIN_DEACTIVATED"
            );

            return repo.save(code);
        }

        // =====================================================
        // DEACTIVATE
        // =====================================================
        //
        // Persist the explicit DEACTIVATED status so admin screens and
        // API responses can distinguish it from normal expiry.
        // =====================================================

        code.setTruvishCodeStatus(
                VoucherStatus.DEACTIVATED
        );

        code.setCodeClosedReason(
                "ADMIN_DEACTIVATED"
        );

        // =====================================================
        // REVERSE PROMISE
        // =====================================================

        reverseTruPromiseIfRequired(
                code,
                "ADMIN_DEACTIVATED"
        );

        return repo.save(code);
    }

    // =========================================================
    // INTERNAL TRUPROMISE REVERSAL
    // =========================================================

    private void reverseTruPromiseIfRequired(
            TruvishCode code,
            String closeReason
    ) {

        if (code == null) {

            throw new RuntimeException(
                    "Code required"
            );
        }

        // =====================================================
        // ALREADY RETURNED
        // =====================================================

        if (code.getTrupromiseStatus()
                == TruPromiseStatus.REVERSED) {

            code.setCodeClosedReason(
                    closeReason
            );

            return;
        }

        // =====================================================
        // ALREADY PAID
        // =====================================================

        if (code.getTrupromiseStatus()
                == TruPromiseStatus.PAID) {

            throw new RuntimeException(
                    "Redeemed code cannot be reversed"
            );
        }

        // =====================================================
        // NO PROMISE
        // =====================================================

        BigDecimal promiseAmount =
                code.getTrupromiseAmount();

        if (promiseAmount == null
                || promiseAmount.compareTo(
                BigDecimal.ZERO
        ) <= 0) {

            code.setTrupromiseStatus(null);

            code.setTrupromiseReversedAt(null);

            code.setCodeClosedReason(
                    closeReason
            );

            return;
        }

        // =====================================================
        // MUST BE PROMISED
        // =====================================================

        if (code.getTrupromiseStatus()
                != TruPromiseStatus.PROMISED) {

            throw new RuntimeException(
                    "TruPromise cannot be reversed from status: "
                            + code.getTrupromiseStatus()
            );
        }

        // =====================================================
        // CLIENT ID
        // =====================================================

        Long clientId =
                code.getClientId();

        if (clientId == null
                || clientId <= 0) {

            throw new RuntimeException(
                    "ClientId missing for TruPromise reversal"
            );
        }

        // =====================================================
        // WALLET CREDIT
        // =====================================================
        //
        // Example:
        //
        // Voucher value = ₹1000
        // TruPromise   = ₹100
        //
        // Generate:
        // Wallet -₹1000
        //
        // Deactivate:
        // Wallet +₹100
        //
        // NOT +₹1000
        //
        // =====================================================

        walletService.recordCreditOnce(
                clientId,
                promiseAmount.setScale(2, RoundingMode.HALF_UP),
                "TruPromise returned to wallet - " + code.getTruvishIdCodeNumber(),
                "TRUPROMISE",
                code.getTruvishIdCodeNumber()
        );

        // =====================================================
        // REVERSED
        // =====================================================

        code.setTrupromiseStatus(
                TruPromiseStatus.REVERSED
        );

        code.setTrupromiseReversedAt(
                LocalDateTime.now()
        );

        code.setCodeClosedReason(
                closeReason
        );
    }
}