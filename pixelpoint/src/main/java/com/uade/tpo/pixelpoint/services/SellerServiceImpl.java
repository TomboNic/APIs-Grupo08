package com.uade.tpo.pixelpoint.services;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import com.uade.tpo.pixelpoint.entity.marketplace.Seller;
import com.uade.tpo.pixelpoint.entity.marketplace.User;
import com.uade.tpo.pixelpoint.repository.marketplace.SellerRepository;
import com.uade.tpo.pixelpoint.repository.marketplace.UserRepository;

@Service
public class SellerServiceImpl implements SellerService {

    @Autowired
    private SellerRepository sellerRepository;

    @Autowired
    private UserRepository userRepository;
    @Override
    public Page<Seller> getSellers(PageRequest pageable) {
        return sellerRepository.findAll(pageable);
    }

    @Override
    public Optional<Seller> getSellerById(Long sellerId) {
        return sellerRepository.findById(sellerId);
    }

    @Override
    public Seller createSeller(String email, String shopName, String description) {
        User user = userRepository.findByEmail(email)
            .orElseThrow(() -> new IllegalStateException("Usuario no encontrado"));

        if (sellerRepository.existsByUserId(user.getId())) {
            throw new IllegalStateException("El usuario ya tiene un perfil de Seller");
        }

        Seller seller = new Seller();
        seller.setUser(user);  // <- CLAVE
        seller.setShopName(shopName);
        seller.setDescription(description);

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
