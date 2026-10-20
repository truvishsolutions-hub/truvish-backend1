package com.truvish.truvishbackend.wallet;

import com.truvish.truvishbackend.client.Client;
import jakarta.persistence.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(
        name = "wallet_transactions",
        indexes = {
                @Index(
                        name = "idx_wallet_txn_client_date",
                        columnList = "client_id, txn_date_time"
                ),
                @Index(
                        name = "idx_wallet_txn_created_at",
                        columnList = "created_at"
                ),
                @Index(
                        name = "idx_wallet_txn_status",
                        columnList = "status"
                )
        }
)
public class WalletTransaction {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "txn_id")
    private Long txnId;

    /**
     * HOT DATABASE
     * Current wallet transaction belongs to a client.
     *
     * LAZY is important because ColdArchiveScheduler should not
     * depend on automatic Client loading.
     */
    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(
            name = "client_id",
            nullable = false,
            foreignKey = @ForeignKey(name = "fk_wallet_transaction_client")
    )
    private Client client;

    /**
     * Transaction business date/time.
     */
    @Column(name = "txn_date_time", nullable = false)
    private LocalDateTime txnDateTime;

    /**
     * Transaction amount.
     */
    @Column(
            name = "amount",
            nullable = false,
            precision = 12,
            scale = 2
    )
    private BigDecimal amount;

    /**
     * CREDIT / DEBIT
     */
    @Enumerated(EnumType.STRING)
    @Column(
            name = "type",
            nullable = false,
            length = 20
    )
    private TxnType type;

    /**
     * Human-readable transaction description.
     */
    @Column(
            name = "description",
            length = 255
    )
    private String description;

    /**
     * Example:
     * VOUCHER
     * TRUCARD
     */
    @Column(
            name = "reference_type",
            length = 50
    )
    private String referenceType;

    /**
     * Related business record/reference.
     */
    @Column(
            name = "reference_id",
            length = 100
    )
    private String referenceId;

    /**
     * SUCCESS / FAILED / etc.
     */
    @Enumerated(EnumType.STRING)
    @Column(
            name = "status",
            nullable = false,
            length = 20
    )
    private TxnStatus status;

    /**
     * Technical creation timestamp.
     * Used by Hot/Cold retention logic.
     */
    @Column(
            name = "created_at",
            nullable = false
    )
    private LocalDateTime createdAt;


    // =========================================================
    // JPA LIFECYCLE
    // =========================================================

    @PrePersist
    protected void prePersist() {

        if (txnDateTime == null) {
            txnDateTime = LocalDateTime.now();
        }

        if (status == null) {
            status = TxnStatus.SUCCESS;
        }

        if (createdAt == null) {
            createdAt = LocalDateTime.now();
        }
    }


    // =========================================================
    // GETTERS / SETTERS
    // =========================================================

    public Long getTxnId() {
        return txnId;
    }

    public void setTxnId(Long txnId) {
        this.txnId = txnId;
    }


    public Client getClient() {
        return client;
    }

    public void setClient(Client client) {
        this.client = client;
    }


    public LocalDateTime getTxnDateTime() {
        return txnDateTime;
    }

    public void setTxnDateTime(LocalDateTime txnDateTime) {
        this.txnDateTime = txnDateTime;
    }


    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }


    public TxnType getType() {
        return type;
    }

    public void setType(TxnType type) {
        this.type = type;
    }


    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }


    public String getReferenceType() {
        return referenceType;
    }

    public void setReferenceType(String referenceType) {
        this.referenceType = referenceType;
    }


    public String getReferenceId() {
        return referenceId;
    }

    public void setReferenceId(String referenceId) {
        this.referenceId = referenceId;
    }


    public TxnStatus getStatus() {
        return status;
    }

    public void setStatus(TxnStatus status) {
        this.status = status;
    }


    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}