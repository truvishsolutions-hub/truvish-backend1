package com.truvish.truvishbackend.TruOpeAdmin;

import jakarta.validation.Valid;
import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

public class OrderVoucherRequest {

    // =========================================================
    // CLIENT
    // =========================================================

    @NotNull(message = "Client ID is required")
    private Long clientId;


    // =========================================================
    // THEME
    // =========================================================

    @NotBlank(message = "Theme name is required")
    private String themeName;

    @NotBlank(message = "Theme image is required")
    private String themeImg;


    // =========================================================
    // VALIDITY
    // =========================================================

    @NotNull(message = "Validity months is required")
    @Min(
            value = 1,
            message = "Validity must be at least 1 month"
    )
    private Integer validityMonths;


    // =========================================================
    // BRAND
    // =========================================================

    private List<String> clientBrand =
            new ArrayList<>();


    // =========================================================
    // CATEGORY
    // =========================================================

    private List<String> clientCategory =
            new ArrayList<>();


    // =========================================================
    // TRUPROMISE PERCENTAGE
    //
    // Example:
    //
    // Voucher Value      = ₹1000
    // TruPromise         = 10%
    // TruPromise Amount  = ₹100
    //
    // IMPORTANT:
    //
    // Wallet Debit       = ₹1000 FULL
    //
    // TruPromise is NOT deducted from the voucher value.
    //
    // It is only a future conditional amount:
    //
    // REDEEM:
    // PROMISED -> PAID
    // Wallet credit = ₹0
    //
    // EXPIRY:
    // PROMISED -> REVERSED
    // Wallet credit = ₹100
    //
    // DEACTIVATE:
    // PROMISED -> REVERSED
    // Wallet credit = ₹100
    //
    // Original voucher value ₹1000 is NEVER refunded.
    //
    // Allowed range:
    // 0% - 100%
    // =========================================================

    @DecimalMin(
            value = "0.00",
            message = "TruPromise percentage cannot be less than 0"
    )
    @DecimalMax(
            value = "100.00",
            message = "TruPromise percentage cannot be greater than 100"
    )
    private BigDecimal trupromisePercentage =
            BigDecimal.ZERO;


    // =========================================================
    // DENOMINATION ITEMS
    // =========================================================

    @Valid
    @NotEmpty(
            message =
                    "At least one denomination item is required"
    )
    private List<DenomItem> items;


    // =========================================================
    // GETTERS / SETTERS
    // =========================================================

    // =========================================================
    // CLIENT
    // =========================================================

    public Long getClientId() {
        return clientId;
    }

    public void setClientId(
            Long clientId
    ) {
        this.clientId = clientId;
    }


    // =========================================================
    // THEME
    // =========================================================

    public String getThemeName() {
        return themeName;
    }

    public void setThemeName(
            String themeName
    ) {
        this.themeName = themeName;
    }


    public String getThemeImg() {
        return themeImg;
    }

    public void setThemeImg(
            String themeImg
    ) {
        this.themeImg = themeImg;
    }


    // =========================================================
    // VALIDITY
    // =========================================================

    public Integer getValidityMonths() {
        return validityMonths;
    }

    public void setValidityMonths(
            Integer validityMonths
    ) {
        this.validityMonths =
                validityMonths;
    }


    // =========================================================
    // CLIENT BRAND
    // =========================================================

    public List<String> getClientBrand() {
        return clientBrand;
    }

    public void setClientBrand(
            List<String> clientBrand
    ) {
        this.clientBrand =
                clientBrand == null
                        ? new ArrayList<>()
                        : new ArrayList<>(
                        clientBrand
                );
    }


    // =========================================================
    // CLIENT CATEGORY
    // =========================================================

    public List<String> getClientCategory() {
        return clientCategory;
    }

    public void setClientCategory(
            List<String> clientCategory
    ) {
        this.clientCategory =
                clientCategory == null
                        ? new ArrayList<>()
                        : new ArrayList<>(
                        clientCategory
                );
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


    // =========================================================
    // DENOMINATION ITEMS
    // =========================================================

    public List<DenomItem> getItems() {
        return items;
    }

    public void setItems(
            List<DenomItem> items
    ) {
        this.items = items;
    }


    // =========================================================
    // DENOMINATION ITEM
    // =========================================================

    public static class DenomItem {

        // =====================================================
        // DENOMINATION
        // =====================================================

        @NotNull(
                message =
                        "Denomination is required"
        )
        @Positive(
                message =
                        "Denomination must be greater than 0"
        )
        private Long denomination;


        // =====================================================
        // QUANTITY
        // =====================================================

        @NotNull(
                message =
                        "Quantity is required"
        )
        @Positive(
                message =
                        "Quantity must be greater than 0"
        )
        private Integer quantity;


        // =====================================================
        // GETTERS / SETTERS
        // =====================================================

        public Long getDenomination() {
            return denomination;
        }

        public void setDenomination(
                Long denomination
        ) {
            this.denomination =
                    denomination;
        }


        public Integer getQuantity() {
            return quantity;
        }

        public void setQuantity(
                Integer quantity
        ) {
            this.quantity =
                    quantity;
        }
    }
}