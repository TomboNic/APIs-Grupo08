package com.uade.tpo.pixelpoint.controllers;

import java.net.URI;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.uade.tpo.pixelpoint.entity.catalog.DeviceModel;
import com.uade.tpo.pixelpoint.entity.catalog.Variants;
import com.uade.tpo.pixelpoint.entity.dto.VariantsRequest;
import com.uade.tpo.pixelpoint.entity.dto.VariantsResponse;
import com.uade.tpo.pixelpoint.images.VariantImageService;
import com.uade.tpo.pixelpoint.images.VariantImageServiceImpl;
import com.uade.tpo.pixelpoint.repository.catalog.DeviceModelRepository;
import com.uade.tpo.pixelpoint.repository.catalog.VariantsRepository;
import com.uade.tpo.pixelpoint.services.VariantService;

import jakarta.annotation.security.PermitAll;

@Validated
@RestController
@RequestMapping("variants")
public class VariantsController {

    @Autowired
    private VariantsRepository variantsRepository;

    @Autowired
    private DeviceModelRepository deviceModelRepository;

    @Autowired
    private VariantService variantService;

    @Autowired
    private VariantImageService variantImageService;
    
    @GetMapping
    @PermitAll
    public ResponseEntity<Page<VariantsResponse>> getVariants(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {

        Page<Variants> variants = variantService.getVariants(PageRequest.of(page, size));
        Page<VariantsResponse> body = variants.map(this::toResponse);
        return ResponseEntity.ok(body);
    }

    @GetMapping("/{variantId}")
    @PermitAll
    public ResponseEntity<VariantsResponse> getVariantById(@PathVariable Long variantId) {
        Optional<Variants> result = variantService.getVariantById(variantId);
        return result.map(v -> ResponseEntity.ok(toResponse(v)))
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN','SELLER')")
    public ResponseEntity<VariantsResponse> createVariant(@RequestBody VariantsRequest request) {
        Variants created = variantService.createVariant(
                request.getDeviceModelId(),
                request.getRam(),
                request.getStorage(),
                request.getColor(),
                request.getCondition());
        VariantsResponse body = toResponse(created);
        return ResponseEntity.created(URI.create("/variants/" + created.getId())).body(body);
    }

    // ---------- Mapper (entidad -> respuesta) ----------
    private VariantsResponse toResponse(Variants v) {
    VariantsResponse r = new VariantsResponse();

    // Datos básicos de la variant
    r.setId(v.getId());

    if (v.getDeviceModel() != null) {
        r.setDeviceModelId(v.getDeviceModel().getId());
        // si tu clase DeviceModel tiene getModelName(), usalo; si se llama distinto, ajustá
        r.setDeviceModelName(v.getDeviceModel().getModelName());
    }

    r.setRam(v.getRam());
    r.setStorage(v.getStorage());
    r.setColor(v.getColor());
    r.setCondition(v.getCondition());

    // --- IMÁGENES ---
    // Obtener la imagen principal (si existe)
    var primaryOpt = variantImageService.getPrimary(v.getId());

    // ID de la imagen principal
    Long primaryId = primaryOpt.map(vi -> vi.getId()).orElse(null);
    r.setPrimaryImageId(primaryId);

    // Cantidad total de imágenes asociadas
    r.setImageCount((int) variantImageService.count(v.getId()));

    // URL directa para mostrarla sin llamar al controller de imágenes
    // (Si preferís absoluta, podés usar ServletUriComponentsBuilder)
    r.setPrimaryImageUrl(
        primaryOpt
            .map(vi -> "/variants/" + v.getId() + "/images/" + vi.getId() + "/bytes")
            .orElse(null)
    );

    return r;
}

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN','SELLER')")
    public ResponseEntity<VariantsResponse> updateVariant(@PathVariable Long id, @RequestBody VariantsRequest request) {
        Optional<Variants> opt = variantsRepository.findById(id);
        if (opt.isEmpty())
            return ResponseEntity.notFound().build();

        DeviceModel dm = deviceModelRepository.findById(request.getDeviceModelId())
                .orElseThrow(() -> new IllegalArgumentException(
                        "deviceModelId " + request.getDeviceModelId() + " no existe"));

        Variants v = opt.get();
        v.setDeviceModel(dm);
        v.setRam(request.getRam());
        v.setStorage(request.getStorage());
        v.setColor(request.getColor().trim());
        v.setCondition(request.getCondition());

        Variants saved = variantsRepository.save(v);
        return ResponseEntity.ok(toResponse(saved));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN','SELLER')")
    public ResponseEntity<Void> deleteVariant(@PathVariable Long id) {
        if (!variantsRepository.existsById(id))
            return ResponseEntity.notFound().build();
        variantsRepository.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}