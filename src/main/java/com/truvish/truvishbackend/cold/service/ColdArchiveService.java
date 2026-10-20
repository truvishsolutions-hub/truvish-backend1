package com.truvish.truvishbackend.cold.service;

import com.truvish.truvishbackend.cold.repository.ColdCodeRedemptionHistoryRepository;
import com.truvish.truvishbackend.cold.repository.ColdTruBlankCodeRepository;
import com.truvish.truvishbackend.cold.repository.ColdTruvishCodeGeneratorRepository;
import com.truvish.truvishbackend.cold.repository.ColdUserRedemptionRepository;
import com.truvish.truvishbackend.cold.repository.ColdVoucherRedeemedLogRepository;
import com.truvish.truvishbackend.cold.repository.ColdWalletTransactionRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@Transactional
public class ColdArchiveService {

    // RETENTION POLICY

    private static final long HOT_RETENTION_MONTHS = 6;

    private static final long COLD_RETENTION_MONTHS = 6;

    // REPOSITORIES

    private final ColdTruBlankCodeRepository
            coldTruBlankCodeRepository;

    private final ColdTruvishCodeGeneratorRepository
            coldTruvishCodeGeneratorRepository;

    private final ColdWalletTransactionRepository
            coldWalletTransactionRepository;

    private final ColdVoucherRedeemedLogRepository
            coldVoucherRedeemedLogRepository;

    private final ColdCodeRedemptionHistoryRepository
            coldCodeRedemptionHistoryRepository;

    private final ColdUserRedemptionRepository
            coldUserRedemptionRepository;


    // ============================================================
    // CONSTRUCTOR
    // ============================================================

    public ColdArchiveService(
            ColdTruBlankCodeRepository coldTruBlankCodeRepository,
            ColdTruvishCodeGeneratorRepository coldTruvishCodeGeneratorRepository,
            ColdWalletTransactionRepository coldWalletTransactionRepository,
            ColdVoucherRedeemedLogRepository coldVoucherRedeemedLogRepository,
            ColdCodeRedemptionHistoryRepository coldCodeRedemptionHistoryRepository,
            ColdUserRedemptionRepository coldUserRedemptionRepository
    ) {
        this.coldTruBlankCodeRepository =
                coldTruBlankCodeRepository;

        this.coldTruvishCodeGeneratorRepository =
                coldTruvishCodeGeneratorRepository;

        this.coldWalletTransactionRepository =
                coldWalletTransactionRepository;

        this.coldVoucherRedeemedLogRepository =
                coldVoucherRedeemedLogRepository;

        this.coldCodeRedemptionHistoryRepository =
                coldCodeRedemptionHistoryRepository;

        this.coldUserRedemptionRepository =
                coldUserRedemptionRepository;
    }


    // ============================================================
    // CURRENT ARCHIVE TIME
    // ============================================================

    /**
     * Returns the current timestamp used by
     * the archive process.
     */
    public LocalDateTime archiveTime() {
        return LocalDateTime.now();
    }


    // ============================================================
    // HOT RETENTION
    // ============================================================


    public boolean isHotRetentionExpired(
            LocalDateTime recordTime
    ) {

        if (recordTime == null) {
            return false;
        }

        LocalDateTime retentionDate =
                LocalDateTime.now()
                        .minusMonths(HOT_RETENTION_MONTHS);

        return !recordTime.isAfter(retentionDate);
    }


    // ============================================================
    // COLD RETENTION
    // ============================================================

    public boolean isColdRetentionExpired(
            LocalDateTime archivedAt
    ) {

        if (archivedAt == null) {
            return false;
        }

        LocalDateTime retentionDate =
                LocalDateTime.now()
                        .minusMonths(COLD_RETENTION_MONTHS);

        return !archivedAt.isAfter(retentionDate);
    }


    // ============================================================
    // RETENTION VALUES
    // ============================================================

    /**
     * Returns HOT retention period in months.
     */
    public long getHotRetentionMonths() {
        return HOT_RETENTION_MONTHS;
    }


    /**
     * Returns COLD retention period in months.
     */
    public long getColdRetentionMonths() {
        return COLD_RETENTION_MONTHS;
    }


    // ============================================================
    // TRU BLANK CODE
    // ============================================================

    /**
     * Checks whether a HOT TruBlankCode record
     * has already been archived into COLD.
     */
    @Transactional(readOnly = true)
    public boolean isTruBlankCodeArchived(
            Long hotRecordId
    ) {

        if (hotRecordId == null) {
            return false;
        }

        return coldTruBlankCodeRepository
                .existsByHotRecordId(hotRecordId);
    }


    // ============================================================
    // TRUVISH CODE GENERATOR
    // ============================================================

    /**
     * Checks whether a HOT TruvishCodeGenerator record
     * has already been archived into COLD.
     */
    @Transactional(readOnly = true)
    public boolean isTruvishCodeGeneratorArchived(
            Long hotRecordId
    ) {

        if (hotRecordId == null) {
            return false;
        }

        return coldTruvishCodeGeneratorRepository
                .existsByHotRecordId(hotRecordId);
    }


    // ============================================================
    // WALLET TRANSACTION
    // ============================================================

    /**
     * Checks whether a HOT wallet transaction
     * has already been archived into COLD.
     */
    @Transactional(readOnly = true)
    public boolean isWalletTransactionArchived(
            Long hotRecordId
    ) {

        if (hotRecordId == null) {
            return false;
        }

        return coldWalletTransactionRepository
                .existsByHotRecordId(hotRecordId);
    }


    // ============================================================
    // VOUCHER REDEEMED LOG
    // ============================================================

    /**
     * Checks whether a HOT voucher redeemed log
     * has already been archived into COLD.
     */
    @Transactional(readOnly = true)
    public boolean isVoucherRedeemedLogArchived(
            Long hotRecordId
    ) {

        if (hotRecordId == null) {
            return false;
        }

        return coldVoucherRedeemedLogRepository
                .existsByHotRecordId(hotRecordId);
    }


    // ============================================================
    // CODE REDEMPTION HISTORY
    // ============================================================

    /**
     * Checks whether a HOT code redemption history
     * record has already been archived into COLD.
     */
    @Transactional(readOnly = true)
    public boolean isCodeRedemptionHistoryArchived(
            Long hotRecordId
    ) {

        if (hotRecordId == null) {
            return false;
        }

        return coldCodeRedemptionHistoryRepository
                .existsByHotRecordId(hotRecordId);
    }


    // ============================================================
    // USER REDEMPTION
    // ============================================================

    /**
     * Checks whether a HOT user redemption record
     * has already been archived into COLD.
     */
    @Transactional(readOnly = true)
    public boolean isUserRedemptionArchived(
            Long hotRecordId
    ) {

        if (hotRecordId == null) {
            return false;
        }

        return coldUserRedemptionRepository
                .existsByHotRecordId(hotRecordId);
    }


    // ============================================================
    // DUPLICATE ARCHIVE CHECK
    // ============================================================

    @Transactional(readOnly = true)
    public boolean alreadyArchived(
            ArchiveType archiveType,
            Long hotRecordId
    ) {

        if (archiveType == null || hotRecordId == null) {
            return false;
        }

        return switch (archiveType) {

            case TRU_BLANK_CODE ->
                    coldTruBlankCodeRepository
                            .existsByHotRecordId(hotRecordId);

            case TRUVISH_CODE_GENERATOR ->
                    coldTruvishCodeGeneratorRepository
                            .existsByHotRecordId(hotRecordId);

            case WALLET_TRANSACTION ->
                    coldWalletTransactionRepository
                            .existsByHotRecordId(hotRecordId);

            case VOUCHER_REDEEMED_LOG ->
                    coldVoucherRedeemedLogRepository
                            .existsByHotRecordId(hotRecordId);

            case CODE_REDEMPTION_HISTORY ->
                    coldCodeRedemptionHistoryRepository
                            .existsByHotRecordId(hotRecordId);

            case USER_REDEMPTION ->
                    coldUserRedemptionRepository
                            .existsByHotRecordId(hotRecordId);
        };
    }


    // ============================================================
    // ARCHIVE TYPES
    // ============================================================

    /**
     * Supported HOT -> COLD archive types.
     */
    public enum ArchiveType {

        TRU_BLANK_CODE,

        TRUVISH_CODE_GENERATOR,

        WALLET_TRANSACTION,

        VOUCHER_REDEEMED_LOG,

        CODE_REDEMPTION_HISTORY,

        USER_REDEMPTION
    }
}
