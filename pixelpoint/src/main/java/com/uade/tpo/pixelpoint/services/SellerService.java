package com.uade.tpo.pixelpoint.services;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

import com.uade.tpo.pixelpoint.entity.marketplace.Seller;

public interface SellerService {
    Page<Seller> getSellers(PageRequest pageRequest);
    Optional<Seller> getSellerById(Long sellerId);
    Seller createSeller(String displayName, String shopDescription);
    Seller updateSeller(Seller seller);
    void deleteSeller(Long sellerId);
}
