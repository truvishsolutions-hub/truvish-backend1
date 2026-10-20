package com.truvish.truvishbackend.cold.repository;

import com.truvish.truvishbackend.cold.entity.ColdTruBlankCode;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * COLD DATABASE repository for archived TruBlankCode records.
 *
 * This repository is responsible only for historical /
 * archived blank-code data stored in the COLD PostgreSQL database.
 *
 * Current/active blank-code data must continue to use
 * TruBlankCodeRepository from the HOT database.
 */
@Repository
public interface ColdTruBlankCodeRepository
        extends JpaRepository<ColdTruBlankCode, Long> {


    // =========================================================
    // ORIGINAL HOT RECORD
    // =========================================================

    /**
     * Find one archived blank-code record using
     * the original HOT database record ID.
     */
    Optional<ColdTruBlankCode> findByHotRecordId(
            Long hotRecordId
    );


    /**
     * Find all archived records associated with
     * the same original HOT record.
     *
     * Useful when multiple historical snapshots exist.
     */
    List<ColdTruBlankCode> findAllByHotRecordId(
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
    // CODE NUMBER
    // =========================================================

    /**
     * Find archived blank-code records by code number.
     */
    List<ColdTruBlankCode> findAllByCodeNumber(
            String codeNumber
    );


    /**
     * Check whether an archived record exists
     * for a particular code number.
     */
    boolean existsByCodeNumber(
            String codeNumber
    );


    // =========================================================
    // CLIENT
    // =========================================================

    /**
     * Find archived blank-code records by client ID.
     */
    List<ColdTruBlankCode> findAllByClientId(
            Long clientId
    );


    /**
     * Check whether archived blank-code records
     * exist for a client.
     */
    boolean existsByClientId(
            Long clientId
    );


    // =========================================================
    // STATUS
    // =========================================================

    /**
     * Find archived blank-code records by status.
     */
    List<ColdTruBlankCode> findAllByStatus(
            String status
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
    List<ColdTruBlankCode> findAllByArchivedAtBefore(
            LocalDateTime archivedAt
    );


    /**
     * Find COLD records whose archive time is equal to
     * or older than the supplied date/time.
     *
     * Useful for the permanent-delete process.
     */
    List<ColdTruBlankCode> findAllByArchivedAtLessThanEqual(
            LocalDateTime archivedAt
    );


    // =========================================================
    // ARCHIVE REASON
    // =========================================================

    /**
     * Find archived blank-code records by archive reason.
     *
     * Example values:
     *
     * - HOT_RETENTION_EXPIRED
     * - MANUAL_ARCHIVE
     * - SYSTEM_ARCHIVE
     */
    List<ColdTruBlankCode> findAllByArchiveReason(
            String archiveReason
    );
}