package vn.iotstar.security.service.impl;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;
import org.springframework.web.multipart.MultipartFile;

import jakarta.transaction.Transactional;
import vn.iotstar.security.model.Category;
import vn.iotstar.security.model.Product;
import vn.iotstar.security.model.ProductOrder;
import vn.iotstar.security.model.Shop;
import vn.iotstar.security.repository.CartRepository;
import vn.iotstar.security.repository.CategoryRepository;
import vn.iotstar.security.repository.FavoriteProductRepository;
import vn.iotstar.security.repository.ProductRepository;
import vn.iotstar.security.service.ProductService;


@Service
@Transactional
public class ProductServiceImpl implements ProductService {

	@Autowired
	private ProductRepository productRepository;
	
	@Autowired
	private CategoryRepository categoryRepository;
	
	 @Autowired
	 private FavoriteProductRepository favoriteProductRepository;
	 
	 @Autowired
	private CartRepository cartRepository;
	 
	 @Autowired
	private OrderServiceImpl orderServiceImpl;

	@Override
	public Product saveProduct(Product product) {
		return productRepository.save(product);
	}

	@Override
	public List<Product> getAllProducts() {
		return productRepository.findAll();
	}

	@Override
	public Page<Product> getAllProductsPagination(Integer pageNo, Integer pageSize) {
		Pageable pageable = PageRequest.of(pageNo, pageSize);
		return productRepository.findAll(pageable);
	}

	@Override
	public Boolean deleteProduct(Integer id) {
		favoriteProductRepository.deleteByProduct_Id(id);
		cartRepository.deleteByProductId(id);
		orderServiceImpl.productIdToNull(id);
		
		Product product = productRepository.findById(id).orElse(null);

		if (!ObjectUtils.isEmpty(product)) {
			productRepository.delete(product);
			return true;
		}
		return false;
	}

	@Override
	public Product getProductById(Integer id) {
		Product product = productRepository.findById(id).orElse(null);
		return product;
	}

	@Override
	public Product updateProduct(Product product, MultipartFile image) {

		Product dbProduct = getProductById(product.getId());

		String imageName = image.isEmpty() ? dbProduct.getImage() : image.getOriginalFilename();

		dbProduct.setTitle(product.getTitle());
		dbProduct.setDescription(product.getDescription());
		dbProduct.setCategory(product.getCategory());
		dbProduct.setPrice(product.getPrice());
		dbProduct.setStock(product.getStock());
		dbProduct.setImage(imageName);
		dbProduct.setIsActive(product.getIsActive());
		dbProduct.setDiscount(product.getDiscount());

		// 5=100*(5/100); 100-5=95
		Double disocunt = product.getPrice() * (product.getDiscount() / 100.0);
		Double discountPrice = product.getPrice() - disocunt;
		dbProduct.setDiscountPrice(discountPrice);

		Product updateProduct = productRepository.save(dbProduct);

		if (!ObjectUtils.isEmpty(updateProduct)) {

			if (!image.isEmpty()) {

				try {
					File saveFile = new ClassPathResource("static/img").getFile();

					Path path = Paths.get(saveFile.getAbsolutePath() + File.separator + "product_img" + File.separator
							+ image.getOriginalFilename());
					Files.copy(image.getInputStream(), path, StandardCopyOption.REPLACE_EXISTING);

				} catch (Exception e) {
					e.printStackTrace();
				}
			}
			return product;
		}
		return null;
	}

	@Override
	public List<Product> getAllActiveProducts(String category_name) {
		List<Product> products = null;
		if (ObjectUtils.isEmpty(category_name)) {
			products = productRepository.findByIsActiveTrue();
		} else {
			Category cate = categoryRepository.findByName(category_name);
			products = productRepository.findByCategory(cate);
		}

		return products;
	}

	@Override
	public List<Product> searchProduct(String ch) {
		return productRepository.findByTitleContainingIgnoreCaseOrCategory_NameContainingIgnoreCase(ch, ch);
	}

	@Override
	public Page<Product> searchProductPagination(Integer pageNo, Integer pageSize, String ch, int type) {
		Pageable pageable = PageRequest.of(pageNo, pageSize);
		
		switch (type) {
	    case 2:
	        return productRepository.findByTitleContainingIgnoreCase(ch, pageable);
	    case 3:
	        return productRepository.findByCategory_NameContainingIgnoreCase(ch, pageable);
	    case 4:
	        return productRepository.findByShop_NameContainingIgnoreCase(ch, pageable);
	    default:
	        return productRepository.findByTitleContainingIgnoreCaseOrCategory_NameContainingIgnoreCaseOrShop_NameContainingIgnoreCase(ch, ch, ch, pageable);
		}
	}

	@Override
	public Page<Product> getAllActiveProductPagination(Integer pageNo, Integer pageSize, String category_name) {

		Pageable pageable = PageRequest.of(pageNo, pageSize);
		Page<Product> pageProduct = null;

		if (ObjectUtils.isEmpty(category_name)) {
			pageProduct = productRepository.findByIsActiveTrue(pageable);
		} else {	
			Category cate = categoryRepository.findByName(category_name);
			pageProduct = productRepository.findByCategory(pageable, cate);
		}
		return pageProduct;
	}

	@Override
	public Page<Product> searchActiveProductPagination(Integer pageNo, Integer pageSize, String category, String ch) {

		Page<Product> pageProduct = null;
		Pageable pageable = PageRequest.of(pageNo, pageSize);

		pageProduct = productRepository.findByisActiveTrueAndTitleContainingIgnoreCaseOrCategory_NameContainingIgnoreCase(ch,
				ch, pageable);

//		if (ObjectUtils.isEmpty(category)) {
//			pageProduct = productRepository.findByIsActiveTrue(pageable);
//		} else {
//			pageProduct = productRepository.findByCategory(pageable, category);
//		}
		return pageProduct;
	}

	@Override
	public Page<Product> getProductsByShop(Shop shop, Integer pageNo, Integer pageSize) {
	    Pageable pageable = PageRequest.of(pageNo, pageSize);
	    return productRepository.findByShop(shop, pageable);
	}
	@Override
	public List<Product> getDiscountedProducts() {
        return productRepository.findByDiscountGreaterThan(0);
    }

    // Apply promotion logic (update discount price based on discount field)
    @Override
	public void applyPromotion(Product product) {
        if (product.getDiscount() > 0) {
            double discountAmount = product.getPrice() * (product.getDiscount() / 100.0);
            product.setDiscountPrice(product.getPrice() - discountAmount);
        }
    }
    @Override
	public List<Product> getProductsSoldMoreThan10() {
        return productRepository.findBySoldGreaterThanOrderBySoldDesc(10);
    }

}

