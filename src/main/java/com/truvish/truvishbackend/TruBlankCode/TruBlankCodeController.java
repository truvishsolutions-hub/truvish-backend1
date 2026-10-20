package com.truvish.truvishbackend.TruBlankCode;

import com.truvish.truvishbackend.TruBlankCode.dto.ActivateBlankCodeRequest;
import com.truvish.truvishbackend.TruBlankCode.dto.AssignClientRequest;
import com.truvish.truvishbackend.TruBlankCode.dto.GenerateBlankCodeRequest;
import com.truvish.truvishbackend.TruBlankCode.dto.UpdateBlankCodeRequest;
import com.truvish.truvishbackend.TruBlankCode.response.TruBlankCodeResponse;
import com.truvish.truvishbackend.TruOpeAdmin.TruvishCode;
import com.truvish.truvishbackend.TruOpeAdmin.TruvishCodeRepository;
import com.truvish.truvishbackend.TruOpeAdmin.TruvishCodeService;
import com.truvish.truvishbackend.TruOpeAdmin.VoucherStatus;

import jakarta.validation.Valid;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;

@RestController
@RequestMapping("/api/admin/tru-blank-code")
@CrossOrigin(
        origins = {
                "http://localhost:5173",
                "http://localhost:5174",
                "http://localhost:5175",
                "http://localhost:5176"
        },
        allowCredentials = "true"
)
public class TruBlankCodeController {

    private final TruBlankCodeService service;
    private final TruvishCodeRepository truvishCodeRepository;
    private final TruvishCodeService truvishCodeService;

    // =========================================================
    // CONSTRUCTOR
    // =========================================================

    public TruBlankCodeController(
            TruBlankCodeService service,
            TruvishCodeRepository truvishCodeRepository,
            TruvishCodeService truvishCodeService
    ) {
        this.service = service;
        this.truvishCodeRepository = truvishCodeRepository;
        this.truvishCodeService = truvishCodeService;
    }

    // =========================================================
    // GENERATE BLANK CODES
    // =========================================================

    @PostMapping("/generate")
    public ResponseEntity<List<TruBlankCodeResponse>> generateCodes(

            @Valid
            @RequestBody
            GenerateBlankCodeRequest request,

            @RequestParam("adminId")
            Long adminId

    ) {

        List<TruBlankCodeResponse> response =
                service.generateCodes(
                        request,
                        adminId
                );

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(response);
    }

    // =========================================================
    // GET ALL BLANK CODES
    // =========================================================

    @GetMapping
    public ResponseEntity<Page<TruBlankCodeResponse>> getAllCodes(
            Pageable pageable
    ) {

        return ResponseEntity.ok(
                service.getAllCodes(
                        pageable
                )
        );
    }

    // =========================================================
    // GET ALL CODES BY CLIENT
    // =========================================================

    @GetMapping("/client/{clientId}")
    public ResponseEntity<List<TruBlankCodeResponse>> getCodesByClient(

            @PathVariable
            Long clientId

    ) {

        return ResponseEntity.ok(
                service.getCodesByClient(
                        clientId
                )
        );
    }

    // =========================================================
    // GET PAGINATED CODES BY CLIENT
    // =========================================================

    @GetMapping("/client/{clientId}/page")
    public ResponseEntity<Page<TruBlankCodeResponse>> getCodesByClientPaged(

            @PathVariable
            Long clientId,

            Pageable pageable

    ) {

        return ResponseEntity.ok(
                service.getCodesByClient(
                        clientId,
                        pageable
                )
        );
    }

    // =========================================================
    // CLIENT SUMMARY
    // =========================================================

    @GetMapping("/client/{clientId}/summary")
    public ResponseEntity<ClientTruCardSummary> getClientSummary(

            @PathVariable
            Long clientId

    ) {

        // -----------------------------------------------------
        // COUNTS
        // -----------------------------------------------------

        long issuedCodes =
                service.countClientCodes(
                        clientId
                );

        long inactiveCodes =
                service.countClientInactive(
                        clientId
                );

        long activeCodes =
                service.countClientActive(
                        clientId
                );

        long redeemedCodes =
                service.countClientRedeemed(
                        clientId
                );

        long expiredCodes =
                service.countClientExpired(
                        clientId
                );

        /*
         * Client-specific cancelled count is not currently
         * exposed by TruBlankCodeService.
         *
         * Keep 0 until the corresponding service/repository
         * method is added.
         */
        long cancelledCodes = 0L;

        // -----------------------------------------------------
        // VALUES
        // -----------------------------------------------------

        BigDecimal issuedValue =
                service.clientIssuedValue(
                        clientId
                );

        BigDecimal redeemedValue =
                service.clientRedeemedValue(
                        clientId
                );

        BigDecimal activeValue =
                service.clientActiveValue(
                        clientId
                );

        BigDecimal expiredValue =
                service.clientExpiredValue(
                        clientId
                );

        // -----------------------------------------------------
        // REDEMPTION RATE
        // -----------------------------------------------------

        double redemptionRate =
                issuedCodes > 0
                        ? (
                        (double) redeemedCodes
                                / (double) issuedCodes
                ) * 100.0
                        : 0.0;

        // -----------------------------------------------------
        // RESPONSE
        // -----------------------------------------------------

        ClientTruCardSummary summary =
                new ClientTruCardSummary(
                        issuedCodes,
                        issuedValue,
                        inactiveCodes,
                        activeCodes,
                        redeemedCodes,
                        redeemedValue,
                        expiredCodes,
                        expiredValue,
                        cancelledCodes,
                        redemptionRate
                );

        return ResponseEntity.ok(
                summary
        );
    }

    // =========================================================
    // GET CODE BY ID
    // =========================================================

    @GetMapping("/{id}")
    public ResponseEntity<TruBlankCodeResponse> getById(

            @PathVariable
            Long id

    ) {

        return ResponseEntity.ok(
                service.getById(
                        id
                )
        );
    }

    // =========================================================
    // GET CODE BY SERIAL
    // =========================================================

    @GetMapping("/serial/{serialNumber}")
    public ResponseEntity<TruBlankCodeResponse> getBySerial(

            @PathVariable
            String serialNumber

    ) {

        try {
            return ResponseEntity.ok(service.getBySerial(serialNumber));
        } catch (RuntimeException physicalNotFound) {
            TruvishCode digital = truvishCodeRepository.findBySerialNumber(serialNumber.trim())
                    .orElseThrow(() -> physicalNotFound);
            return ResponseEntity.ok(toGeneratedCodeResponse(digital));
        }
    }

    // =========================================================
    // GET CODE BY REFERENCE
    // =========================================================

    @GetMapping("/reference/{referenceNumber}")
    public ResponseEntity<TruBlankCodeResponse> getByReference(

            @PathVariable
            String referenceNumber

    ) {

        try {
            return ResponseEntity.ok(service.getByReference(referenceNumber));
        } catch (RuntimeException physicalNotFound) {
            TruvishCode digital = truvishCodeRepository.findByReferenceNumber(referenceNumber.trim())
                    .orElseThrow(() -> physicalNotFound);
            return ResponseEntity.ok(toGeneratedCodeResponse(digital));
        }
    }

    // =========================================================
    // SEARCH TRUVISH CODE-GENERATOR CODES BY SERIAL/REFERENCE
    // The TruBlankCode screen searches both physical and digital records.
    // =========================================================

    @GetMapping("/generator/serial/{serialNumber}")
    public ResponseEntity<TruBlankCodeResponse> getGeneratedBySerial(
            @PathVariable String serialNumber
    ) {
        TruvishCode code = truvishCodeRepository.findBySerialNumber(serialNumber.trim())
                .orElseThrow(() -> new RuntimeException("Generated code not found for serial: " + serialNumber));
        return ResponseEntity.ok(toGeneratedCodeResponse(code));
    }

    @GetMapping("/generator/reference/{referenceNumber}")
    public ResponseEntity<TruBlankCodeResponse> getGeneratedByReference(
            @PathVariable String referenceNumber
    ) {
        TruvishCode code = truvishCodeRepository.findByReferenceNumber(referenceNumber.trim())
                .orElseThrow(() -> new RuntimeException("Generated code not found for reference: " + referenceNumber));
        return ResponseEntity.ok(toGeneratedCodeResponse(code));
    }

    private TruBlankCodeResponse toGeneratedCodeResponse(TruvishCode code) {
        TruBlankCodeResponse response = new TruBlankCodeResponse();
        response.setId(code.getTruvishId());
        response.setGenerationNumber(code.getTruvishId());
        response.setCodeNumber(code.getTruvishIdCodeNumber());
        response.setSerialNumber(code.getSerialNumber());
        response.setReferenceNumber(code.getReferenceNumber());
        response.setDenomination(code.getOriginalCodeValue() != null
                ? code.getOriginalCodeValue() : code.getTruvishCodeValue());
        response.setValidityMonths(code.getValidity());
        if (code.getTruvishCodeTimestamp() != null && code.getValidity() != null) {
            response.setExpiryDate(code.getTruvishCodeTimestamp().plusMonths(code.getValidity()));
        }
        response.setBrandNames(code.getClientBrand() == null ? java.util.List.of() : code.getClientBrand());
        response.setThemeName(code.getClientTheme());
        response.setThemeImg(code.getClientThemeImg());
        VoucherStatus status = code.getTruvishCodeStatus();
        if (status != null) response.setStatus(TruBlankCodeStatus.valueOf(status.name()));
        response.setCreatedAt(code.getTruvishCodeTimestamp());
        response.setUpdatedAt(code.getTruvishCodeTimestamp());
        response.setClientId(code.getClientId());
        response.setClientName(code.getClientName());
        response.setClientImg(code.getClientImg());
        response.setClientBrand(code.getClientBrand() == null ? java.util.List.of() : code.getClientBrand());
        response.setClientTheme(code.getClientTheme());
        response.setClientThemeImg(code.getClientThemeImg());
        response.setTrupromisePercentage(code.getTrupromisePercentage());
        response.setTrupromiseAmount(code.getTrupromiseAmount());
        if (code.getTrupromiseStatus() != null) {
            String promiseStatus = code.getTrupromiseStatus().name();
            response.setTrupromiseStatus("REVERSED".equals(promiseStatus)
                    ? TruPromiseStatus.BackToWallet : TruPromiseStatus.valueOf(promiseStatus));
        }
        response.setTrupromiseCreatedAt(code.getTrupromiseCreatedAt());
        response.setTrupromisePaidAt(code.getTrupromisePaidAt());
        response.setTrupromiseReversedAt(code.getTrupromiseReversedAt());
        response.setSourceType("DIGITAL");
        return response;
    }

    // =========================================================
    // GET CODE BY CODE NUMBER
    // =========================================================

    @GetMapping("/code/{codeNumber}")
    public ResponseEntity<TruBlankCodeResponse> getByCodeNumber(

            @PathVariable
            String codeNumber

    ) {

        return ResponseEntity.ok(
                service.getByCodeNumber(
                        codeNumber
                )
        );
    }

    // =========================================================
    // UNIVERSAL SEARCH
    // =========================================================

    @GetMapping("/search")
    public ResponseEntity<TruBlankCodeResponse> search(

            @RequestParam("value")
            String value

    ) {

        try {
            return ResponseEntity.ok(service.search(value));
        } catch (RuntimeException physicalNotFound) {
            String searchValue = value == null ? "" : value.trim();
            TruvishCode digital = truvishCodeRepository.findByTruvishIdCodeNumber(searchValue)
                    .or(() -> truvishCodeRepository.findBySerialNumber(searchValue))
                    .or(() -> truvishCodeRepository.findByReferenceNumber(searchValue))
                    .orElseThrow(() -> physicalNotFound);
            return ResponseEntity.ok(toGeneratedCodeResponse(digital));
        }
    }

    // =========================================================
    // ASSIGN CLIENT
    // =========================================================

    @PutMapping("/{id}/client")
    public ResponseEntity<TruBlankCodeResponse> assignClient(

            @PathVariable
            Long id,

            @Valid
            @RequestBody
            AssignClientRequest request

    ) {

        return ResponseEntity.ok(
                service.assignClient(
                        id,
                        request
                )
        );
    }

    // =========================================================
    // UPDATE BLANK CODE
    // =========================================================

    @PutMapping("/{id}")
    public ResponseEntity<TruBlankCodeResponse> updateCode(

            @PathVariable
            Long id,

            @Valid
            @RequestBody
            UpdateBlankCodeRequest request

    ) {

        return ResponseEntity.ok(
                service.updateCode(
                        id,
                        request
                )
        );
    }

    // =========================================================
    // ACTIVATE TRUCARD
    //
    // FLOW:
    //
    // INACTIVE
    //     ↓
    // CHECK CLIENT
    //     ↓
    // CHECK BALANCE
    //     ↓
    // CALCULATE TRUPROMISE
    //     ↓
    // DEDUCT FULL VOUCHER VALUE
    //     ↓
    // SAVE TRUPROMISE AS PROMISED
    //     ↓
    // SET ACTIVE
    //     ↓
    // SET EXPIRY
    //
    // IMPORTANT:
    // TruPromise is NOT additionally deducted from wallet.
    // =========================================================

    @PutMapping("/{id}/activate")
    public ResponseEntity<TruBlankCodeResponse> activateCode(

            @PathVariable
            Long id,

            @Valid
            @RequestBody
            ActivateBlankCodeRequest request

    ) {

        return ResponseEntity.ok(
                service.activateCode(
                        id,
                        request
                )
        );
    }

    // =========================================================
    // REDEEM
    //
    // TruPromise flow:
    //
    // PROMISED
    //     ↓
    // CUSTOMER REDEEMS
    //     ↓
    // PAID
    //
    // The original activation wallet debit is NOT refunded.
    // =========================================================

    @PutMapping("/{id}/redeem")
    public ResponseEntity<TruBlankCodeResponse> redeemCode(

            @PathVariable
            Long id

    ) {

        return ResponseEntity.ok(
                service.redeemCode(
                        id
                )
        );
    }

    // =========================================================
    // DEACTIVATE
    // =========================================================

    @PutMapping("/{id}/deactivate")
    public ResponseEntity<TruBlankCodeResponse> deactivateCode(
            @PathVariable Long id,
            @RequestParam(defaultValue = "PHYSICAL") String sourceType
    ) {
        if ("TRUVISH_GENERATOR".equalsIgnoreCase(sourceType)
                || "DIGITAL".equalsIgnoreCase(sourceType)) {
            TruvishCode deactivated = truvishCodeService.deactivateCode(id);
            return ResponseEntity.ok(toGeneratedCodeResponse(deactivated));
        }
        return ResponseEntity.ok(service.deactivateCode(id));
    }

    // =========================================================
    // CANCEL
    //
    // If cancellation/reversal is handled by the service,
    // the TruPromise reversal logic belongs there.
    // Controller only forwards the request.
    // =========================================================

    @PutMapping("/{id}/cancel")
    public ResponseEntity<TruBlankCodeResponse> cancelCode(

            @PathVariable
            Long id

    ) {

        return ResponseEntity.ok(
                service.cancelCode(
                        id
                )
        );
    }

    // =========================================================
    // GET BY STATUS
    // =========================================================

    @GetMapping("/status/{status}")
    public ResponseEntity<Page<TruBlankCodeResponse>> getByStatus(

            @PathVariable
            TruBlankCodeStatus status,

            Pageable pageable

    ) {

        return ResponseEntity.ok(
                service.getByStatus(
                        status,
                        pageable
                )
        );
    }

    // =========================================================
    // GLOBAL COUNT - INACTIVE
    // =========================================================

    @GetMapping("/count/inactive")
    public ResponseEntity<Long> countInactive() {

        return ResponseEntity.ok(
                service.countInactive()
        );
    }

    // =========================================================
    // GLOBAL COUNT - ACTIVE
    // =========================================================

    @GetMapping("/count/active")
    public ResponseEntity<Long> countActive() {

        return ResponseEntity.ok(
                service.countActive()
        );
    }

    // =========================================================
    // GLOBAL COUNT - REDEEMED
    // =========================================================

    @GetMapping("/count/redeemed")
    public ResponseEntity<Long> countRedeemed() {

        return ResponseEntity.ok(
                service.countRedeemed()
        );
    }

    // =========================================================
    // GLOBAL COUNT - EXPIRED
    // =========================================================

    @GetMapping("/count/expired")
    public ResponseEntity<Long> countExpired() {

        return ResponseEntity.ok(
                service.countExpired()
        );
    }

    // =========================================================
    // GLOBAL COUNT - CANCELLED
    // =========================================================

    @GetMapping("/count/cancelled")
    public ResponseEntity<Long> countCancelled() {

        return ResponseEntity.ok(
                service.countCancelled()
        );
    }

    // =========================================================
    // CLIENT COUNT - INACTIVE
    // =========================================================

    @GetMapping("/client/{clientId}/count/inactive")
    public ResponseEntity<Long> countClientInactive(

            @PathVariable
            Long clientId

    ) {

        return ResponseEntity.ok(
                service.countClientInactive(
                        clientId
                )
        );
    }

    // =========================================================
    // CLIENT COUNT - ACTIVE
    // =========================================================

    @GetMapping("/client/{clientId}/count/active")
    public ResponseEntity<Long> countClientActive(

            @PathVariable
            Long clientId

    ) {

        return ResponseEntity.ok(
                service.countClientActive(
                        clientId
                )
        );
    }

    // =========================================================
    // CLIENT COUNT - REDEEMED
    // =========================================================

    @GetMapping("/client/{clientId}/count/redeemed")
    public ResponseEntity<Long> countClientRedeemed(

            @PathVariable
            Long clientId

    ) {

        return ResponseEntity.ok(
                service.countClientRedeemed(
                        clientId
                )
        );
    }

    // =========================================================
    // CLIENT COUNT - EXPIRED
    // =========================================================

    @GetMapping("/client/{clientId}/count/expired")
    public ResponseEntity<Long> countClientExpired(

            @PathVariable
            Long clientId

    ) {

        return ResponseEntity.ok(
                service.countClientExpired(
                        clientId
                )
        );
    }

    // =========================================================
    // CLIENT VALUE - ISSUED
    // =========================================================

    @GetMapping("/client/{clientId}/value/issued")
    public ResponseEntity<BigDecimal> clientIssuedValue(

            @PathVariable
            Long clientId

    ) {

        return ResponseEntity.ok(
                service.clientIssuedValue(
                        clientId
                )
        );
    }

    // =========================================================
    // CLIENT VALUE - REDEEMED
    // =========================================================

    @GetMapping("/client/{clientId}/value/redeemed")
    public ResponseEntity<BigDecimal> clientRedeemedValue(

            @PathVariable
            Long clientId

    ) {

        return ResponseEntity.ok(
                service.clientRedeemedValue(
                        clientId
                )
        );
    }

    // =========================================================
    // CLIENT VALUE - ACTIVE
    // =========================================================

    @GetMapping("/client/{clientId}/value/active")
    public ResponseEntity<BigDecimal> clientActiveValue(

            @PathVariable
            Long clientId

    ) {

        return ResponseEntity.ok(
                service.clientActiveValue(
                        clientId
                )
        );
    }

    // =========================================================
    // CLIENT VALUE - EXPIRED
    // =========================================================

    @GetMapping("/client/{clientId}/value/expired")
    public ResponseEntity<BigDecimal> clientExpiredValue(

            @PathVariable
            Long clientId

    ) {

        return ResponseEntity.ok(
                service.clientExpiredValue(
                        clientId
                )
        );
    }

    // =========================================================
    // CLIENT TRUCARD SUMMARY DTO
    // =========================================================

    public record ClientTruCardSummary(

            long issuedCodes,

            BigDecimal issuedValue,

            long inactiveCodes,

            long activeCodes,

            long redeemedCodes,

            BigDecimal redeemedValue,

            long expiredCodes,

            BigDecimal expiredValue,

            long cancelledCodes,

            double redemptionRate

    ) {
    }
}