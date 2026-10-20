package com.truvish.truvishbackend.TruBlankCode.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

public class GenerateBlankCodeRequest {

    // =========================================================
    // QUANTITY
    // =========================================================
    //
    // Number of TruBlankCodes to generate.
    //
    // Example:
    //
    // 10
    // 100
    // 500
    //
    // Minimum = 1
    //
    // =========================================================

    @NotNull(message = "quantity is required")
    @Min(
            value = 1,
            message = "quantity must be at least 1"
    )
    private Integer quantity;

    // =========================================================
    // DENOMINATION
    // =========================================================
    //
    // OPTIONAL
    //
    // Blank code generation ke waqt denomination
    // normally set nahi hogi.
    //
    // Denomination activation se pehle configure hogi.
    //
    // =========================================================

    private Long denomination;

    // =========================================================
    // VALIDITY MONTHS
    // =========================================================
    //
    // OPTIONAL
    //
    // Blank code generation ke waqt validity
    // normally set nahi hogi.
    //
    // Activation se pehle Update Blank Code ke through
    // validity configure ki jayegi.
    //
    // =========================================================

    private Integer validityMonths;

    // =========================================================
    // CONSTRUCTOR
    // =========================================================

    public GenerateBlankCodeRequest() {
    }

    // =========================================================
    // GET / SET - QUANTITY
    // =========================================================

    public Integer getQuantity() {
        return quantity;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }

    // =========================================================
    // GET / SET - DENOMINATION
    // =========================================================

    public Long getDenomination() {
        return denomination;
    }

    public void setDenomination(Long denomination) {
        this.denomination = denomination;
    }

    // =========================================================
    // GET / SET - VALIDITY MONTHS
    // =========================================================

    public Integer getValidityMonths() {
        return validityMonths;
    }

    public void setValidityMonths(Integer validityMonths) {
        this.validityMonths = validityMonths;
    }
}