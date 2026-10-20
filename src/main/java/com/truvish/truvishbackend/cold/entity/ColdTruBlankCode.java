package com.truvish.truvishbackend.cold.entity;

import jakarta.persistence.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Cold storage entity for historical TruBlankCode records.
 *
 * IMPORTANT:
 * - This entity is independent from HOT entities.
 * - Do NOT add @ManyToOne / @OneToMany relationships to HOT entities.
 * - Historical references are stored as IDs / snapshot values.
 * - Records can be moved here after they are no longer operationally active.
 */
@Entity
@Table(
        name = "cold_truvish_blank_code",
        indexes = {
                @Index(
                        name = "idx_cold_blank_code_number",
                        columnList = "code_number"
                ),
                @Index(
                        name = "idx_cold_blank_code_status",
                        columnList = "status"
                ),
                @Index(
                        name = "idx_cold_blank_code_created_at",
                        columnList = "created_at"
                ),
                @Index(
                        name = "idx_cold_blank_code_activated_at",
                        columnList = "activated_at"
                ),
                @Index(
                        name = "idx_cold_blank_code_redeemed_at",
                        columnList = "redeemed_at"
                ),
                @Index(
                        name = "idx_cold_blank_code_archived_at",
                        columnList = "archived_at"
                ),
                @Index(
                        name = "idx_cold_blank_code_client_id",
                        columnList = "client_id"
                )
        }
)
public class ColdTruBlankCode {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * Original HOT TruBlankCode ID.
     * Kept only as a historical reference.
     */
    @Column(name = "hot_record_id")
    private Long hotRecordId;

    /**
     * Original blank/code number.
     */
    @Column(
            name = "code_number",
            nullable = false,
            unique = true,
            length = 255
    )
    private String codeNumber;

    /**
     * Historical status of the code.
     *
     * Stored as String so that future enum changes
     * do not break historical records.
     */
    @Column(
            name = "status",
            nullable = false,
            length = 50
    )
    private String status;

    /**
     * Client reference from HOT database.
     * No JPA relationship is intentionally used.
     */
    @Column(name = "client_id")
    private Long clientId;

    /**
     * Client name snapshot at the time of archival.
     */
    @Column(name = "client_name", length = 255)
    private String clientName;

    /**
     * Brand name snapshot.
     *
     * Existing TruVish implementation can contain
     * comma-separated brand values.
     */
    @Column(
            name = "brand_name",
            columnDefinition = "TEXT"
    )
    private String brandName;

    /**
     * Client-brand snapshot.
     *
     * Existing implementation can contain
     * comma-separated values.
     */
    @Column(
            name = "client_brand",
            columnDefinition = "TEXT"
    )
    private String clientBrand;

    /**
     * Monetary value associated with the code.
     */
    @Column(
            name = "amount",
            precision = 19,
            scale = 2
    )
    private BigDecimal amount;

    /**
     * Currency snapshot.
     */
    @Column(name = "currency", length = 10)
    private String currency;

    /**
     * Original creation timestamp.
     */
    @Column(name = "created_at")
    private LocalDateTime createdAt;

    /**
     * Last update timestamp while the record
     * was still in HOT storage.
     */
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    /**
     * Time when the code was assigned.
     */
    @Column(name = "assigned_at")
    private LocalDateTime assignedAt;

    /**
     * Time when the code was activated.
     */
    @Column(name = "activated_at")
    private LocalDateTime activatedAt;

    /**
     * Time when the code was redeemed.
     */
    @Column(name = "redeemed_at")
    private LocalDateTime redeemedAt;

    /**
     * Time when the code was archived
     * from HOT to COLD storage.
     */
    @Column(
            name = "archived_at",
            nullable = false
    )
    private LocalDateTime archivedAt;

    /**
     * User/member reference responsible for
     * activation or redemption, if available.
     *
     * Stored as an ID only to keep COLD storage
     * independent from HOT entities.
     */
    @Column(name = "user_id")
    private Long userId;

    /**
     * User name/email snapshot if available.
     */
    @Column(name = "user_snapshot", columnDefinition = "TEXT")
    private String userSnapshot;

    /**
     * Reason for archival.
     *
     * Examples:
     * - REDEEMED
     * - EXPIRED
     * - CANCELLED
     * - INACTIVE
     * - MANUAL_ARCHIVE
     */
    @Column(name = "archive_reason", length = 50)
    private String archiveReason;

    /**
     * Optional historical notes.
     */
    @Column(name = "notes", columnDefinition = "TEXT")
    private String notes;

    public ColdTruBlankCode() {
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

    public String getBrandName() {
        return brandName;
    }

    public void setBrandName(String brandName) {
        this.brandName = brandName;
    }

    public String getClientBrand() {
        return clientBrand;
    }

    public void setClientBrand(String clientBrand) {
        this.clientBrand = clientBrand;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
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

    public LocalDateTime getAssignedAt() {
        return assignedAt;
    }

    public void setAssignedAt(LocalDateTime assignedAt) {
        this.assignedAt = assignedAt;
    }

    public LocalDateTime getActivatedAt() {
        return activatedAt;
    }

    public void setActivatedAt(LocalDateTime activatedAt) {
        this.activatedAt = activatedAt;
    }

    public LocalDateTime getRedeemedAt() {
        return redeemedAt;
    }

    public void setRedeemedAt(LocalDateTime redeemedAt) {
        this.redeemedAt = redeemedAt;
    }

    public LocalDateTime getArchivedAt() {
        return archivedAt;
    }

    public void setArchivedAt(LocalDateTime archivedAt) {
        this.archivedAt = archivedAt;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getUserSnapshot() {
        return userSnapshot;
    }

    public void setUserSnapshot(String userSnapshot) {
        this.userSnapshot = userSnapshot;
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
