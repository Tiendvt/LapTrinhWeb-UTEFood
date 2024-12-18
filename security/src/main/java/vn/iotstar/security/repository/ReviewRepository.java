package vn.iotstar.security.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import vn.iotstar.security.model.Review;
@Repository
public interface ReviewRepository extends JpaRepository<Review, Integer>{
	@Query("SELECT COUNT(r) > 0 FROM Review r WHERE r.order.id = :orderId")
	boolean existsByOrderId(Integer orderId);

    @Query("SELECT r FROM Review r WHERE r.order.id = :orderId")
    Optional<Review> findByOrderId(Integer orderId);
 
    @Query("SELECT r FROM Review r JOIN r.order o WHERE o.product.id = :productId")
    List<Review> findReviewsByProductId(@Param("productId") Integer productId);
}
