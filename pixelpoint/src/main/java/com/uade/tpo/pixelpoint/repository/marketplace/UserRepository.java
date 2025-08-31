package com.uade.tpo.pixelpoint.repository.marketplace;

import org.springframework.data.jpa.repository.JpaRepository;
import com.uade.tpo.pixelpoint.entity.marketplace.User;

public interface UserRepository extends JpaRepository <User, Long>{
    
}
