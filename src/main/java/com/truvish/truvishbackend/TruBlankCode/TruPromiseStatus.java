package com.truvish.truvishbackend.TruBlankCode;

public enum TruPromiseStatus {

    // =========================================================
    // TRUPROMISE CREATED
    // =========================================================
    PROMISED,

    // =========================================================
    // TRUPROMISE PAID AFTER REDEMPTION
    // =========================================================
    PAID,

    // =========================================================
    // TRUPROMISE RETURNED TO CLIENT WALLET
    // =========================================================
    BackToWallet
}