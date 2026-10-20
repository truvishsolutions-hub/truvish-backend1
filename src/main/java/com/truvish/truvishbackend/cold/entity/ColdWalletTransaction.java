package com.truvish.truvishbackend.cold.entity;

import jakarta.persistence.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Cold storage entity for historical wallet transactions.
 *
 * IMPORTANT:
 * - This entity is independent from HOT entities.
 * - No @ManyToOne / @OneToMany relationships.
 * - Client/wallet references are stored as IDs and snapshots.
 * - Used for long-term historical/archive records.
 */
@Entity
@Table(
        name = "cold_wallet_transaction",
        indexes = {
                @Index(
                        name = "idx_cold_wallet_tx_hot_record_id",
                        columnList = "hot_record_id"
                ),
                @Index(
                        name = "idx_cold_wallet_tx_client_id",
                        columnList = "client_id"
                ),
                @Index(
                        name = "idx_cold_wallet_tx_reference",
                        columnList = "reference"
                ),
                @Index(
                        name = "idx_cold_wallet_tx_type",
                        columnList = "transaction_type"
                ),
                @Index(
                        name = "idx_cold_wallet_tx_created_at",
                        columnList = "created_at"
                ),
                @Index(
                        name = "idx_cold_wallet_tx_archived_at",
                        columnList = "archived_at"
                )
        }
)
public class ColdWalletTransaction {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * Original transaction ID from HOT wallet transaction table.
     */
    @Column(name = "hot_record_id")
    private Long hotRecordId;

    /**
     * Client ID from HOT database.
     *
     * No JPA relationship is used intentionally.
     */
    @Column(name = "client_id")
    private Long clientId;

    /**
     * Client name snapshot.
     */
    @Column(name = "client_name", length = 255)
    private String clientName;

    /**
     * Wallet ID/reference if available.
     */
    @Column(name = "wallet_id")
    private Long walletId;

    /**
     * Transaction type.
     *
     * Examples:
     * CREDIT
     * DEBIT
     * REFUND
     * REVERSAL
     */
    @Column(
            name = "transaction_type",
            length = 50
    )
    private String transactionType;

    /**
     * Transaction amount.
     */
    @Column(
            name = "amount",
            precision = 19,
            scale = 2
    )
    private BigDecimal amount;

    /**
     * Wallet balance before transaction.
     */
    @Column(
            name = "balance_before",
            precision = 19,
            scale = 2
    )
    private BigDecimal balanceBefore;

    /**
     * Wallet balance after transaction.
     */
    @Column(
            name = "balance_after",
            precision = 19,
            scale = 2
    )
    private BigDecimal balanceAfter;

    /**
     * Transaction description.
     */
    @Column(
            name = "description",
            columnDefinition = "TEXT"
    )
    private String description;

    /**
     * Reference value associated with transaction.
     *
     * Example:
     * voucher/code/order reference.
     */
    @Column(
            name = "reference",
            length = 255
    )
    private String reference;

    /**
     * Reference type.
     *
     * Examples:
     * VOUCHER
     * TRUCARD
     * ORDER
     * REFUND
     */
    @Column(
            name = "reference_type",
            length = 50
    )
    private String referenceType;

    /**
     * Currency used for transaction.
     */
    @Column(
            name = "currency",
            length = 10
    )
    private String currency;

    /**
     * Transaction status.
     */
    @Column(
            name = "status",
            length = 50
    )
    private String status;

    /**
     * Original transaction creation timestamp.
     */
    @Column(name = "created_at")
    private LocalDateTime createdAt;

    /**
     * Last update timestamp while transaction
     * was stored in HOT database.
     */
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    /**
     * Time when the transaction was moved
     * from HOT to COLD storage.
     */
    @Column(
            name = "archived_at",
            nullable = false
    )
    private LocalDateTime archivedAt;

    /**
     * Reason for archival.
     *
     * Examples:
     * OLD_TRANSACTION
     * COMPLETED
     * EXPIRED
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

    public ColdWalletTransaction() {
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

    public Long getWalletId() {
        return walletId;
    }

    public void setWalletId(Long walletId) {
        this.walletId = walletId;
    }

    public String getTransactionType() {
        return transactionType;
    }

    public void setTransactionType(String transactionType) {
        this.transactionType = transactionType;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public BigDecimal getBalanceBefore() {
        return balanceBefore;
    }

    public void setBalanceBefore(BigDecimal balanceBefore) {
        this.balanceBefore = balanceBefore;
    }

    public BigDecimal getBalanceAfter() {
        return balanceAfter;
    }

    public void setBalanceAfter(BigDecimal balanceAfter) {
        this.balanceAfter = balanceAfter;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getReference() {
        return reference;
    }

    public void setReference(String reference) {
        this.reference = reference;
    }

    public String getReferenceType() {
        return referenceType;
    }

    public void setReferenceType(String referenceType) {
        this.referenceType = referenceType;
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
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
