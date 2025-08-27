package com.uade.tpo.pixelpoint.repository.marketplace;

import org.springframework.data.jpa.repository.JpaRepository;

import com.uade.tpo.pixelpoint.entity.marketplace.Listing;

public interface ListingRepository extends JpaRepository<Listing, Long>{
    
}
