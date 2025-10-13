package com.uade.tpo.pixelpoint.images;


import com.uade.tpo.pixelpoint.repository.catalog.VariantsRepository;
import com.uade.tpo.pixelpoint.entity.catalog.Variants;

import org.springframework.http.*;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import lombok.RequiredArgsConstructor;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/variants/{variantId}/images")
@RequiredArgsConstructor
public class VariantImagesController {

    private final VariantImageService imageService;
    private final VariantsRepository variantsRepository;

    private VariantImageResponse toDto(VariantImage img) {
        VariantImageResponse dto = new VariantImageResponse();
        dto.setId(img.getId());
        dto.setFilename(img.getFilename());
        dto.setContentType(img.getContentType());
        dto.setSizeBytes(img.getSizeBytes());
        dto.setPrimaryImage(img.isPrimaryImage());
        return dto;
    }

    // Listado de metadata
    @GetMapping
    public ResponseEntity<List<VariantImageResponse>> list(@PathVariable Long variantId) {
        var images = imageService.list(variantId).stream().map(this::toDto).toList();
        return ResponseEntity.ok(images);
    }

    // Descarga del binario
    @GetMapping("/{imageId}/bytes")
    public ResponseEntity<byte[]> download(@PathVariable Long variantId, @PathVariable Long imageId) {
        return imageService.get(variantId, imageId)
                .map(img -> ResponseEntity.ok()
                        .contentType(MediaType.parseMediaType(img.getContentType()))
                        .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"" + img.getFilename() + "\"")
                        .body(img.getData()))
                .orElse(ResponseEntity.notFound().build());
    }

    // Subida (multipart/form-data: file, asPrimary=true/false)
    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @PreAuthorize("hasAnyRole('ADMIN','SELLER')")
    public ResponseEntity<VariantImageResponse> upload(
            @PathVariable Long variantId,
            @RequestPart("file") MultipartFile file,
            @RequestParam(name = "asPrimary", defaultValue = "false") boolean asPrimary
    ) throws IOException {
        VariantImage saved = imageService.upload(variantId, file, asPrimary);
        return ResponseEntity.status(HttpStatus.CREATED).body(toDto(saved));
    }

    // Marcar principal
    @PutMapping("/{imageId}/primary")
    @PreAuthorize("hasAnyRole('ADMIN','SELLER')")
    public ResponseEntity<Void> setPrimary(@PathVariable Long variantId, @PathVariable Long imageId) {
        imageService.setPrimary(variantId, imageId);
        return ResponseEntity.noContent().build();
    }

    // Borrar
    @DeleteMapping("/{imageId}")
    @PreAuthorize("hasAnyRole('ADMIN','SELLER')")
    public ResponseEntity<Void> delete(@PathVariable Long variantId, @PathVariable Long imageId) {
        imageService.delete(variantId, imageId);
        return ResponseEntity.noContent().build();
    }
}
