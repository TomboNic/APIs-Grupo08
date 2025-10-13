package com.uade.tpo.pixelpoint.images;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.uade.tpo.pixelpoint.entity.catalog.Variants;
import com.uade.tpo.pixelpoint.repository.catalog.VariantsRepository;

import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class VariantImageServiceImpl implements VariantImageService {

    private final VariantImageRepository imageRepo;
    private final VariantsRepository variantsRepo;

    private Variants requireVariant(Long variantId) {
        return variantsRepo.findById(variantId)
                .orElseThrow(() -> new EntityNotFoundException("Variant id=" + variantId + " no existe"));
    }

    @Override
    public List<VariantImage> list(Long variantId) {
        return imageRepo.findByVariantIdOrderByPrimaryImageDescCreatedAtDesc(variantId);
    }

    @Override
    @Transactional
    public VariantImage upload(Long variantId, MultipartFile file, boolean asPrimary) throws IOException {
        if (file == null || file.isEmpty()) {
            throw new IllegalArgumentException("Archivo vacío");
        }
        Variants variant = requireVariant(variantId);

        VariantImage img = VariantImage.builder()
                .variant(variant)
                .data(file.getBytes())
                .contentType(file.getContentType() != null ? file.getContentType() : "application/octet-stream")
                .filename(file.getOriginalFilename() != null ? file.getOriginalFilename() : "image")
                .sizeBytes(file.getSize())
                .primaryImage(false) // seteo abajo
                .build();

        VariantImage saved = imageRepo.save(img);

        // Si es la primera imagen, o pidieron asPrimary, la marcamos
        boolean noHayPrimaria = imageRepo.findFirstByVariantIdAndPrimaryImageTrue(variantId).isEmpty();
        if (asPrimary || noHayPrimaria) {
            setPrimary(variantId, saved.getId());
        }

        return saved;
    }

    @Override
    @Transactional
    public void setPrimary(Long variantId, Long imageId) {
        // Desmarcar la anterior
        imageRepo.findFirstByVariantIdAndPrimaryImageTrue(variantId)
                .ifPresent(prev -> {
                    prev.setPrimaryImage(false);
                    imageRepo.save(prev);
                });

        // Marcar la nueva
        VariantImage img = imageRepo.findById(imageId)
                .orElseThrow(() -> new EntityNotFoundException("Imagen id=" + imageId + " no existe"));
        if (!img.getVariant().getId().equals(variantId)) {
            throw new IllegalArgumentException("La imagen no pertenece a esta variant");
        }
        img.setPrimaryImage(true);
        imageRepo.save(img);
    }

    @Override
    @Transactional
    public void delete(Long variantId, Long imageId) {
        VariantImage img = imageRepo.findById(imageId)
                .orElseThrow(() -> new EntityNotFoundException("Imagen id=" + imageId + " no existe"));
        if (!img.getVariant().getId().equals(variantId)) {
            throw new IllegalArgumentException("La imagen no pertenece a esta variant");
        }
        boolean eraPrimaria = img.isPrimaryImage();
        imageRepo.delete(img);

        // Si borramos la primaria, asignar otra si existe
        if (eraPrimaria) {
            imageRepo.findByVariantIdOrderByPrimaryImageDescCreatedAtDesc(variantId).stream().findFirst()
                    .ifPresent(next -> {
                        next.setPrimaryImage(true);
                        imageRepo.save(next);
                    });
        }
    }

    @Override
    public Optional<VariantImage> get(Long variantId, Long imageId) {
        return imageRepo.findById(imageId)
                .filter(i -> i.getVariant().getId().equals(variantId));
    }

    @Override
    public Optional<VariantImage> getPrimary(Long variantId) {
        return imageRepo.findFirstByVariantIdAndPrimaryImageTrue(variantId);
    }

    @Override
    public long count(Long variantId) {
        return imageRepo.countByVariantId(variantId);
    }
}
