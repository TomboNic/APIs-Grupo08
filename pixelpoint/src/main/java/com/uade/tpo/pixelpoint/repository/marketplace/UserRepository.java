package com.uade.tpo.pixelpoint.repository.marketplace;

import org.springframework.data.jpa.repository.JpaRepository;
import com.uade.tpo.pixelpoint.entity.marketplace.User;
import java.util.Optional;
import org.springframework.stereotype.Repository;


@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByEmail(String email);
}
