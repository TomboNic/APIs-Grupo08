package com.uade.tpo.pixelpoint.repository.cart;

import org.apache.catalina.startup.ClassLoaderFactory.Repository;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CartItemsRepository extends JpaRepository <Repository, Long>{
    
}
