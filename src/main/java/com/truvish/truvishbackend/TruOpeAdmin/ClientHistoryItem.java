package com.truvish.truvishbackend.TruOpeAdmin;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class ClientHistoryItem {

    // =========================================================
    // BASIC HISTORY
    // =========================================================

    private LocalDateTime eventTime;
    private String code;
    private Long amount;
    private Long remainingBalance;
    private String message;
    private String eventType;

    // =========================================================
    // VOUCHER DETAILS
    // =========================================================

    private Integer validityMonths;
    private LocalDateTime expiryDate;

    private String redeemedBrand;
    private String redeemedPhone;

    private LocalDateTime issuedDate;
    private LocalDateTime redeemedDate;

    // DIGITAL or PHYSICAL
    private String codeType;

    // =========================================================
    // PHYSICAL CODE DETAILS
    // =========================================================

    private String serialNumber;
    private String referenceNumber;

    // =========================================================
    // TRUPROMISE
    // =========================================================

    /**
     * TruPromise percentage.
     *
     * Example:
     * Voucher Value = 100
     * TruPromise = 10%
     */
    private BigDecimal trupromisePercentage;

    /**
     * Actual TruPromise amount.
     *
     * Example:
     * Voucher Value = 100
     * TruPromise = 10%
     * Amount = 10.00
     */
    private BigDecimal trupromiseAmount;

    /**
     * PROMISED
     * PAID
     * REVERSED
     * BACK_TO_WALLET
     */
    private String trupromiseStatus;

    /**
     * TruPromise created timestamp.
     */
    private LocalDateTime trupromiseCreatedAt;

    /**
     * TruPromise paid timestamp.
     */
    private LocalDateTime trupromisePaidAt;

    /**
     * TruPromise reversed timestamp.
     */
    private LocalDateTime trupromiseReversedAt;


    // =========================================================
    // DEFAULT CONSTRUCTOR
    // =========================================================

    public ClientHistoryItem() {
    }


    // =========================================================
    // OLD CONSTRUCTOR
    //
    // Existing code compatibility ke liye rakha gaya hai.
    // =========================================================

    public ClientHistoryItem(
            LocalDateTime eventTime,
            String code,
            Long amount,
            Long remainingBalance,
            String message,
            String eventType,
            Integer validityMonths,
            LocalDateTime expiryDate,
            String redeemedBrand,
            String redeemedPhone,
            LocalDateTime issuedDate,
            LocalDateTime redeemedDate
    ) {
        this.eventTime = eventTime;
        this.code = code;
        this.amount = amount;
        this.remainingBalance = remainingBalance;
        this.message = message;
        this.eventType = eventType;
        this.validityMonths = validityMonths;
        this.expiryDate = expiryDate;
        this.redeemedBrand = redeemedBrand;
        this.redeemedPhone = redeemedPhone;
        this.issuedDate = issuedDate;
        this.redeemedDate = redeemedDate;
    }


    // =========================================================
    // NEW CONSTRUCTOR
    //
    // TruPromise ke saath complete history object
    // =========================================================

    public ClientHistoryItem(
            LocalDateTime eventTime,
            String code,
            Long amount,
            Long remainingBalance,
            String message,
            String eventType,
            Integer validityMonths,
            LocalDateTime expiryDate,
            String redeemedBrand,
            String redeemedPhone,
            LocalDateTime issuedDate,
            LocalDateTime redeemedDate,
            BigDecimal trupromiseAmount,
            BigDecimal trupromisePercentage,
            String trupromiseStatus
    ) {
        this.eventTime = eventTime;
        this.code = code;
        this.amount = amount;
        this.remainingBalance = remainingBalance;
        this.message = message;
        this.eventType = eventType;
        this.validityMonths = validityMonths;
        this.expiryDate = expiryDate;
        this.redeemedBrand = redeemedBrand;
        this.redeemedPhone = redeemedPhone;
        this.issuedDate = issuedDate;
        this.redeemedDate = redeemedDate;

        this.trupromiseAmount = trupromiseAmount;
        this.trupromisePercentage = trupromisePercentage;
        this.trupromiseStatus = trupromiseStatus;
    }


    // =========================================================
    // BASIC GETTERS / SETTERS
    // =========================================================

    public LocalDateTime getEventTime() {
        return eventTime;
    }

    public void setEventTime(LocalDateTime eventTime) {
        this.eventTime = eventTime;
    }


    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }


    public Long getAmount() {
        return amount;
    }

    public void setAmount(Long amount) {
        this.amount = amount;
    }


    public Long getRemainingBalance() {
        return remainingBalance;
    }

    public void setRemainingBalance(Long remainingBalance) {
        this.remainingBalance = remainingBalance;
    }


    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }


    public String getEventType() {
        return eventType;
    }

    public void setEventType(String eventType) {
        this.eventType = eventType;
    }


    // =========================================================
    // VOUCHER GETTERS / SETTERS
    // =========================================================

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


    public String getRedeemedBrand() {
        return redeemedBrand;
    }

    public void setRedeemedBrand(String redeemedBrand) {
        this.redeemedBrand = redeemedBrand;
    }


    public String getRedeemedPhone() {
        return redeemedPhone;
    }

    public void setRedeemedPhone(String redeemedPhone) {
        this.redeemedPhone = redeemedPhone;
    }


    public LocalDateTime getIssuedDate() {
        return issuedDate;
    }

    public void setIssuedDate(LocalDateTime issuedDate) {
        this.issuedDate = issuedDate;
    }


    public LocalDateTime getRedeemedDate() {
        return redeemedDate;
    }

    public void setRedeemedDate(LocalDateTime redeemedDate) {
        this.redeemedDate = redeemedDate;
    }


    public String getCodeType() {
        return codeType;
    }

    public void setCodeType(String codeType) {
        this.codeType = codeType;
    }


    // =========================================================
    // PHYSICAL CODE GETTERS / SETTERS
    // =========================================================

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


    // =========================================================
    // TRUPROMISE GETTERS / SETTERS
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


    public String getTrupromiseStatus() {
        return trupromiseStatus;
    }

    public void setTrupromiseStatus(
            String trupromiseStatus
    ) {
        this.trupromiseStatus =
                trupromiseStatus;
    }


    // =========================================================
    // TRUPROMISE TIMESTAMP GETTERS / SETTERS
    // =========================================================

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
}
