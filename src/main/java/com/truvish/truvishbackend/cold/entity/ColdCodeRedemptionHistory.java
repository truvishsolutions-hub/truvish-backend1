package com.truvish.truvishbackend.cold.entity;

import jakarta.persistence.*;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(
        name = "cold_code_redemption_history",
        indexes = {
                @Index(
                        name = "idx_cold_code_redemption_hot_record_id",
                        columnList = "hot_record_id"
                ),
                @Index(
                        name = "idx_cold_code_redemption_code",
                        columnList = "truvish_code"
                ),
                @Index(
                        name = "idx_cold_code_redemption_phone",
                        columnList = "phone_number"
                ),
                @Index(
                        name = "idx_cold_code_redemption_time",
                        columnList = "redeemed_at"
                ),
                @Index(
                        name = "idx_cold_code_redemption_archived_at",
                        columnList = "archived_at"
                )
        }
)
public class ColdCodeRedemptionHistory {

    // =========================================================
    // COLD DATABASE PRIMARY KEY
    // =========================================================

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;


    // =========================================================
    // HOT DATABASE REFERENCE
    // =========================================================

    /**
     * Original record ID from HOT database.
     *
     * Logical reference only.
     * No JPA relationship is used.
     */
    @Column(name = "hot_record_id")
    private Long hotRecordId;


    // =========================================================
    // CLIENT INFORMATION
    // =========================================================

    @Column(name = "client_id")
    private Long clientId;

    @Column(name = "client_company_name", length = 200)
    private String clientCompanyName;


    // =========================================================
    // CODE INFORMATION
    // =========================================================

    @Column(
            name = "truvish_code",
            nullable = false,
            length = 100
    )
    private String truvishCode;

    @Column(name = "phone_number", length = 30)
    private String phoneNumber;


    // =========================================================
    // BRAND INFORMATION
    // =========================================================

    @Column(
            name = "brand_name",
            nullable = false,
            length = 150
    )
    private String brandName;

    @Column(name = "brand_logo", length = 500)
    private String brandLogo;


    // =========================================================
    // REDEMPTION INFORMATION
    // =========================================================

    @Column(name = "redeemed_value", nullable = false)
    private Long redeemedValue;

    @Column(name = "voucher_code", length = 255)
    private String voucherCode;

    @Column(name = "voucher_pin", length = 255)
    private String voucherPin;

    @Column(name = "validity_till")
    private LocalDate validityTill;

    @Column(name = "redeem_status", length = 50)
    private String redeemStatus;

    @Column(name = "history_message", length = 255)
    private String historyMessage;

    @Column(
            name = "redemption_process",
            columnDefinition = "text"
    )
    private String redemptionProcess;


    // =========================================================
    // BALANCE INFORMATION
    // =========================================================

    @Column(name = "before_balance")
    private Long beforeBalance;

    @Column(name = "after_balance")
    private Long afterBalance;


    // =========================================================
    // REDEMPTION TIMESTAMP
    // =========================================================

    @Column(name = "redeemed_at", nullable = false)
    private LocalDateTime redeemedAt;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;


    // =========================================================
    // COLD DATABASE RETENTION / ARCHIVE INFORMATION
    // =========================================================

    /**
     * Time when the record was moved from HOT to COLD.
     *
     * Retention flow:
     *
     * HOT
     *   ↓
     * record age >= 6 months
     *   ↓
     * COLD
     *   ↓
     * archivedAt age >= 6 months
     *   ↓
     * Permanent Delete
     */
    @Column(name = "archived_at")
    private LocalDateTime archivedAt;

    /**
     * Reason why the record was moved from HOT to COLD.
     *
     * Example:
     * "HOT record older than 6 months"
     */
    @Column(
            name = "archive_reason",
            columnDefinition = "text"
    )
    private String archiveReason;


    // =========================================================
    // DEFAULT CONSTRUCTOR
    // =========================================================

    /**
     * Default constructor required by JPA.
     */
    public ColdCodeRedemptionHistory() {
    }


    // =========================================================
    // PRE-PERSIST
    // =========================================================

    /**
     * Automatically sets timestamps when a new
     * COLD record is created.
     */
    @PrePersist
    public void prePersist() {

        if (redeemedAt == null) {
            redeemedAt = LocalDateTime.now();
        }

        if (createdAt == null) {
            createdAt = LocalDateTime.now();
        }

        if (archivedAt == null) {
            archivedAt = LocalDateTime.now();
        }
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


    // ---------------------------------------------------------
    // HOT RECORD ID
    // ---------------------------------------------------------

    public Long getHotRecordId() {
        return hotRecordId;
    }

    public void setHotRecordId(Long hotRecordId) {
        this.hotRecordId = hotRecordId;
    }


    // ---------------------------------------------------------
    // CLIENT
    // ---------------------------------------------------------

    public Long getClientId() {
        return clientId;
    }

    public void setClientId(Long clientId) {
        this.clientId = clientId;
    }

    public String getClientCompanyName() {
        return clientCompanyName;
    }

    public void setClientCompanyName(String clientCompanyName) {
        this.clientCompanyName = clientCompanyName;
    }


    // ---------------------------------------------------------
    // TRUVISH CODE
    // ---------------------------------------------------------

    public String getTruvishCode() {
        return truvishCode;
    }

    public void setTruvishCode(String truvishCode) {
        this.truvishCode = truvishCode;
    }


    // ---------------------------------------------------------
    // PHONE
    // ---------------------------------------------------------

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }


    // ---------------------------------------------------------
    // BRAND
    // ---------------------------------------------------------

    public String getBrandName() {
        return brandName;
    }

    public void setBrandName(String brandName) {
        this.brandName = brandName;
    }

    public String getBrandLogo() {
        return brandLogo;
    }

    public void setBrandLogo(String brandLogo) {
        this.brandLogo = brandLogo;
    }


    // ---------------------------------------------------------
    // REDEEMED VALUE
    // ---------------------------------------------------------

    public Long getRedeemedValue() {
        return redeemedValue;
    }

    public void setRedeemedValue(Long redeemedValue) {
        this.redeemedValue = redeemedValue;
    }


    // ---------------------------------------------------------
    // VOUCHER
    // ---------------------------------------------------------

    public String getVoucherCode() {
        return voucherCode;
    }

    public void setVoucherCode(String voucherCode) {
        this.voucherCode = voucherCode;
    }

    public String getVoucherPin() {
        return voucherPin;
    }

    public void setVoucherPin(String voucherPin) {
        this.voucherPin = voucherPin;
    }

    public LocalDate getValidityTill() {
        return validityTill;
    }

    public void setValidityTill(LocalDate validityTill) {
        this.validityTill = validityTill;
    }


    // ---------------------------------------------------------
    // BALANCE
    // ---------------------------------------------------------

    public Long getBeforeBalance() {
        return beforeBalance;
    }

    public void setBeforeBalance(Long beforeBalance) {
        this.beforeBalance = beforeBalance;
    }

    public Long getAfterBalance() {
        return afterBalance;
    }

    public void setAfterBalance(Long afterBalance) {
        this.afterBalance = afterBalance;
    }


    // ---------------------------------------------------------
    // REDEMPTION STATUS
    // ---------------------------------------------------------

    public String getRedeemStatus() {
        return redeemStatus;
    }

    public void setRedeemStatus(String redeemStatus) {
        this.redeemStatus = redeemStatus;
    }

    public String getHistoryMessage() {
        return historyMessage;
    }

    public void setHistoryMessage(String historyMessage) {
        this.historyMessage = historyMessage;
    }

    public String getRedemptionProcess() {
        return redemptionProcess;
    }

    public void setRedemptionProcess(String redemptionProcess) {
        this.redemptionProcess = redemptionProcess;
    }


    // ---------------------------------------------------------
    // REDEMPTION TIME
    // ---------------------------------------------------------

    public LocalDateTime getRedeemedAt() {
        return redeemedAt;
    }

    public void setRedeemedAt(LocalDateTime redeemedAt) {
        this.redeemedAt = redeemedAt;
    }


    // ---------------------------------------------------------
    // CREATED TIME
    // ---------------------------------------------------------

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    /**
     * Setter required by archive/service code.
     */
    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }


    // ---------------------------------------------------------
    // ARCHIVE TIME
    // ---------------------------------------------------------

    public LocalDateTime getArchivedAt() {
        return archivedAt;
    }

    public void setArchivedAt(LocalDateTime archivedAt) {
        this.archivedAt = archivedAt;
    }


    // ---------------------------------------------------------
    // ARCHIVE REASON
    // ---------------------------------------------------------

    public String getArchiveReason() {
        return archiveReason;
    }

    public void setArchiveReason(String archiveReason) {
        this.archiveReason = archiveReason;
    }
}
