package com.truvish.truvishbackend.wallet;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;
import java.time.LocalDateTime;


public interface WalletTransactionRepository
        extends JpaRepository<WalletTransaction, Long> {




    Page<WalletTransaction> findByClient_IdOrderByTxnDateTimeDesc(
            Long clientId,
            Pageable pageable
    );




    @Query("""
        SELECT COALESCE(
            SUM(w.amount),
            0
        )
        FROM WalletTransaction w
        WHERE w.client.id = :clientId
          AND w.type = com.truvish.truvishbackend.wallet.TxnType.CREDIT
          AND w.status = com.truvish.truvishbackend.wallet.TxnStatus.SUCCESS
    """)
    BigDecimal sumTotalLoadByClientId(
            @Param("clientId") Long clientId
    );




    @Query("""
        SELECT COALESCE(
            SUM(w.amount),
            0
        )
        FROM WalletTransaction w
        WHERE w.type = com.truvish.truvishbackend.wallet.TxnType.CREDIT
          AND w.status = com.truvish.truvishbackend.wallet.TxnStatus.SUCCESS
    """)
    BigDecimal sumAllLoadedValue();


    // TRUCARD ORDER TRANSACTION CHECK

    boolean existsByReferenceTypeAndReferenceId(
            String referenceType,
            String referenceId
    );

    java.util.Optional<WalletTransaction> findFirstByReferenceTypeAndReferenceId(
            String referenceType,
            String referenceId
    );


    // =========================================================
    // TRUPROMISE CASHBACK
    //
    // Gets the actual welcome/gift money credited when the
    // client account was created.
    //
    // Existing client-create flow stores:
    //
    // type          = CREDIT
    // status        = SUCCESS
    // description   = Truvish Gifts
    // referenceType = SYSTEM
    // referenceId   = WELCOME
    // =========================================================

    @Query("""
        SELECT COALESCE(
            SUM(w.amount),
            0
        )
        FROM WalletTransaction w
        WHERE w.client.id = :clientId
          AND w.type = com.truvish.truvishbackend.wallet.TxnType.CREDIT
          AND w.status = com.truvish.truvishbackend.wallet.TxnStatus.SUCCESS
          AND w.description = 'Truvish Gifts'
          AND w.referenceType = 'SYSTEM'
          AND w.referenceId = 'WELCOME'
    """)
    BigDecimal sumWelcomeGiftByClientId(
            @Param("clientId") Long clientId
    );


    // =========================================================
    // TRUPROMISE CASHBACK - CURRENT MONTH
    //
    // Same welcome/gift transaction, but only transactions
    // created during the current month.
    // =========================================================

    @Query("""
        SELECT COALESCE(
            SUM(w.amount),
            0
        )
        FROM WalletTransaction w
        WHERE w.client.id = :clientId
          AND w.type = com.truvish.truvishbackend.wallet.TxnType.CREDIT
          AND w.status = com.truvish.truvishbackend.wallet.TxnStatus.SUCCESS
          AND w.description = 'Truvish Gifts'
          AND w.referenceType = 'SYSTEM'
          AND w.referenceId = 'WELCOME'
          AND w.txnDateTime >= :monthStart
          AND w.txnDateTime < :nextMonthStart
    """)
    BigDecimal sumWelcomeGiftByClientIdBetween(
            @Param("clientId") Long clientId,
            @Param("monthStart") LocalDateTime monthStart,
            @Param("nextMonthStart") LocalDateTime nextMonthStart
    );
}