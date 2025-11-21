package com.uade.tpo.pixelpoint.repository.marketplace;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.uade.tpo.pixelpoint.entity.marketplace.Listing;

public interface ListingRepository extends JpaRepository<Listing, Long> {
    Page<Listing> findByActiveTrueAndStockGreaterThan(int stock, org.springframework.data.domain.Pageable pageable);

    Page<Listing> findBySellerId(Long sellerId, Pageable pageable);
    List<Listing> findBySellerId(Long sellerId);

    @Modifying
    @Query("""
            update Listing l
               set l.stock = l.stock - :qty
             where l.id = :id
               and l.stock >= :qty
            """)
    int decrementStock(@Param("id") Long listingId, @Param("qty") int qty);
}
