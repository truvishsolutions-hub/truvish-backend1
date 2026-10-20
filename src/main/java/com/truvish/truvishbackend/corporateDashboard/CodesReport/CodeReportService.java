package com.truvish.truvishbackend.corporateDashboard.CodesReport;

import com.truvish.truvishbackend.TruOpeAdmin.TruvishCode;
import com.truvish.truvishbackend.TruOpeAdmin.TruvishCodeRepository;
import com.truvish.truvishbackend.TruOpeAdmin.VoucherStatus;
import com.truvish.truvishbackend.redemption.UserRedemption;
import com.truvish.truvishbackend.redemption.UserRedemptionRepository;

import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class CodeReportService {

    private final TruvishCodeRepository truvishCodeRepository;
    private final UserRedemptionRepository userRedemptionRepository;

    public CodeReportService(
            TruvishCodeRepository truvishCodeRepository,
            UserRedemptionRepository userRedemptionRepository
    ) {
        this.truvishCodeRepository = truvishCodeRepository;
        this.userRedemptionRepository = userRedemptionRepository;
    }

    // =========================================================
    // GET ALL CODE REPORTS BY CLIENT ID
    // =========================================================

    public List<CodeReportResponse> getCodeReport(Long clientId) {

        return truvishCodeRepository
                .findByClientIdOrderByTruvishCodeTimestampDesc(clientId)
                .stream()
                .map(this::mapToResponse)
                .toList();
    }


    // =========================================================
    // GET CODE REPORTS BY STATUS
    // =========================================================

    public List<CodeReportResponse> getCodeReportByStatus(
            Long clientId,
            CodeReportStatus status
    ) {

        return truvishCodeRepository
                .findByClientIdOrderByTruvishCodeTimestampDesc(clientId)
                .stream()
                .filter(code -> calculateStatus(code) == status)
                .map(this::mapToResponse)
                .toList();
    }




    public CodeReportResponse getCodeReportByCode(
            String codeNumber
    ) {

        TruvishCode code =
                truvishCodeRepository
                        .findByTruvishIdCodeNumber(codeNumber)
                        .orElseThrow(() ->
                                new RuntimeException(
                                        "Code not found: " + codeNumber
                                )
                        );

        return mapToResponse(code);
    }




    private CodeReportResponse mapToResponse(
            TruvishCode code
    ) {

        LocalDateTime issuedDateTime =
                code.getTruvishCodeTimestamp();

        LocalDateTime expiryDate =
                calculateExpiryDate(
                        issuedDateTime,
                        code.getValidity()
                );

        CodeReportStatus reportStatus =
                calculateStatus(code);

        String originalCode =
                code.getTruvishIdCodeNumber();

        String redeemedBy =
                getRedeemedBy(originalCode);

        return new CodeReportResponse(


                maskCode(originalCode),

                // DENOMINATION
                code.getOriginalCodeValue(),

                // STATUS
                reportStatus.getLabel(),

                // ISSUED DATE & TIME
                issuedDateTime,

                // VALIDITY
                formatValidity(code.getValidity()),

                // EXPIRY DATE
                expiryDate,

                // CAMPAIGN NAME
                code.getClientTheme(),

                // THEME IMAGE
                code.getClientThemeImg(),

                // REDEEMED BY
                redeemedBy
        );
    }




    private String maskCode(String code) {

        if (code == null || code.isBlank()) {
            return "-";
        }

        // Remove all separators
        String cleanCode =
                code.replaceAll("[^a-zA-Z0-9]", "");


        if (cleanCode.length() < 6) {
            return code;
        }

        // First 4 characters
        String firstPart =
                cleanCode.substring(0, 4);

        // Last 2 characters
        String lastPart =
                cleanCode.substring(
                        cleanCode.length() - 2
                );

        // Result:
        // 813E XXXX XX7D
        return firstPart + " XXXX XX" + lastPart;
    }


    // =========================================================
    // CALCULATE EXPIRY DATE
    // =========================================================

    private LocalDateTime calculateExpiryDate(
            LocalDateTime issuedDateTime,
            Integer validity
    ) {

        if (
                issuedDateTime == null
                        ||
                        validity == null
        ) {
            return null;
        }

        return issuedDateTime.plusMonths(validity);
    }


    // =========================================================
    // FORMAT VALIDITY
    // =========================================================

    private String formatValidity(
            Integer validity
    ) {

        if (validity == null) {
            return "N/A";
        }

        if (validity == 1) {
            return "1 Month";
        }

        return validity + " Months";
    }




    private CodeReportStatus calculateStatus(
            TruvishCode code
    ) {

        String codeNumber =
                code.getTruvishIdCodeNumber();




        if (code.getTruvishCodeStatus() == VoucherStatus.REDEEMED) {
            return CodeReportStatus.REDEEMED;
        }

        // Respect persisted lifecycle status before applying calculated expiry.
        // Admin deactivation is represented as EXPIRED + ADMIN_DEACTIVATED.
        if (code.getTruvishCodeStatus() == VoucherStatus.CANCELLED) {
            return CodeReportStatus.CANCELLED;
        }
        if (code.getTruvishCodeStatus() == VoucherStatus.INACTIVE) {
            return CodeReportStatus.INACTIVE;
        }
        if (code.getTruvishCodeStatus() == VoucherStatus.DEACTIVATED) {
            return CodeReportStatus.DEACTIVATED;
        }
        if (code.getTruvishCodeStatus() == VoucherStatus.EXPIRED) {
            if ("ADMIN_DEACTIVATED".equalsIgnoreCase(code.getCodeClosedReason())) {
                return CodeReportStatus.DEACTIVATED;
            }
            return CodeReportStatus.EXPIRED_BACK_TO_WALLET;
        }

        if (
                codeNumber != null
                        &&
                        !codeNumber.isBlank()
        ) {

            List<UserRedemption> redemptions =
                    userRedemptionRepository
                            .findByUserTruvishCodeOrderByUserBrandTimeTempDesc(
                                    codeNumber
                            );

            if (
                    redemptions != null
                            &&
                            !redemptions.isEmpty()
            ) {

                return CodeReportStatus.REDEEMED;
            }
        }




        LocalDateTime expiryDate =
                calculateExpiryDate(
                        code.getTruvishCodeTimestamp(),
                        code.getValidity()
                );

        if (
                expiryDate != null
                        &&
                        !expiryDate.isAfter(
                                LocalDateTime.now()
                        )
        ) {

            return CodeReportStatus
                    .EXPIRED_BACK_TO_WALLET;
        }


        // =====================================================
        // 3. ACTIVE
        // =====================================================

        return CodeReportStatus.ACTIVE;
    }




    private String getRedeemedBy(
            String codeNumber
    ) {

        if (
                codeNumber == null
                        ||
                        codeNumber.isBlank()
        ) {
            return null;
        }

        List<UserRedemption> redemptions =
                userRedemptionRepository
                        .findByUserTruvishCodeOrderByUserBrandTimeTempDesc(
                                codeNumber
                        );

        if (
                redemptions == null
                        ||
                        redemptions.isEmpty()
        ) {
            return null;
        }

        return redemptions
                .get(0)
                .getUserPhoneNumber();
    }
}