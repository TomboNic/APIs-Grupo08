package com.uade.tpo.pixelpoint.services;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import com.uade.tpo.pixelpoint.entity.marketplace.Seller;
import com.uade.tpo.pixelpoint.repository.marketplace.SellerRepository;

@Service
public class SellerServiceImpl implements SellerService {

    @Autowired
    private SellerRepository sellerRepository;

    @Override
    public Page<Seller> getSellers(PageRequest pageable) {
        return sellerRepository.findAll(pageable);
    }

    @Override
    public Optional<Seller> getSellerById(Long sellerId) {
        return sellerRepository.findById(sellerId);
    }

    @Override
    public Seller createSeller(String displayName, String shopDescription) {
        Seller seller = new Seller();
        seller.setDisplayName(displayName);
        seller.setShopDescription(shopDescription);
        return sellerRepository.save(seller);
    }

    @Override
    public Seller updateSeller(Seller seller) {
        return sellerRepository.save(seller);
    }

    @Override
    public void deleteSeller(Long sellerId) {
        sellerRepository.deleteById(sellerId);
    }
}
