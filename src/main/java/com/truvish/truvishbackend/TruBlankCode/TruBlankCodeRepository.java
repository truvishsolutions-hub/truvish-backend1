package com.truvish.truvishbackend.TruBlankCode;

import jakarta.persistence.LockModeType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * HOT DATABASE repository for truvish_blank_code.
 *
 * This repository works only with the current/active
 * TruBlankCode records stored in the HOT database.
 *
 * Historical/archived records must be accessed through
 * the Cold database repository.
 */
public interface TruBlankCodeRepository
        extends JpaRepository<TruBlankCode, Long> {

    // =========================================================
    // GET ALL - PAGED
    // =========================================================

    Page<TruBlankCode> findAllByOrderByCreatedAtDesc(
            Pageable pageable
    );

    // =========================================================
    // GET BY CLIENT
    // =========================================================

    List<TruBlankCode> findByClientIdOrderByCreatedAtDesc(
            Long clientId
    );

    // =========================================================
    // GET BY CLIENT - PAGED
    // =========================================================

    Page<TruBlankCode> findByClientIdOrderByCreatedAtDesc(
            Long clientId,
            Pageable pageable
    );

    // =========================================================
    // GET BY ID
    // =========================================================

    Optional<TruBlankCode> findById(
            Long id
    );

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select t from TruBlankCode t where t.id = :codeId")
    Optional<TruBlankCode> findByIdForUpdate(@Param("codeId") Long codeId);

    // =========================================================
    // GET BY SERIAL NUMBER
    // =========================================================

    Optional<TruBlankCode> findBySerialNumber(
            String serialNumber
    );

    // =========================================================
    // GET BY REFERENCE NUMBER
    // =========================================================

    Optional<TruBlankCode> findByReferenceNumber(
            String referenceNumber
    );

    // =========================================================
    // GET BY CODE NUMBER
    // =========================================================

    Optional<TruBlankCode> findByCodeNumber(
            String codeNumber
    );

    // =========================================================
    // GET LATEST GENERATION NUMBER
    // =========================================================

    Optional<TruBlankCode> findTopByOrderByGenerationNumberDesc();

    // =========================================================
    // GET LATEST SERIAL NUMBER
    // =========================================================

    Optional<TruBlankCode> findTopByOrderBySerialNumberDesc();

    // =========================================================
    // UNIQUE CHECK - CODE NUMBER
    // =========================================================

    boolean existsByCodeNumber(
            String codeNumber
    );

    // =========================================================
    // UNIQUE CHECK - REFERENCE NUMBER
    // =========================================================

    boolean existsByReferenceNumber(
            String referenceNumber
    );

    // =========================================================
    // GET BY STATUS - PAGED
    // =========================================================

    Page<TruBlankCode> findByStatusOrderByCreatedAtDesc(
            TruBlankCodeStatus status,
            Pageable pageable
    );

    // =========================================================
    // GLOBAL COUNT BY STATUS
    // =========================================================

    long countByStatus(
            TruBlankCodeStatus status
    );

    // =========================================================
    // TOTAL COUNT BY CLIENT
    // =========================================================

    long countByClientId(
            Long clientId
    );

    // =========================================================
    // CLIENT + STATUS COUNT
    // =========================================================

    long countByClientIdAndStatus(
            Long clientId,
            TruBlankCodeStatus status
    );

    // =========================================================
    // CLIENT ACTIVE COUNT
    //
    // Active status AND:
    // expiryDate is NULL
    // OR expiryDate is still in the future
    // =========================================================

    @Query("""
        SELECT COUNT(t)
        FROM TruBlankCode t
        WHERE t.clientId = :clientId
          AND t.status = :activeStatus
          AND (
                t.expiryDate IS NULL
                OR t.expiryDate > :now
          )
    """)
    long countClientActiveCodes(
            @Param("clientId") Long clientId,
            @Param("activeStatus") TruBlankCodeStatus activeStatus,
            @Param("now") LocalDateTime now
    );

    // =========================================================
    // CLIENT EXPIRED COUNT
    //
    // EXPIRED status
    // OR
    // ACTIVE status but expiryDate has been reached
    // =========================================================

    @Query("""
        SELECT COUNT(t)
        FROM TruBlankCode t
        WHERE t.clientId = :clientId
          AND (
                t.status = :expiredStatus
                OR (
                    t.status = :activeStatus
                    AND t.expiryDate IS NOT NULL
                    AND t.expiryDate <= :now
                )
          )
    """)
    long countClientExpiredCodes(
            @Param("clientId") Long clientId,
            @Param("expiredStatus") TruBlankCodeStatus expiredStatus,
            @Param("activeStatus") TruBlankCodeStatus activeStatus,
            @Param("now") LocalDateTime now
    );

    // =========================================================
    // FIND ACTIVE CODES WHOSE EXPIRY DATE HAS PASSED
    //
    // Used by refreshExpiredCodes()
    //
    // These records are still ACTIVE in the database,
    // but their expiry date has already been reached.
    // =========================================================

    @Query("""
        SELECT t
        FROM TruBlankCode t
        WHERE t.status = :activeStatus
          AND t.expiryDate IS NOT NULL
          AND t.expiryDate <= :now
    """)
    List<TruBlankCode> findExpiredActiveCodes(
            @Param("activeStatus") TruBlankCodeStatus activeStatus,
            @Param("now") LocalDateTime now
    );

    // =========================================================
    // CLIENT - TOTAL DENOMINATION
    //
    // Total value of all blank codes belonging to the client.
    // =========================================================

    @Query("""
        SELECT COALESCE(
            SUM(t.denomination),
            0
        )
        FROM TruBlankCode t
        WHERE t.clientId = :clientId
    """)
    BigDecimal sumDenominationByClientId(
            @Param("clientId") Long clientId
    );

    // =========================================================
    // CLIENT - DENOMINATION BY STATUS
    // =========================================================

    @Query("""
        SELECT COALESCE(
            SUM(t.denomination),
            0
        )
        FROM TruBlankCode t
        WHERE t.clientId = :clientId
          AND t.status = :status
    """)
    BigDecimal sumDenominationByClientIdAndStatus(
            @Param("clientId") Long clientId,
            @Param("status") TruBlankCodeStatus status
    );

    // =========================================================
    // CLIENT - ACTIVE VALUE
    //
    // Active status AND:
    // expiryDate is NULL
    // OR expiryDate is still in the future
    // =========================================================

    @Query("""
        SELECT COALESCE(
            SUM(t.denomination),
            0
        )
        FROM TruBlankCode t
        WHERE t.clientId = :clientId
          AND t.status = :activeStatus
          AND (
                t.expiryDate IS NULL
                OR t.expiryDate > :now
          )
    """)
    BigDecimal sumClientActiveValue(
            @Param("clientId") Long clientId,
            @Param("activeStatus") TruBlankCodeStatus activeStatus,
            @Param("now") LocalDateTime now
    );

    // =========================================================
    // CLIENT - EXPIRED VALUE
    //
    // EXPIRED status
    // OR
    // ACTIVE status but expiryDate has been reached
    // =========================================================

    @Query("""
        SELECT COALESCE(
            SUM(t.denomination),
            0
        )
        FROM TruBlankCode t
        WHERE t.clientId = :clientId
          AND (
                t.status = :expiredStatus
                OR (
                    t.status = :activeStatus
                    AND t.expiryDate IS NOT NULL
                    AND t.expiryDate <= :now
                )
          )
    """)
    BigDecimal sumClientExpiredValue(
            @Param("clientId") Long clientId,
            @Param("expiredStatus") TruBlankCodeStatus expiredStatus,
            @Param("activeStatus") TruBlankCodeStatus activeStatus,
            @Param("now") LocalDateTime now
    );


    // Corporate TruPromise Cashback: count the exact saved promise only after
    // it has actually been returned to the wallet. Activation alone does not count.
    @Query("""
        SELECT COALESCE(SUM(t.trupromiseAmount), 0)
        FROM TruBlankCode t
        WHERE t.clientId = :clientId
          AND t.trupromiseStatus = com.truvish.truvishbackend.TruBlankCode.TruPromiseStatus.BackToWallet
    """)
    BigDecimal sumReturnedTruPromiseByClientId(@Param("clientId") Long clientId);

    @Query("""
        SELECT COALESCE(SUM(t.trupromiseAmount), 0)
        FROM TruBlankCode t
        WHERE t.clientId = :clientId
          AND t.trupromiseStatus = com.truvish.truvishbackend.TruBlankCode.TruPromiseStatus.BackToWallet
          AND t.trupromiseReversedAt >= :monthStart
          AND t.trupromiseReversedAt < :nextMonthStart
    """)
    BigDecimal sumReturnedTruPromiseByClientIdBetween(
            @Param("clientId") Long clientId,
            @Param("monthStart") LocalDateTime monthStart,
            @Param("nextMonthStart") LocalDateTime nextMonthStart
    );

}
