package vn.iotstar.security.service;

import java.util.List;

import org.springframework.data.domain.Page;

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
}

