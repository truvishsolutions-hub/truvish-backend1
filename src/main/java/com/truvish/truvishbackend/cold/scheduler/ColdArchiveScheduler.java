package com.truvish.truvishbackend.cold.scheduler;

import com.truvish.truvishbackend.TruBlankCode.TruBlankCode;
import com.truvish.truvishbackend.TruBlankCode.TruBlankCodeRepository;
import com.truvish.truvishbackend.TruBlankCode.TruBlankCodeStatus;

import com.truvish.truvishbackend.TruOpeAdmin.TruvishCode;
import com.truvish.truvishbackend.TruOpeAdmin.TruvishCodeRepository;

import com.truvish.truvishbackend.client.Client;
import com.truvish.truvishbackend.client.ClientRepository;

import com.truvish.truvishbackend.cold.entity.ColdCodeRedemptionHistory;
import com.truvish.truvishbackend.cold.entity.ColdTruBlankCode;
import com.truvish.truvishbackend.cold.entity.ColdTruvishCodeGenerator;
import com.truvish.truvishbackend.cold.entity.ColdVoucherRedeemedLog;
import com.truvish.truvishbackend.cold.entity.ColdWalletTransaction;

import com.truvish.truvishbackend.cold.repository.ColdCodeRedemptionHistoryRepository;
import com.truvish.truvishbackend.cold.repository.ColdTruBlankCodeRepository;
import com.truvish.truvishbackend.cold.repository.ColdTruvishCodeGeneratorRepository;
import com.truvish.truvishbackend.cold.repository.ColdVoucherRedeemedLogRepository;
import com.truvish.truvishbackend.cold.repository.ColdWalletTransactionRepository;

import com.truvish.truvishbackend.redemption.CodeRedemptionHistory;
import com.truvish.truvishbackend.redemption.CodeRedemptionHistoryRepository;

import com.truvish.truvishbackend.voucherinventory.VoucherRedeemedLog;
import com.truvish.truvishbackend.voucherinventory.VoucherRedeemedLogRepository;

import com.truvish.truvishbackend.wallet.WalletTransaction;
import com.truvish.truvishbackend.wallet.WalletTransactionRepository;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Component
public class ColdArchiveScheduler {

    // =========================================================
    // FINAL RETENTION POLICY
    // =========================================================
    //
    // HOT DATABASE
    //      |
    //      | record age >= 6 months
    //      v
    // COLD DATABASE
    //      |
    //      | archivedAt age >= 6 months
    //      v
    // PERMANENT DELETE
    //
    // This policy applies to:
    //
    // 1. cold_code_redemption_history
    // 2. cold_truvish_blank_code
    // 3. cold_truvish_code_generator
    // 4. cold_user_redemption
    // 5. cold_voucher_redeemed_log
    // 6. cold_wallet_transaction
    //
    // IMPORTANT:
    // COLD data deletion is based on archivedAt,
    // NOT the original createdAt/redeemedAt.
    // =========================================================

    private static final long HOT_RETENTION_MONTHS = 6;

    private static final long COLD_RETENTION_MONTHS = 6;

    /*
     * Scheduler runs once every hour.
     */
    private static final long SCHEDULER_DELAY_MS =
            60L * 60L * 1000L;


    // =========================================================
    // HOT REPOSITORIES
    // =========================================================

    private final TruBlankCodeRepository truBlankCodeRepository;

    private final TruvishCodeRepository truvishCodeRepository;

    private final WalletTransactionRepository
            walletTransactionRepository;

    private final VoucherRedeemedLogRepository
            voucherRedeemedLogRepository;

    private final CodeRedemptionHistoryRepository
            codeRedemptionHistoryRepository;

    private final ClientRepository clientRepository;


    // =========================================================
    // COLD REPOSITORIES
    // =========================================================

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


    // =========================================================
    // CONSTRUCTOR
    // =========================================================

    public ColdArchiveScheduler(
            TruBlankCodeRepository truBlankCodeRepository,
            TruvishCodeRepository truvishCodeRepository,
            WalletTransactionRepository walletTransactionRepository,
            VoucherRedeemedLogRepository voucherRedeemedLogRepository,
            CodeRedemptionHistoryRepository
                    codeRedemptionHistoryRepository,
            ClientRepository clientRepository,

            ColdTruBlankCodeRepository
                    coldTruBlankCodeRepository,
            ColdTruvishCodeGeneratorRepository
                    coldTruvishCodeGeneratorRepository,
            ColdWalletTransactionRepository
                    coldWalletTransactionRepository,
            ColdVoucherRedeemedLogRepository
                    coldVoucherRedeemedLogRepository,
            ColdCodeRedemptionHistoryRepository
                    coldCodeRedemptionHistoryRepository
    ) {

        this.truBlankCodeRepository =
                truBlankCodeRepository;

        this.truvishCodeRepository =
                truvishCodeRepository;

        this.walletTransactionRepository =
                walletTransactionRepository;

        this.voucherRedeemedLogRepository =
                voucherRedeemedLogRepository;

        this.codeRedemptionHistoryRepository =
                codeRedemptionHistoryRepository;

        this.clientRepository =
                clientRepository;

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
    }


    // =========================================================
    // MAIN SCHEDULER
    // =========================================================

    @Scheduled(fixedDelay = SCHEDULER_DELAY_MS)
    public void runColdArchiveJob() {

        LocalDateTime now =
                LocalDateTime.now();

        /*
         * HOT:
         * Records older than 6 months
         * become eligible for COLD.
         */
        LocalDateTime hotCutoff =
                now.minusMonths(
                        HOT_RETENTION_MONTHS
                );

        /*
         * COLD:
         * Records whose archivedAt is older
         * than 6 months are permanently deleted.
         */
        LocalDateTime coldCutoff =
                now.minusMonths(
                        COLD_RETENTION_MONTHS
                );


        System.out.println();
        System.out.println(
                "============================================================"
        );

        System.out.println(
                "[COLD ARCHIVE] JOB STARTED"
        );

        System.out.println(
                "[COLD ARCHIVE] Current time       : "
                        + now
        );

        System.out.println(
                "[COLD ARCHIVE] HOT cutoff        : "
                        + hotCutoff
        );

        System.out.println(
                "[COLD ARCHIVE] COLD cutoff       : "
                        + coldCutoff
        );

        System.out.println(
                "[COLD ARCHIVE] HOT retention     : 6 MONTHS"
        );

        System.out.println(
                "[COLD ARCHIVE] COLD retention    : 6 MONTHS"
        );


        // =====================================================
        // HOT -> COLD
        // =====================================================

        try {

            archiveTruBlankCodes(
                    hotCutoff
            );

        } catch (Exception e) {

            logError(
                    "TruBlankCode archive failed",
                    e
            );
        }


        try {

            archiveTruvishCodes(
                    hotCutoff
            );

        } catch (Exception e) {

            logError(
                    "TruvishCode archive failed",
                    e
            );
        }


        try {

            archiveWalletTransactions(
                    hotCutoff
            );

        } catch (Exception e) {

            logError(
                    "WalletTransaction archive failed",
                    e
            );
        }


        try {

            archiveVoucherRedeemedLogs(
                    hotCutoff
            );

        } catch (Exception e) {

            logError(
                    "VoucherRedeemedLog archive failed",
                    e
            );
        }


        try {

            archiveCodeRedemptionHistory(
                    hotCutoff
            );

        } catch (Exception e) {

            logError(
                    "CodeRedemptionHistory archive failed",
                    e
            );
        }


        // =====================================================
        // COLD -> PERMANENT DELETE
        // =====================================================

        try {

            cleanupColdData(
                    coldCutoff
            );

        } catch (Exception e) {

            logError(
                    "COLD cleanup failed",
                    e
            );
        }


        System.out.println(
                "[COLD ARCHIVE] JOB FINISHED"
        );

        System.out.println(
                "============================================================"
        );

        System.out.println();
    }


    // =========================================================
    // TRU BLANK CODE
    // =========================================================

    private void archiveTruBlankCodes(
            LocalDateTime hotCutoff
    ) {

        List<TruBlankCode> records =
                truBlankCodeRepository.findAll();

        int archived = 0;
        int skipped = 0;


        for (TruBlankCode hot : records) {

            try {

                if (hot.getId() == null) {
                    skipped++;
                    continue;
                }

                if (hot.getCreatedAt() == null) {
                    skipped++;
                    continue;
                }


                /*
                 * HOT retention = 6 months.
                 */
                if (hot.getCreatedAt()
                        .isAfter(hotCutoff)) {

                    skipped++;
                    continue;
                }


                /*
                 * Only terminal blank codes are archived.
                 *
                 * Active/configured codes stay HOT.
                 */
                boolean terminal =
                        hot.getStatus()
                                == TruBlankCodeStatus.REDEEMED

                                || hot.getStatus()
                                == TruBlankCodeStatus.EXPIRED

                                || hot.getStatus()
                                == TruBlankCodeStatus.CANCELLED;


                if (!terminal) {
                    skipped++;
                    continue;
                }


                Long hotId =
                        hot.getId();


                // =================================================
                // DUPLICATE PROTECTION
                // =================================================

                if (coldTruBlankCodeRepository
                        .existsByHotRecordId(hotId)) {

                    /*
                     * COLD already exists.
                     * Safe to remove HOT.
                     */
                    truBlankCodeRepository
                            .deleteById(hotId);

                    continue;
                }


                // =================================================
                // CREATE COLD SNAPSHOT
                // =================================================

                ColdTruBlankCode cold =
                        new ColdTruBlankCode();

                cold.setHotRecordId(
                        hotId
                );

                cold.setCodeNumber(
                        hot.getCodeNumber()
                );

                cold.setStatus(
                        hot.getStatus() != null
                                ? hot.getStatus().name()
                                : null
                );

                cold.setClientId(
                        hot.getClientId()
                );

                cold.setClientName(
                        hot.getClientName()
                );

                cold.setBrandName(
                        hot.getBrandName()
                );

                cold.setClientBrand(
                        hot.getClientBrandValue()
                );


                if (hot.getDenomination() != null) {

                    cold.setAmount(
                            BigDecimal.valueOf(
                                    hot.getDenomination()
                            )
                    );
                }


                cold.setCreatedAt(
                        hot.getCreatedAt()
                );

                cold.setUpdatedAt(
                        hot.getUpdatedAt()
                );

                cold.setAssignedAt(
                        hot.getAssignedAt()
                );

                cold.setActivatedAt(
                        hot.getActivatedAt()
                );

                cold.setRedeemedAt(
                        hot.getRedeemedAt()
                );


                /*
                 * IMPORTANT:
                 * This is the time when the record
                 * entered COLD.
                 */
                cold.setArchivedAt(
                        LocalDateTime.now()
                );

                cold.setArchiveReason(
                        hot.getStatus() != null
                                ? hot.getStatus().name()
                                : "HOT_RETENTION_EXPIRED"
                );


                // =================================================
                // SAVE COLD FIRST
                // =================================================

                coldTruBlankCodeRepository
                        .save(cold);


                // =================================================
                // VERIFY COLD
                // =================================================

                if (coldTruBlankCodeRepository
                        .existsByHotRecordId(hotId)) {

                    /*
                     * Only after COLD exists:
                     * delete HOT.
                     */
                    truBlankCodeRepository
                            .deleteById(hotId);

                    archived++;

                    System.out.println(
                            "[COLD ARCHIVE] TruBlankCode ARCHIVED: "
                                    + hotId
                    );
                }

            } catch (Exception e) {

                logRecordError(
                        "TruBlankCode",
                        hot.getId(),
                        e
                );
            }
        }


        System.out.println(
                "[COLD ARCHIVE] TruBlankCode -> "
                        + "archived=" + archived
                        + ", skipped=" + skipped
        );
    }


    // =========================================================
    // TRUVISH CODE GENERATOR
    // =========================================================

    private void archiveTruvishCodes(
            LocalDateTime hotCutoff
    ) {

        /*
         * Repository query should return:
         *
         * old + terminal records only.
         *
         * Active/current codes must remain HOT.
         */
        List<TruvishCode> records =
                truvishCodeRepository
                        .findArchivableCodesBefore(
                                hotCutoff
                        );

        int archived = 0;


        System.out.println(
                "[COLD ARCHIVE] TruvishCode eligible records: "
                        + records.size()
        );


        for (TruvishCode hot : records) {

            try {

                if (hot.getTruvishId() == null) {
                    continue;
                }


                Long hotId =
                        hot.getTruvishId();


                // =================================================
                // DUPLICATE PROTECTION
                // =================================================

                if (coldTruvishCodeGeneratorRepository
                        .existsByHotRecordId(hotId)) {

                    truvishCodeRepository
                            .deleteById(hotId);

                    continue;
                }


                ColdTruvishCodeGenerator cold =
                        new ColdTruvishCodeGenerator();

                cold.setHotRecordId(
                        hotId
                );

                cold.setCodeNumber(
                        hot.getTruvishIdCodeNumber()
                );

                cold.setStatus(
                        hot.getTruvishCodeStatus() != null
                                ? hot.getTruvishCodeStatus().name()
                                : null
                );

                cold.setClientId(
                        hot.getClientId()
                );

                cold.setClientName(
                        hot.getClientName()
                );

                cold.setClientBrand(
                        hot.getClientBrandValue()
                );

                cold.setClientCategory(
                        hot.getClientCategoryValue()
                );

                cold.setClientTheme(
                        hot.getClientTheme()
                );


                /*
                 * Historical snapshot.
                 */
                cold.setBrandName(
                        hot.getClientName()
                );


                if (hot.getOriginalCodeValue() != null) {

                    cold.setDenomination(
                            BigDecimal.valueOf(
                                    hot.getOriginalCodeValue()
                            )
                    );
                }


                cold.setValidityMonths(
                        hot.getValidity()
                );

                cold.setExpiryDate(
                        null
                );

                cold.setCreatedAt(
                        hot.getTruvishCodeTimestamp()
                );

                cold.setUpdatedAt(
                        hot.getTruvishCodeTimestamp()
                );

                cold.setCodeType(
                        "DIGITAL"
                );

                cold.setArchivedAt(
                        LocalDateTime.now()
                );

                cold.setArchiveReason(
                        hot.getTruvishCodeStatus() != null
                                ? hot.getTruvishCodeStatus().name()
                                : "HOT_RETENTION_EXPIRED"
                );


                // =================================================
                // SAVE COLD FIRST
                // =================================================

                coldTruvishCodeGeneratorRepository
                        .save(cold);


                // =================================================
                // VERIFY COLD
                // =================================================

                if (coldTruvishCodeGeneratorRepository
                        .existsByHotRecordId(hotId)) {

                    truvishCodeRepository
                            .deleteById(hotId);

                    archived++;

                    System.out.println(
                            "[COLD ARCHIVE] TruvishCode ARCHIVED: "
                                    + hotId
                                    + " | code="
                                    + hot.getTruvishIdCodeNumber()
                    );
                }

            } catch (Exception e) {

                logRecordError(
                        "TruvishCode",
                        hot.getTruvishId(),
                        e
                );
            }
        }


        System.out.println(
                "[COLD ARCHIVE] TruvishCode -> "
                        + "archived=" + archived
        );
    }


    // =========================================================
    // WALLET TRANSACTION
    // =========================================================

    private void archiveWalletTransactions(
            LocalDateTime hotCutoff
    ) {

        /*
         * FINAL POLICY:
         *
         * Wallet HOT = 6 months
         * Wallet COLD = 6 months
         * Then permanent delete.
         *
         * Client.balance is NEVER changed here.
         */

        List<WalletTransaction> records =
                walletTransactionRepository.findAll();

        int archived = 0;
        int skipped = 0;


        for (WalletTransaction hot : records) {

            try {

                if (hot.getTxnId() == null) {
                    skipped++;
                    continue;
                }

                if (hot.getCreatedAt() == null) {
                    skipped++;
                    continue;
                }


                if (hot.getCreatedAt()
                        .isAfter(hotCutoff)) {

                    skipped++;
                    continue;
                }


                Long hotId =
                        hot.getTxnId();


                // =================================================
                // DUPLICATE PROTECTION
                // =================================================

                if (coldWalletTransactionRepository
                        .existsByHotRecordId(hotId)) {

                    walletTransactionRepository
                            .deleteById(hotId);

                    continue;
                }


                // =================================================
                // CREATE COLD SNAPSHOT
                // =================================================

                ColdWalletTransaction cold =
                        new ColdWalletTransaction();

                cold.setHotRecordId(
                        hotId
                );


                Long clientId = null;

                if (hot.getClient() != null) {

                    clientId =
                            hot.getClient().getId();
                }


                if (clientId != null) {

                    cold.setClientId(
                            clientId
                    );

                    Client client =
                            clientRepository
                                    .findById(clientId)
                                    .orElse(null);

                    if (client != null) {

                        cold.setClientName(
                                client.getCompanyName()
                        );
                    }
                }


                cold.setAmount(
                        hot.getAmount()
                );

                cold.setTransactionType(
                        hot.getType() != null
                                ? hot.getType().name()
                                : null
                );

                cold.setDescription(
                        hot.getDescription()
                );

                cold.setReference(
                        hot.getReferenceId()
                );

                cold.setReferenceType(
                        hot.getReferenceType()
                );

                cold.setStatus(
                        hot.getStatus() != null
                                ? hot.getStatus().name()
                                : null
                );

                cold.setCreatedAt(
                        hot.getCreatedAt()
                );

                cold.setUpdatedAt(
                        hot.getCreatedAt()
                );


                /*
                 * Time when record entered COLD.
                 */
                cold.setArchivedAt(
                        LocalDateTime.now()
                );

                cold.setArchiveReason(
                        "HOT_RETENTION_6_MONTHS"
                );


                // =================================================
                // SAVE COLD FIRST
                // =================================================

                coldWalletTransactionRepository
                        .save(cold);


                // =================================================
                // VERIFY COLD
                // =================================================

                if (coldWalletTransactionRepository
                        .existsByHotRecordId(hotId)) {

                    walletTransactionRepository
                            .deleteById(hotId);

                    archived++;

                    System.out.println(
                            "[COLD ARCHIVE] WalletTransaction ARCHIVED: "
                                    + hotId
                    );
                }

            } catch (Exception e) {

                logRecordError(
                        "WalletTransaction",
                        hot.getTxnId(),
                        e
                );
            }
        }


        System.out.println(
                "[COLD ARCHIVE] WalletTransaction -> "
                        + "archived=" + archived
                        + ", skipped=" + skipped
        );
    }


    // =========================================================
    // VOUCHER REDEEMED LOG
    // =========================================================

    private void archiveVoucherRedeemedLogs(
            LocalDateTime hotCutoff
    ) {

        List<VoucherRedeemedLog> records =
                voucherRedeemedLogRepository.findAll();

        int archived = 0;
        int skipped = 0;


        for (VoucherRedeemedLog hot : records) {

            try {

                if (hot.getId() == null) {
                    skipped++;
                    continue;
                }

                if (hot.getRedeemedAt() == null) {
                    skipped++;
                    continue;
                }


                if (hot.getRedeemedAt()
                        .isAfter(hotCutoff)) {

                    skipped++;
                    continue;
                }


                Long hotId =
                        hot.getId();


                if (coldVoucherRedeemedLogRepository
                        .existsByHotRecordId(hotId)) {

                    voucherRedeemedLogRepository
                            .deleteById(hotId);

                    continue;
                }


                ColdVoucherRedeemedLog cold =
                        new ColdVoucherRedeemedLog();

                cold.setHotRecordId(
                        hotId
                );

                cold.setInventoryId(
                        hot.getInventoryId()
                );

                cold.setBrandName(
                        hot.getBrandName()
                );

                cold.setDenomination(
                        hot.getDenomination()
                );

                cold.setVoucherCode(
                        hot.getVoucherCode()
                );

                cold.setVoucherPin(
                        hot.getVoucherPin()
                );

                cold.setValidityTill(
                        hot.getValidityTill()
                );

                cold.setRedeemedAt(
                        hot.getRedeemedAt()
                );

                cold.setRedeemedBy(
                        hot.getRedeemedBy()
                );

                cold.setOrderReference(
                        hot.getOrderReference()
                );


                /*
                 * IMPORTANT:
                 * archivedAt = actual COLD archive time.
                 */
                cold.setArchivedAt(
                        LocalDateTime.now()
                );

                cold.setArchiveReason(
                        "HOT_RETENTION_6_MONTHS"
                );


                // =================================================
                // SAVE COLD FIRST
                // =================================================

                coldVoucherRedeemedLogRepository
                        .save(cold);


                // =================================================
                // VERIFY COLD
                // =================================================

                if (coldVoucherRedeemedLogRepository
                        .existsByHotRecordId(hotId)) {

                    voucherRedeemedLogRepository
                            .deleteById(hotId);

                    archived++;

                    System.out.println(
                            "[COLD ARCHIVE] VoucherRedeemedLog ARCHIVED: "
                                    + hotId
                    );
                }

            } catch (Exception e) {

                logRecordError(
                        "VoucherRedeemedLog",
                        hot.getId(),
                        e
                );
            }
        }


        System.out.println(
                "[COLD ARCHIVE] VoucherRedeemedLog -> "
                        + "archived=" + archived
                        + ", skipped=" + skipped
        );
    }


    // =========================================================
    // CODE REDEMPTION HISTORY
    // =========================================================

    private void archiveCodeRedemptionHistory(
            LocalDateTime hotCutoff
    ) {

        List<CodeRedemptionHistory> records =
                codeRedemptionHistoryRepository.findAll();

        int archived = 0;
        int skipped = 0;


        for (CodeRedemptionHistory hot : records) {

            try {

                if (hot.getId() == null) {
                    skipped++;
                    continue;
                }


                /*
                 * Use redemption event time.
                 * If redeemedAt is null, use createdAt.
                 */
                LocalDateTime eventTime =
                        hot.getRedeemedAt() != null
                                ? hot.getRedeemedAt()
                                : hot.getCreatedAt();


                if (eventTime == null) {
                    skipped++;
                    continue;
                }


                if (eventTime.isAfter(hotCutoff)) {
                    skipped++;
                    continue;
                }


                Long hotId =
                        hot.getId();


                if (coldCodeRedemptionHistoryRepository
                        .existsByHotRecordId(hotId)) {

                    codeRedemptionHistoryRepository
                            .deleteById(hotId);

                    continue;
                }


                ColdCodeRedemptionHistory cold =
                        new ColdCodeRedemptionHistory();

                cold.setHotRecordId(
                        hotId
                );

                cold.setClientId(
                        hot.getClientId()
                );

                cold.setClientCompanyName(
                        hot.getClientCompanyName()
                );

                cold.setTruvishCode(
                        hot.getTruvishCode()
                );

                cold.setPhoneNumber(
                        hot.getPhoneNumber()
                );

                cold.setBrandName(
                        hot.getBrandName()
                );

                cold.setRedeemedValue(
                        hot.getRedeemedValue()
                );

                cold.setVoucherCode(
                        hot.getVoucherCode()
                );

                cold.setVoucherPin(
                        hot.getVoucherPin()
                );

                cold.setValidityTill(
                        hot.getValidityTill()
                );

                cold.setBeforeBalance(
                        hot.getBeforeBalance()
                );

                cold.setAfterBalance(
                        hot.getAfterBalance()
                );

                cold.setRedeemStatus(
                        hot.getRedeemStatus()
                );

                cold.setHistoryMessage(
                        hot.getHistoryMessage()
                );

                cold.setBrandLogo(
                        hot.getBrandLogo()
                );

                cold.setRedemptionProcess(
                        hot.getRedemptionProcess()
                );

                cold.setRedeemedAt(
                        hot.getRedeemedAt()
                );

                cold.setCreatedAt(
                        hot.getCreatedAt()
                );


                /*
                 * REQUIRED for COLD retention.
                 */
                cold.setArchivedAt(
                        LocalDateTime.now()
                );

                cold.setArchiveReason(
                        "HOT_RETENTION_6_MONTHS"
                );


                // =================================================
                // SAVE COLD FIRST
                // =================================================

                coldCodeRedemptionHistoryRepository
                        .save(cold);


                // =================================================
                // VERIFY COLD
                // =================================================

                if (coldCodeRedemptionHistoryRepository
                        .existsByHotRecordId(hotId)) {

                    codeRedemptionHistoryRepository
                            .deleteById(hotId);

                    archived++;

                    System.out.println(
                            "[COLD ARCHIVE] "
                                    + "CodeRedemptionHistory ARCHIVED: "
                                    + hotId
                    );
                }

            } catch (Exception e) {

                logRecordError(
                        "CodeRedemptionHistory",
                        hot.getId(),
                        e
                );
            }
        }


        System.out.println(
                "[COLD ARCHIVE] CodeRedemptionHistory -> "
                        + "archived=" + archived
                        + ", skipped=" + skipped
        );
    }


    // =========================================================
    // COLD CLEANUP
    // =========================================================

    private void cleanupColdData(
            LocalDateTime coldCutoff
    ) {

        /*
         * ALL COLD TABLES use the same rule:
         *
         * archivedAt older than 6 months
         * -> permanent delete.
         */

        cleanupColdTruBlankCodes(
                coldCutoff
        );

        cleanupColdTruvishCodes(
                coldCutoff
        );

        cleanupColdWalletTransactions(
                coldCutoff
        );

        cleanupColdVoucherRedeemedLogs(
                coldCutoff
        );

        cleanupColdCodeRedemptionHistory(
                coldCutoff
        );

        /*
         * UserRedemption will be added here
         * after its exact HOT entity/repository
         * methods are confirmed.
         */
    }


    // =========================================================
    // COLD TRU BLANK CODE CLEANUP
    // =========================================================

    private void cleanupColdTruBlankCodes(
            LocalDateTime coldCutoff
    ) {

        List<ColdTruBlankCode> records =
                coldTruBlankCodeRepository.findAll();


        for (ColdTruBlankCode cold : records) {

            try {

                if (cold.getArchivedAt() == null) {
                    continue;
                }

                if (cold.getArchivedAt()
                        .isAfter(coldCutoff)) {
                    continue;
                }


                Long id =
                        cold.getId();

                coldTruBlankCodeRepository
                        .deleteById(id);


                System.out.println(
                        "[COLD CLEANUP] "
                                + "ColdTruBlankCode deleted: "
                                + id
                );

            } catch (Exception e) {

                logRecordError(
                        "ColdTruBlankCode cleanup",
                        cold.getId(),
                        e
                );
            }
        }
    }


    // =========================================================
    // COLD TRUVISH CODE CLEANUP
    // =========================================================

    private void cleanupColdTruvishCodes(
            LocalDateTime coldCutoff
    ) {

        List<ColdTruvishCodeGenerator> records =
                coldTruvishCodeGeneratorRepository
                        .findAll();


        for (ColdTruvishCodeGenerator cold : records) {

            try {

                if (cold.getArchivedAt() == null) {
                    continue;
                }

                if (cold.getArchivedAt()
                        .isAfter(coldCutoff)) {
                    continue;
                }


                Long id =
                        cold.getId();

                coldTruvishCodeGeneratorRepository
                        .deleteById(id);


                System.out.println(
                        "[COLD CLEANUP] "
                                + "ColdTruvishCodeGenerator deleted: "
                                + id
                );

            } catch (Exception e) {

                logRecordError(
                        "ColdTruvishCodeGenerator cleanup",
                        cold.getId(),
                        e
                );
            }
        }
    }


    // =========================================================
    // COLD WALLET CLEANUP
    // =========================================================

    private void cleanupColdWalletTransactions(
            LocalDateTime coldCutoff
    ) {

        List<ColdWalletTransaction> records =
                coldWalletTransactionRepository
                        .findAll();


        for (ColdWalletTransaction cold : records) {

            try {

                if (cold.getArchivedAt() == null) {
                    continue;
                }

                if (cold.getArchivedAt()
                        .isAfter(coldCutoff)) {
                    continue;
                }


                Long id =
                        cold.getId();

                coldWalletTransactionRepository
                        .deleteById(id);


                System.out.println(
                        "[COLD CLEANUP] "
                                + "ColdWalletTransaction deleted: "
                                + id
                );

            } catch (Exception e) {

                logRecordError(
                        "ColdWalletTransaction cleanup",
                        cold.getId(),
                        e
                );
            }
        }
    }


    // =========================================================
    // COLD VOUCHER REDEEMED LOG CLEANUP
    // =========================================================

    private void cleanupColdVoucherRedeemedLogs(
            LocalDateTime coldCutoff
    ) {

        List<ColdVoucherRedeemedLog> records =
                coldVoucherRedeemedLogRepository
                        .findAll();


        for (ColdVoucherRedeemedLog cold : records) {

            try {

                if (cold.getArchivedAt() == null) {
                    continue;
                }

                if (cold.getArchivedAt()
                        .isAfter(coldCutoff)) {
                    continue;
                }


                Long id =
                        cold.getId();

                coldVoucherRedeemedLogRepository
                        .deleteById(id);


                System.out.println(
                        "[COLD CLEANUP] "
                                + "ColdVoucherRedeemedLog deleted: "
                                + id
                );

            } catch (Exception e) {

                logRecordError(
                        "ColdVoucherRedeemedLog cleanup",
                        cold.getId(),
                        e
                );
            }
        }
    }


    // =========================================================
    // COLD CODE REDEMPTION HISTORY CLEANUP
    // =========================================================

    private void cleanupColdCodeRedemptionHistory(
            LocalDateTime coldCutoff
    ) {

        List<ColdCodeRedemptionHistory> records =
                coldCodeRedemptionHistoryRepository
                        .findAll();


        for (ColdCodeRedemptionHistory cold : records) {

            try {

                if (cold.getArchivedAt() == null) {
                    continue;
                }

                if (cold.getArchivedAt()
                        .isAfter(coldCutoff)) {
                    continue;
                }


                Long id =
                        cold.getId();

                coldCodeRedemptionHistoryRepository
                        .deleteById(id);


                System.out.println(
                        "[COLD CLEANUP] "
                                + "ColdCodeRedemptionHistory deleted: "
                                + id
                );

            } catch (Exception e) {

                logRecordError(
                        "ColdCodeRedemptionHistory cleanup",
                        cold.getId(),
                        e
                );
            }
        }
    }


    // =========================================================
    // ERROR LOGGING
    // =========================================================

    private void logError(
            String message,
            Exception e
    ) {

        System.err.println(
                "[COLD ARCHIVE ERROR] "
                        + message
        );

        System.err.println(
                "[COLD ARCHIVE ERROR] "
                        + e.getClass().getName()
        );

        System.err.println(
                "[COLD ARCHIVE ERROR] "
                        + e.getMessage()
        );

        e.printStackTrace();
    }


    private void logRecordError(
            String entity,
            Long id,
            Exception e
    ) {

        System.err.println(
                "[COLD ARCHIVE ERROR] "
                        + entity
                        + " | hotId="
                        + id
                        + " | "
                        + e.getClass().getName()
                        + " | "
                        + e.getMessage()
        );

        e.printStackTrace();
    }
}