package com.truvish.truvishbackend.TruOpeAdmin;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Entity
@Table(name = "truvish_code_generator")
public class TruvishCode {

    // =========================================================
    // PRIMARY KEY
    // =========================================================

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long truvishId;


    // =========================================================
    // CLIENT
    // =========================================================

    @Column(name = "client_id")
    private Long clientId;

    @Column(name = "client_name")
    private String clientName;

    @Column(
            name = "client_img",
            columnDefinition = "TEXT"
    )
    private String clientImg;


    // =========================================================
    // TRUVISH CODE
    // =========================================================

    @Column(name = "truvish_id_code_number")
    private String truvishIdCodeNumber;

    @Column(name = "truvish_code_timestamp")
    private LocalDateTime truvishCodeTimestamp;

    @Enumerated(EnumType.STRING)
    @Column(name = "truvish_code_status")
    private VoucherStatus truvishCodeStatus;


    // =========================================================
    // CODE VALUE
    // =========================================================

    /**
     * Current remaining value of the voucher.
     *
     * Example:
     * Original = 100
     * Partial redeem = 40
     * Current value = 60
     */
    @Column(name = "truvish_code_value")
    private Long truvishCodeValue;

    /**
     * Original voucher value.
     *
     * This must never be changed after code creation.
     *
     * Example:
     * Voucher = 100
     * Original value = 100
     */
    @Column(name = "original_code_value")
    private Long originalCodeValue;

    @Column(name = "validity")
    private Integer validity;

    @Column(name = "truvish_id_is_partial_redeem_allowed")
    private Boolean truvishIdIsPartialRedeemAllowed;


    // =========================================================
    // CLIENT THEME
    // =========================================================

    @Column(
            name = "client_theme",
            length = 150
    )
    private String clientTheme;

    @Column(
            name = "client_theme_img",
            columnDefinition = "TEXT"
    )
    private String clientThemeImg;


    // =========================================================
    // CLIENT BRAND
    // =========================================================
    //
    // Stored directly in:
    // truvish_code_generator.client_brand
    //
    // Example:
    // ["Amazon", "Flipkart"]
    //
    // Database:
    // "Amazon, Flipkart"
    //
    // NO separate brand table.
    // NO @ElementCollection.
    // =========================================================

    @Column(
            name = "client_brand",
            columnDefinition = "TEXT"
    )
    private String clientBrand;


    // =========================================================
    // CLIENT CATEGORY
    // =========================================================
    //
    // Stored directly in:
    // truvish_code_generator.client_category
    //
    // Example:
    // ["Shopping", "E-Commerce"]
    //
    // Database:
    // "Shopping, E-Commerce"
    //
    // NO separate category table.
    // NO @ElementCollection.
    // =========================================================

    @Column(
            name = "client_category",
            columnDefinition = "TEXT"
    )
    private String clientCategory;


    // =========================================================
    // SERIAL NUMBER
    // =========================================================

    @Column(
            name = "serial_number",
            length = 12,
            unique = true
    )
    private String serialNumber;


    // =========================================================
    // REFERENCE NUMBER
    // =========================================================

    @Column(
            name = "reference_number",
            length = 22,
            unique = true
    )
    private String referenceNumber;


    // =========================================================
    // TRUPROMISE
    // =========================================================
    //
    // TruPromise belongs directly to this voucher.
    //
    // No separate TruPromise table.
    //
    // FINAL LIFECYCLE:
    //
    // PROMISED
    //      |
    //      +------> PAID
    //      |
    //      +------> REVERSED
    //
    // PROMISED:
    // Promise amount is reserved as a future conditional
    // wallet reversal amount.
    //
    // PAID:
    // Voucher was redeemed.
    // NO wallet credit is given.
    //
    // REVERSED:
    // Voucher expired/deactivated/cancelled.
    // ONLY trupromiseAmount is credited back to wallet.
    //
    // IMPORTANT:
    // TruPromise NEVER reduces the original wallet debit.
    //
    // Example:
    //
    // Voucher value       = ₹100
    // TruPromise           = 10%
    // TruPromise amount    = ₹10
    //
    // At issue:
    // Wallet debit         = ₹100
    //
    // If redeemed:
    // Wallet credit        = ₹0
    //
    // If expired:
    // Wallet credit        = ₹10
    //
    // Original ₹100 is NEVER refunded.
    // =========================================================

    @Column(
            name = "trupromise_percentage",
            precision = 10,
            scale = 2
    )
    private BigDecimal trupromisePercentage;

    @Column(
            name = "trupromise_amount",
            precision = 12,
            scale = 2
    )
    private BigDecimal trupromiseAmount;

    @Enumerated(EnumType.STRING)
    @Column(
            name = "trupromise_status",
            length = 30
    )
    private TruPromiseStatus trupromiseStatus;

    @Column(name = "trupromise_created_at")
    private LocalDateTime trupromiseCreatedAt;

    @Column(name = "trupromise_paid_at")
    private LocalDateTime trupromisePaidAt;

    @Column(name = "trupromise_reversed_at")
    private LocalDateTime trupromiseReversedAt;


    // =========================================================
    // CODE CLOSED REASON
    // =========================================================
    //
    // Possible values:
    //
    // REDEEMED
    // NORMAL_EXPIRY
    // ADMIN_DEACTIVATED
    // CANCELLED
    //
    // This explains why the voucher lifecycle was closed.
    // =========================================================

    @Column(
            name = "code_closed_reason",
            columnDefinition = "TEXT"
    )
    private String codeClosedReason;


    // =========================================================
    // VALUE HELPERS
    // =========================================================

    private static String joinValues(List<String> values) {

        if (values == null || values.isEmpty()) {
            return null;
        }

        String result =
                values.stream()
                        .filter(Objects::nonNull)
                        .map(String::trim)
                        .filter(value -> !value.isBlank())
                        .distinct()
                        .collect(Collectors.joining(", "));

        return result.isBlank()
                ? null
                : result;
    }


    private static List<String> splitValues(String value) {

        if (value == null || value.isBlank()) {
            return new ArrayList<>();
        }

        return Arrays.stream(value.split(","))
                .map(String::trim)
                .filter(item -> !item.isBlank())
                .distinct()
                .collect(Collectors.toList());
    }


    private static String normalizeValue(String value) {

        if (value == null) {
            return null;
        }

        String trimmed = value.trim();

        return trimmed.isBlank()
                ? null
                : trimmed;
    }


    // =========================================================
    // PRIMARY KEY
    // =========================================================

    public Long getTruvishId() {
        return truvishId;
    }

    public void setTruvishId(Long truvishId) {
        this.truvishId = truvishId;
    }


    // =========================================================
    // CLIENT
    // =========================================================

    public Long getClientId() {
        return clientId;
    }

    public void setClientId(Long clientId) {
        this.clientId = clientId;
    }

    public String getClientName() {
        return clientName;
    }

    public void setClientName(String clientName) {
        this.clientName = clientName;
    }

    public String getClientImg() {
        return clientImg;
    }

    public void setClientImg(String clientImg) {
        this.clientImg = clientImg;
    }


    // =========================================================
    // TRUVISH CODE
    // =========================================================

    public String getTruvishIdCodeNumber() {
        return truvishIdCodeNumber;
    }

    public void setTruvishIdCodeNumber(String truvishIdCodeNumber) {
        this.truvishIdCodeNumber =
                normalizeValue(truvishIdCodeNumber);
    }

    public LocalDateTime getTruvishCodeTimestamp() {
        return truvishCodeTimestamp;
    }

    public void setTruvishCodeTimestamp(
            LocalDateTime truvishCodeTimestamp
    ) {
        this.truvishCodeTimestamp =
                truvishCodeTimestamp;
    }

    public VoucherStatus getTruvishCodeStatus() {
        return truvishCodeStatus;
    }

    public void setTruvishCodeStatus(
            VoucherStatus truvishCodeStatus
    ) {
        this.truvishCodeStatus =
                truvishCodeStatus;
    }


    // =========================================================
    // CODE VALUE
    // =========================================================

    public Long getTruvishCodeValue() {
        return truvishCodeValue;
    }

    public void setTruvishCodeValue(Long value) {
        this.truvishCodeValue = value;
    }

    public Long getOriginalCodeValue() {
        return originalCodeValue;
    }

    public void setOriginalCodeValue(Long value) {
        this.originalCodeValue = value;
    }

    public Integer getValidity() {
        return validity;
    }

    public void setValidity(Integer validity) {
        this.validity = validity;
    }

    public Boolean getTruvishIdIsPartialRedeemAllowed() {
        return truvishIdIsPartialRedeemAllowed;
    }

    public void setTruvishIdIsPartialRedeemAllowed(Boolean value) {
        this.truvishIdIsPartialRedeemAllowed = value;
    }


    // =========================================================
    // CLIENT THEME
    // =========================================================

    public String getClientTheme() {
        return clientTheme;
    }

    public void setClientTheme(String clientTheme) {
        this.clientTheme = clientTheme;
    }

    public String getClientThemeImg() {
        return clientThemeImg;
    }

    public void setClientThemeImg(String clientThemeImg) {
        this.clientThemeImg = clientThemeImg;
    }


    // =========================================================
    // CLIENT BRAND
    // =========================================================

    public List<String> getClientBrand() {
        return splitValues(clientBrand);
    }

    public void setClientBrand(List<String> clientBrand) {
        this.clientBrand = joinValues(clientBrand);
    }

    public String getClientBrandValue() {
        return clientBrand;
    }

    public void setClientBrandValue(String clientBrand) {
        this.clientBrand =
                normalizeValue(clientBrand);
    }


    // =========================================================
    // CLIENT CATEGORY
    // =========================================================

    public List<String> getClientCategory() {
        return splitValues(clientCategory);
    }

    public void setClientCategory(List<String> clientCategory) {
        this.clientCategory = joinValues(clientCategory);
    }

    public String getClientCategoryValue() {
        return clientCategory;
    }

    public void setClientCategoryValue(String clientCategory) {
        this.clientCategory =
                normalizeValue(clientCategory);
    }


    // =========================================================
    // SERIAL NUMBER
    // =========================================================

    public String getSerialNumber() {
        return serialNumber;
    }

    public void setSerialNumber(String serialNumber) {
        this.serialNumber =
                normalizeValue(serialNumber);
    }


    // =========================================================
    // REFERENCE NUMBER
    // =========================================================

    public String getReferenceNumber() {
        return referenceNumber;
    }

    public void setReferenceNumber(String referenceNumber) {
        this.referenceNumber =
                normalizeValue(referenceNumber);
    }


    // =========================================================
    // TRUPROMISE
    // =========================================================

    public BigDecimal getTrupromisePercentage() {
        return trupromisePercentage;
    }

    public void setTrupromisePercentage(
            BigDecimal trupromisePercentage
    ) {
        this.trupromisePercentage =
                trupromisePercentage;
    }


    public BigDecimal getTrupromiseAmount() {
        return trupromiseAmount;
    }

    public void setTrupromiseAmount(
            BigDecimal trupromiseAmount
    ) {
        this.trupromiseAmount =
                trupromiseAmount;
    }


    public TruPromiseStatus getTrupromiseStatus() {
        return trupromiseStatus;
    }

    public void setTrupromiseStatus(
            TruPromiseStatus trupromiseStatus
    ) {
        this.trupromiseStatus =
                trupromiseStatus;
    }


    public LocalDateTime getTrupromiseCreatedAt() {
        return trupromiseCreatedAt;
    }

    public void setTrupromiseCreatedAt(
            LocalDateTime trupromiseCreatedAt
    ) {
        this.trupromiseCreatedAt =
                trupromiseCreatedAt;
    }


    public LocalDateTime getTrupromisePaidAt() {
        return trupromisePaidAt;
    }

    public void setTrupromisePaidAt(
            LocalDateTime trupromisePaidAt
    ) {
        this.trupromisePaidAt =
                trupromisePaidAt;
    }


    public LocalDateTime getTrupromiseReversedAt() {
        return trupromiseReversedAt;
    }

    public void setTrupromiseReversedAt(
            LocalDateTime trupromiseReversedAt
    ) {
        this.trupromiseReversedAt =
                trupromiseReversedAt;
    }


    // =========================================================
    // CODE CLOSED REASON
    // =========================================================

    public String getCodeClosedReason() {
        return codeClosedReason;
    }

    public void setCodeClosedReason(
            String codeClosedReason
    ) {
        this.codeClosedReason =
                normalizeValue(codeClosedReason);
    }
}