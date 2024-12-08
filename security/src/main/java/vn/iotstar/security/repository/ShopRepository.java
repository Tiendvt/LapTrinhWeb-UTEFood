package vn.iotstar.security.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import vn.iotstar.security.model.Shop;

@Repository
public interface ShopRepository extends JpaRepository<Shop, Integer> {

    Shop findByOwnerEmail(String email);

    boolean existsByOwnerEmail(String email);
    @Modifying
    @Query("UPDATE Shop s SET s.sold = s.sold + :quantity, s.revenue = s.revenue + :revenue WHERE s.id = :shopId")
    void updateShopSoldAndRevenue(@Param("shopId") Integer shopId, @Param("revenue") Double revenue,@Param("quantity") Integer quantity);

}
