package com.truvish.truvishbackend.TruBlankCode;

import jakarta.persistence.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Entity
@Table(
        name = "truvish_blank_code",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uk_blank_code_number",
                        columnNames = "code_number"
                ),
                @UniqueConstraint(
                        name = "uk_blank_serial_number",
                        columnNames = "serial_number"
                ),
                @UniqueConstraint(
                        name = "uk_blank_reference_number",
                        columnNames = "reference_number"
                )
        }
)
public class TruBlankCode {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // =========================================================
    // CODE INFORMATION
    // =========================================================

    @Column(
            name = "generation_number",
            nullable = false
    )
    private Long generationNumber;

    @Column(
            name = "code_number",
            nullable = false,
            length = 20
    )
    private String codeNumber;

    @Column(
            name = "serial_number",
            nullable = false,
            length = 30
    )
    private String serialNumber;

    @Column(
            name = "reference_number",
            nullable = false,
            length = 100
    )
    private String referenceNumber;

    // =========================================================
    // REWARD
    // =========================================================

    @Column(name = "denomination")
    private Long denomination;

    @Column(name = "validity_months")
    private Integer validityMonths;

    @Column(name = "expiry_date")
    private LocalDateTime expiryDate;

    // =========================================================
    // TRUCARD BRAND
    // =========================================================
    // IMPORTANT:
    // Stored directly in truvish_blank_code.
    // No @ElementCollection.
    // No separate truvish_blank_code_brands table.
    //
    // Multiple values are stored as comma-separated TEXT.
    // Existing List<String> getter/setter is preserved.
    // =========================================================

    @Column(
            name = "brand_name",
            columnDefinition = "TEXT"
    )
    private String brandName;

    @Column(
            name = "brand_category",
            length = 100
    )
    private String brandCategory;

    // =========================================================
    // TRUCARD THEME
    // =========================================================

    @Column(
            name = "theme_name",
            length = 150
    )
    private String themeName;

    @Column(
            name = "theme_img",
            columnDefinition = "TEXT"
    )
    private String themeImg;

    // =========================================================
    // STATUS
    // =========================================================

    @Enumerated(EnumType.STRING)
    @Column(
            name = "status",
            nullable = false,
            length = 30
    )
    private TruBlankCodeStatus status;

    // =========================================================
    // LIFECYCLE
    // =========================================================

    @Column(
            name = "created_at",
            nullable = false,
            updatable = false
    )
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @Column(name = "assigned_at")
    private LocalDateTime assignedAt;

    @Column(name = "activated_at")
    private LocalDateTime activatedAt;

    @Column(name = "activated_by")
    private Long activatedBy;

    @Column(name = "redeemed_at")
    private LocalDateTime redeemedAt;

    @Column(name = "created_by")
    private Long createdBy;

    // =========================================================
    // CLIENT
    // =========================================================

    @Column(name = "client_id")
    private Long clientId;

    @Column(
            name = "client_name",
            length = 200
    )
    private String clientName;

    @Column(
            name = "client_img",
            columnDefinition = "TEXT"
    )
    private String clientImg;

    // =========================================================
    // CLIENT BRAND
    // =========================================================
    // IMPORTANT:
    // Stored directly in truvish_blank_code.
    // No @ElementCollection.
    // No separate truvish_blank_code_client_brands table.
    //
    // Multiple values are stored as comma-separated TEXT.
    // Existing List<String> getter/setter is preserved.
    // =========================================================

    @Column(
            name = "client_brand",
            columnDefinition = "TEXT"
    )
    private String clientBrand;

    @Column(
            name = "client_category",
            length = 100
    )
    private String clientCategory;

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
    // BALANCE SNAPSHOT
    // =========================================================

    @Column(
            name = "client_balance_before_activation",
            precision = 19,
            scale = 2
    )
    private BigDecimal clientBalanceBeforeActivation;

    @Column(
            name = "client_balance_after_activation",
            precision = 19,
            scale = 2
    )
    private BigDecimal clientBalanceAfterActivation;

    // =========================================================
    // TRUPROMISE
    // =========================================================

    /**
     * TruPromise percentage configured for this voucher.
     *
     * Example:
     * Voucher = 100
     * Percentage = 10
     * Promise = 10
     */
    @Column(
            name = "trupromise_percentage",
            precision = 5,
            scale = 2
    )
    private BigDecimal trupromisePercentage;

    /**
     * Calculated TruPromise amount.
     *
     * Example:
     * Voucher = 100
     * Percentage = 10%
     * Amount = 10
     */
    @Column(
            name = "trupromise_amount",
            precision = 19,
            scale = 2
    )
    private BigDecimal trupromiseAmount;

    /**
     * TruPromise lifecycle:
     *
     * PROMISED  -> Promise created
     * PAID      -> Customer successfully redeemed voucher
     * REVERSED  -> Promise returned to client wallet
     */
    @Enumerated(EnumType.STRING)
    @Column(
            name = "trupromise_status",
            length = 30
    )
    private TruPromiseStatus trupromiseStatus;

    /**
     * When the TruPromise was created.
     */
    @Column(name = "trupromise_created_at")
    private LocalDateTime trupromiseCreatedAt;

    /**
     * When the TruPromise became PAID.
     */
    @Column(name = "trupromise_paid_at")
    private LocalDateTime trupromisePaidAt;

    /**
     * When the TruPromise was reversed back to wallet.
     */
    @Column(name = "trupromise_reversed_at")
    private LocalDateTime trupromiseReversedAt;

    // =========================================================
    // CONSTRUCTOR
    // =========================================================

    public TruBlankCode() {
        this.brandName = null;
        this.clientBrand = null;
    }

    // =========================================================
    // PRE PERSIST
    // =========================================================

    @PrePersist
    protected void onCreate() {

        LocalDateTime now = LocalDateTime.now();

        if (createdAt == null) {
            createdAt = now;
        }

        if (updatedAt == null) {
            updatedAt = now;
        }

        if (status == null) {
            status = TruBlankCodeStatus.INACTIVE;
        }
    }

    // =========================================================
    // PRE UPDATE
    // =========================================================

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }

    // =========================================================
    // VALUE CONVERSION HELPERS
    // =========================================================

    /**
     * Converts List<String> into one TEXT value.
     *
     * Example:
     * ["Amazon", "Flipkart"]
     *
     * becomes:
     * "Amazon, Flipkart"
     */
    private static String joinValues(List<String> values) {

        if (values == null || values.isEmpty()) {
            return null;
        }

        String result = values.stream()
                .filter(Objects::nonNull)
                .map(String::trim)
                .filter(value -> !value.isBlank())
                .distinct()
                .collect(Collectors.joining(", "));

        return result.isBlank() ? null : result;
    }

    /**
     * Converts one TEXT value back into List<String>.
     *
     * Example:
     * "Amazon, Flipkart"
     *
     * becomes:
     * ["Amazon", "Flipkart"]
     */
    private static List<String> splitValues(String value) {

        if (value == null || value.isBlank()) {
            return new ArrayList<>();
        }

        return Arrays.stream(value.split(","))
                .map(String::trim)
                .filter(valueItem -> !valueItem.isBlank())
                .distinct()
                .collect(Collectors.toList());
    }

    // =========================================================
    // GETTERS / SETTERS
    // =========================================================

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getGenerationNumber() {
        return generationNumber;
    }

    public void setGenerationNumber(Long generationNumber) {
        this.generationNumber = generationNumber;
    }

    public String getCodeNumber() {
        return codeNumber;
    }

    public void setCodeNumber(String codeNumber) {
        this.codeNumber = codeNumber;
    }

    public String getSerialNumber() {
        return serialNumber;
    }

    public void setSerialNumber(String serialNumber) {
        this.serialNumber = serialNumber;
    }

    public String getReferenceNumber() {
        return referenceNumber;
    }

    public void setReferenceNumber(String referenceNumber) {
        this.referenceNumber = referenceNumber;
    }

    public Long getDenomination() {
        return denomination;
    }

    public void setDenomination(Long denomination) {
        this.denomination = denomination;
    }

    public Integer getValidityMonths() {
        return validityMonths;
    }

    public void setValidityMonths(Integer validityMonths) {
        this.validityMonths = validityMonths;
    }

    public LocalDateTime getExpiryDate() {
        return expiryDate;
    }

    public void setExpiryDate(LocalDateTime expiryDate) {
        this.expiryDate = expiryDate;
    }

    // =========================================================
    // BRAND
    // =========================================================

    /**
     * Backward-compatible getter.
     *
     * Existing code can continue using:
     * getBrandNames()
     */
    public List<String> getBrandNames() {
        return splitValues(brandName);
    }

    /**
     * Backward-compatible setter.
     *
     * Existing code can continue using:
     * setBrandNames(List<String>)
     */
    public void setBrandNames(List<String> brandNames) {
        this.brandName = joinValues(brandNames);
    }

    /**
     * Direct single TEXT getter.
     */
    public String getBrandName() {
        return brandName;
    }

    /**
     * Direct single TEXT setter.
     */
    public void setBrandName(String brandName) {
        this.brandName = normalizeValue(brandName);
    }

    private static String normalizeValue(String value) {

        if (value == null) {
            return null;
        }

        String trimmed = value.trim();

        return trimmed.isBlank() ? null : trimmed;
    }

    public String getBrandCategory() {
        return brandCategory;
    }

    public void setBrandCategory(String brandCategory) {
        this.brandCategory = brandCategory;
    }

    // =========================================================
    // THEME
    // =========================================================

    public String getThemeName() {
        return themeName;
    }

    public void setThemeName(String themeName) {
        this.themeName = themeName;
    }

    public String getThemeImg() {
        return themeImg;
    }

    public void setThemeImg(String themeImg) {
        this.themeImg = themeImg;
    }

    // =========================================================
    // STATUS
    // =========================================================

    public TruBlankCodeStatus getStatus() {
        return status;
    }

    public void setStatus(TruBlankCodeStatus status) {
        this.status = status;
    }

    // =========================================================
    // LIFECYCLE
    // =========================================================

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }

    public LocalDateTime getAssignedAt() {
        return assignedAt;
    }

    public void setAssignedAt(LocalDateTime assignedAt) {
        this.assignedAt = assignedAt;
    }

    public LocalDateTime getActivatedAt() {
        return activatedAt;
    }

    public void setActivatedAt(LocalDateTime activatedAt) {
        this.activatedAt = activatedAt;
    }

    public Long getActivatedBy() {
        return activatedBy;
    }

    public void setActivatedBy(Long activatedBy) {
        this.activatedBy = activatedBy;
    }

    public LocalDateTime getRedeemedAt() {
        return redeemedAt;
    }

    public void setRedeemedAt(LocalDateTime redeemedAt) {
        this.redeemedAt = redeemedAt;
    }

    public Long getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(Long createdBy) {
        this.createdBy = createdBy;
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
    // CLIENT BRAND
    // =========================================================

    /**
     * Backward-compatible getter.
     *
     * Existing code can continue using:
     * getClientBrand()
     */
    public List<String> getClientBrand() {
        return splitValues(clientBrand);
    }

    /**
     * Backward-compatible setter.
     *
     * Existing code can continue using:
     * setClientBrand(List<String>)
     */
    public void setClientBrand(List<String> clientBrands) {
        this.clientBrand = joinValues(clientBrands);
    }

    /**
     * Direct TEXT getter.
     *
     * Named differently so it does not conflict with
     * the existing List<String> getClientBrand() method.
     */
    public String getClientBrandValue() {
        return clientBrand;
    }

    /**
     * Direct TEXT setter.
     */
    public void setClientBrandValue(String clientBrand) {
        this.clientBrand = normalizeValue(clientBrand);
    }

    public String getClientCategory() {
        return clientCategory;
    }

    public void setClientCategory(String clientCategory) {
        this.clientCategory = clientCategory;
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
    // BALANCE
    // =========================================================

    public BigDecimal getClientBalanceBeforeActivation() {
        return clientBalanceBeforeActivation;
    }

    public void setClientBalanceBeforeActivation(BigDecimal value) {
        this.clientBalanceBeforeActivation = value;
    }

    public BigDecimal getClientBalanceAfterActivation() {
        return clientBalanceAfterActivation;
    }

    public void setClientBalanceAfterActivation(BigDecimal value) {
        this.clientBalanceAfterActivation = value;
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
        this.trupromisePercentage = trupromisePercentage;
    }

    public BigDecimal getTrupromiseAmount() {
        return trupromiseAmount;
    }

    public void setTrupromiseAmount(
            BigDecimal trupromiseAmount
    ) {
        this.trupromiseAmount = trupromiseAmount;
    }

    public TruPromiseStatus getTrupromiseStatus() {
        return trupromiseStatus;
    }

    public void setTrupromiseStatus(
            TruPromiseStatus trupromiseStatus
    ) {
        this.trupromiseStatus = trupromiseStatus;
    }

    public LocalDateTime getTrupromiseCreatedAt() {
        return trupromiseCreatedAt;
    }

    public void setTrupromiseCreatedAt(
            LocalDateTime trupromiseCreatedAt
    ) {
        this.trupromiseCreatedAt = trupromiseCreatedAt;
    }

    public LocalDateTime getTrupromisePaidAt() {
        return trupromisePaidAt;
    }

    public void setTrupromisePaidAt(
            LocalDateTime trupromisePaidAt
    ) {
        this.trupromisePaidAt = trupromisePaidAt;
    }

    public LocalDateTime getTrupromiseReversedAt() {
        return trupromiseReversedAt;
    }

    public void setTrupromiseReversedAt(
            LocalDateTime trupromiseReversedAt
    ) {
        this.trupromiseReversedAt = trupromiseReversedAt;
    }
}