package com.uade.tpo.pixelpoint.images;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

import org.springframework.web.multipart.MultipartFile;

public interface VariantImageService {
    List<VariantImage> list(Long variantId);
    VariantImage upload(Long variantId, MultipartFile file, boolean asPrimary) throws IOException;
    void setPrimary(Long variantId, Long imageId);
    void delete(Long variantId, Long imageId);
    Optional<VariantImage> get(Long variantId, Long imageId);
    Optional<VariantImage> getPrimary(Long variantId);
    long count(Long variantId);
}
