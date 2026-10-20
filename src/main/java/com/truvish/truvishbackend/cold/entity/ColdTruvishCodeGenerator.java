package com.truvish.truvishbackend.cold.entity;

import jakarta.persistence.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * Cold-storage entity for historical Truvish Code Generator records.
 *
 * This entity stores archived/historical generator data.
 *
 * IMPORTANT:
 * - No relationship with HOT entities.
 * - No @ManyToOne / @OneToMany / @OneToOne.
 * - Client, brand and category information is stored as snapshots.
 * - The original HOT record ID is preserved in hot_record_id.
 */
@Entity
@Table(
        name = "cold_truvish_code_generator",
        indexes = {
                @Index(
                        name = "idx_cold_generator_hot_record_id",
                        columnList = "hot_record_id"
                ),
                @Index(
                        name = "idx_cold_generator_code_number",
                        columnList = "code_number"
                ),
                @Index(
                        name = "idx_cold_generator_client_id",
                        columnList = "client_id"
                ),
                @Index(
                        name = "idx_cold_generator_status",
                        columnList = "status"
                ),
                @Index(
                        name = "idx_cold_generator_created_at",
                        columnList = "created_at"
                ),
                @Index(
                        name = "idx_cold_generator_archived_at",
                        columnList = "archived_at"
                )
        }
)
public class ColdTruvishCodeGenerator {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * Original ID from HOT truvish_code_generator.
     */
    @Column(name = "hot_record_id")
    private Long hotRecordId;

    /**
     * Generated code number.
     */
    @Column(
            name = "code_number",
            length = 255
    )
    private String codeNumber;

    /**
     * Historical status.
     */
    @Column(
            name = "status",
            length = 50
    )
    private String status;

    /**
     * Client ID snapshot.
     */
    @Column(name = "client_id")
    private Long clientId;

    /**
     * Client name snapshot.
     */
    @Column(
            name = "client_name",
            length = 255
    )
    private String clientName;

    /**
     * Client category snapshot.
     *
     * Multiple categories can be stored as comma-separated TEXT
     * if required by the existing HOT implementation.
     */
    @Column(
            name = "client_category",
            columnDefinition = "TEXT"
    )
    private String clientCategory;

    /**
     * Client brand snapshot.
     *
     * Multiple brands can be stored as comma-separated TEXT
     * if required.
     */
    @Column(
            name = "client_brand",
            columnDefinition = "TEXT"
    )
    private String clientBrand;

    /**
     * Client theme snapshot.
     *
     * Stored as historical data so that the COLD record
     * represents the state of the record at archive time.
     */
    @Column(
            name = "client_theme",
            columnDefinition = "TEXT"
    )
    private String clientTheme;

    /**
     * Brand name snapshot.
     */
    @Column(
            name = "brand_name",
            columnDefinition = "TEXT"
    )
    private String brandName;

    /**
     * Denomination/value of generated code.
     */
    @Column(
            name = "denomination",
            precision = 19,
            scale = 2
    )
    private BigDecimal denomination;

    /**
     * Validity period in months.
     */
    @Column(name = "validity_months")
    private Integer validityMonths;

    /**
     * Expiry date.
     */
    @Column(name = "expiry_date")
    private LocalDate expiryDate;

    /**
     * Generation/batch number.
     */
    @Column(
            name = "generation_number",
            length = 255
    )
    private String generationNumber;

    /**
     * Number of generated codes.
     */
    @Column(name = "quantity")
    private Integer quantity;

    /**
     * Code type.
     *
     * Examples:
     * DIGITAL
     * PHYSICAL
     */
    @Column(
            name = "code_type",
            length = 30
    )
    private String codeType;

    /**
     * Original creation timestamp.
     */
    @Column(name = "created_at")
    private LocalDateTime createdAt;

    /**
     * Last update timestamp while record was HOT.
     */
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    /**
     * Time when record was archived to COLD.
     */
    @Column(
            name = "archived_at",
            nullable = false
    )
    private LocalDateTime archivedAt;

    /**
     * Reason for moving record to COLD.
     *
     * Examples:
     * REDEEMED
     * EXPIRED
     * CANCELLED
     * INACTIVE
     * MANUAL_ARCHIVE
     */
    @Column(
            name = "archive_reason",
            length = 50
    )
    private String archiveReason;

    /**
     * Optional historical notes.
     */
    @Column(
            name = "notes",
            columnDefinition = "TEXT"
    )
    private String notes;

    public ColdTruvishCodeGenerator() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getHotRecordId() {
        return hotRecordId;
    }

    public void setHotRecordId(Long hotRecordId) {
        this.hotRecordId = hotRecordId;
    }

    public String getCodeNumber() {
        return codeNumber;
    }

    public void setCodeNumber(String codeNumber) {
        this.codeNumber = codeNumber;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

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

    public String getClientCategory() {
        return clientCategory;
    }

    public void setClientCategory(String clientCategory) {
        this.clientCategory = clientCategory;
    }

    public String getClientBrand() {
        return clientBrand;
    }

    public void setClientBrand(String clientBrand) {
        this.clientBrand = clientBrand;
    }

    public String getClientTheme() {
        return clientTheme;
    }

    /**
     * Setter required by archive/service code.
     */
    public void setClientTheme(String clientTheme) {
        this.clientTheme = clientTheme;
    }

    public String getBrandName() {
        return brandName;
    }

    public void setBrandName(String brandName) {
        this.brandName = brandName;
    }

    public BigDecimal getDenomination() {
        return denomination;
    }

    public void setDenomination(BigDecimal denomination) {
        this.denomination = denomination;
    }

    public Integer getValidityMonths() {
        return validityMonths;
    }

    public void setValidityMonths(Integer validityMonths) {
        this.validityMonths = validityMonths;
    }

    public LocalDate getExpiryDate() {
        return expiryDate;
    }

    public void setExpiryDate(LocalDate expiryDate) {
        this.expiryDate = expiryDate;
    }

    public String getGenerationNumber() {
        return generationNumber;
    }

    public void setGenerationNumber(String generationNumber) {
        this.generationNumber = generationNumber;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }

    public String getCodeType() {
        return codeType;
    }

    public void setCodeType(String codeType) {
        this.codeType = codeType;
    }

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

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }
}