
package com.truvish.truvishbackend.inventory;

import jakarta.persistence.*;
import java.time.LocalDate;

@Entity
@Table(name = "brand_inventory")
public class BrandInventory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "inventoryid")
    private Long inventoryId;

    @Column(name = "inventoryvouchername")
    private String inventoryVoucherName;

    @Column(name = "inventoryvoucherexpire")
    private LocalDate inventoryVoucherExpire;

    @Column(name = "inventoryvoucherlogourl")
    private String inventoryVoucherLogoUrl;

    @Column(name = "inventoryvoucherbrandurl")
    private String inventoryVoucherBrandUrl;

    @Column(name = "inventoryvouchercatogry")
    private String inventoryVoucherCatogry;

    // New fields
    @Column(name = "howtoredeem", columnDefinition = "TEXT")
    private String howToRedeem;

    @Column(name = "term_condition", columnDefinition = "TEXT")
    private String termCondition;

    // ================= GETTERS =================

    public Long getInventoryId() {
        return inventoryId;
    }

    public String getInventoryVoucherName() {
        return inventoryVoucherName;
    }

    public LocalDate getInventoryVoucherExpire() {
        return inventoryVoucherExpire;
    }

    public String getInventoryVoucherLogoUrl() {
        return inventoryVoucherLogoUrl;
    }

    public String getInventoryVoucherBrandUrl() {
        return inventoryVoucherBrandUrl;
    }

    public String getInventoryVoucherCatogry() {
        return inventoryVoucherCatogry;
    }

    public String getHowToRedeem() {
        return howToRedeem;
    }

    public String getTermCondition() {
        return termCondition;
    }

    // ================= SETTERS =================

    public void setInventoryId(Long inventoryId) {
        this.inventoryId = inventoryId;
    }

    public void setInventoryVoucherName(String inventoryVoucherName) {
        this.inventoryVoucherName = inventoryVoucherName;
    }

    public void setInventoryVoucherExpire(LocalDate inventoryVoucherExpire) {
        this.inventoryVoucherExpire = inventoryVoucherExpire;
    }

    public void setInventoryVoucherLogoUrl(String inventoryVoucherLogoUrl) {
        this.inventoryVoucherLogoUrl = inventoryVoucherLogoUrl;
    }

    public void setInventoryVoucherBrandUrl(String inventoryVoucherBrandUrl) {
        this.inventoryVoucherBrandUrl = inventoryVoucherBrandUrl;
    }

    public void setInventoryVoucherCatogry(String inventoryVoucherCatogry) {
        this.inventoryVoucherCatogry = inventoryVoucherCatogry;
    }

    public void setHowToRedeem(String howToRedeem) {
        this.howToRedeem = howToRedeem;
    }

    public void setTermCondition(String termCondition) {
        this.termCondition = termCondition;
    }
}
