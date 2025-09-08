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

import com.uade.tpo.pixelpoint.entity.catalog.Brand;
import com.uade.tpo.pixelpoint.entity.catalog.DeviceModel;
import com.uade.tpo.pixelpoint.entity.dto.DeviceModelRequest;
import com.uade.tpo.pixelpoint.entity.dto.DeviceModelResponse;
import com.uade.tpo.pixelpoint.repository.catalog.BrandRepository;
import com.uade.tpo.pixelpoint.repository.catalog.DeviceModelRepository;

import jakarta.annotation.security.PermitAll;


@Validated
@RestController
@RequestMapping("device-models")
public class DeviceModelController {

    @Autowired
    private DeviceModelRepository deviceModelRepository; // usamos repo directo

    @Autowired
    private BrandRepository brandRepository; // para resolver brandId -> Brand

    // GET /device-models?page=0&size=20
    @GetMapping
    @PermitAll
    public ResponseEntity<Page<DeviceModelResponse>> getDeviceModels(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {

        Page<DeviceModel> models = deviceModelRepository.findAll(PageRequest.of(page, size));
        Page<DeviceModelResponse> body = models.map(this::toResponse);
        return ResponseEntity.ok(body);
    }

    // GET /device-models/{id}
    @GetMapping("/{id}")
    @PermitAll
    public ResponseEntity<DeviceModelResponse> getDeviceModelById(@PathVariable Long id) {
        Optional<DeviceModel> result = deviceModelRepository.findById(id);
        return result.map(m -> ResponseEntity.ok(toResponse(m)))
                     .orElseGet(() -> ResponseEntity.notFound().build());
    }

    // POST /device-models
    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN','SELLER')")
    public ResponseEntity<DeviceModelResponse> createDeviceModel(@RequestBody DeviceModelRequest request) {

        Brand brand = brandRepository.findById(request.getBrandId())
                .orElseThrow(() -> new IllegalArgumentException("brandId " + request.getBrandId() + " no existe"));

        DeviceModel created = new DeviceModel();
        created.setModelName(request.getModelName());
        created.setBrand(brand);
        created = deviceModelRepository.save(created);

        DeviceModelResponse body = toResponse(created);
        return ResponseEntity.created(URI.create("/device-models/" + created.getId())).body(body);
    }

    // ---------- Mapper (entity -> response) ----------
    private DeviceModelResponse toResponse(DeviceModel m) {
        DeviceModelResponse r = new DeviceModelResponse();
        r.setId(m.getId());
        r.setModelName(m.getModelName());

        Brand b = m.getBrand();
        if (b != null) {
            r.setBrandId(b.getId());
            try { r.setBrandName(b.getName()); } catch (Exception ignored) {}
        }
        return r;
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN','SELLER')")
    public ResponseEntity<DeviceModelResponse> updateDeviceModel(@PathVariable Long id, @RequestBody DeviceModelRequest request) {
        Optional<DeviceModel> opt = deviceModelRepository.findById(id);
        if (opt.isEmpty()) return ResponseEntity.notFound().build();

        Brand brand = brandRepository.findById(request.getBrandId())
                .orElseThrow(() -> new IllegalArgumentException("brandId " + request.getBrandId() + " no existe"));

        DeviceModel m = opt.get();
        m.setModelName(request.getModelName().trim());
        m.setBrand(brand);
        DeviceModel saved = deviceModelRepository.save(m);

        return ResponseEntity.ok(toResponse(saved));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN','SELLER')")
    public ResponseEntity<Void> deleteDeviceModel(@PathVariable Long id) {
        if (!deviceModelRepository.existsById(id)) return ResponseEntity.notFound().build();
        deviceModelRepository.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}