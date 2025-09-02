package com.uade.tpo.pixelpoint.repository.marketplace;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import com.uade.tpo.pixelpoint.entity.marketplace.Seller;


public interface SellerRepository extends JpaRepository<Seller, Long> {
    Optional<Seller> findByUserId(Long userId);
    boolean existsByUserId(Long userId);
    boolean existsByShopNameIgnoreCase(String shopName);
}
