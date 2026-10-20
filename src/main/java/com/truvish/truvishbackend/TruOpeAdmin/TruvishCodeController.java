package com.truvish.truvishbackend.TruOpeAdmin;

import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/truvish")
@CrossOrigin(origins = "*")
public class TruvishCodeController {

    private final TruvishCodeService service;

    /*
     * Upload directory.
     *
     * application.properties:
     *
     * app.upload.dir=uploads/client
     *
     * Default:
     * uploads/client
     */
    @Value("${app.upload.dir:uploads/client}")
    private String uploadDirectory;


    // =========================================================
    // CONSTRUCTOR
    // =========================================================

    public TruvishCodeController(
            TruvishCodeService service
    ) {
        this.service = service;
    }


    // =========================================================
    // CREATE TRUVISH CODE
    // =========================================================

    @PostMapping("/code")
    public ResponseEntity<?> create(
            @RequestBody TruvishCode code
    ) {
        try {

            return ResponseEntity.ok(
                    service.create(code)
            );

        } catch (Exception e) {

            e.printStackTrace();

            return ResponseEntity.badRequest().body(
                    Map.of(
                            "message",
                            "Failed to create Truvish code",
                            "error",
                            safeMessage(e)
                    )
            );
        }
    }


    // =========================================================
    // VERIFY CODE
    // =========================================================

    @GetMapping("/verify/{code}")
    public ResponseEntity<?> verify(
            @PathVariable String code
    ) {
        try {

            if (code == null
                    || code.trim().isEmpty()) {

                return ResponseEntity.badRequest().body(
                        Map.of(
                                "message",
                                "Code is required"
                        )
                );
            }

            /*
             * IMPORTANT:
             *
             * verifyCode() sirf code verify karega.
             *
             * Agar code expired hai:
             * - verification EXPIRED return karega
             * - wallet credit yahan nahi hoga
             *
             * TruPromise reversal dedicated:
             * expireCode()
             * deactivateCode()
             *
             * methods ke through hoga.
             */

            return ResponseEntity.ok(
                    service.verifyCode(
                            code.trim()
                    )
            );

        } catch (RuntimeException e) {

            return ResponseEntity.badRequest().body(
                    Map.of(
                            "message",
                            safeMessage(e)
                    )
            );
        }
    }


    // =========================================================
    // CREATE VOUCHER
    // =========================================================

    @PostMapping("/create-voucher")
    public ResponseEntity<?> createVoucher(
            @Valid @RequestBody CodeAssignmentDto dto
    ) {
        try {

            return ResponseEntity.ok(
                    service.updateClient(dto)
            );

        } catch (Exception e) {

            e.printStackTrace();

            return ResponseEntity.badRequest().body(
                    Map.of(
                            "message",
                            "Failed to create voucher",
                            "error",
                            safeMessage(e)
                    )
            );
        }
    }


    // =========================================================
    // UPDATE CLIENT / GENERATE VOUCHER
    // =========================================================

    @PostMapping("/update-client")
    public ResponseEntity<?> updateClient(
            @Valid @RequestBody CodeAssignmentDto dto
    ) {
        try {

            return ResponseEntity.ok(
                    service.updateClient(dto)
            );

        } catch (Exception e) {

            e.printStackTrace();

            return ResponseEntity.badRequest().body(
                    Map.of(
                            "message",
                            "Failed to update client",
                            "error",
                            safeMessage(e)
                    )
            );
        }
    }


    // =========================================================
    // GENERATE ORDER
    // MULTIPLE DENOMINATIONS
    //
    // TruPromise percentage:
    //
    // request.trupromisePercentage
    //
    // IMPORTANT:
    //
    // Wallet debit = FULL voucher value
    //
    // Example:
    // Voucher = 100
    // TruPromise = 10%
    //
    // Wallet debit = 100
    // TruPromise stored = 10
    // =========================================================

    @PostMapping("/generate-order")
    public ResponseEntity<?> generateOrder(
            @Valid @RequestBody OrderVoucherRequest request
    ) {
        try {

            List<TruvishCode> codes =
                    service.generateOrderVouchers(
                            request
                    );

            return ResponseEntity.ok(codes);

        } catch (Exception e) {

            e.printStackTrace();

            return ResponseEntity.badRequest().body(
                    Map.of(
                            "message",
                            "Failed to generate order",
                            "error",
                            safeMessage(e)
                    )
            );
        }
    }


    // =========================================================
    // TRUPROMISE - PAY
    //
    // PROMISED -> PAID
    //
    // Used when voucher is successfully redeemed.
    //
    // IMPORTANT:
    //
    // This DOES NOT credit wallet.
    //
    // Example:
    //
    // Voucher = 100
    // TruPromise = 10
    //
    // Issue:
    // Wallet -100
    //
    // Redeem:
    // Promise PROMISED -> PAID
    // Wallet remains unchanged
    // =========================================================

    @PostMapping("/trupromise/{codeId}/pay")
    public ResponseEntity<?> payTruPromise(
            @PathVariable Long codeId
    ) {
        try {

            if (codeId == null || codeId <= 0) {

                return ResponseEntity.badRequest().body(
                        Map.of(
                                "message",
                                "Invalid codeId"
                        )
                );
            }

            return ResponseEntity.ok(
                    service.payTruPromise(
                            codeId
                    )
            );

        } catch (RuntimeException e) {

            e.printStackTrace();

            return ResponseEntity.badRequest().body(
                    Map.of(
                            "message",
                            safeMessage(e)
                    )
            );

        } catch (Exception e) {

            e.printStackTrace();

            return ResponseEntity.status(
                    HttpStatus.INTERNAL_SERVER_ERROR
            ).body(
                    Map.of(
                            "message",
                            "Failed to pay TruPromise",
                            "error",
                            safeMessage(e)
                    )
            );
        }
    }


    // =========================================================
    // TRUPROMISE - REVERSE TO WALLET
    //
    // PROMISED -> REVERSED
    //
    // Only TruPromise amount is credited.
    //
    // Original voucher value is NEVER refunded.
    //
    // Example:
    //
    // Voucher = 100
    // TruPromise = 10
    //
    // Wallet before = 900
    // Wallet after  = 910
    //
    // NOT 1000
    // =========================================================

    @PostMapping("/trupromise/{codeId}/reverse")
    public ResponseEntity<?> reverseTruPromise(
            @PathVariable Long codeId
    ) {
        try {

            if (codeId == null || codeId <= 0) {

                return ResponseEntity.badRequest().body(
                        Map.of(
                                "message",
                                "Invalid codeId"
                        )
                );
            }

            return ResponseEntity.ok(
                    service.reverseTruPromise(
                            codeId
                    )
            );

        } catch (RuntimeException e) {

            e.printStackTrace();

            return ResponseEntity.badRequest().body(
                    Map.of(
                            "message",
                            safeMessage(e)
                    )
            );

        } catch (Exception e) {

            e.printStackTrace();

            return ResponseEntity.status(
                    HttpStatus.INTERNAL_SERVER_ERROR
            ).body(
                    Map.of(
                            "message",
                            "Failed to reverse TruPromise",
                            "error",
                            safeMessage(e)
                    )
            );
        }
    }


    // =========================================================
    // NORMAL EXPIRY
    //
    // ACTIVE -> EXPIRED
    //
    // TruPromise:
    // PROMISED -> REVERSED
    //
    // Only TruPromise amount is credited.
    //
    // Example:
    //
    // Voucher = 100
    // Promise = 10
    //
    // Wallet:
    // 900 -> 910
    //
    // Original 100 is NOT refunded.
    // =========================================================

    @PostMapping("/code/{codeId}/expire")
    public ResponseEntity<?> expireCode(
            @PathVariable Long codeId
    ) {
        try {

            if (codeId == null || codeId <= 0) {

                return ResponseEntity.badRequest().body(
                        Map.of(
                                "message",
                                "Invalid codeId"
                        )
                );
            }

            return ResponseEntity.ok(
                    service.expireCode(
                            codeId
                    )
            );

        } catch (RuntimeException e) {

            e.printStackTrace();

            return ResponseEntity.badRequest().body(
                    Map.of(
                            "message",
                            safeMessage(e)
                    )
            );

        } catch (Exception e) {

            e.printStackTrace();

            return ResponseEntity.status(
                    HttpStatus.INTERNAL_SERVER_ERROR
            ).body(
                    Map.of(
                            "message",
                            "Failed to expire code",
                            "error",
                            safeMessage(e)
                    )
            );
        }
    }


    // =========================================================
    // ADMIN DEACTIVATE
    //
    // ACTIVE -> EXPIRED
    //
    // TruPromise:
    // PROMISED -> REVERSED
    //
    // Only TruPromise amount is credited.
    //
    // This endpoint is for ADMIN deactivation.
    // =========================================================

    @PostMapping("/code/{codeId}/deactivate")
    public ResponseEntity<?> deactivateCode(
            @PathVariable Long codeId
    ) {
        try {

            if (codeId == null || codeId <= 0) {

                return ResponseEntity.badRequest().body(
                        Map.of(
                                "message",
                                "Invalid codeId"
                        )
                );
            }

            return ResponseEntity.ok(
                    service.deactivateCode(
                            codeId
                    )
            );

        } catch (RuntimeException e) {

            e.printStackTrace();

            return ResponseEntity.badRequest().body(
                    Map.of(
                            "message",
                            safeMessage(e)
                    )
            );

        } catch (Exception e) {

            e.printStackTrace();

            return ResponseEntity.status(
                    HttpStatus.INTERNAL_SERVER_ERROR
            ).body(
                    Map.of(
                            "message",
                            "Failed to deactivate code",
                            "error",
                            safeMessage(e)
                    )
            );
        }
    }


    // =========================================================
    // CLIENT HISTORY BY NAME
    // =========================================================

    @GetMapping("/history/{clientName}")
    public ResponseEntity<?> history(
            @PathVariable String clientName,

            @RequestParam(defaultValue = "0")
            int page,

            @RequestParam(defaultValue = "20")
            int size
    ) {
        try {

            if (clientName == null
                    || clientName.trim().isEmpty()) {

                return ResponseEntity.badRequest().body(
                        Map.of(
                                "message",
                                "Client name is required"
                        )
                );
            }

            if (page < 0) {
                page = 0;
            }

            if (size <= 0) {
                size = 20;
            }

            return ResponseEntity.ok(
                    service.history(
                            clientName.trim(),
                            page,
                            size
                    )
            );

        } catch (Exception e) {

            e.printStackTrace();

            return ResponseEntity.status(
                    HttpStatus.INTERNAL_SERVER_ERROR
            ).body(
                    Map.of(
                            "message",
                            "Failed to load client history",
                            "error",
                            safeMessage(e)
                    )
            );
        }
    }


    // =========================================================
    // CLIENT HISTORY BY ID
    // =========================================================

    @GetMapping("/history/client/{clientId}")
    public ResponseEntity<?> historyByClientId(
            @PathVariable Long clientId,

            @RequestParam(defaultValue = "0")
            int page,

            @RequestParam(defaultValue = "20")
            int size
    ) {
        try {

            if (clientId == null || clientId <= 0) {

                return ResponseEntity.badRequest().body(
                        Map.of(
                                "message",
                                "Invalid clientId"
                        )
                );
            }

            if (page < 0) {
                page = 0;
            }

            if (size <= 0) {
                size = 20;
            }

            return ResponseEntity.ok(
                    service.historyByClientId(
                            clientId,
                            page,
                            size
                    )
            );

        } catch (Exception e) {

            e.printStackTrace();

            return ResponseEntity.status(
                    HttpStatus.INTERNAL_SERVER_ERROR
            ).body(
                    Map.of(
                            "message",
                            "Failed to load client history",
                            "error",
                            safeMessage(e)
                    )
            );
        }
    }


    // =========================================================
    // UPLOAD CLIENT IMAGE
    // =========================================================

    @PostMapping(
            value = "/upload-client-img",
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE
    )
    public ResponseEntity<?> uploadClientImg(
            @RequestParam("file") MultipartFile file
    ) {

        try {

            // -------------------------------------------------
            // FILE VALIDATION
            // -------------------------------------------------

            if (file == null || file.isEmpty()) {

                return ResponseEntity.badRequest().body(
                        Map.of(
                                "message",
                                "Please select an image"
                        )
                );
            }

            String originalFilename =
                    file.getOriginalFilename();

            if (!StringUtils.hasText(originalFilename)) {

                return ResponseEntity.badRequest().body(
                        Map.of(
                                "message",
                                "Invalid filename"
                        )
                );
            }


            // -------------------------------------------------
            // FILE EXTENSION
            // -------------------------------------------------

            String extension =
                    StringUtils.getFilenameExtension(
                            originalFilename
                    );

            if (extension == null) {
                extension = "";
            }

            extension = extension.toLowerCase();


            // -------------------------------------------------
            // ALLOWED IMAGE TYPES
            // -------------------------------------------------

            if (!extension.equals("jpg")
                    && !extension.equals("jpeg")
                    && !extension.equals("png")
                    && !extension.equals("webp")) {

                return ResponseEntity.badRequest().body(
                        Map.of(
                                "message",
                                "Only JPG, JPEG, PNG and WEBP images are allowed"
                        )
                );
            }


            // -------------------------------------------------
            // MIME TYPE VALIDATION
            // -------------------------------------------------

            String contentType =
                    file.getContentType();

            if (contentType == null
                    || !contentType.startsWith("image/")) {

                return ResponseEntity.badRequest().body(
                        Map.of(
                                "message",
                                "Uploaded file must be an image"
                        )
                );
            }


            // -------------------------------------------------
            // CREATE UPLOAD DIRECTORY
            // -------------------------------------------------

            Path uploadPath =
                    Paths.get(uploadDirectory)
                            .toAbsolutePath()
                            .normalize();

            Files.createDirectories(
                    uploadPath
            );


            // -------------------------------------------------
            // SAFE UNIQUE FILE NAME
            // -------------------------------------------------

            String fileName =
                    UUID.randomUUID()
                            .toString()
                            .replace("-", "")
                            + "." + extension;

            Path targetPath =
                    uploadPath
                            .resolve(fileName)
                            .normalize();


            // -------------------------------------------------
            // PREVENT PATH TRAVERSAL
            // -------------------------------------------------

            if (!targetPath.getParent()
                    .equals(uploadPath)) {

                return ResponseEntity.badRequest().body(
                        Map.of(
                                "message",
                                "Invalid file path"
                        )
                );
            }


            // -------------------------------------------------
            // SAVE FILE
            // -------------------------------------------------

            Files.copy(
                    file.getInputStream(),
                    targetPath,
                    StandardCopyOption.REPLACE_EXISTING
            );


            // -------------------------------------------------
            // RESPONSE PATH
            // -------------------------------------------------

            String imagePath =
                    "/uploads/client/"
                            + fileName;


            return ResponseEntity.ok(
                    Map.of(
                            "message",
                            "Client image uploaded successfully",

                            "clientImg",
                            imagePath,

                            "fileName",
                            fileName
                    )
            );

        } catch (IOException e) {

            e.printStackTrace();

            return ResponseEntity.status(
                    HttpStatus.INTERNAL_SERVER_ERROR
            ).body(
                    Map.of(
                            "message",
                            "Failed to upload client image",
                            "error",
                            safeMessage(e)
                    )
            );

        } catch (Exception e) {

            e.printStackTrace();

            return ResponseEntity.badRequest().body(
                    Map.of(
                            "message",
                            "Upload failed",
                            "error",
                            safeMessage(e)
                    )
            );
        }
    }


    // =========================================================
    // SAFE ERROR MESSAGE
    // =========================================================

    private String safeMessage(
            Exception e
    ) {

        if (e == null
                || e.getMessage() == null) {

            return "Unknown error";
        }

        return e.getMessage();
    }
}