package com.truvish.truvishbackend.ClientChooseBrand;

import com.truvish.truvishbackend.ClientChooseBrand.DTO.ClientChooseBrandResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/client-choose-brand")
@CrossOrigin(
        origins = {
                "http://localhost:5173",
                "http://localhost:5174",
                "http://localhost:5175",
                "http://localhost:5176",
                "http://localhost:5177"
        },
        allowCredentials = "true"
)
public class ClientChooseBrandController {

    private final ClientChooseBrandService service;

    public ClientChooseBrandController(
            ClientChooseBrandService service
    ) {
        this.service = service;
    }


    // =========================================================
    // UPLOAD BRAND
    // =========================================================

    @PostMapping(
            value = "/upload",
            consumes = "multipart/form-data"
    )
    public ResponseEntity<ClientChooseBrandResponse> uploadBrand(

            @RequestParam("brandName")
            String brandName,


            @RequestParam("category")
            String category,

            @RequestParam(
                    value = "termsAndConditions",
                    required = false
            )
            String termsAndConditions,

            @RequestParam(
                    value = "howToRedeem",
                    required = false
            )
            String howToRedeem,

            @RequestPart(
                    value = "image",
                    required = false
            )
            MultipartFile image
    ) {

        ClientChooseBrandResponse response =
                service.saveBrand(
                        brandName,
                        category,
                        termsAndConditions,
                        howToRedeem,
                        image
                );

        return ResponseEntity.ok(response);
    }


    // =========================================================
    // GET ALL BRANDS
    // =========================================================

    @GetMapping
    public ResponseEntity<List<ClientChooseBrandResponse>>
    getAllBrands() {

        return ResponseEntity.ok(
                service.getAllBrands()
        );
    }


    // =========================================================
    // GET CATEGORIES
    // =========================================================

    @GetMapping("/categories")
    public ResponseEntity<List<String>> getCategories() {

        return ResponseEntity.ok(
                service.getAllCategories()
        );
    }


    // =========================================================
    // GROUPED BRANDS
    // =========================================================

    @GetMapping("/grouped")
    public ResponseEntity<
            Map<String, List<ClientChooseBrandResponse>>
            > getGroupedBrands() {

        return ResponseEntity.ok(
                service.getBrandsGroupedByCategory()
        );
    }


    // =========================================================
    // DELETE BRAND
    // =========================================================

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteBrand(
            @PathVariable Long id
    ) {

        service.deleteBrand(id);

        return ResponseEntity.noContent().build();
    }
}