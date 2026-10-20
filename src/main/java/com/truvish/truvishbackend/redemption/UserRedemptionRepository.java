
package com.truvish.truvishbackend.redemption;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

/**
 * HOT DATABASE repository for user redemption records.
 *
 * This repository handles current/operational redemption
 * data stored in the HOT PostgreSQL database.
 *
 * Historical/archived redemption records will be handled
 * through a separate COLD database repository.
 */
public interface UserRedemptionRepository
        extends JpaRepository<UserRedemption, Long> {


    // =========================================================
    // PHONE
    // =========================================================

    List<UserRedemption> findByUserPhoneNumber(
            String userPhoneNumber
    );


    // =========================================================
    // PHONE - NEWEST FIRST
    // =========================================================

    List<UserRedemption> findByUserPhoneNumberOrderByUserBrandTimeTempDesc(
            String userPhoneNumber
    );


    // =========================================================
    // CODE - NEWEST FIRST
    // =========================================================

    List<UserRedemption> findByUserTruvishCodeOrderByUserBrandTimeTempDesc(
            String userTruvishCode
    );


    // =========================================================
    // PHONE OR CODE - NEWEST FIRST
    // =========================================================

    List<UserRedemption>
    findByUserPhoneNumberOrUserTruvishCodeOrderByUserBrandTimeTempDesc(
            String userPhoneNumber,
            String userTruvishCode
    );


    // =========================================================
    // CODE - OLDEST FIRST
    //
    // Used by TruvishCodeService history.
    // =========================================================

    List<UserRedemption> findByUserTruvishCodeOrderByUserBrandTimeTempAsc(
            String userTruvishCode
    );


    // =========================================================
    // CLIENT ID - NEWEST FIRST
    // =========================================================

    List<UserRedemption> findByClientIdOrderByUserBrandTimeTempDesc(
            Long clientId
    );


    // =========================================================
    // COUNT BY CLIENT
    //
    // Counts all redemption rows belonging to the client.
    // =========================================================

    long countByClientId(
            Long clientId
    );


    // =========================================================
    // COUNT DISTINCT USERS - GLOBAL
    //
    // userId is treated as the unique user identifier.
    // =========================================================

    @Query("""
            SELECT COUNT(DISTINCT u.userId)
            FROM UserRedemption u
            WHERE u.userId IS NOT NULL
            """)
    Long countDistinctUsers();


    // =========================================================
    // COUNT DISTINCT USERS BY CLIENT
    // =========================================================

    @Query("""
            SELECT COUNT(DISTINCT u.userId)
            FROM UserRedemption u
            WHERE u.clientId = :clientId
              AND u.userId IS NOT NULL
            """)
    Long countDistinctUsersByClientId(
            @Param("clientId") Long clientId
    );


    // =========================================================
    // COUNT DISTINCT REDEEMED CODES BY CLIENT
    //
    // If the same code appears multiple times in history,
    // it is counted only once.
    // =========================================================

    @Query("""
            SELECT COUNT(DISTINCT u.userTruvishCode)
            FROM UserRedemption u
            WHERE u.clientId = :clientId
              AND u.userTruvishCode IS NOT NULL
            """)
    Long countDistinctRedeemedCodesByClientId(
            @Param("clientId") Long clientId
    );


    // =========================================================
    // REDEEMED AMOUNT BY CLIENT
    // =========================================================

    @Query("""
            SELECT COALESCE(SUM(u.userBrandValue), 0)
            FROM UserRedemption u
            WHERE u.clientId = :clientId
            """)
    Long sumRedeemedAmountByClientId(
            @Param("clientId") Long clientId
    );


    // =========================================================
    // TOTAL REDEEMED AMOUNT - ALL CLIENTS
    // =========================================================

    @Query("""
            SELECT COALESCE(SUM(u.userBrandValue), 0)
            FROM UserRedemption u
            """)
    Long sumTotalRedeemedAmount();


    // =========================================================
    // COUNT REDEMPTIONS BY CLIENT
    //
    // Explicit query version.
    // Kept for existing service compatibility.
    // =========================================================

    @Query("""
            SELECT COUNT(u)
            FROM UserRedemption u
            WHERE u.clientId = :clientId
            """)
    Long countRedemptionsByClientId(
            @Param("clientId") Long clientId
    );
}
