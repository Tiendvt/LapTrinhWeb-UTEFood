package vn.iotstar.security.repository;


import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import vn.iotstar.security.model.ProductOrder;
import vn.iotstar.security.model.Shop;

import java.util.List;

@Repository
public interface ProductOrderRepository extends JpaRepository<ProductOrder, Integer> {

    // Fetch orders by user ID
    List<ProductOrder> findByUserId(Integer userId);

    // Fetch an order by order ID
    ProductOrder findByOrderId(String orderId);

    // Fetch orders by shop
    List<ProductOrder> findAllByShop(Shop shop);

    // Fetch paginated orders by shop
    Page<ProductOrder> findAllByShop(Shop shop, Pageable pageable);

    // Fetch paginated orders by status and shop
    Page<ProductOrder> findAllByStatusAndShop(String status, Shop shop, Pageable pageable);
    List<ProductOrder> findByStatusAndUserId(String status, Integer userId);

}
