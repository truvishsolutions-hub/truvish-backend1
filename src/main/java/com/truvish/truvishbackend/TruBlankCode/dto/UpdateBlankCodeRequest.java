package com.truvish.truvishbackend.TruBlankCode.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.Min;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

public class UpdateBlankCodeRequest {

    // =========================================================
    // DENOMINATION
    // =========================================================

    @DecimalMin(
            value = "0.01",
            message = "Denomination must be greater than 0"
    )
    private Double denomination;


    // =========================================================
    // VALIDITY MONTHS
    // =========================================================

    @Min(
            value = 1,
            message = "Validity months must be greater than 0"
    )
    private Integer validityMonths;


    // =========================================================
    // BRAND NAMES
    // =========================================================

    private List<String> brandNames;


    // =========================================================
    // BRAND CATEGORY
    // =========================================================

    private String brandCategory;


    // =========================================================
    // THEME NAME
    // =========================================================

    private String themeName;


    // =========================================================
    // THEME IMAGE
    // =========================================================

    private String themeImg;


    // =========================================================
    // TRUPROMISE PERCENTAGE (0 to 100)
    // Backend calculates and stores the actual amount.
    // =========================================================

    @DecimalMin(value = "0.00", message = "TruPromise percentage cannot be negative")
    @DecimalMax(value = "100.00", message = "TruPromise percentage cannot exceed 100")
    private BigDecimal trupromisePercentage;


    // =========================================================
    // CONSTRUCTOR
    // =========================================================

    public UpdateBlankCodeRequest() {
    }


    // =========================================================
    // DENOMINATION
    // =========================================================

    public Double getDenomination() {
        return denomination;
    }

    public void setDenomination(Double denomination) {
        this.denomination = denomination;
    }


    // =========================================================
    // VALIDITY MONTHS
    // =========================================================

    public Integer getValidityMonths() {
        return validityMonths;
    }

    public void setValidityMonths(Integer validityMonths) {
        this.validityMonths = validityMonths;
    }


    // =========================================================
    // BRAND NAMES
    // =========================================================

    public List<String> getBrandNames() {
        return brandNames;
    }

    public void setBrandNames(List<String> brandNames) {
        this.brandNames = brandNames == null
                ? null
                : new ArrayList<>(brandNames);
    }


    // =========================================================
    // BRAND CATEGORY
    // =========================================================

    public String getBrandCategory() {
        return brandCategory;
    }

    public void setBrandCategory(String brandCategory) {
        this.brandCategory = brandCategory;
    }


    // =========================================================
    // THEME NAME
    // =========================================================

    public String getThemeName() {
        return themeName;
    }

    public void setThemeName(String themeName) {
        this.themeName = themeName;
    }


    // =========================================================
    // THEME IMAGE
    // =========================================================

    public String getThemeImg() {
        return themeImg;
    }

    public void setThemeImg(String themeImg) {
        this.themeImg = themeImg;
    }


    public BigDecimal getTrupromisePercentage() {
        return trupromisePercentage;
    }

    public void setTrupromisePercentage(BigDecimal trupromisePercentage) {
        this.trupromisePercentage = trupromisePercentage;
    }
}
