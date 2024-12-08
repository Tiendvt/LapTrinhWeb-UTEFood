package vn.iotstar.security.service.impl;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import vn.iotstar.security.model.Cart;
import vn.iotstar.security.model.OrderAddress;
import vn.iotstar.security.model.OrderRequest;
import vn.iotstar.security.model.ProductOrder;
import vn.iotstar.security.model.Review;
import vn.iotstar.security.model.Shop;
import vn.iotstar.security.repository.CartRepository;
import vn.iotstar.security.repository.ProductOrderRepository;
import vn.iotstar.security.repository.ReviewRepository;
import vn.iotstar.security.service.OrderService;
import vn.iotstar.security.util.CommonUtil;
import vn.iotstar.security.util.OrderStatus;

@Service
@Transactional
public class OrderServiceImpl implements OrderService {

    @Autowired
    private ProductOrderRepository orderRepository;

    @Autowired
    private CartRepository cartRepository;

    @Autowired
    private CommonUtil commonUtil;
    @Autowired
    private FileStorageService fileStorageService;
    @Autowired
    private ReviewRepository reviewRepository;
    @Override
    public void saveOrder(Integer userId, OrderRequest orderRequest) throws Exception {
        List<Cart> carts = cartRepository.findByUserId(userId);

        for (Cart cart : carts) {
            ProductOrder order = new ProductOrder();

            order.setOrderId(UUID.randomUUID().toString());
            order.setOrderDate(LocalDate.now());

            order.setProduct(cart.getProduct());
            order.setPrice(cart.getProduct().getDiscountPrice());
            order.setQuantity(cart.getQuantity());
            order.setUser(cart.getUser());
            order.setShop(cart.getProduct().getShop()); // Ensure shop linkage
            order.setStatus(OrderStatus.NEW_ORDER.getName());
            order.setPaymentType(orderRequest.getPaymentType());

            OrderAddress address = new OrderAddress();
            address.setFirstName(orderRequest.getFirstName());
            address.setLastName(orderRequest.getLastName());
            address.setEmail(orderRequest.getEmail());
            address.setMobileNo(orderRequest.getMobileNo());
            address.setAddress(orderRequest.getAddress());
            address.setCity(orderRequest.getCity());
            address.setProvince(orderRequest.getProvince());
            address.setPincode(orderRequest.getPincode());

            order.setOrderAddress(address);

            ProductOrder savedOrder = orderRepository.save(order);
            commonUtil.sendMailForProductOrder(savedOrder, "Order Placed Successfully");
        }
    }

    @Override
    public List<ProductOrder> getOrdersByUser(Integer userId) {
        return orderRepository.findByUserId(userId);
    }

    @Override
    public ProductOrder updateOrderStatus(Integer id, String status) {
        Optional<ProductOrder> optionalOrder = orderRepository.findById(id);
        if (optionalOrder.isPresent()) {
            ProductOrder order = optionalOrder.get();
            order.setStatus(status);
            return orderRepository.save(order);
        }
        return null;
    }

    @Override
    public List<ProductOrder> getAllOrders() {
        return orderRepository.findAll();
    }

    @Override
    public Page<ProductOrder> getAllOrdersPagination(Integer pageNo, Integer pageSize) {
        Pageable pageable = PageRequest.of(pageNo, pageSize);
        return orderRepository.findAll(pageable);
    }

    @Override
    public ProductOrder getOrdersByOrderId(String orderId) {
        return orderRepository.findByOrderId(orderId);
    }

    @Override
    public Page<ProductOrder> getOrdersByShopPagination(Shop shop, Integer pageNo, Integer pageSize) {
        Pageable pageable = PageRequest.of(pageNo, pageSize);
        return orderRepository.findAllByShop(shop, pageable);
    }

    @Override
    public Page<ProductOrder> getOrdersByStatusAndShop(String status, Shop shop, Integer pageNo, Integer pageSize) {
        Pageable pageable = PageRequest.of(pageNo, pageSize);
        return orderRepository.findAllByStatusAndShop(status, shop, pageable);
    }

    public List<ProductOrder> getOrdersByStatusAndUser(String status, Integer userId) {
        return orderRepository.findByStatusAndUserId(status, userId);
        
    }

    public void submitReview(Integer orderId, String comment, MultipartFile[] files) {
    	ProductOrder order = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Order not found"));

        if (!"Delivered".equalsIgnoreCase(order.getStatus())) {
            throw new RuntimeException("Only completed orders can be reviewed");
        }

        // Giả định có bảng Review hoặc lưu trực tiếp vào bảng ProductOrder
        Review review = new Review();
        review.setComment(comment);
        review.setOrder(order);

        // Xử lý upload file (nếu có)
        if (files != null) {
            for (MultipartFile file : files) {
                String fileName = fileStorageService.storeFile(file); // Lưu file và lấy đường dẫn
                review.addFile(fileName); // Giả sử có danh sách file trong entity Review
            }
        }

        reviewRepository.save(review); // Lưu đánh giá vào cơ sở dữ liệu
    }

	@Override
	public ProductOrder getOrderById(Integer id) {
		System.out.print("orderid: "+orderRepository.findById(id));
		return orderRepository.findById(id).orElse(null);
	}
	@Override
	public Map<Integer, Boolean> getReviewStatusForOrders(List<ProductOrder> orders) {
	    Map<Integer, Boolean> reviewStatus = new HashMap<>();
	    for (ProductOrder order : orders) {
	        boolean isReviewed = reviewRepository.existsByOrderId(order.getId());
	        reviewStatus.put(order.getId(), isReviewed);
	    }
	    return reviewStatus;
	}


	@Override
	public List<ProductOrder> getOrdersByStatus(String status) {
		return orderRepository.findAllByStatus(status);
	}
}