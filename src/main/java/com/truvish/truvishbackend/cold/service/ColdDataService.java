package com.truvish.truvishbackend.cold.service;

import com.truvish.truvishbackend.cold.entity.ColdCodeRedemptionHistory;
import com.truvish.truvishbackend.cold.entity.ColdTruBlankCode;
import com.truvish.truvishbackend.cold.entity.ColdTruvishCodeGenerator;
import com.truvish.truvishbackend.cold.entity.ColdUserRedemption;
import com.truvish.truvishbackend.cold.entity.ColdVoucherRedeemedLog;
import com.truvish.truvishbackend.cold.entity.ColdWalletTransaction;

import com.truvish.truvishbackend.cold.repository.ColdCodeRedemptionHistoryRepository;
import com.truvish.truvishbackend.cold.repository.ColdTruBlankCodeRepository;
import com.truvish.truvishbackend.cold.repository.ColdTruvishCodeGeneratorRepository;
import com.truvish.truvishbackend.cold.repository.ColdUserRedemptionRepository;
import com.truvish.truvishbackend.cold.repository.ColdVoucherRedeemedLogRepository;
import com.truvish.truvishbackend.cold.repository.ColdWalletTransactionRepository;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

/**
 * Service for reading historical / archived data
 * from the COLD PostgreSQL database.
 *
 * IMPORTANT:
 * - Current/live data belongs to the HOT database.
 * - Historical/archived data belongs to the COLD database.
 * - This service only reads COLD data.
 * - Archive and delete operations are handled separately
 *   by ColdArchiveService.
 */
@Service
@Transactional(readOnly = true)
public class ColdDataService {


    // ============================================================
    // REPOSITORIES
    // ============================================================

    private final ColdTruBlankCodeRepository coldTruBlankCodeRepository;

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

    public ColdDataService(
            ColdTruBlankCodeRepository coldTruBlankCodeRepository,
            ColdTruvishCodeGeneratorRepository
                    coldTruvishCodeGeneratorRepository,
            ColdWalletTransactionRepository
                    coldWalletTransactionRepository,
            ColdVoucherRedeemedLogRepository
                    coldVoucherRedeemedLogRepository,
            ColdCodeRedemptionHistoryRepository
                    coldCodeRedemptionHistoryRepository,
            ColdUserRedemptionRepository
                    coldUserRedemptionRepository
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
    // TRU BLANK CODE
    // ============================================================

    /**
     * Find one archived TruBlankCode record
     * using the original HOT record ID.
     */
    public Optional<ColdTruBlankCode> getTruBlankCode(
            Long hotRecordId
    ) {

        if (hotRecordId == null) {
            return Optional.empty();
        }

        return coldTruBlankCodeRepository
                .findByHotRecordId(hotRecordId);
    }


    /**
     * Find all archived TruBlankCode records
     * using the original HOT record ID.
     */
    public List<ColdTruBlankCode> getTruBlankCodes(
            Long hotRecordId
    ) {

        if (hotRecordId == null) {
            return Collections.emptyList();
        }

        return coldTruBlankCodeRepository
                .findAllByHotRecordId(hotRecordId);
    }


    /**
     * Find archived TruBlankCode records by code number.
     */
    public List<ColdTruBlankCode> getTruBlankCodesByCodeNumber(
            String codeNumber
    ) {

        if (codeNumber == null || codeNumber.isBlank()) {
            return Collections.emptyList();
        }

        return coldTruBlankCodeRepository
                .findAllByCodeNumber(codeNumber);
    }


    /**
     * Find archived TruBlankCode records by client ID.
     */
    public List<ColdTruBlankCode> getTruBlankCodesByClientId(
            Long clientId
    ) {

        if (clientId == null) {
            return Collections.emptyList();
        }

        return coldTruBlankCodeRepository
                .findAllByClientId(clientId);
    }


    /**
     * Find archived TruBlankCode records by status.
     */
    public List<ColdTruBlankCode> getTruBlankCodesByStatus(
            String status
    ) {

        if (status == null || status.isBlank()) {
            return Collections.emptyList();
        }

        return coldTruBlankCodeRepository
                .findAllByStatus(status);
    }


    // ============================================================
    // TRUVISH CODE GENERATOR
    // ============================================================

    /**
     * Find one archived TruvishCodeGenerator record
     * using the original HOT record ID.
     */
    public Optional<ColdTruvishCodeGenerator>
    getTruvishCodeGenerator(
            Long hotRecordId
    ) {

        if (hotRecordId == null) {
            return Optional.empty();
        }

        return coldTruvishCodeGeneratorRepository
                .findByHotRecordId(hotRecordId);
    }


    /**
     * Find all archived TruvishCodeGenerator records
     * using the original HOT record ID.
     */
    public List<ColdTruvishCodeGenerator>
    getTruvishCodeGenerators(
            Long hotRecordId
    ) {

        if (hotRecordId == null) {
            return Collections.emptyList();
        }

        return coldTruvishCodeGeneratorRepository
                .findAllByHotRecordId(hotRecordId);
    }


    /**
     * Find archived code-generator records by code number.
     */
    public List<ColdTruvishCodeGenerator>
    getTruvishCodeGeneratorsByCodeNumber(
            String codeNumber
    ) {

        if (codeNumber == null || codeNumber.isBlank()) {
            return Collections.emptyList();
        }

        return coldTruvishCodeGeneratorRepository
                .findAllByCodeNumber(codeNumber);
    }


    /**
     * Find archived code-generator records by client ID.
     */
    public List<ColdTruvishCodeGenerator>
    getTruvishCodeGeneratorsByClientId(
            Long clientId
    ) {

        if (clientId == null) {
            return Collections.emptyList();
        }

        return coldTruvishCodeGeneratorRepository
                .findAllByClientId(clientId);
    }


    /**
     * Find archived code-generator records by status.
     */
    public List<ColdTruvishCodeGenerator>
    getTruvishCodeGeneratorsByStatus(
            String status
    ) {

        if (status == null || status.isBlank()) {
            return Collections.emptyList();
        }

        return coldTruvishCodeGeneratorRepository
                .findAllByStatus(status);
    }


    /**
     * Find archived code-generator records by brand name.
     */
    public List<ColdTruvishCodeGenerator>
    getTruvishCodeGeneratorsByBrandName(
            String brandName
    ) {

        if (brandName == null || brandName.isBlank()) {
            return Collections.emptyList();
        }

        return coldTruvishCodeGeneratorRepository
                .findAllByBrandName(brandName);
    }


    /**
     * Find archived code-generator records by client brand.
     */
    public List<ColdTruvishCodeGenerator>
    getTruvishCodeGeneratorsByClientBrand(
            String clientBrand
    ) {

        if (clientBrand == null || clientBrand.isBlank()) {
            return Collections.emptyList();
        }

        return coldTruvishCodeGeneratorRepository
                .findAllByClientBrand(clientBrand);
    }


    /**
     * Find archived code-generator records by client category.
     */
    public List<ColdTruvishCodeGenerator>
    getTruvishCodeGeneratorsByClientCategory(
            String clientCategory
    ) {

        if (clientCategory == null || clientCategory.isBlank()) {
            return Collections.emptyList();
        }

        return coldTruvishCodeGeneratorRepository
                .findAllByClientCategory(clientCategory);
    }


    // ============================================================
    // WALLET TRANSACTION
    // ============================================================

    /**
     * Find one archived wallet transaction
     * using the original HOT record ID.
     */
    public Optional<ColdWalletTransaction>
    getWalletTransaction(
            Long hotRecordId
    ) {

        if (hotRecordId == null) {
            return Optional.empty();
        }

        return coldWalletTransactionRepository
                .findByHotRecordId(hotRecordId);
    }


    /**
     * Find all archived wallet transactions
     * using the original HOT record ID.
     */
    public List<ColdWalletTransaction>
    getWalletTransactions(
            Long hotRecordId
    ) {

        if (hotRecordId == null) {
            return Collections.emptyList();
        }

        return coldWalletTransactionRepository
                .findAllByHotRecordId(hotRecordId);
    }


    /**
     * Find archived wallet transactions by client ID.
     */
    public List<ColdWalletTransaction>
    getWalletTransactionsByClientId(
            Long clientId
    ) {

        if (clientId == null) {
            return Collections.emptyList();
        }

        return coldWalletTransactionRepository
                .findAllByClientId(clientId);
    }


    /**
     * Find archived wallet transactions by wallet ID.
     */
    public List<ColdWalletTransaction>
    getWalletTransactionsByWalletId(
            Long walletId
    ) {

        if (walletId == null) {
            return Collections.emptyList();
        }

        return coldWalletTransactionRepository
                .findAllByWalletId(walletId);
    }


    /**
     * Find archived wallet transactions by transaction type.
     */
    public List<ColdWalletTransaction>
    getWalletTransactionsByType(
            String transactionType
    ) {

        if (transactionType == null || transactionType.isBlank()) {
            return Collections.emptyList();
        }

        return coldWalletTransactionRepository
                .findAllByTransactionType(transactionType);
    }


    /**
     * Find archived wallet transactions by reference.
     */
    public List<ColdWalletTransaction>
    getWalletTransactionsByReference(
            String reference
    ) {

        if (reference == null || reference.isBlank()) {
            return Collections.emptyList();
        }

        return coldWalletTransactionRepository
                .findAllByReference(reference);
    }


    /**
     * Find archived wallet transactions by reference type.
     */
    public List<ColdWalletTransaction>
    getWalletTransactionsByReferenceType(
            String referenceType
    ) {

        if (referenceType == null || referenceType.isBlank()) {
            return Collections.emptyList();
        }

        return coldWalletTransactionRepository
                .findAllByReferenceType(referenceType);
    }


    /**
     * Find archived wallet transactions by status.
     */
    public List<ColdWalletTransaction>
    getWalletTransactionsByStatus(
            String status
    ) {

        if (status == null || status.isBlank()) {
            return Collections.emptyList();
        }

        return coldWalletTransactionRepository
                .findAllByStatus(status);
    }


    // ============================================================
    // VOUCHER REDEEMED LOG
    // ============================================================

    /**
     * Find one archived voucher redemption log
     * using the original HOT record ID.
     */
    public Optional<ColdVoucherRedeemedLog>
    getVoucherRedeemedLog(
            Long hotRecordId
    ) {

        if (hotRecordId == null) {
            return Optional.empty();
        }

        return coldVoucherRedeemedLogRepository
                .findByHotRecordId(hotRecordId);
    }


    /**
     * Find all archived voucher redemption logs
     * using the original HOT record ID.
     */
    public List<ColdVoucherRedeemedLog>
    getVoucherRedeemedLogs(
            Long hotRecordId
    ) {

        if (hotRecordId == null) {
            return Collections.emptyList();
        }

        return coldVoucherRedeemedLogRepository
                .findAllByHotRecordId(hotRecordId);
    }


    /**
     * Find archived voucher redemption logs by inventory ID.
     */
    public List<ColdVoucherRedeemedLog>
    getVoucherRedeemedLogsByInventoryId(
            Long inventoryId
    ) {

        if (inventoryId == null) {
            return Collections.emptyList();
        }

        return coldVoucherRedeemedLogRepository
                .findAllByInventoryId(inventoryId);
    }


    /**
     * Find archived voucher redemption logs by voucher code.
     */
    public List<ColdVoucherRedeemedLog>
    getVoucherRedeemedLogsByVoucherCode(
            String voucherCode
    ) {

        if (voucherCode == null || voucherCode.isBlank()) {
            return Collections.emptyList();
        }

        return coldVoucherRedeemedLogRepository
                .findAllByVoucherCode(voucherCode);
    }


    /**
     * Find archived voucher redemption logs by brand name.
     */
    public List<ColdVoucherRedeemedLog>
    getVoucherRedeemedLogsByBrandName(
            String brandName
    ) {

        if (brandName == null || brandName.isBlank()) {
            return Collections.emptyList();
        }

        return coldVoucherRedeemedLogRepository
                .findAllByBrandName(brandName);
    }


    // ============================================================
    // CODE REDEMPTION HISTORY
    // ============================================================

    /**
     * Find one archived code-redemption history record
     * using the original HOT record ID.
     */
    public Optional<ColdCodeRedemptionHistory>
    getCodeRedemptionHistory(
            Long hotRecordId
    ) {

        if (hotRecordId == null) {
            return Optional.empty();
        }

        return coldCodeRedemptionHistoryRepository
                .findByHotRecordId(hotRecordId);
    }


    /**
     * Find all archived code-redemption history records
     * using the original HOT record ID.
     */
    public List<ColdCodeRedemptionHistory>
    getCodeRedemptionHistories(
            Long hotRecordId
    ) {

        if (hotRecordId == null) {
            return Collections.emptyList();
        }

        return coldCodeRedemptionHistoryRepository
                .findAllByHotRecordId(hotRecordId);
    }


    /**
     * Find archived redemption history by TruVish code.
     */
    public List<ColdCodeRedemptionHistory>
    getCodeRedemptionHistoriesByTruvishCode(
            String truvishCode
    ) {

        if (truvishCode == null || truvishCode.isBlank()) {
            return Collections.emptyList();
        }

        return coldCodeRedemptionHistoryRepository
                .findAllByTruvishCode(truvishCode);
    }


    /**
     * Find archived redemption history by client ID.
     */
    public List<ColdCodeRedemptionHistory>
    getCodeRedemptionHistoriesByClientId(
            Long clientId
    ) {

        if (clientId == null) {
            return Collections.emptyList();
        }

        return coldCodeRedemptionHistoryRepository
                .findAllByClientId(clientId);
    }


    /**
     * Find archived redemption history by phone number.
     */
    public List<ColdCodeRedemptionHistory>
    getCodeRedemptionHistoriesByPhoneNumber(
            String phoneNumber
    ) {

        if (phoneNumber == null || phoneNumber.isBlank()) {
            return Collections.emptyList();
        }

        return coldCodeRedemptionHistoryRepository
                .findAllByPhoneNumber(phoneNumber);
    }


    /**
     * Find archived redemption history by brand name.
     */
    public List<ColdCodeRedemptionHistory>
    getCodeRedemptionHistoriesByBrandName(
            String brandName
    ) {

        if (brandName == null || brandName.isBlank()) {
            return Collections.emptyList();
        }

        return coldCodeRedemptionHistoryRepository
                .findAllByBrandName(brandName);
    }


    /**
     * Find archived redemption history by redemption status.
     */
    public List<ColdCodeRedemptionHistory>
    getCodeRedemptionHistoriesByRedeemStatus(
            String redeemStatus
    ) {

        if (redeemStatus == null || redeemStatus.isBlank()) {
            return Collections.emptyList();
        }

        return coldCodeRedemptionHistoryRepository
                .findAllByRedeemStatus(redeemStatus);
    }


    // ============================================================
    // USER REDEMPTION
    // ============================================================

    /**
     * Find one archived user-redemption record
     * using the original HOT record ID.
     */
    public Optional<ColdUserRedemption>
    getUserRedemption(
            Long hotRecordId
    ) {

        if (hotRecordId == null) {
            return Optional.empty();
        }

        return coldUserRedemptionRepository
                .findByHotRecordId(hotRecordId);
    }


    /**
     * Find all archived user-redemption records
     * using the original HOT record ID.
     */
    public List<ColdUserRedemption>
    getUserRedemptions(
            Long hotRecordId
    ) {

        if (hotRecordId == null) {
            return Collections.emptyList();
        }

        return coldUserRedemptionRepository
                .findAllByHotRecordId(hotRecordId);
    }


    /**
     * Find one COLD user-redemption record by its
     * COLD database primary key.
     *
     * IMPORTANT:
     *
     * userId here is the @Id of ColdUserRedemption.
     * It is NOT the original HOT record ID.
     *
     * For the original HOT record ID, use:
     *
     * getUserRedemptions(hotRecordId)
     */
    public List<ColdUserRedemption>
    getUserRedemptionsByUserId(
            Long userId
    ) {

        if (userId == null) {
            return Collections.emptyList();
        }

        Optional<ColdUserRedemption> result =
                coldUserRedemptionRepository.findById(userId);

        return result
                .map(List::of)
                .orElseGet(Collections::emptyList);
    }


    /**
     * Find archived user-redemption records by client ID.
     */
    public List<ColdUserRedemption>
    getUserRedemptionsByClientId(
            Long clientId
    ) {

        if (clientId == null) {
            return Collections.emptyList();
        }

        return coldUserRedemptionRepository
                .findAllByClientId(clientId);
    }


    /**
     * Find archived user-redemption records by
     * user's TruVish code.
     */
    public List<ColdUserRedemption>
    getUserRedemptionsByCodeNumber(
            String codeNumber
    ) {

        if (codeNumber == null || codeNumber.isBlank()) {
            return Collections.emptyList();
        }

        return coldUserRedemptionRepository
                .findAllByUserTruvishCode(codeNumber);
    }
}