package vn.iotstar.security.service;

import java.util.List;

import vn.iotstar.security.model.Cart;



public interface CartService {

	public Cart saveCart(Integer productId, Integer userId);

	public List<Cart> getCartsByUser(Integer userId);
	
	public Integer getCountCart(Integer userId);

	public void updateQuantity(String sy, Integer cid);
	public void clearCartByUserId(Integer userId);
}
