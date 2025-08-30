package com.uade.tpo.pixelpoint.services;

import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import com.uade.tpo.pixelpoint.entity.marketplace.Listing;

public interface ListingService {
    // Catálogo público
    Page<Listing> catalog(Pageable pageable);
    Optional<Listing> getById(Long id);

    // ABM de la listing (Lo hace SELLER)
    Listing create(Long sellerId, Long variantId, Float price, Integer stock, Boolean active);
    Listing update(Long listingId, Float price, Integer stock, Boolean active, Long sellerId);
    void delete(Long listingId, Long sellerId);
}
