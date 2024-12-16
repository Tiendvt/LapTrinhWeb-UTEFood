package vn.iotstar.security.controllers;

import java.security.Principal;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.ObjectUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import vn.iotstar.security.model.Cart;
import vn.iotstar.security.model.Category;
import vn.iotstar.security.model.OrderRequest;
import vn.iotstar.security.model.Product;
import vn.iotstar.security.model.ProductOrder;
import vn.iotstar.security.model.Review;
import vn.iotstar.security.model.User;
import vn.iotstar.security.service.CartService;
import vn.iotstar.security.service.CategoryService;
import vn.iotstar.security.service.OrderService;
import vn.iotstar.security.service.ProductService;
import vn.iotstar.security.service.ReviewService;
import vn.iotstar.security.service.UserService;
import vn.iotstar.security.service.impl.FileStorageService;
import vn.iotstar.security.util.CommonUtil;
import vn.iotstar.security.util.OrderStatus;
@Controller
@RequestMapping("/user")
public class UserController {
	@Autowired
	private CategoryService categoryService;
	@Autowired
	private OrderService orderService;
	@Autowired
	private CartService cartService;
	@Autowired
	private UserService userService;
	@Autowired
	private FileStorageService fileStorageService;
	
	@Autowired
	private CommonUtil commonUtil;
		@Autowired
		private ProductService productService;
		@Autowired
		private ReviewService reviewService;
	@Autowired
	private PasswordEncoder passwordEncoder;
	
	//Thêm ModelAttribute để hiển thị nút điều hướng cho người dùng trên thanh nav bar của base.html. nếu ko có cái này thì trong base.html kiểm tra user==null và sau khi đăng nhập nó vẫn chỉ hiển thị 2 nút bấm LOGIN, REGISTER
	@ModelAttribute
	public void getUserDetails(Principal p, Model m) {
		if (p != null) {
			String email = p.getName();
			User user = userService.getUserByEmail(email);
			m.addAttribute("user", user);
			Integer countCart = cartService.getCountCart(user.getId());
			m.addAttribute("countCart", countCart);
		}

		List<Category> allActiveCategory = categoryService.getAllActiveCategory();
		m.addAttribute("categories", allActiveCategory);
	}
	@GetMapping("/")
	public String home(Model m) {
			
		List<Category> allActiveCategory = categoryService.getAllActiveCategory().stream()
				.sorted((c1, c2) -> c2.getId().compareTo(c1.getId())).limit(6).toList();
		List<Product> allActiveProducts = productService.getAllActiveProducts("").stream()
				.sorted((p1, p2) -> p2.getId().compareTo(p1.getId())).limit(8).toList();
		m.addAttribute("categories", allActiveCategory);
		m.addAttribute("products", allActiveProducts);
		return "/user/home";
	}
	@GetMapping("/addCart")
	public String addToCart(@RequestParam Integer pid, @RequestParam Integer uid, HttpSession session) {
		Cart saveCart = cartService.saveCart(pid, uid);

		if (ObjectUtils.isEmpty(saveCart)) {
			session.setAttribute("errorMsg", "Product add to cart failed");
		} else {
			session.setAttribute("succMsg", "Product added to cart");
		}
		 // Cập nhật số lượng sản phẩm trong giỏ
        int cartItemCount = cartService.getCountCart(uid);
        session.setAttribute("cartItemCount", cartItemCount);
		return "redirect:/product/" + pid;
	}
	@GetMapping("/cart")
	public String loadCartPage(Principal p, Model m) {

		User user = getLoggedInUserDetails(p);
		List<Cart> carts = cartService.getCartsByUser(user.getId());
		m.addAttribute("carts", carts);
		if (carts.size() > 0) {
			Double totalOrderPrice = carts.get(carts.size() - 1).getTotalOrderPrice();
			m.addAttribute("totalOrderPrice", totalOrderPrice);
		}
		
		return "user/cart";
	}
	private User getLoggedInUserDetails(Principal p) {
		String email = p.getName();
		User user = userService.getUserByEmail(email);
		return user;
	}
	@GetMapping("/cartQuantityUpdate")
	public String updateCartQuantity(@RequestParam String sy, @RequestParam Integer cid, HttpSession session) {
		Cart cart = cartService.getCartById(cid);
	    Product product = cart.getProduct();

	    // Kiểm tra nếu số lượng vượt quá stock
	    if ("in".equalsIgnoreCase(sy) && cart.getQuantity() + 1 > product.getStock()) {
	        session.setAttribute("errorMsg", "Số lượng sản phẩm vượt quá kho hiện tại!");
	        return "redirect:/user/cart";
	    }

	    // Cập nhật số lượng trong giỏ hàng
	    cartService.updateQuantity(sy, cid);
	    return "redirect:/user/cart";
	}
	@GetMapping("/orders")
	public String orderPage(Principal p, Model m) {
		User user = getLoggedInUserDetails(p);
		List<Cart> carts = cartService.getCartsByUser(user.getId());
		m.addAttribute("carts", carts);
		if (carts.size() > 0) {
			Double orderPrice = carts.get(carts.size() - 1).getTotalOrderPrice();
			Double totalOrderPrice = carts.get(carts.size() - 1).getTotalOrderPrice() + 250 + 100;
			m.addAttribute("orderPrice", orderPrice);
			m.addAttribute("totalOrderPrice", totalOrderPrice);
		}
		return "/user/order";
	}
	@PostMapping("/save-order")
	public String saveOrder(@ModelAttribute OrderRequest request, Principal p) throws Exception {
	    User user = getLoggedInUserDetails(p);

	    // Validate total price
	    if (request.getTotalPrice() == null) {
	        throw new IllegalArgumentException("Total price is required.");
	    }
	    // Giảm stock sản phẩm ngay khi đặt hàng
	    List<Cart> carts = cartService.getCartsByUser(user.getId());
	    for (Cart cart : carts) {
	        Product product = cart.getProduct();
	        if (product.getStock() < cart.getQuantity()) {
	            throw new IllegalArgumentException("Not enough stock for product: " + product.getTitle());
	        }
	        product.setStock(product.getStock() - cart.getQuantity());
	        productService.saveProduct(product);
	    }
	     
	    
	    
	    // Check payment type
	    if ("ONLINE".equalsIgnoreCase(request.getPaymentType())) {
	        // Generate VNPay URL
	        String vnpayUrl = "/api/payment/create_payment?amount="+ request.getTotalPrice();

	        // Save the order details before redirecting (if needed for tracking)
	        orderService.saveOrder(user.getId(), request);

	        
	        return "redirect:" + vnpayUrl;
	    } else if ("COD".equalsIgnoreCase(request.getPaymentType())) {
	        // Process Cash on Delivery (COD)
	        orderService.saveOrder(user.getId(), request);

	        // Clear cart after successful order placement
	        cartService.clearCartByUserId(user.getId());

	        // Redirect to success page
	        return "redirect:/user/success";
	    } else {
	        throw new IllegalArgumentException("Invalid payment type.");
	    }
	}

	@GetMapping("/success")
	public String loadSuccess() {
		return "/user/success";
	}
	@GetMapping("/user-orders")
	public String myOrder(Model m, Principal p) {
		User loginUser = getLoggedInUserDetails(p);
		List<ProductOrder> orders = orderService.getOrdersByUser(loginUser.getId());
		 // Kiểm tra trạng thái đánh giá cho từng đơn hàng
	    Map<Integer, Boolean> reviewStatus = new HashMap<>();
	    for (ProductOrder order : orders) {
	        boolean isReviewed = reviewService.existsByOrderId(order.getId());
	        reviewStatus.put(order.getId(), isReviewed);
	    }
		m.addAttribute("orders", orders);
		m.addAttribute("reviewStatus", reviewStatus); // Truyền thông tin trạng thái đánh giá vào giao diện
		m.addAttribute("activeTab", "all");
		
		return "/user/my_orders";
	}
	@GetMapping("/user-orders/{status}")
	public String getOrdersByStatus(@PathVariable String status, Model model, Principal principal) {
		User loginUser = getLoggedInUserDetails(principal);
	    List<ProductOrder> orders;

	    // Xử lý trạng thái tương ứng với các từ khóa mới
	    switch (status.toUpperCase()) {
	        case "ALL":
	            orders = orderService.getOrdersByUser(loginUser.getId());
	            break;
	        case "IN PROGRESS":
	        case "ORDER_RECEIVED":
	        case "PRODUCT_PACKED":
	        case "OUT_FOR_DELIVERY":
	        case "DELIVERED":
	        	
	        case "CANCELLED":
	        case "SUCCESS":
	            orders = orderService.getOrdersByStatusAndUser(status, loginUser.getId());
	            break;
	        default:
	            throw new IllegalArgumentException("Invalid status: " + status);
	    }
	 // Tính toán trạng thái đánh giá
	    Map<Integer, Boolean> reviewStatus = new HashMap<>();
	    for (ProductOrder order : orders) {
	        boolean isReviewed = reviewService.existsByOrderId(order.getId());
	        reviewStatus.put(order.getId(), isReviewed);
	    }
	    model.addAttribute("orders", orders);
	   // System.out.print("orders: " + orders.size() );
	    model.addAttribute("reviewStatus", reviewStatus);
	    model.addAttribute("activeTab", status.toLowerCase());
	    return "/user/my_orders"; // Đảm bảo file my_orders.html tồn tại
	}
	@GetMapping("/review-order/{orderId}")
	public String reviewOrder(@PathVariable Integer orderId, Model model) {
		System.out.print("orderID: "+orderId);
	    ProductOrder order = orderService.getOrderById(orderId);
	    //System.out.print("Bình thường ");
	    if (order == null || !"Delivered".equalsIgnoreCase(order.getStatus())) {
	        throw new RuntimeException("Order not found or not eligible for review");
	    }
	    model.addAttribute("order", order);
	    return "user/review_orders"; // Tên file HTML chứa form đánh giá
	}
	@PostMapping("/review-order")
	public String submitReview(@RequestParam Integer orderId,
	                           @RequestParam String comment,
	                           @RequestParam Integer rating, // Nhận rating từ form
	                           @RequestParam(value = "files", required = false) MultipartFile[] files, RedirectAttributes redirectAttributes) {
		 
		try {
		        if (files == null || files.length == 0 || Arrays.stream(files).allMatch(MultipartFile::isEmpty)) {
		            // Không có file được tải lên, chỉ lưu đánh giá text và rating
		            orderService.submitReview(orderId, comment, rating, null);
		        } else {
		            // Lưu đánh giá kèm các file được tải lên
		            orderService.submitReview(orderId, comment, rating, files);
		        }
		        redirectAttributes.addFlashAttribute("succMsg", "Review submitted successfully!");
		    } catch (Exception e) {
		        redirectAttributes.addFlashAttribute("errorMsg", "Error submitting review: " + e.getMessage());
		    }
	    return "redirect:/user/user-orders/delivered"; // Quay lại tab "Completed"
	}
	@GetMapping("/update-status")
	public String updateOrderStatus(@RequestParam Integer id, @RequestParam Integer st, HttpSession session) {

		OrderStatus[] values = OrderStatus.values();
		String status = null;

		for (OrderStatus orderSt : values) {
			if (orderSt.getId().equals(st)) {
				status = orderSt.getName();
			}
		}

		ProductOrder updateOrder = orderService.updateOrderStatus(id, status);
		
		try {
			commonUtil.sendMailForProductOrder(updateOrder, status);
		} catch (Exception e) {
			e.printStackTrace();
		}

		if (!ObjectUtils.isEmpty(updateOrder)) {
			session.setAttribute("succMsg", "Status Updated");
		} else {
			session.setAttribute("errorMsg", "status not updated");
		}
		return "redirect:/user/user-orders";
	}
	@GetMapping("/profile")
	public String profile() {
		return "user/profile";
	}
	@PostMapping("/update-profile")
	public String updateProfile(@ModelAttribute User user, @RequestParam MultipartFile img, HttpSession session) {
		System.out.print("Province cua User truoc khi qua service: "+ user.getProvince());
		User updateUserProfile = userService.updateUserProfile(user, img);
		System.out.print("Province cua User sau khi qua service: "+ updateUserProfile.getProvince());
		if (ObjectUtils.isEmpty(updateUserProfile)) {
			session.setAttribute("errorMsg", "Profile not updated");
		} else {
			session.setAttribute("succMsg", "Profile Updated");
		}
		return "redirect:/user/profile";
	}

	@PostMapping("/change-password")
	public String changePassword(@RequestParam String newPassword, @RequestParam String currentPassword, Principal p,
			HttpSession session) {
		User loggedInUserDetails = getLoggedInUserDetails(p);

		boolean matches = passwordEncoder.matches(currentPassword, loggedInUserDetails.getPassword());

		if (matches) {
			String encodePassword = passwordEncoder.encode(newPassword);
			loggedInUserDetails.setPassword(encodePassword);
			User updateUser = userService.updateUser(loggedInUserDetails);
			if (ObjectUtils.isEmpty(updateUser)) {
				session.setAttribute("errorMsg", "Password not updated !! Error in server");
			} else {
				session.setAttribute("succMsg", "Password Updated sucessfully");
			}
		} else {
			session.setAttribute("errorMsg", "Current Password incorrect");
		}

		return "redirect:/user/profile";
	}
	@GetMapping("/cancel-order/{id}")
	public String cancelOrder(@PathVariable Integer id, RedirectAttributes redirectAttributes) {
	    ProductOrder order = orderService.getOrderById(id);
	    
	    //Đơn hàng sẽ ko hủy nếu từ trạng thái được xác nhận trở lên.	
	    if (order == null || "Cancelled".equals(order.getStatus()) || 
	        "Delivered".equals(order.getStatus()) || 
	        "Order Confirmed".equals(order.getStatus()) || 
	        "In Transit".equals(order.getStatus())) {
	        redirectAttributes.addFlashAttribute("errorMsg", "Order cannot be cancelled.");
	        return "redirect:/user/user-orders";
	    }
	    
	    // Hoàn trả stock cho sản phẩm trong đơn hàng
	    Product product = order.getProduct(); // Truy cập sản phẩm trong đơn hàng
	    int quantity = order.getQuantity();   // Lấy số lượng sản phẩm trong đơn hàng
	   
	    // Cập nhật lại stock
	    product.setStock(product.getStock() + quantity);
	    productService.saveProduct(product);
	    
	    
	 // Cập nhật trạng thái đơn hàng thành "Cancelled" nếu hủy thành công.
	    order.setStatus(OrderStatus.CANCELLED.getName());
	    orderService.updateOrderStatus(order.getId(), OrderStatus.CANCELLED.getName());
	    redirectAttributes.addFlashAttribute("succMsg", "Order cancelled successfully.");
	    
	    return "redirect:/user/user-orders";
	}

    @GetMapping("/edit-review/{orderId}")
    public String editReview(@PathVariable Integer orderId, Model model) {
        ProductOrder order = orderService.getOrderById(orderId);

        if (order == null || !"Delivered".equalsIgnoreCase(order.getStatus())) {
            throw new RuntimeException("Order not found or not eligible for review");
        }

        Review review = reviewService.getReviewByOrderId(orderId); // Fetch review from database
        System.out.print("review object: " + review.getFileUrls());
        model.addAttribute("order", order);
        model.addAttribute("review", review); // Pass review data to the view
        
        model.addAttribute("rating", review.getRating()); // Pass rating to the view
      
        return "user/edit_review_orders"; // Use the same review_order.html template
    }

    @PostMapping("/update-review")
    public String updateReview(@RequestParam Integer reviewId,
                               @RequestParam String comment,
                               @RequestParam Integer rating, // Nhận rating từ form
                               @RequestParam("files") MultipartFile[] files,
                               RedirectAttributes redirectAttributes) {
    	 try {
    	        if (files == null || files.length == 0 || Arrays.stream(files).allMatch(MultipartFile::isEmpty)) {
    	            // Update review without files
    	            reviewService.updateReview(reviewId, comment, rating, null);
    	          
    	        } else {
    	            // Update review with files
    	            reviewService.updateReview(reviewId, comment, rating, files);
    	        }
    	        redirectAttributes.addFlashAttribute("succMsg", "Review updated successfully!");
    	    } catch (Exception e) {
    	        redirectAttributes.addFlashAttribute("errorMsg", "Error updating review: " + e.getMessage());
    	    }
    	    return "redirect:/user/user-orders";

    }
    @PostMapping("/upload-files")
    @ResponseBody
    public Map<String, Object> uploadFiles(@RequestParam("files") MultipartFile[] files) {
        Map<String, Object> response = new HashMap<>();
        try {
            // Lưu các file vào thư mục tĩnh
            List<String> fileUrls = Arrays.stream(files)
                                          .map(fileStorageService::storeFile)
                                          .collect(Collectors.toList());
            // Trả về danh sách các đường dẫn file
            response.put("success", true);
            response.put("fileUrls", fileUrls);
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "Failed to upload files: " + e.getMessage());
        }
        return response;
    }
    @GetMapping("/vnpay-payment")
    public String handleVnPayReturn(Principal p, HttpServletRequest request, Model model) {
        // Extract parameters from VNPay's callback
    	System.out.print("thanh toán");
        String orderInfo = request.getParameter("vnp_OrderInfo");
        String paymentTime = request.getParameter("vnp_PayDate");
        String transactionId = request.getParameter("vnp_TransactionNo");
        String totalPrice = request.getParameter("vnp_Amount");
        String paymentStatus = request.getParameter("vnp_ResponseCode");

        // Convert the amount back to normal format (VNPay sends the amount in *100)
        double actualPrice = Double.parseDouble(totalPrice) / 100;

        // Add parameters to the model for the view
        
        model.addAttribute("orderId", orderInfo);
        model.addAttribute("totalPrice", actualPrice);
        model.addAttribute("paymentTime", paymentTime);
        model.addAttribute("transactionId", transactionId);
        
        User user = getLoggedInUserDetails(p);

        // Check payment status

        if ("00".equals(paymentStatus)) { // "00" indicates success
        	// Clear cart after successful order placement
	        cartService.clearCartByUserId(user.getId());
        	       	

            return "user/ordersuccess"; // Redirect to success page
        } else {
        	
        	List<ProductOrder> order = orderService.getOrdersByStatusAndUser(OrderStatus.ONLINE.getName(), user.getId());
        	
        	   
        	order.get(0).setStatus(OrderStatus.CANCELLED.getName());
        	orderService.updateOrderStatus(order.get(0).getId(), OrderStatus.CANCELLED.getName());
        	
            return "user/orderfail"; // Redirect to failure page
        }
    }

}