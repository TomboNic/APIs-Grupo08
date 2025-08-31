package com.uade.tpo.pixelpoint.repository.marketplace;

import org.springframework.data.domain.Page;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.domain.Pageable;

import com.uade.tpo.pixelpoint.entity.marketplace.Listing;

public interface ListingRepository extends JpaRepository<Listing, Long> {
     Page<Listing> findByActiveTrueAndStockGreaterThan(int stock, org.springframework.data.domain.Pageable pageable);
    Page<Listing> findBySellerId(Long sellerId, Pageable pageable);

}
