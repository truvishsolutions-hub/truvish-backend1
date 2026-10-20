# TruVish Backend — Final Update (JWT unchanged)

## TruPromise reversal rule (physical and digital)

- Deactivation/eligible expiry returns exactly the `trupromise_amount` already saved on that code. It does not recalculate the promise and does not refund the voucher denomination.
- The exact saved amount is credited to the client's wallet balance and recorded as a successful CREDIT wallet transaction.
- Physical and digital reversals use stable transaction references (`TRU_PROMISE` + physical code ID; `TRUPROMISE` + digital code number) and a locked code record/status transition to prevent duplicate credits.
- A code with no positive saved promise amount receives no promise refund. A redeemed/paid code cannot be reversed.
- Corporate Promise Cashback includes the saved TruPromise amount only after it is returned to the wallet (`BackToWallet` for physical, `REVERSED` for digital). It does not increase just because a code is activated. Current-month cashback uses the actual reversal timestamp.

## Other targeted updates retained

- Physical and digital code search fallback by code, serial number, and reference number.
- Search response identifies digital records as `sourceType: DIGITAL` and physical records as `sourceType: PHYSICAL`.
- Client-ID history combines digital generator records and assigned physical codes, including TruPromise fields/timestamps and code identifiers.
- Digital deactivation persists an explicit deactivated state/reason for history.
- Existing unrelated features and frontend were not intentionally changed.

## JWT/configuration

- `src/main/resources/application.properties` is unchanged from the uploaded source ZIP.
- `JwtService.java` and `JwtAuthenticationFilter.java` are unchanged from the uploaded source ZIP.

## Validation

- ZIP contents and targeted source changes were inspected. A Maven build and live database/API tests were not completed in this environment; run the backend locally and test physical/digital reversal against your database before production deployment.
