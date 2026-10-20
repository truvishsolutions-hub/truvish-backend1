package com.truvish.truvishbackend.cold.repository;

import com.truvish.truvishbackend.cold.entity.ColdCodeRedemptionHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * COLD DATABASE repository for archived code redemption history.
 *
 * This repository handles historical/archived redemption
 * records stored in the COLD PostgreSQL database.
 *
 * Current/live redemption history must continue to use
 * CodeRedemptionHistoryRepository from the HOT database.
 */
@Repository
public interface ColdCodeRedemptionHistoryRepository
        extends JpaRepository<ColdCodeRedemptionHistory, Long> {


    // =========================================================
    // ORIGINAL HOT RECORD
    // =========================================================

    /**
     * Find one archived redemption-history record
     * using the original HOT database record ID.
     */
    Optional<ColdCodeRedemptionHistory> findByHotRecordId(
            Long hotRecordId
    );


    /**
     * Find all archived redemption-history records
     * associated with the same original HOT record.
     *
     * Useful when multiple historical snapshots exist
     * for the same HOT record.
     */
    List<ColdCodeRedemptionHistory> findAllByHotRecordId(
            Long hotRecordId
    );


    /**
     * Check whether the original HOT record has
     * already been archived in the COLD database.
     */
    boolean existsByHotRecordId(
            Long hotRecordId
    );


    // =========================================================
    // CLIENT
    // =========================================================

    /**
     * Find archived redemption history by client ID.
     */
    List<ColdCodeRedemptionHistory> findAllByClientId(
            Long clientId
    );


    /**
     * Check whether archived redemption history
     * exists for a client.
     */
    boolean existsByClientId(
            Long clientId
    );


    // =========================================================
    // TRUVISH CODE
    // =========================================================

    /**
     * Find archived redemption history by TruVish code.
     */
    List<ColdCodeRedemptionHistory> findAllByTruvishCode(
            String truvishCode
    );


    /**
     * Check whether archived redemption history
     * exists for a TruVish code.
     */
    boolean existsByTruvishCode(
            String truvishCode
    );


    // =========================================================
    // PHONE
    // =========================================================

    /**
     * Find archived redemption history by phone number.
     */
    List<ColdCodeRedemptionHistory> findAllByPhoneNumber(
            String phoneNumber
    );


    // =========================================================
    // BRAND
    // =========================================================

    /**
     * Find archived redemption history by brand name.
     */
    List<ColdCodeRedemptionHistory> findAllByBrandName(
            String brandName
    );


    // =========================================================
    // REDEMPTION STATUS
    // =========================================================

    /**
     * Find archived redemption history by redemption status.
     */
    List<ColdCodeRedemptionHistory> findAllByRedeemStatus(
            String redeemStatus
    );


    // =========================================================
    // COLD RETENTION
    // =========================================================

    /**
     * Find COLD records whose archive time is older than
     * the given date/time.
     *
     * Example:
     * If COLD retention is 6 months, pass:
     *
     * LocalDateTime.now().minusMonths(6)
     *
     * These records can then be considered for permanent deletion,
     * according to the application's retention policy.
     */
    List<ColdCodeRedemptionHistory> findAllByArchivedAtBefore(
            LocalDateTime archivedAt
    );


    /**
     * Find COLD records whose archive time is equal to
     * or older than the given date/time.
     *
     * Useful for the permanent-delete process.
     */
    List<ColdCodeRedemptionHistory> findAllByArchivedAtLessThanEqual(
            LocalDateTime archivedAt
    );


    // =========================================================
    // ARCHIVE REASON
    // =========================================================

    /**
     * Find archived records by archive reason.
     *
     * Example values:
     * - HOT_RETENTION_EXPIRED
     * - MANUAL_ARCHIVE
     * - SYSTEM_ARCHIVE
     */
    List<ColdCodeRedemptionHistory> findAllByArchiveReason(
            String archiveReason
    );
}

