
package vn.iotstar.security.service;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.web.multipart.MultipartFile;




import vn.iotstar.security.model.Product;
import vn.iotstar.security.model.Shop;


public interface ProductService {

	public Product saveProduct(Product product);

	public List<Product> getAllProducts();

	public Boolean deleteProduct(Integer id);

	public Product getProductById(Integer id);

	public Product updateProduct(Product product, MultipartFile file);

	public List<Product> getAllActiveProducts(String category);

	public List<Product> searchProduct(String ch);

	public Page<Product> getAllActiveProductPagination(Integer pageNo, Integer pageSize, String category);

	public Page<Product> searchProductPagination(Integer pageNo, Integer pageSize, String ch, int type);

	public Page<Product> getAllProductsPagination(Integer pageNo, Integer pageSize);

	public Page<Product> searchActiveProductPagination(Integer pageNo, Integer pageSize, String category, String ch);

	public Page<Product> getProductsByShop(Shop shop, Integer pageNo, Integer pageSize);

	public List<Product> getProductsByCategory(String category);
}