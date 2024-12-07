package vn.iotstar.security.service;

public interface FavoriteService {
	boolean toggleFavoriteProduct(String email, Integer productId);
	int countByProductId(int product_id);
}
