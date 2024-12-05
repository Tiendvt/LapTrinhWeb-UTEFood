package vn.iotstar.security.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import vn.iotstar.security.model.Shop;

@Repository
public interface ShopRepository extends JpaRepository<Shop, Integer> {

    Shop findByOwnerEmail(String email);

    boolean existsByOwnerEmail(String email);
}
