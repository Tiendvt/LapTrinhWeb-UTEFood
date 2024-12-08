package vn.iotstar.security.repository;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import vn.iotstar.security.model.Category;
import vn.iotstar.security.model.Product;
import vn.iotstar.security.model.Shop;

public interface ProductRepository extends JpaRepository<Product, Integer> {

	List<Product> findByIsActiveTrue();

	Page<Product> findByIsActiveTrue(Pageable pageable);

	List<Product> findByCategory(Category category);

	List<Product> findByTitleContainingIgnoreCaseOrCategory_NameContainingIgnoreCase(String ch, String ch2);

	Page<Product> findByCategory(Pageable pageable, Category category);

	Page<Product> findByTitleContainingIgnoreCaseOrCategory_NameContainingIgnoreCaseOrShop_NameContainingIgnoreCase(
			String ch, String ch2, String ch3, Pageable pageable);

	Page<Product> findByTitleContainingIgnoreCase(String ch, Pageable pageable);

	Page<Product> findByCategory_NameContainingIgnoreCase(String ch, Pageable pageable);

	Page<Product> findByShop_NameContainingIgnoreCase(String ch, Pageable pageable);

	Page<Product> findByisActiveTrueAndTitleContainingIgnoreCaseOrCategory_NameContainingIgnoreCase(String ch,
			String ch2, Pageable pageable);

	Page<Product> findByShop(Shop shop, Pageable pageable);
	Page<Product> findByShopAndTitleContainingIgnoreCase(Shop shop, String searchQuery, Pageable pageable);
	List<Product> findByDiscountGreaterThan(int discount);
	List<Product> findBySoldGreaterThanOrderBySoldDesc(int soldCount);
	///---- Lọc sản phẩm ----
	// Lọc sản phẩm theo tiêu chí
	// Sản phẩm mới nhất
	@Query("SELECT p FROM Product p WHERE p.isActive = TRUE ORDER BY p.createdDate DESC")
	Page<Product> findNewestProducts(Pageable pageable);

	// Sản phẩm bán chạy nhất
	@Query("SELECT p FROM Product p WHERE p.isActive = TRUE ORDER BY p.sold DESC")
	Page<Product> findBestSellingProducts(Pageable pageable);

	// Sản phẩm được đánh giá cao nhất
	@Query("SELECT p FROM Product p WHERE p.isActive = TRUE ORDER BY (SELECT COUNT(r) FROM Review r WHERE r.order.product = p) DESC")
	Page<Product> findTopRatedProducts(Pageable pageable);

	// Sản phẩm được yêu thích nhiều nhất
	@Query("SELECT p FROM Product p WHERE p.isActive = TRUE ORDER BY (SELECT COUNT(f) FROM FavoriteProduct f WHERE f.product = p) DESC")
	Page<Product> findMostFavoriteProducts(Pageable pageable);

}

