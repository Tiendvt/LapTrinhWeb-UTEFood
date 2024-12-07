package vn.iotstar.security.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import vn.iotstar.security.model.FavoriteProduct;
@Repository
public interface FavoriteProductRepository extends JpaRepository<FavoriteProduct, Integer> {
	 boolean existsByUserIdAndProductId(Integer userId, Integer productId);
	 void deleteByUserIdAndProductId(Integer userId, Integer productId);
	 FavoriteProduct findByUserIdAndProductId(Integer userId, Integer productId);
	 int countByProductId(int product_id);
}
