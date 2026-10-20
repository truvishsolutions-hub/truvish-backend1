package com.truvish.truvishbackend.cold.repository;

import com.truvish.truvishbackend.cold.entity.ColdVoucherRedeemedLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * COLD DATABASE repository for archived voucher redemption logs.
 *
 * This repository handles historical/archived voucher redemption
 * records stored in the COLD PostgreSQL database.
 *
 * Current/live voucher redemption logs must continue to use
 * VoucherRedeemedLogRepository from the HOT database.
 */
@Repository
public interface ColdVoucherRedeemedLogRepository
        extends JpaRepository<ColdVoucherRedeemedLog, Long> {


    // =========================================================
    // ORIGINAL HOT RECORD
    // =========================================================

    /**
     * Find one archived voucher redemption log
     * using the original HOT database record ID.
     */
    Optional<ColdVoucherRedeemedLog> findByHotRecordId(
            Long hotRecordId
    );


    /**
     * Find all archived voucher redemption logs
     * associated with the same original HOT record.
     *
     * Useful when multiple historical snapshots exist.
     */
    List<ColdVoucherRedeemedLog> findAllByHotRecordId(
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
    // INVENTORY
    // =========================================================

    /**
     * Find archived voucher redemption logs by inventory ID.
     */
    List<ColdVoucherRedeemedLog> findAllByInventoryId(
            Long inventoryId
    );


    /**
     * Check whether archived voucher redemption logs
     * exist for an inventory ID.
     */
    boolean existsByInventoryId(
            Long inventoryId
    );


    // =========================================================
    // VOUCHER CODE
    // =========================================================

    /**
     * Find archived voucher redemption logs by voucher code.
     */
    List<ColdVoucherRedeemedLog> findAllByVoucherCode(
            String voucherCode
    );


    /**
     * Check whether an archived voucher redemption log
     * exists for a voucher code.
     */
    boolean existsByVoucherCode(
            String voucherCode
    );


    // =========================================================
    // BRAND
    // =========================================================

    /**
     * Find archived voucher redemption logs by brand name.
     */
    List<ColdVoucherRedeemedLog> findAllByBrandName(
            String brandName
    );


    // =========================================================
    // REDEEMED BY
    // =========================================================

    /**
     * Find archived voucher redemption logs by
     * the person/user who redeemed the voucher.
     */
    List<ColdVoucherRedeemedLog> findAllByRedeemedBy(
            String redeemedBy
    );


    // =========================================================
    // ORDER REFERENCE
    // =========================================================

    /**
     * Find archived voucher redemption logs by
     * order reference.
     */
    List<ColdVoucherRedeemedLog> findAllByOrderReference(
            String orderReference
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
    List<ColdVoucherRedeemedLog> findAllByArchivedAtBefore(
            LocalDateTime archivedAt
    );


    /**
     * Find COLD records whose archive time is equal to
     * or older than the supplied date/time.
     *
     * Useful for the permanent-delete process.
     */
    List<ColdVoucherRedeemedLog> findAllByArchivedAtLessThanEqual(
            LocalDateTime archivedAt
    );


    // =========================================================
    // ARCHIVE REASON
    // =========================================================

    /**
     * Find archived voucher redemption logs by archive reason.
     *
     * Example values:
     *
     * - HOT_RETENTION_EXPIRED
     * - MANUAL_ARCHIVE
     * - SYSTEM_ARCHIVE
     */
    List<ColdVoucherRedeemedLog> findAllByArchiveReason(
            String archiveReason
    );
}
