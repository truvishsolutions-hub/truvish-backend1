package com.truvish.truvishbackend.cold.entity;

import jakarta.persistence.*;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(
        name = "cold_user_redemption",
        indexes = {
                @Index(
                        name = "idx_cold_user_redemption_hot_record_id",
                        columnList = "hot_record_id"
                ),
                @Index(
                        name = "idx_cold_user_redemption_client_id",
                        columnList = "client_id"
                ),
                @Index(
                        name = "idx_cold_user_redemption_code",
                        columnList = "user_truvish_code"
                ),
                @Index(
                        name = "idx_cold_user_redemption_phone",
                        columnList = "user_phone_number"
                ),
                @Index(
                        name = "idx_cold_user_redemption_archived_at",
                        columnList = "archived_at"
                )
        }
)
public class ColdUserRedemption {

    // =========================================================
    // COLD TABLE PRIMARY KEY
    // =========================================================

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long userId;


    // =========================================================
    // HOT DATABASE REFERENCE
    // =========================================================

    /**
     * ID of the original record from the HOT table.
     *
     * This is kept separately because the COLD database
     * has its own generated primary key.
     */
    @Column(name = "hot_record_id")
    private Long hotRecordId;


    // =========================================================
    // USER / CLIENT INFORMATION
    // =========================================================

    @Column(name = "client_id")
    private Long clientId;

    @Column(name = "client_company_name")
    private String clientCompanyName;

    @Column(name = "user_phone_number")
    private String userPhoneNumber;

    @Column(name = "user_truvish_code")
    private String userTruvishCode;


    // =========================================================
    // BRAND INFORMATION
    // =========================================================

    @Column(name = "user_brand_name")
    private String userBrandName;

    @Column(name = "user_brand_value")
    private Long userBrandValue;

    @Column(name = "user_brand_voucher")
    private String userBrandVoucher;

    @Column(name = "user_brand_pin")
    private String userBrandPin;

    @Column(name = "user_brand_validity")
    private LocalDate userBrandValidity;

    @Column(name = "user_brand_time_temp")
    private LocalDateTime userBrandTimeTemp;

    @Column(name = "brand_logo")
    private String brandLogo;


    // =========================================================
    // BALANCE INFORMATION
    // =========================================================

    @Column(name = "before_balance")
    private Long beforeBalance;

    @Column(name = "after_balance")
    private Long afterBalance;


    // =========================================================
    // REDEMPTION INFORMATION
    // =========================================================

    @Column(name = "history_message")
    private String historyMessage;

    @Column(name = "redeem_status")
    private String redeemStatus;

    @Column(
            name = "redemption_process",
            columnDefinition = "text"
    )
    private String redemptionProcess;


    // =========================================================
    // COLD DATABASE RETENTION / ARCHIVE INFORMATION
    // =========================================================

    /**
     * Time when this record was moved from HOT to COLD.
     *
     * Used for the COLD retention rule:
     *
     * COLD
     *   ↓
     * archivedAt age >= 6 months
     *   ↓
     * Permanent delete
     */
    @Column(name = "archived_at")
    private LocalDateTime archivedAt;

    /**
     * Reason why the record was archived.
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
    // GETTERS / SETTERS
    // =========================================================

    public Long getUserId() {
        return userId;
    }

    public Long getHotRecordId() {
        return hotRecordId;
    }

    public void setHotRecordId(Long hotRecordId) {
        this.hotRecordId = hotRecordId;
    }

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

    public String getUserPhoneNumber() {
        return userPhoneNumber;
    }

    public void setUserPhoneNumber(String userPhoneNumber) {
        this.userPhoneNumber = userPhoneNumber;
    }

    public String getUserTruvishCode() {
        return userTruvishCode;
    }

    public void setUserTruvishCode(String userTruvishCode) {
        this.userTruvishCode = userTruvishCode;
    }

    public String getUserBrandName() {
        return userBrandName;
    }

    public void setUserBrandName(String userBrandName) {
        this.userBrandName = userBrandName;
    }

    public Long getUserBrandValue() {
        return userBrandValue;
    }

    public void setUserBrandValue(Long userBrandValue) {
        this.userBrandValue = userBrandValue;
    }

    public String getUserBrandVoucher() {
        return userBrandVoucher;
    }

    public void setUserBrandVoucher(String userBrandVoucher) {
        this.userBrandVoucher = userBrandVoucher;
    }

    public String getUserBrandPin() {
        return userBrandPin;
    }

    public void setUserBrandPin(String userBrandPin) {
        this.userBrandPin = userBrandPin;
    }

    public LocalDate getUserBrandValidity() {
        return userBrandValidity;
    }

    public void setUserBrandValidity(LocalDate userBrandValidity) {
        this.userBrandValidity = userBrandValidity;
    }

    public LocalDateTime getUserBrandTimeTemp() {
        return userBrandTimeTemp;
    }

    public void setUserBrandTimeTemp(LocalDateTime userBrandTimeTemp) {
        this.userBrandTimeTemp = userBrandTimeTemp;
    }

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

    public String getHistoryMessage() {
        return historyMessage;
    }

    public void setHistoryMessage(String historyMessage) {
        this.historyMessage = historyMessage;
    }

    public String getRedeemStatus() {
        return redeemStatus;
    }

    public void setRedeemStatus(String redeemStatus) {
        this.redeemStatus = redeemStatus;
    }

    public String getBrandLogo() {
        return brandLogo;
    }

    public void setBrandLogo(String brandLogo) {
        this.brandLogo = brandLogo;
    }

    public String getRedemptionProcess() {
        return redemptionProcess;
    }

    public void setRedemptionProcess(String redemptionProcess) {
        this.redemptionProcess = redemptionProcess;
    }

    public LocalDateTime getArchivedAt() {
        return archivedAt;
    }

    public void setArchivedAt(LocalDateTime archivedAt) {
        this.archivedAt = archivedAt;
    }

    public String getArchiveReason() {
        return archiveReason;
    }

    public void setArchiveReason(String archiveReason) {
        this.archiveReason = archiveReason;
    }
}
