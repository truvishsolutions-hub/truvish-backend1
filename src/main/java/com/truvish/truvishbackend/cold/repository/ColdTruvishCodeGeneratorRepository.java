package com.truvish.truvishbackend.cold.repository;

import com.truvish.truvishbackend.cold.entity.ColdTruvishCodeGenerator;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * COLD DATABASE repository for archived
 * TruvishCodeGenerator records.
 *
 * This repository uses the COLD database datasource.
 *
 * It is responsible only for reading/writing
 * historical and archived code-generator data.
 *
 * HOT database access must continue through
 * the normal TruvishCodeRepository.
 */
@Repository
public interface ColdTruvishCodeGeneratorRepository
        extends JpaRepository<ColdTruvishCodeGenerator, Long> {


    // =========================================================
    // ORIGINAL HOT RECORD
    // =========================================================

    /**
     * Find one archived code-generator record using
     * the original HOT database record ID.
     */
    Optional<ColdTruvishCodeGenerator> findByHotRecordId(
            Long hotRecordId
    );


    /**
     * Find all archived code-generator records
     * associated with the same original HOT record.
     *
     * Useful when multiple historical snapshots exist.
     */
    List<ColdTruvishCodeGenerator> findAllByHotRecordId(
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
     * Find archived code-generator records by code number.
     */
    List<ColdTruvishCodeGenerator> findAllByCodeNumber(
            String codeNumber
    );


    /**
     * Check whether an archived record exists
     * for the given code number.
     */
    boolean existsByCodeNumber(
            String codeNumber
    );


    // =========================================================
    // CLIENT
    // =========================================================

    /**
     * Find archived code-generator records by client ID.
     */
    List<ColdTruvishCodeGenerator> findAllByClientId(
            Long clientId
    );


    /**
     * Check whether archived code-generator records
     * exist for a client.
     */
    boolean existsByClientId(
            Long clientId
    );


    // =========================================================
    // STATUS
    // =========================================================

    /**
     * Find archived code-generator records by status.
     */
    List<ColdTruvishCodeGenerator> findAllByStatus(
            String status
    );


    // =========================================================
    // BRAND NAME
    // =========================================================

    /**
     * Find archived code-generator records by brand name.
     */
    List<ColdTruvishCodeGenerator> findAllByBrandName(
            String brandName
    );


    // =========================================================
    // CLIENT BRAND
    // =========================================================

    /**
     * Find archived code-generator records by client brand.
     */
    List<ColdTruvishCodeGenerator> findAllByClientBrand(
            String clientBrand
    );


    // =========================================================
    // CLIENT CATEGORY
    // =========================================================

    /**
     * Find archived code-generator records by client category.
     */
    List<ColdTruvishCodeGenerator> findAllByClientCategory(
            String clientCategory
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
    List<ColdTruvishCodeGenerator> findAllByArchivedAtBefore(
            LocalDateTime archivedAt
    );


    /**
     * Find COLD records whose archive time is equal to
     * or older than the supplied date/time.
     *
     * Useful for the permanent-delete process.
     */
    List<ColdTruvishCodeGenerator> findAllByArchivedAtLessThanEqual(
            LocalDateTime archivedAt
    );


    // =========================================================
    // ARCHIVE REASON
    // =========================================================

    /**
     * Find archived code-generator records by archive reason.
     *
     * Example values:
     *
     * - HOT_RETENTION_EXPIRED
     * - MANUAL_ARCHIVE
     * - SYSTEM_ARCHIVE
     */
    List<ColdTruvishCodeGenerator> findAllByArchiveReason(
            String archiveReason
    );
}