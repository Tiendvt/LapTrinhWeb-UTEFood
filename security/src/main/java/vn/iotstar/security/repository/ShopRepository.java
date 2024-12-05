package vn.iotstar.security.repository;


import org.springframework.data.jpa.repository.JpaRepository;

import vn.iotstar.security.model.Shop;

public interface ShopRepository extends JpaRepository<Shop, Integer> {

    Shop findByOwnerEmail(String email);
}

