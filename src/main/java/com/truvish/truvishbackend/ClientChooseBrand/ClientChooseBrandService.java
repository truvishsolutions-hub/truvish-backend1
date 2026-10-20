package com.truvish.truvishbackend.ClientChooseBrand;

import com.truvish.truvishbackend.ClientChooseBrand.DTO.ClientChooseBrandResponse;
import com.truvish.truvishbackend.common.FileStorageService;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.TreeMap;
import java.util.stream.Collectors;

@Service
public class ClientChooseBrandService {

    private static final long MAX_IMAGE_SIZE = 1024L * 1024L; // 1 MB

    private final ClientChooseBrandRepository repository;
    private final FileStorageService fileStorageService;

    public ClientChooseBrandService(
            ClientChooseBrandRepository repository,
            FileStorageService fileStorageService
    ) {
        this.repository = repository;
        this.fileStorageService = fileStorageService;
    }


    // =========================================================
    // VALIDATE BRAND IMAGE
    // =========================================================

    private void validateBrandImage(MultipartFile image) {

        // Image is optional
        if (image == null || image.isEmpty()) {
            return;
        }

        // Maximum 1 MB
        if (image.getSize() > MAX_IMAGE_SIZE) {
            throw new IllegalArgumentException(
                    "Brand image size must be 1MB or less"
            );
        }

        String contentType = image.getContentType();

        if (contentType == null ||
                !contentType.toLowerCase().startsWith("image/")) {

            throw new IllegalArgumentException(
                    "Only image files are allowed for brand upload"
            );
        }
    }


    // =========================================================
    // SAVE BRAND
    // =========================================================

    public ClientChooseBrandResponse saveBrand(
            String brandName,
            String category,
            String termsAndConditions,
            String howToRedeem,
            MultipartFile image
    ) {

        // -----------------------------------------------------
        // Validate required fields
        // -----------------------------------------------------

        String cleanBrandName =
                brandName != null ? brandName.trim() : "";

        String cleanCategory =
                category != null ? category.trim() : "";

        if (cleanBrandName.isEmpty()) {
            throw new IllegalArgumentException(
                    "Brand name is required"
            );
        }

        if (cleanCategory.isEmpty()) {
            throw new IllegalArgumentException(
                    "Category is required"
            );
        }


        // -----------------------------------------------------
        // Validate image
        // -----------------------------------------------------

        validateBrandImage(image);


        String imgFileName = null;

        try {

            // -------------------------------------------------
            // Store image first
            // -------------------------------------------------

            if (image != null && !image.isEmpty()) {

                imgFileName =
                        fileStorageService.storeFile(image);
            }


            // -------------------------------------------------
            // Create entity
            // -------------------------------------------------

            ClientChooseBrand brand =
                    new ClientChooseBrand();

            brand.setBrandName(cleanBrandName);

            brand.setCategory(cleanCategory);

            brand.setBrandImg(imgFileName);


            // -------------------------------------------------
            // Terms & Conditions
            // -------------------------------------------------

            if (termsAndConditions != null &&
                    !termsAndConditions.trim().isEmpty()) {

                brand.setTermsAndConditions(
                        termsAndConditions.trim()
                );

            } else {

                brand.setTermsAndConditions(null);
            }


            // -------------------------------------------------
            // How To Redeem
            // -------------------------------------------------

            if (howToRedeem != null &&
                    !howToRedeem.trim().isEmpty()) {

                brand.setHowToRedeem(
                        howToRedeem.trim()
                );

            } else {

                brand.setHowToRedeem(null);
            }


            // -------------------------------------------------
            // Save database record
            // -------------------------------------------------

            ClientChooseBrand saved =
                    repository.save(brand);


            // -------------------------------------------------
            // Return response
            // -------------------------------------------------

            return toResponse(saved);

        } catch (IllegalArgumentException e) {

            // If DB save failed after image upload,
            // remove uploaded image.
            if (imgFileName != null) {
                fileStorageService.delete(imgFileName);
            }

            throw e;

        } catch (IOException e) {

            // File storage error
            if (imgFileName != null) {
                fileStorageService.delete(imgFileName);
            }

            throw new RuntimeException(
                    "Failed to store brand image",
                    e
            );

        } catch (Exception e) {

            // Any database/application error
            if (imgFileName != null) {
                fileStorageService.delete(imgFileName);
            }

            throw new RuntimeException(
                    "Failed to save client brand: " + e.getMessage(),
                    e
            );
        }
    }


    // =========================================================
    // GET ALL BRANDS
    // =========================================================

    public List<ClientChooseBrandResponse> getAllBrands() {

        return repository
                .findAllByOrderByBrandNameAsc()
                .stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }


    // =========================================================
    // GET ALL CATEGORIES
    // =========================================================

    public List<String> getAllCategories() {

        return repository
                .findAllByOrderByBrandNameAsc()
                .stream()
                .map(ClientChooseBrand::getCategory)
                .filter(Objects::nonNull)
                .map(String::trim)
                .filter(s -> !s.isEmpty())
                .distinct()
                .sorted()
                .collect(Collectors.toList());
    }


    // =========================================================
    // GROUP BRANDS BY CATEGORY
    // =========================================================

    public Map<String, List<ClientChooseBrandResponse>>
    getBrandsGroupedByCategory() {

        List<ClientChooseBrand> all =
                repository.findAllByOrderByBrandNameAsc();

        Map<String, List<ClientChooseBrandResponse>> grouped =
                new LinkedHashMap<>();

        all.stream()
                .collect(
                        Collectors.groupingBy(
                                b -> {

                                    String cat =
                                            b.getCategory();

                                    if (cat == null ||
                                            cat.trim().isEmpty()) {

                                        return "Other";
                                    }

                                    return cat.trim();
                                },

                                TreeMap::new,

                                Collectors.toList()
                        )
                )
                .forEach(
                        (category, brands) ->
                                grouped.put(
                                        category,
                                        brands.stream()
                                                .map(this::toResponse)
                                                .collect(Collectors.toList())
                                )
                );

        return grouped;
    }


    // =========================================================
    // DELETE BRAND
    // =========================================================

    public void deleteBrand(Long id) {

        if (id == null) {
            throw new IllegalArgumentException(
                    "Brand ID is required"
            );
        }

        ClientChooseBrand brand =
                repository.findById(id).orElse(null);

        if (brand == null) {
            return;
        }


        // Delete image first
        if (brand.getBrandImg() != null &&
                !brand.getBrandImg().isBlank()) {

            fileStorageService.delete(
                    brand.getBrandImg()
            );
        }


        // Delete database record
        repository.deleteById(id);
    }


    // =========================================================
    // ENTITY -> RESPONSE
    // =========================================================

    private ClientChooseBrandResponse toResponse(
            ClientChooseBrand brand
    ) {

        String imgUrl = null;

        if (brand.getBrandImg() != null &&
                !brand.getBrandImg().isBlank()) {

            imgUrl =
                    "/uploads/" + brand.getBrandImg();
        }

        return new ClientChooseBrandResponse(
                brand.getId(),
                brand.getBrandName(),
                brand.getCategory(),
                imgUrl,
                brand.getTermsAndConditions(),
                brand.getHowToRedeem()
        );
    }
}