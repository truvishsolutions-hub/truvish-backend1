package com.truvish.truvishbackend.voucherinventory;

import org.springframework.data.jpa.repository.JpaRepository;

/**
 * HOT DATABASE repository for voucher redemption logs.
 *
 * This repository handles current/operational redemption
 * log records stored in the HOT PostgreSQL database.
 *
 * Historical/archived redemption logs must be accessed
 * through the COLD database repository.
 */
public interface VoucherRedeemedLogRepository
        extends JpaRepository<VoucherRedeemedLog, Long> {
}
