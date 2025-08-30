package com.uade.tpo.pixelpoint.services;

import com.uade.tpo.pixelpoint.entity.catalog.Variants;
import com.uade.tpo.pixelpoint.entity.marketplace.Listing;
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
        Variants variant = variantRepository.findById(variantId)
                .orElseThrow(() -> new NoSuchElementException("Variant no encontrada: " + variantId));

        Listing l = new Listing();
        l.setVariant(variant);

        // Si tenés entidad Seller y repo:
        if (sellerRepository != null) {
            Seller seller = sellerRepository.findById(sellerId)
                    .orElseThrow(() -> new NoSuchElementException("Seller no encontrado: " + sellerId));
            l.setSeller(seller);
        } else {
            // l.setSellerId(sellerId);
            throw new IllegalStateException("Aún no está integrado Seller/SellerRepository");
        }

        l.setPrice(price);
        l.setStock(stock != null ? stock : 0);
        l.setActive(active != null ? active : Boolean.TRUE);
        return listingRepository.save(l);
    }

    @Override
    public Listing update(Long listingId, Float price, Integer stock, Boolean active, Long sellerId) {
        Listing l = listingRepository.findById(listingId)
                .orElseThrow(() -> new NoSuchElementException("Listing no encontrado: " + listingId));

        // cuadno tengamos Auth, validamos que l.getSeller().getId().equals(sellerId)
        if (price != null)  l.setPrice(price);
        if (stock != null)  l.setStock(stock);
        if (active != null) l.setActive(active);
        return listingRepository.save(l);
    }

    @Override
    public void delete(Long listingId, Long sellerId) {
        listingRepository.deleteById(listingId);
    }
}
