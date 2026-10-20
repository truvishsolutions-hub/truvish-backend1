package com.truvish.truvishbackend.corporateDashboard.CodesReport;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/corporate/code-report")
@CrossOrigin(
        origins = {
                "http://localhost:5173",
                "http://localhost:5174",
                "http://localhost:5175",
                "http://localhost:5176"
        },
        allowCredentials = "true"
)
public class CodeReportController {

    private final CodeReportService codeReportService;


    public CodeReportController(
            CodeReportService codeReportService
    ) {
        this.codeReportService = codeReportService;
    }




    @GetMapping("/{clientId}")
    public ResponseEntity<List<CodeReportResponse>> getCodeReport(
            @PathVariable Long clientId
    ) {

        return ResponseEntity.ok(
                codeReportService.getCodeReport(clientId)
        );
    }




    @GetMapping("/{clientId}/status/{status}")
    public ResponseEntity<List<CodeReportResponse>> getCodeReportByStatus(
            @PathVariable Long clientId,
            @PathVariable CodeReportStatus status
    ) {

        return ResponseEntity.ok(
                codeReportService.getCodeReportByStatus(
                        clientId,
                        status
                )
        );
    }




    @GetMapping("/code/{code}")
    public ResponseEntity<CodeReportResponse> getSingleCodeReport(
            @PathVariable String code
    ) {

        return ResponseEntity.ok(
                codeReportService.getCodeReportByCode(code)
        );
    }
}