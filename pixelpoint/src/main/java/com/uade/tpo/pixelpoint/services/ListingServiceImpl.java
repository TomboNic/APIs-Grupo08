package com.uade.tpo.pixelpoint.services;

import com.uade.tpo.pixelpoint.entity.catalog.Variants;
import com.uade.tpo.pixelpoint.entity.marketplace.Listing;
import com.uade.tpo.pixelpoint.entity.marketplace.Listing.DiscountType;
import com.uade.tpo.pixelpoint.entity.marketplace.Seller;
import com.uade.tpo.pixelpoint.repository.catalog.VariantsRepository;
import com.uade.tpo.pixelpoint.repository.marketplace.ListingRepository;
import com.uade.tpo.pixelpoint.repository.marketplace.SellerRepository; // si tenés Seller como entidad
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.NoSuchElementException;
import java.util.Optional;

@Service
public class ListingServiceImpl implements ListingService {

    @Autowired
    private ListingRepository listingRepository;
    @Autowired
    private VariantsRepository variantRepository;
    @Autowired(required = false)
    private SellerRepository sellerRepository;

    @Override
    public Page<Listing> catalog(Pageable pageable) {
        return listingRepository.findByActiveTrueAndStockGreaterThan(0, pageable);
    }

    @Override
    public Optional<Listing> getById(Long id) {
        return listingRepository.findById(id);
    }

    @Override
    public Listing create(Long sellerId, Long variantId, Float price, Integer stock, Boolean active) {
        return create(sellerId, variantId, price, stock, active,
                null, null, null); // sin descuento explícito => defaults
    }

    @Override
    public Listing update(Long listingId, Float price, Integer stock, Boolean active, Long sellerId) {
        return update(listingId, price, stock, active, sellerId,
                null, null, null); // no cambia descuento si viene null
    }

    @Override
    public Listing create(Long sellerId, Long variantId, Float price, Integer stock, Boolean active,
            DiscountType discountType, Double discountValue, Boolean discountActive) {

        Variants variant = variantRepository.findById(variantId)
                .orElseThrow(() -> new NoSuchElementException("Variant no encontrada: " + variantId));

        Listing l = new Listing();
        l.setVariant(variant);

        if (sellerRepository != null) {
            Seller seller = sellerRepository.findById(sellerId)
                    .orElseThrow(() -> new NoSuchElementException("Seller no encontrado: " + sellerId));
            l.setSeller(seller);
        } else {
            throw new IllegalStateException("Aún no está integrado Seller/SellerRepository");
        }

        l.setPrice(price);
        l.setStock(stock != null ? stock : 0);
        l.setActive(active != null ? active : Boolean.TRUE);

        DiscountType type = discountType != null ? discountType : DiscountType.NONE;
        Double value = discountValue != null ? discountValue : 0.0;
        Boolean dActive = discountActive != null ? discountActive : Boolean.FALSE;

        if (type == DiscountType.PERCENT) {
            value = Math.max(0.0, Math.min(100.0, value));
        }
        if (type == DiscountType.NONE) {
            dActive = false;
            value = 0.0;
        }

        l.setDiscountType(type);
        l.setDiscountValue(value);
        l.setDiscountActive(dActive);

        return listingRepository.save(l);
    }

    @Override
    public Listing update(Long listingId, Float price, Integer stock, Boolean active, Long sellerId,
            DiscountType discountType, Double discountValue, Boolean discountActive) {

        Listing l = listingRepository.findById(listingId)
                .orElseThrow(() -> new NoSuchElementException("Listing no encontrado: " + listingId));

        // TODO: cuando tengas Auth, validar que l.getSeller().getId().equals(sellerId)

        if (price != null)
            l.setPrice(price);
        if (stock != null)
            l.setStock(stock);
        if (active != null)
            l.setActive(active);

        if (discountType != null) {
            var type = discountType;
            var value = discountValue != null ? discountValue : l.getDiscountValue();
            var dActive = discountActive != null ? discountActive : l.getDiscountActive();

            if (type == DiscountType.PERCENT) {
                value = Math.max(0.0, Math.min(100.0, value));
            }
            if (type == DiscountType.NONE) {
                dActive = false;
                value = 0.0;
            }

            l.setDiscountType(type);
            l.setDiscountValue(value);
            l.setDiscountActive(dActive);
        } else {
            if (discountValue != null)
                l.setDiscountValue(discountValue);
            if (discountActive != null)
                l.setDiscountActive(discountActive);
        }

        return listingRepository.save(l);
    }

    @Override
    public void delete(Long listingId, Long sellerId) {
        listingRepository.deleteById(listingId);
    }
}