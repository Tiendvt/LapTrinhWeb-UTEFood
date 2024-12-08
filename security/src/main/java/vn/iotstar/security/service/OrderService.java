package vn.iotstar.security.service;

import java.util.List;
import java.util.Map;

import org.springframework.data.domain.Page;
import org.springframework.web.multipart.MultipartFile;

import vn.iotstar.security.model.OrderRequest;
import vn.iotstar.security.model.ProductOrder;

import vn.iotstar.security.model.Shop;



public interface OrderService {

    void saveOrder(Integer userId, OrderRequest orderRequest) throws Exception;

    List<ProductOrder> getOrdersByUser(Integer userId);

    ProductOrder updateOrderStatus(Integer id, String status);

    List<ProductOrder> getAllOrders();

    ProductOrder getOrdersByOrderId(String orderId);

    Page<ProductOrder> getAllOrdersPagination(Integer pageNo, Integer pageSize);

    Page<ProductOrder> getOrdersByShopPagination(Shop shop, Integer pageNo, Integer pageSize);

    Page<ProductOrder> getOrdersByStatusAndShop(String status, Shop shop, Integer pageNo, Integer pageSize);

    
    List<ProductOrder> getOrdersByStatus(String status);


    List<ProductOrder> getOrdersByStatusAndUser(String status, Integer userId);
    ProductOrder getOrderById(Integer id);
    void submitReview(Integer orderId, String comment, MultipartFile[] files);
    Map<Integer, Boolean> getReviewStatusForOrders(List<ProductOrder> orders);

	/**
	 * Get monthly revenue for a specific shop.
	 */
	Map<String, Double> getMonthlyRevenueForShop(Shop shop, int year);

	/**
	 * Get total products sold for a specific shop.
	 */
	int getTotalProductsSoldForShop(Shop shop);

	double getTotalRevenueForShop(Shop shop);
        
    void productIdToNull(int product_id);
}

