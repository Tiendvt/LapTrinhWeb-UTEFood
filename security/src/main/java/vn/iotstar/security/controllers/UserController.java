package vn.iotstar.security.controllers;

import java.security.Principal;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.ObjectUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;


import jakarta.servlet.http.HttpSession;
import vn.iotstar.security.model.Cart;
import vn.iotstar.security.model.Category;
import vn.iotstar.security.model.OrderRequest;
import vn.iotstar.security.model.Product;
import vn.iotstar.security.model.ProductOrder;
import vn.iotstar.security.model.User;
import vn.iotstar.security.service.CartService;
import vn.iotstar.security.service.CategoryService;
import vn.iotstar.security.service.OrderService;
import vn.iotstar.security.service.ProductService;
import vn.iotstar.security.service.UserService;
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
	private CommonUtil commonUtil;
	@Autowired
	private ProductService productService;
	@GetMapping("/")
	public String home(Model m) {

		List<Category> allActiveCategory = categoryService.getAllActiveCategory().stream()
				.sorted((c1, c2) -> c2.getId().compareTo(c1.getId())).limit(6).toList();
		List<Product> allActiveProducts = productService.getAllActiveProducts("").stream()
				.sorted((p1, p2) -> p2.getId().compareTo(p1.getId())).limit(8).toList();
		m.addAttribute("category", allActiveCategory);
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
		return "/user/cart";
	}
	private User getLoggedInUserDetails(Principal p) {
		String email = p.getName();
		User user = userService.getUserByEmail(email);
		return user;
	}
	@GetMapping("/cartQuantityUpdate")
	public String updateCartQuantity(@RequestParam String sy, @RequestParam Integer cid) {
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
		// System.out.println(request);
		User user = getLoggedInUserDetails(p);
		orderService.saveOrder(user.getId(), request);

		return "redirect:/user/success";
	}
	@GetMapping("/success")
	public String loadSuccess() {
		return "/user/success";
	}
	@GetMapping("/user-orders")
	public String myOrder(Model m, Principal p) {
		User loginUser = getLoggedInUserDetails(p);
		List<ProductOrder> orders = orderService.getOrdersByUser(loginUser.getId());
		m.addAttribute("orders", orders);
		return "/user/my_orders";
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
}
