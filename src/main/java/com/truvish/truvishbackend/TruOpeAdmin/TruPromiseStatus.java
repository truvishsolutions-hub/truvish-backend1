package com.truvish.truvishbackend.TruOpeAdmin;

public enum TruPromiseStatus {

    // =========================================================
    // TRUPROMISE CREATED
    // =========================================================
    //
    // Voucher generate/issue hone par:
    //
    // Voucher       = ₹1000
    // TruPromise    = ₹100
    //
    // Wallet se FULL ₹1000 debit hoga.
    //
    // TruPromise database me:
    // PROMISED
    //
    // =========================================================

    PROMISED,

    // =========================================================
    // TRUPROMISE PAID
    // =========================================================
    //
    // Voucher successfully redeem hone par:
    //
    // PROMISED -> PAID
    //
    // Wallet me koi additional credit/debit nahi.
    //
    // =========================================================

    PAID,

    // =========================================================
    // TRUPROMISE REVERSED
    // =========================================================
    //
    // Voucher expire/deactivate/cancel hone par:
    //
    // PROMISED -> REVERSED
    //
    // Sirf TruPromise amount wallet me CREDIT hoga.
    //
    // Example:
    //
    // Voucher      = ₹1000
    // TruPromise   = ₹100
    //
    // Initial wallet:
    // ₹2000
    //
    // Voucher generate:
    // ₹2000 -> ₹1000
    //
    // Deactivate:
    // ₹1000 -> ₹1100
    //
    // Original voucher ₹1000 dobara refund nahi hoga.
    //
    // =========================================================

    REVERSED
}