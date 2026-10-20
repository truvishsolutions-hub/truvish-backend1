package com.truvish.truvishbackend.cold.repository;

import com.truvish.truvishbackend.cold.entity.ColdUserRedemption;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * COLD DATABASE repository for archived user redemption records.
 *
 * This repository handles historical/archived user redemption
 * records stored in the COLD PostgreSQL database.
 *
 * Current/live user redemption data must continue to use
 * the appropriate repository from the HOT database.
 */
@Repository
public interface ColdUserRedemptionRepository
        extends JpaRepository<ColdUserRedemption, Long> {


    // =========================================================
    // ORIGINAL HOT RECORD
    // =========================================================

    /**
     * Find one archived user-redemption record using
     * the original HOT database record ID.
     */
    Optional<ColdUserRedemption> findByHotRecordId(
            Long hotRecordId
    );


    /**
     * Find all archived user-redemption records associated
     * with the same original HOT record.
     *
     * Useful when multiple historical snapshots exist.
     */
    List<ColdUserRedemption> findAllByHotRecordId(
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
     * Find archived user-redemption records by client ID.
     */
    List<ColdUserRedemption> findAllByClientId(
            Long clientId
    );


    /**
     * Check whether archived user-redemption records
     * exist for a client.
     */
    boolean existsByClientId(
            Long clientId
    );


    // =========================================================
    // USER PHONE NUMBER
    // =========================================================

    /**
     * Find archived user-redemption records by
     * user phone number.
     */
    List<ColdUserRedemption> findAllByUserPhoneNumber(
            String userPhoneNumber
    );


    // =========================================================
    // TRUVISH CODE
    // =========================================================

    /**
     * Find archived user-redemption records by
     * user's TruVish code.
     */
    List<ColdUserRedemption> findAllByUserTruvishCode(
            String userTruvishCode
    );


    /**
     * Check whether archived user-redemption records
     * exist for a user's TruVish code.
     */
    boolean existsByUserTruvishCode(
            String userTruvishCode
    );


    // =========================================================
    // USER BRAND
    // =========================================================

    /**
     * Find archived user-redemption records by
     * user brand name.
     */
    List<ColdUserRedemption> findAllByUserBrandName(
            String userBrandName
    );


    // =========================================================
    // REDEMPTION STATUS
    // =========================================================

    /**
     * Find archived user-redemption records by
     * redemption status.
     */
    List<ColdUserRedemption> findAllByRedeemStatus(
            String redeemStatus
    );


    // =========================================================
    // COLD RETENTION
    // =========================================================

    /**
     * Find COLD records whose archive time is older than
     * the supplied date/time.
     *
     * Example:
     *
     * LocalDateTime.now().minusMonths(6)
     *
     * These records can then be considered for
     * permanent deletion according to the retention policy.
     */
    List<ColdUserRedemption> findAllByArchivedAtBefore(
            LocalDateTime archivedAt
    );


    /**
     * Find COLD records whose archive time is equal to
     * or older than the supplied date/time.
     *
     * Useful for the permanent-delete process.
     */
    List<ColdUserRedemption> findAllByArchivedAtLessThanEqual(
            LocalDateTime archivedAt
    );


    // =========================================================
    // ARCHIVE REASON
    // =========================================================

    /**
     * Find archived user-redemption records by archive reason.
     *
     * Example values:
     *
     * - HOT_RETENTION_EXPIRED
     * - MANUAL_ARCHIVE
     * - SYSTEM_ARCHIVE
     */
    List<ColdUserRedemption> findAllByArchiveReason(
            String archiveReason
    );
}
