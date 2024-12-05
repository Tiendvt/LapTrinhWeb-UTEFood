package vn.iotstar.security.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import vn.iotstar.security.model.Cart;



public interface CartRepository extends JpaRepository<Cart, Integer> {

	public Cart findByProductIdAndUserId(Integer productId, Integer userId);

	public Integer countByUserId(Integer userId);

	public List<Cart> findByUserId(Integer userId);

}
