package com.truvish.truvishbackend.redemption;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

/**
 * HOT DATABASE repository for code redemption history.
 *
 * This repository handles current redemption-history records
 * stored in the HOT PostgreSQL database.
 *
 * Historical/archived redemption records must be accessed
 * through the COLD database repository.
 */
public interface CodeRedemptionHistoryRepository
        extends JpaRepository<CodeRedemptionHistory, Long> {


    // =========================================================
    // GET REDEMPTION HISTORY BY TRUVISH CODE
    //
    // Latest redemption first.
    // =========================================================

    List<CodeRedemptionHistory> findByTruvishCodeOrderByRedeemedAtDesc(
            String truvishCode
    );


    // =========================================================
    // GET REDEMPTION HISTORY BY PHONE NUMBER
    //
    // Latest redemption first.
    // =========================================================

    List<CodeRedemptionHistory> findByPhoneNumberOrderByRedeemedAtDesc(
            String phoneNumber
    );
}
