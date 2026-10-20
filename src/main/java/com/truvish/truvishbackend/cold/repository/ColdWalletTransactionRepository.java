package com.truvish.truvishbackend.cold.repository;

import com.truvish.truvishbackend.cold.entity.ColdWalletTransaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * COLD DATABASE repository for archived wallet transactions.
 *
 * This repository handles historical/archived wallet transaction
 * records stored in the COLD PostgreSQL database.
 *
 * Current/live wallet transactions must continue to use
 * WalletTransactionRepository from the HOT database.
 */
@Repository
public interface ColdWalletTransactionRepository
        extends JpaRepository<ColdWalletTransaction, Long> {


    // =========================================================
    // ORIGINAL HOT RECORD
    // =========================================================

    /**
     * Find one archived wallet transaction using
     * the original HOT database record ID.
     */
    Optional<ColdWalletTransaction> findByHotRecordId(
            Long hotRecordId
    );


    /**
     * Find all archived wallet transactions associated
     * with the same original HOT record.
     *
     * Useful when multiple historical snapshots exist.
     */
    List<ColdWalletTransaction> findAllByHotRecordId(
            Long hotRecordId
    );


    /**
     * Check whether the original HOT wallet transaction
     * has already been archived in the COLD database.
     */
    boolean existsByHotRecordId(
            Long hotRecordId
    );


    // =========================================================
    // CLIENT
    // =========================================================

    /**
     * Find archived wallet transactions by client ID.
     */
    List<ColdWalletTransaction> findAllByClientId(
            Long clientId
    );


    /**
     * Check whether archived wallet transactions
     * exist for a client.
     */
    boolean existsByClientId(
            Long clientId
    );


    // =========================================================
    // WALLET
    // =========================================================

    /**
     * Find archived wallet transactions by wallet ID.
     */
    List<ColdWalletTransaction> findAllByWalletId(
            Long walletId
    );


    // =========================================================
    // TRANSACTION TYPE
    // =========================================================

    /**
     * Find archived wallet transactions by transaction type.
     *
     * Example:
     * CREDIT
     * DEBIT
     */
    List<ColdWalletTransaction> findAllByTransactionType(
            String transactionType
    );


    // =========================================================
    // REFERENCE
    // =========================================================

    /**
     * Find archived wallet transactions by reference.
     */
    List<ColdWalletTransaction> findAllByReference(
            String reference
    );


    // =========================================================
    // REFERENCE TYPE
    // =========================================================

    /**
     * Find archived wallet transactions by reference type.
     */
    List<ColdWalletTransaction> findAllByReferenceType(
            String referenceType
    );


    // =========================================================
    // STATUS
    // =========================================================

    /**
     * Find archived wallet transactions by status.
     */
    List<ColdWalletTransaction> findAllByStatus(
            String status
    );


    // =========================================================
    // COLD RETENTION
    // =========================================================

    /**
     * Find COLD wallet transactions whose archive time
     * is older than the supplied date/time.
     *
     * Example:
     *
     * LocalDateTime.now().minusMonths(6)
     *
     * These records can then be considered for
     * permanent deletion according to the retention policy.
     */
    List<ColdWalletTransaction> findAllByArchivedAtBefore(
            LocalDateTime archivedAt
    );


    /**
     * Find COLD wallet transactions whose archive time
     * is equal to or older than the supplied date/time.
     *
     * Useful for the permanent-delete process.
     */
    List<ColdWalletTransaction> findAllByArchivedAtLessThanEqual(
            LocalDateTime archivedAt
    );


    // =========================================================
    // ARCHIVE REASON
    // =========================================================

    /**
     * Find archived wallet transactions by archive reason.
     *
     * Example values:
     *
     * - HOT_RETENTION_EXPIRED
     * - MANUAL_ARCHIVE
     * - SYSTEM_ARCHIVE
     */
    List<ColdWalletTransaction> findAllByArchiveReason(
            String archiveReason
    );
}