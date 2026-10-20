package com.truvish.truvishbackend.cold.entity;

import jakarta.persistence.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(
        name = "cold_voucher_redeemed_log",
        indexes = {
                @Index(
                        name = "idx_cold_voucher_redeemed_hot_record_id",
                        columnList = "hot_record_id"
                ),
                @Index(
                        name = "idx_cold_voucher_redeemed_inventory_id",
                        columnList = "inventory_id"
                ),
                @Index(
                        name = "idx_cold_voucher_redeemed_code",
                        columnList = "voucher_code"
                ),
                @Index(
                        name = "idx_cold_voucher_redeemed_at",
                        columnList = "redeemed_at"
                ),
                @Index(
                        name = "idx_cold_voucher_redeemed_brand",
                        columnList = "brand_name"
                ),
                @Index(
                        name = "idx_cold_voucher_redeemed_archived_at",
                        columnList = "archived_at"
                )
        }
)
public class ColdVoucherRedeemedLog {

    // =========================================================
    // COLD DATABASE PRIMARY KEY
    // =========================================================

    /**
     * Primary key of the COLD record.
     *
     * This ID belongs to the COLD table.
     * It does not have to be the same as the HOT record ID.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;


    // =========================================================
    // HOT DATABASE REFERENCE
    // =========================================================

    /**
     * Original primary key / record ID
     * from the HOT database.
     *
     * Logical reference only.
     * No JPA relationship is used.
     */
    @Column(
            name = "hot_record_id",
            nullable = false
    )
    private Long hotRecordId;


    // =========================================================
    // INVENTORY INFORMATION
    // =========================================================

    /**
     * Original inventory ID.
     */
    @Column(
            name = "inventory_id",
            nullable = false
    )
    private Long inventoryId;


    // =========================================================
    // VOUCHER / BRAND INFORMATION
    // =========================================================

    /**
     * Voucher brand.
     */
    @Column(
            name = "brand_name",
            nullable = false,
            length = 150
    )
    private String brandName;

    /**
     * Voucher denomination/value.
     */
    @Column(
            name = "denomination",
            nullable = false,
            precision = 12,
            scale = 2
    )
    private BigDecimal denomination;

    /**
     * Voucher code.
     */
    @Column(
            name = "voucher_code",
            nullable = false,
            length = 255
    )
    private String voucherCode;

    /**
     * Voucher PIN.
     */
    @Column(
            name = "voucher_pin",
            nullable = false,
            length = 255
    )
    private String voucherPin;

    /**
     * Voucher validity date.
     */
    @Column(
            name = "validity_till",
            nullable = false
    )
    private LocalDate validityTill;


    // =========================================================
    // REDEMPTION INFORMATION
    // =========================================================

    /**
     * Date/time when voucher was redeemed.
     */
    @Column(
            name = "redeemed_at",
            nullable = false
    )
    private LocalDateTime redeemedAt;

    /**
     * Person/system that redeemed the voucher.
     */
    @Column(
            name = "redeemed_by",
            length = 200
    )
    private String redeemedBy;

    /**
     * Order reference associated with redemption.
     */
    @Column(
            name = "order_reference",
            length = 100
    )
    private String orderReference;


    // =========================================================
    // COLD DATABASE RETENTION / ARCHIVE INFORMATION
    // =========================================================

    /**
     * Date/time when this record was moved
     * from HOT database to COLD database.
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
     * Reason why the record was moved
     * from HOT database to COLD database.
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
    public ColdVoucherRedeemedLog() {
    }


    // =========================================================
    // PRE-PERSIST
    // =========================================================

    /**
     * Automatically sets archivedAt when a new
     * COLD record is created.
     *
     * redeemedAt is NOT automatically replaced because
     * it represents the actual voucher redemption time.
     */
    @PrePersist
    public void prePersist() {

        if (archivedAt == null) {
            archivedAt = LocalDateTime.now();
        }
    }


    // =========================================================
    // GETTERS / SETTERS
    // =========================================================

    // ---------------------------------------------------------
    // COLD PRIMARY KEY
    // ---------------------------------------------------------

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
    // INVENTORY ID
    // ---------------------------------------------------------

    public Long getInventoryId() {
        return inventoryId;
    }

    public void setInventoryId(Long inventoryId) {
        this.inventoryId = inventoryId;
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


    // ---------------------------------------------------------
    // DENOMINATION
    // ---------------------------------------------------------

    public BigDecimal getDenomination() {
        return denomination;
    }

    public void setDenomination(BigDecimal denomination) {
        this.denomination = denomination;
    }


    // ---------------------------------------------------------
    // VOUCHER CODE
    // ---------------------------------------------------------

    public String getVoucherCode() {
        return voucherCode;
    }

    public void setVoucherCode(String voucherCode) {
        this.voucherCode = voucherCode;
    }


    // ---------------------------------------------------------
    // VOUCHER PIN
    // ---------------------------------------------------------

    public String getVoucherPin() {
        return voucherPin;
    }

    public void setVoucherPin(String voucherPin) {
        this.voucherPin = voucherPin;
    }


    // ---------------------------------------------------------
    // VALIDITY
    // ---------------------------------------------------------

    public LocalDate getValidityTill() {
        return validityTill;
    }

    public void setValidityTill(LocalDate validityTill) {
        this.validityTill = validityTill;
    }


    // ---------------------------------------------------------
    // REDEEMED AT
    // ---------------------------------------------------------

    public LocalDateTime getRedeemedAt() {
        return redeemedAt;
    }

    public void setRedeemedAt(LocalDateTime redeemedAt) {
        this.redeemedAt = redeemedAt;
    }


    // ---------------------------------------------------------
    // REDEEMED BY
    // ---------------------------------------------------------

    public String getRedeemedBy() {
        return redeemedBy;
    }

    public void setRedeemedBy(String redeemedBy) {
        this.redeemedBy = redeemedBy;
    }


    // ---------------------------------------------------------
    // ORDER REFERENCE
    // ---------------------------------------------------------

    public String getOrderReference() {
        return orderReference;
    }

    public void setOrderReference(String orderReference) {
        this.orderReference = orderReference;
    }


    // ---------------------------------------------------------
    // ARCHIVED AT
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
