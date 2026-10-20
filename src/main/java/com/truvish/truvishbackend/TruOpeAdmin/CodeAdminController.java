package com.truvish.truvishbackend.TruOpeAdmin;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin/client")
public class CodeAdminController {

    private final TruvishCodeService service;

    public CodeAdminController(TruvishCodeService service) {
        this.service = service;
    }

    // =========================================================
    // UPDATE / ASSIGN CLIENT
    // =========================================================

    @PostMapping("/update")
    public ResponseEntity<List<TruvishCode>> updateClient(
            @RequestBody CodeAssignmentDto dto
    ) {

        return ResponseEntity.ok(
                service.updateClient(dto)
        );
    }

    // =========================================================
    // ADMIN DEACTIVATE CODE
    // =========================================================

    @PostMapping("/deactivate/{codeId}")
    public ResponseEntity<TruvishCode> deactivateCode(
            @PathVariable Long codeId
    ) {

        return ResponseEntity.ok(
                service.deactivateCode(codeId)
        );
    }

    // =========================================================
    // EXPIRE CODE
    // =========================================================

    @PostMapping("/expire/{codeId}")
    public ResponseEntity<TruvishCode> expireCode(
            @PathVariable Long codeId
    ) {

        return ResponseEntity.ok(
                service.expireCode(codeId)
        );
    }

    // =========================================================
    // REVERSE TRUPROMISE
    // =========================================================

    @PostMapping("/reverse-trupromise/{codeId}")
    public ResponseEntity<TruvishCode> reverseTruPromise(
            @PathVariable Long codeId
    ) {

        return ResponseEntity.ok(
                service.reverseTruPromise(codeId)
        );
    }

    // =========================================================
    // PAY TRUPROMISE
    // =========================================================

    @PostMapping("/pay-trupromise/{codeId}")
    public ResponseEntity<TruvishCode> payTruPromise(
            @PathVariable Long codeId
    ) {

        return ResponseEntity.ok(
                service.payTruPromise(codeId)
        );
    }
}