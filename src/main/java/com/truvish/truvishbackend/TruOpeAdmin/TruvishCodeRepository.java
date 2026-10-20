package com.truvish.truvishbackend.TruOpeAdmin;

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

public interface TruvishCodeRepository
        extends JpaRepository<TruvishCode, Long> {


    // =========================================================
    // FIND CODE
    // =========================================================

    Optional<TruvishCode> findByTruvishIdCodeNumber(
            String code
    );


    // =========================================================
    // CHECK DUPLICATE CODE
    // =========================================================

    boolean existsByTruvishIdCodeNumber(
            String code
    );


    // =========================================================
    // SERIAL NUMBER
    // =========================================================

    Optional<TruvishCode> findBySerialNumber(
            String serialNumber
    );

    boolean existsBySerialNumber(
            String serialNumber
    );


    // =========================================================
    // REFERENCE NUMBER
    // =========================================================

    Optional<TruvishCode> findByReferenceNumber(
            String referenceNumber
    );

    boolean existsByReferenceNumber(
            String referenceNumber
    );


    // =========================================================
    // LOCKED FIND BY ID
    // =========================================================
    //
    // Used for financial / lifecycle operations:
    //
    // - TruPromise PAID
    // - TruPromise REVERSED
    // - Admin deactivation
    // - Expiry reversal
    //
    // PESSIMISTIC_WRITE prevents two transactions from
    // processing the same voucher at the same time.
    //
    // Example:
    //
    // Transaction A:
    // PROMISED -> REVERSED
    //
    // Transaction B:
    // waits for Transaction A
    // then reads REVERSED
    // and MUST NOT credit again.
    // =========================================================

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("""
            select t
            from TruvishCode t
            where t.truvishId = :codeId
            """)
    Optional<TruvishCode> findByIdForUpdate(
            @Param("codeId")
            Long codeId
    );


    // =========================================================
    // LOCKED FIND BY CODE
    // =========================================================

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("""
            select t
            from TruvishCode t
            where t.truvishIdCodeNumber = :code
            """)
    Optional<TruvishCode> findByTruvishIdCodeNumberForUpdate(
            @Param("code")
            String code
    );


    // =========================================================
    // LOCKED FIND BY SERIAL NUMBER
    // =========================================================

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("""
            select t
            from TruvishCode t
            where t.serialNumber = :serialNumber
            """)
    Optional<TruvishCode> findBySerialNumberForUpdate(
            @Param("serialNumber")
            String serialNumber
    );


    // =========================================================
    // LOCKED FIND BY REFERENCE NUMBER
    // =========================================================

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("""
            select t
            from TruvishCode t
            where t.referenceNumber = :referenceNumber
            """)
    Optional<TruvishCode> findByReferenceNumberForUpdate(
            @Param("referenceNumber")
            String referenceNumber
    );


    // =========================================================
    // CLIENT HISTORY
    // =========================================================

    Page<TruvishCode>
    findByClientNameOrderByTruvishCodeTimestampDesc(
            String clientName,
            Pageable pageable
    );


    List<TruvishCode>
    findByClientNameIgnoreCaseOrderByTruvishCodeTimestampDesc(
            String clientName
    );


    // =========================================================
    // CLIENT CODE REPORT
    // =========================================================

    List<TruvishCode>
    findByClientIdOrderByTruvishCodeTimestampDesc(
            Long clientId
    );


    // =========================================================
    // EMPTY ROW
    // =========================================================

    @Query(
            value = """
                    SELECT *
                    FROM truvish_code_generator
                    WHERE client_name IS NULL
                      AND client_img IS NULL
                      AND truvish_code_value IS NULL
                      AND client_theme IS NULL
                      AND client_theme_img IS NULL
                      AND client_brand IS NULL
                      AND client_category IS NULL
                      AND validity IS NULL
                    LIMIT 1
                    """,
            nativeQuery = true
    )
    Optional<TruvishCode> findEmptyRow();


    // =========================================================
    // TOTAL ISSUED CODES BY CLIENT
    // =========================================================

    long countByClientId(
            Long clientId
    );


    // =========================================================
    // TOTAL ISSUED VALUE BY CLIENT
    // =========================================================

    @Query("""
            select coalesce(
                sum(t.originalCodeValue),
                0
            )
            from TruvishCode t
            where t.clientId = :clientId
            """)
    Long sumDistributedValueByClientId(
            @Param("clientId")
            Long clientId
    );


    // =========================================================
    // TOTAL DIGITAL VALUE BY CLIENT
    // =========================================================

    @Query("""
            select coalesce(
                sum(t.originalCodeValue),
                0
            )
            from TruvishCode t
            where t.clientId = :clientId
            """)
    BigDecimal sumTruvishCodeValueByClientId(
            @Param("clientId")
            Long clientId
    );


    // =========================================================
    // COUNT BY CLIENT + STATUS
    // =========================================================

    long countByClientIdAndTruvishCodeStatus(
            Long clientId,
            VoucherStatus status
    );


    // =========================================================
    // VALUE BY CLIENT + STATUS
    // =========================================================

    @Query("""
            select coalesce(
                sum(t.originalCodeValue),
                0
            )
            from TruvishCode t
            where t.clientId = :clientId
              and t.truvishCodeStatus = :status
            """)
    BigDecimal sumTruvishCodeValueByClientIdAndStatus(
            @Param("clientId")
            Long clientId,

            @Param("status")
            VoucherStatus status
    );


    // =========================================================
    // ORIGINAL VALUE BY CLIENT + STATUS
    // =========================================================

    @Query("""
            select coalesce(
                sum(t.originalCodeValue),
                0
            )
            from TruvishCode t
            where t.clientId = :clientId
              and t.truvishCodeStatus = :status
            """)
    BigDecimal sumOriginalCodeValueByClientIdAndStatus(
            @Param("clientId")
            Long clientId,

            @Param("status")
            VoucherStatus status
    );


    // =========================================================
    // ACTIVE COUNT
    // =========================================================

    @Query("""
            select count(t)
            from TruvishCode t
            where t.clientId = :clientId
              and t.truvishCodeStatus = :status
            """)
    Long countActiveByClientIdAndStatus(
            @Param("clientId")
            Long clientId,

            @Param("status")
            VoucherStatus status
    );


    // =========================================================
    // DISTRIBUTED CODES BY CLIENT
    // =========================================================

    @Query("""
            select count(t)
            from TruvishCode t
            where t.clientId = :clientId
            """)
    Long countDistributedCodesByClientId(
            @Param("clientId")
            Long clientId
    );


    // =========================================================
    // TOTAL DIGITAL CODES
    // =========================================================

    @Query("""
            select count(t)
            from TruvishCode t
            where t.clientId is not null
            """)
    Long countAllDistributedCodes();


    // =========================================================
    // TOTAL DIGITAL DISTRIBUTED VALUE
    // =========================================================

    @Query("""
            select coalesce(
                sum(t.originalCodeValue),
                0
            )
            from TruvishCode t
            where t.clientId is not null
            """)
    Long sumAllDistributedValue();


    // =========================================================
    // =========================================================
    // TRUPROMISE
    // =========================================================
    // =========================================================


    // =========================================================
    // FIND BY TRUPROMISE STATUS
    // =========================================================

    List<TruvishCode>
    findByTrupromiseStatus(
            TruPromiseStatus status
    );


    // =========================================================
    // FIND PROMISED CODES
    // =========================================================

    default List<TruvishCode> findPromisedCodes() {

        return findByTrupromiseStatus(
                TruPromiseStatus.PROMISED
        );
    }


    // =========================================================
    // FIND PAID CODES
    // =========================================================

    default List<TruvishCode> findPaidPromiseCodes() {

        return findByTrupromiseStatus(
                TruPromiseStatus.PAID
        );
    }


    // =========================================================
    // FIND REVERSED CODES
    // =========================================================
    //
    // REVERSED means:
    //
    // Voucher expired / cancelled / administratively closed
    // and only the TruPromise amount was returned to wallet.
    //
    // The original voucher value is NOT returned.
    // =========================================================

    default List<TruvishCode> findReversedPromiseCodes() {

        return findByTrupromiseStatus(
                TruPromiseStatus.REVERSED
        );
    }


    // =========================================================
    // TOTAL TRUPROMISE AMOUNT
    // =========================================================

    @Query("""
            select coalesce(
                sum(t.trupromiseAmount),
                0
            )
            from TruvishCode t
            """)
    BigDecimal sumTotalTruPromiseAmount();


    // =========================================================
    // TRUPROMISE AMOUNT BY STATUS
    // =========================================================

    @Query("""
            select coalesce(
                sum(t.trupromiseAmount),
                0
            )
            from TruvishCode t
            where t.trupromiseStatus = :status
            """)
    BigDecimal sumTruPromiseAmountByStatus(
            @Param("status")
            TruPromiseStatus status
    );


    // =========================================================
    // TOTAL PAID TRUPROMISE
    // =========================================================

    default BigDecimal sumPaidTruPromiseAmount() {

        return sumTruPromiseAmountByStatus(
                TruPromiseStatus.PAID
        );
    }


    // =========================================================
    // TOTAL REVERSED TRUPROMISE
    // =========================================================

    default BigDecimal sumReversedTruPromiseAmount() {

        return sumTruPromiseAmountByStatus(
                TruPromiseStatus.REVERSED
        );
    }


    // =========================================================
    // TOTAL PENDING TRUPROMISE
    // =========================================================

    default BigDecimal sumPendingTruPromiseAmount() {

        return sumTruPromiseAmountByStatus(
                TruPromiseStatus.PROMISED
        );
    }


    // =========================================================
    // TRUPROMISE COUNT BY STATUS
    // =========================================================

    long countByTrupromiseStatus(
            TruPromiseStatus status
    );


    // =========================================================
    // CLIENT + TRUPROMISE STATUS
    // =========================================================

    long countByClientIdAndTrupromiseStatus(
            Long clientId,
            TruPromiseStatus status
    );


    // =========================================================
    // CLIENT TRUPROMISE AMOUNT BY STATUS
    // =========================================================

    @Query("""
            select coalesce(
                sum(t.trupromiseAmount),
                0
            )
            from TruvishCode t
            where t.clientId = :clientId
              and t.trupromiseStatus = :status
            """)
    BigDecimal sumTruPromiseAmountByClientIdAndStatus(
            @Param("clientId")
            Long clientId,

            @Param("status")
            TruPromiseStatus status
    );


    // =========================================================
    // CLIENT TOTAL TRUPROMISE
    // =========================================================

    @Query("""
            select coalesce(
                sum(t.trupromiseAmount),
                0
            )
            from TruvishCode t
            where t.clientId = :clientId
            """)
    BigDecimal sumTotalTruPromiseAmountByClientId(
            @Param("clientId")
            Long clientId
    );


    // =========================================================
    // PENDING TRUPROMISE BY CLIENT
    // =========================================================

    default BigDecimal sumPendingTruPromiseAmountByClientId(
            Long clientId
    ) {

        return sumTruPromiseAmountByClientIdAndStatus(
                clientId,
                TruPromiseStatus.PROMISED
        );
    }


    // =========================================================
    // PAID TRUPROMISE BY CLIENT
    // =========================================================

    default BigDecimal sumPaidTruPromiseAmountByClientId(
            Long clientId
    ) {

        return sumTruPromiseAmountByClientIdAndStatus(
                clientId,
                TruPromiseStatus.PAID
        );
    }


    // =========================================================
    // REVERSED TRUPROMISE BY CLIENT
    // =========================================================

    default BigDecimal sumReversedTruPromiseAmountByClientId(
            Long clientId
    ) {

        return sumTruPromiseAmountByClientIdAndStatus(
                clientId,
                TruPromiseStatus.REVERSED
        );
    }


    // =========================================================
    // TRUPROMISE CREATED BEFORE
    // =========================================================

    List<TruvishCode>
    findByTrupromiseCreatedAtBefore(
            LocalDateTime cutoff
    );


    // =========================================================
    // TRUPROMISE CREATED BEFORE + STATUS
    // =========================================================

    List<TruvishCode>
    findByTrupromiseCreatedAtBeforeAndTrupromiseStatus(
            LocalDateTime cutoff,
            TruPromiseStatus status
    );


    // =========================================================
    // PENDING OLD TRUPROMISE
    // =========================================================

    default List<TruvishCode> findPendingTruPromisesBefore(
            LocalDateTime cutoff
    ) {

        return findByTrupromiseCreatedAtBeforeAndTrupromiseStatus(
                cutoff,
                TruPromiseStatus.PROMISED
        );
    }


    // =========================================================
    // PAID DATE
    // =========================================================

    List<TruvishCode>
    findByTrupromisePaidAtBetween(
            LocalDateTime start,
            LocalDateTime end
    );


    // =========================================================
    // REVERSED DATE
    // =========================================================

    List<TruvishCode>
    findByTrupromiseReversedAtBetween(
            LocalDateTime start,
            LocalDateTime end
    );


    // =========================================================
    // HOT -> COLD ARCHIVE
    // =========================================================
    //
    // Finds only old terminal voucher records.
    //
    // ACTIVE / INACTIVE are NOT returned.
    //
    // Terminal statuses:
    //
    // REDEEMED
    // EXPIRED
    // CANCELLED
    // =========================================================

    List<TruvishCode>
    findByTruvishCodeTimestampBeforeAndTruvishCodeStatusIn(
            LocalDateTime cutoff,
            List<VoucherStatus> statuses
    );


    // =========================================================
    // OLD REDEEMED CODES
    // =========================================================

    default List<TruvishCode> findRedeemedCodesBefore(
            LocalDateTime cutoff
    ) {

        return findByTruvishCodeTimestampBeforeAndTruvishCodeStatusIn(
                cutoff,
                List.of(
                        VoucherStatus.REDEEMED
                )
        );
    }


    // =========================================================
    // OLD EXPIRED CODES
    // =========================================================

    default List<TruvishCode> findExpiredCodesBefore(
            LocalDateTime cutoff
    ) {

        return findByTruvishCodeTimestampBeforeAndTruvishCodeStatusIn(
                cutoff,
                List.of(
                        VoucherStatus.EXPIRED
                )
        );
    }


    // =========================================================
    // OLD CANCELLED CODES
    // =========================================================

    default List<TruvishCode> findCancelledCodesBefore(
            LocalDateTime cutoff
    ) {

        return findByTruvishCodeTimestampBeforeAndTruvishCodeStatusIn(
                cutoff,
                List.of(
                        VoucherStatus.CANCELLED
                )
        );
    }


    // =========================================================
    // ALL TERMINAL OLD CODES
    // =========================================================
    //
    // Main method for cold archive processing.
    //
    // ACTIVE / INACTIVE codes are never archived here.
    // =========================================================

    default List<TruvishCode> findArchivableCodesBefore(
            LocalDateTime cutoff
    ) {

        return findByTruvishCodeTimestampBeforeAndTruvishCodeStatusIn(
                cutoff,
                List.of(
                        VoucherStatus.REDEEMED,
                        VoucherStatus.EXPIRED,
                        VoucherStatus.CANCELLED
                )
        );
    }


    // Corporate TruPromise Cashback: count the saved promise only after it is
    // actually returned to the wallet. Activation alone does not count.
    @Query("""
        SELECT COALESCE(SUM(t.trupromiseAmount), 0)
        FROM TruvishCode t
        WHERE t.clientId = :clientId
          AND t.trupromiseStatus = com.truvish.truvishbackend.TruOpeAdmin.TruPromiseStatus.REVERSED
    """)
    BigDecimal sumReturnedTruPromiseByClientId(@Param("clientId") Long clientId);

    @Query("""
        SELECT COALESCE(SUM(t.trupromiseAmount), 0)
        FROM TruvishCode t
        WHERE t.clientId = :clientId
          AND t.trupromiseStatus = com.truvish.truvishbackend.TruOpeAdmin.TruPromiseStatus.REVERSED
          AND t.trupromiseReversedAt >= :monthStart
          AND t.trupromiseReversedAt < :nextMonthStart
    """)
    BigDecimal sumReturnedTruPromiseByClientIdBetween(
            @Param("clientId") Long clientId,
            @Param("monthStart") LocalDateTime monthStart,
            @Param("nextMonthStart") LocalDateTime nextMonthStart
    );

}
