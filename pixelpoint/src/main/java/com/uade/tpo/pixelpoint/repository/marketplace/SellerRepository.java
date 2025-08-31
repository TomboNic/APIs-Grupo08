package com.uade.tpo.pixelpoint.repository.marketplace;

import org.springframework.data.jpa.repository.JpaRepository;
import com.uade.tpo.pixelpoint.entity.marketplace.Seller;

public interface SellerRepository extends JpaRepository<Seller, Long>{
    
}
