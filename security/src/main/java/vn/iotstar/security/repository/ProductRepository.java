package vn.iotstar.security.repository;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

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

	List<Product> findByDiscountGreaterThan(int discount);
	List<Product> findBySoldGreaterThanOrderBySoldDesc(int soldCount);
}

