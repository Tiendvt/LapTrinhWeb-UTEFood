package vn.iotstar.security.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import vn.iotstar.security.model.FavoriteProduct;
import vn.iotstar.security.model.Product;
import vn.iotstar.security.model.User;
import vn.iotstar.security.repository.FavoriteProductRepository;
import vn.iotstar.security.service.FavoriteService;
import vn.iotstar.security.service.ProductService;
import vn.iotstar.security.service.UserService;
@Service
public class FavoriteServiceImpl implements FavoriteService {
	  @Autowired
	    private UserService userService;
	    @Autowired
	    private FavoriteProductRepository favoriteProductRepository;
	    @Autowired
	    ProductService productService;
	    @Transactional
	@Override 
	public boolean toggleFavoriteProduct(String email, Integer productId) {
		User user = userService.getUserByEmail(email);
	    if (favoriteProductRepository.existsByUserIdAndProductId(user.getId(), productId)) {
	        favoriteProductRepository.deleteByUserIdAndProductId(user.getId(), productId);
	        return false; // Removed
	    } else {
	        FavoriteProduct favoriteProduct = new FavoriteProduct();
	        favoriteProduct.setUser(user);
	        favoriteProduct.setProduct(productService.getProductById(productId));
	        favoriteProductRepository.save(favoriteProduct);
	        return true; // Added
	    }
	}
		@Override
		public int countByProductId(int product_id) {
			
			return favoriteProductRepository.countByProductId(product_id);
		}


	

}
