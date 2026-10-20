package com.truvish.truvishbackend.TruOpeAdmin;

import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.math.BigDecimal;
import java.util.List;

public class CodeAssignmentDto {

    // =========================================================
    // QUANTITY
    // =========================================================

    @NotNull(message = "Quantity is required")
    @Positive(message = "Quantity must be greater than 0")
    private Integer quantity;


    // =========================================================
    // OPTIONAL TRUVISH ID
    // =========================================================

    private Long truvishId;


    // =========================================================
    // CLIENT ID
    //
    // Used for wallet debit.
    //
    // IMPORTANT:
    // Backend will debit the FULL voucher value.
    // =========================================================

    @NotNull(message = "Client ID is required")
    private Long clientId;


    // =========================================================
    // CLIENT NAME
    // =========================================================

    @NotBlank(message = "Client name is required")
    private String clientName;


    // =========================================================
    // CLIENT IMAGE / LOGO
    // =========================================================

    private String clientImg;


    // =========================================================
    // CLIENT THEME
    // =========================================================

    private String clientTheme;


    // =========================================================
    // CLIENT BRAND
    // =========================================================

    private List<String> clientBrand;


    // =========================================================
    // CLIENT CATEGORY
    // =========================================================

    private List<String> clientCategory;


    // =========================================================
    // VOUCHER VALUE
    // =========================================================

    @NotNull(message = "Voucher value is required")
    @Positive(message = "Voucher value must be greater than 0")
    private Long truvishCodeValue;


    // =========================================================
    // VALIDITY
    // =========================================================

    @NotNull(message = "Validity is required")
    @Min(
            value = 1,
            message = "Validity must be at least 1 month"
    )
    private Integer validity;


    // =========================================================
    // CLIENT THEME IMAGE
    // =========================================================

    private String clientThemeImg;


    // =========================================================
    // TRUPROMISE PERCENTAGE
    //
    // Example:
    //
    // Voucher Value       = ₹1000
    // TruPromise          = 10%
    // TruPromise Amount   = ₹100
    //
    // At voucher generation:
    //
    // Wallet Debit        = ₹1000 FULL
    //
    // TruPromise:
    // PROMISED            = ₹100
    //
    // If Redeemed:
    // PROMISED -> PAID
    // Wallet credit       = ₹0
    //
    // If Expired:
    // PROMISED -> REVERSED
    // Wallet credit       = ₹100
    //
    // If Admin Deactivate:
    // PROMISED -> REVERSED
    // Wallet credit       = ₹100
    //
    // Original voucher value ₹1000 is NEVER refunded.
    //
    // Allowed:
    // 0% to 100%
    // =========================================================

    @DecimalMin(
            value = "0.00",
            message = "TruPromise percentage cannot be less than 0"
    )
    @DecimalMax(
            value = "100.00",
            message = "TruPromise percentage cannot be greater than 100"
    )
    private BigDecimal trupromisePercentage = BigDecimal.ZERO;


    // =========================================================
    // GETTERS / SETTERS
    // =========================================================

    // =========================================================
    // QUANTITY
    // =========================================================

    public Integer getQuantity() {
        return quantity;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }


    // =========================================================
    // TRUVISH ID
    // =========================================================

    public Long getTruvishId() {
        return truvishId;
    }

    public void setTruvishId(Long truvishId) {
        this.truvishId = truvishId;
    }


    // =========================================================
    // CLIENT ID
    // =========================================================

    public Long getClientId() {
        return clientId;
    }

    public void setClientId(Long clientId) {
        this.clientId = clientId;
    }


    // =========================================================
    // CLIENT NAME
    // =========================================================

    public String getClientName() {
        return clientName;
    }

    public void setClientName(String clientName) {
        this.clientName = clientName;
    }


    // =========================================================
    // CLIENT IMAGE
    // =========================================================

    public String getClientImg() {
        return clientImg;
    }

    public void setClientImg(String clientImg) {
        this.clientImg = clientImg;
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


    // =========================================================
    // CLIENT BRAND
    // =========================================================

    public List<String> getClientBrand() {
        return clientBrand;
    }

    public void setClientBrand(List<String> clientBrand) {
        this.clientBrand = clientBrand;
    }


    // =========================================================
    // CLIENT CATEGORY
    // =========================================================

    public List<String> getClientCategory() {
        return clientCategory;
    }

    public void setClientCategory(List<String> clientCategory) {
        this.clientCategory = clientCategory;
    }


    // =========================================================
    // VOUCHER VALUE
    // =========================================================

    public Long getTruvishCodeValue() {
        return truvishCodeValue;
    }

    public void setTruvishCodeValue(Long truvishCodeValue) {
        this.truvishCodeValue = truvishCodeValue;
    }


    // =========================================================
    // VALIDITY
    // =========================================================

    public Integer getValidity() {
        return validity;
    }

    public void setValidity(Integer validity) {
        this.validity = validity;
    }


    // =========================================================
    // THEME IMAGE
    // =========================================================

    public String getClientThemeImg() {
        return clientThemeImg;
    }

    public void setClientThemeImg(String clientThemeImg) {
        this.clientThemeImg = clientThemeImg;
    }


    // =========================================================
    // TRUPROMISE PERCENTAGE
    // =========================================================

    public BigDecimal getTrupromisePercentage() {
        return trupromisePercentage;
    }

    public void setTrupromisePercentage(
            BigDecimal trupromisePercentage
    ) {
        this.trupromisePercentage =
                trupromisePercentage == null
                        ? BigDecimal.ZERO
                        : trupromisePercentage;
    }
}