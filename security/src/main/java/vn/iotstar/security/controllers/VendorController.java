package vn.iotstar.security.controllers;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.security.Principal;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.domain.Page;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.ObjectUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import jakarta.servlet.http.HttpSession;
import vn.iotstar.security.model.Category;
import vn.iotstar.security.model.Product;
import vn.iotstar.security.model.ProductOrder;
import vn.iotstar.security.model.Shop;
import vn.iotstar.security.model.User;
import vn.iotstar.security.service.CategoryService;
import vn.iotstar.security.service.OrderService;
import vn.iotstar.security.service.ProductService;
import vn.iotstar.security.service.ShopService;
import vn.iotstar.security.service.UserService;
import vn.iotstar.security.util.CommonUtil;
import vn.iotstar.security.util.OrderStatus;

@Controller
@RequestMapping("/vendor")
public class VendorController {

    @Autowired
    private ShopService shopService;

    @Autowired
    private ProductService productService;

    @Autowired
    private OrderService orderService;

    @Autowired
    private UserService userService;

    @Autowired
    private CategoryService categoryService;
    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
	private CommonUtil commonUtil;
    // Vendor Dashboard
    private User getLoggedInUserDetails(Principal p) {
		String email = p.getName();
		User userDtls = userService.getUserByEmail(email);
		return userDtls;
	}
	//Thêm ModelAttribute để hiển thị nút điều hướng cho người dùng trên thanh nav bar của base.html. nếu ko có cái này thì trong base.html kiểm tra user==null và sau khi đăng nhập nó vẫn chỉ hiển thị 2 nút bấm LOGIN, REGISTER
		@ModelAttribute
		public void getUserDetails(Principal p, Model m) {
			if (p != null) {
				String email = p.getName();
				User user = userService.getUserByEmail(email);
				m.addAttribute("user", user);
			}

			List<Category> allActiveCategory = categoryService.getAllActiveCategory();
			m.addAttribute("categories", allActiveCategory);
		}
    @GetMapping("/")
    public String vendorDashboard(Principal principal, Model model) {

        Shop shop = shopService.getShopByOwnerEmail(principal.getName());

        model.addAttribute("shop", shop);

        return "vendor/dashboard";
    }


    // View Products
    @GetMapping("/products")
    public String viewProducts(Principal principal, Model model,
                               @RequestParam(defaultValue = "0") int pageNo,
                               @RequestParam(defaultValue = "10") int pageSize,
                               @RequestParam(defaultValue = "") String searchQuery) {
        Shop shop = shopService.getShopByOwnerEmail(principal.getName());

        Page<Product> productsPage;
        if (searchQuery.isEmpty()) {
            productsPage = productService.getProductsByShop(shop, pageNo, pageSize);
        } else {
            productsPage = productService.searchVendorProductsPagination(shop, searchQuery, pageNo, pageSize);
        }

        model.addAttribute("products", productsPage.getContent());
        model.addAttribute("pageNo", pageNo);
        model.addAttribute("pageSize", pageSize);
        model.addAttribute("totalElements", productsPage.getTotalElements());
        model.addAttribute("totalPages", productsPage.getTotalPages());
        model.addAttribute("isFirst", productsPage.isFirst());
        model.addAttribute("isLast", productsPage.isLast());
        model.addAttribute("searchQuery", searchQuery);

        return "vendor/products"; // Update this with the actual view for product list
    }

    // Add Product Page
    @GetMapping("/add-product")
    public String addProductPage(Model model) {
        model.addAttribute("product", new Product());
        List<Category> categories = categoryService.getAllCategory();
        model.addAttribute("categories", categories);
        return "vendor/add_product";
    }

    // Save Product
    @PostMapping("/save-product")
    public String saveProduct(@ModelAttribute Product product,
                              @RequestParam("file") MultipartFile image,
                              Principal principal,
                              Model model) {
        Shop shop = shopService.getShopByOwnerEmail(principal.getName());
        product.setShop(shop);
        product.setDiscount(0);
        product.setDiscountPrice(product.getPrice());

        if (image != null && !image.isEmpty()) {
            try {
                String imageName = image.getOriginalFilename();
                product.setImage(imageName);

                File saveFile = new ClassPathResource("static/img").getFile();
                Path path = Paths.get(saveFile.getAbsolutePath() + File.separator + "product_img" + File.separator + imageName);
                Files.copy(image.getInputStream(), path, StandardCopyOption.REPLACE_EXISTING);
            } catch (IOException e) {
                model.addAttribute("errorMsg", "Error uploading image. Please try again.");
                return "vendor/add_product";
            }
        }

        productService.saveProduct(product);
        return "redirect:/vendor/products";
    }

    // Edit Product
    @GetMapping("/edit-product/{id}")
    public String editProduct(@PathVariable("id") Integer id, Model model, Principal principal) {
        Product product = productService.getProductById(id);
        Shop shop = shopService.getShopByOwnerEmail(principal.getName());
        
		model.addAttribute("categories", categoryService.getAllCategory());
        if (!product.getShop().getId().equals(shop.getId())) {
            model.addAttribute("errorMsg", "Unauthorized action. You cannot edit this product.");
            return "vendor/products";
        }

        model.addAttribute("product", product);
        return "vendor/edit_product";
    }

    // Update Product
    @PostMapping("/update-product")
    public String updateProduct(@ModelAttribute Product product,
                                @RequestParam("file") MultipartFile image,
                                Model model,
                                Principal principal) {
        Shop shop = shopService.getShopByOwnerEmail(principal.getName());
        Product existingProduct = productService.getProductById(product.getId());

        if (!existingProduct.getShop().getId().equals(shop.getId())) {
            model.addAttribute("errorMsg", "Unauthorized action. You cannot modify this product.");
            return "vendor/products";
        }

        try {
            Product updatedProduct = productService.updateProduct(product, image);
            if (updatedProduct == null) {
                model.addAttribute("errorMsg", "Error updating product. Please try again.");
                return "vendor/edit_product";
            }
        } catch (Exception e) {
            model.addAttribute("errorMsg", "Error updating product. Please try again.");
            return "vendor/edit_product";
        }

        model.addAttribute("succMsg", "Product updated successfully.");
        return "redirect:/vendor/products";
    }

    // Delete Product
    @GetMapping("/delete-product/{id}")
    public String deleteProduct(@PathVariable("id") Integer id, Principal principal, Model model) {
        Product product = productService.getProductById(id);
        Shop shop = shopService.getShopByOwnerEmail(principal.getName());

        if (!product.getShop().getId().equals(shop.getId())) {
            model.addAttribute("errorMsg", "Unauthorized action. You cannot delete this product.");
            return "vendor/products";
        }

        productService.deleteProduct(id);
        return "redirect:/vendor/products";
    }

    @GetMapping("/orders")
    public String viewOrders(Principal principal, Model model,
                             @RequestParam(name = "pageNo", defaultValue = "0") Integer pageNo,
                             @RequestParam(name = "pageSize", defaultValue = "10") Integer pageSize) {
        // Retrieve the shop associated with the logged-in vendor
        Shop shop = shopService.getShopByOwnerEmail(principal.getName());

        // Retrieve orders for the shop, passing the page number and page size for pagination
        Page<ProductOrder> page = orderService.getOrdersByShopPagination(shop, pageNo, pageSize);

        // Add necessary attributes to the model for pagination and orders
        model.addAttribute("orders", page.getContent());  // Orders specific to the shop
        model.addAttribute("srch", false);  // Set to false as no search is done
        model.addAttribute("pageNo", page.getNumber());
        model.addAttribute("pageSize", pageSize);
        model.addAttribute("totalElements", page.getTotalElements());
        model.addAttribute("totalPages", page.getTotalPages());
        model.addAttribute("isFirst", page.isFirst());
        model.addAttribute("isLast", page.isLast());

        // Return the vendor orders page view
        return "vendor/orders";  // View for vendor orders
    }




    @PostMapping("/update-order-status")
    public String updateOrderStatus(@RequestParam Integer id, @RequestParam Integer st, HttpSession session) {
        // Get the status from the OrderStatus enum
        OrderStatus[] values = OrderStatus.values();
        String status = null;

        // Map the status ID to its corresponding status name
        for (OrderStatus orderSt : values) {
            if (orderSt.getId().equals(st)) {
                status = orderSt.getName();
            }
        }

        // Update the order status in the database
        ProductOrder updateOrder = orderService.updateOrderStatus(id, status);

        if (updateOrder != null && "Delivered".equals(status)) {
            // Get the associated product and shop from the order
            Product product = updateOrder.getProduct();
            Shop shop = product.getShop();

            // Increment sold quantity in Product
            product.setSold(product.getSold() + updateOrder.getQuantity());
            productService.saveProduct(product);  // Update the product
            int productQuantity = updateOrder.getQuantity();
            // Update shop sold and revenue without saving the entire shop
            shopService.updateShopSoldAndRevenue(shop.getId(), product.getDiscountPrice(),productQuantity);

            // Log or display success message
            session.setAttribute("succMsg", "Order status updated to Delivered. Product sold and shop revenue updated.");
        }

        // Send a notification email for the updated order status (if needed)
        try {
            commonUtil.sendMailForProductOrder(updateOrder, status);
        } catch (Exception e) {
            e.printStackTrace();
        }

        // If the order was updated successfully
        if (updateOrder != null) {
            session.setAttribute("succMsg", "Status Updated");
        } else {
            session.setAttribute("errorMsg", "Status not updated");
        }

        return "redirect:/vendor/orders";  // Redirect to the vendor orders page
    }

    @GetMapping("/search-order")
    public String searchOrder(@RequestParam String orderId, Model model, HttpSession session,
                              @RequestParam(name = "pageNo", defaultValue = "0") Integer pageNo,
                              @RequestParam(name = "pageSize", defaultValue = "10") Integer pageSize, Principal principal) {

        // If an orderId is provided, search for the specific order
        if (orderId != null && !orderId.isEmpty()) {
            // Retrieve the order based on the provided orderId
            ProductOrder order = orderService.getOrdersByOrderId(orderId.trim());

            // Ensure the order belongs to the logged-in vendor's shop
            if (order != null && order.getShop().getOwner().getEmail().equals(principal.getName())) {
                model.addAttribute("orderDtls", order);  // Add the order details to the model
                model.addAttribute("srch", true);  // Set the flag for search results
            } else {
                // If the order doesn't belong to the vendor, show an error message
                session.setAttribute("errorMsg", "Incorrect order ID or permission denied.");
                model.addAttribute("orderDtls", null);  // No order details to show
            }
        } else {
            // No orderId provided, fetch orders for the logged-in vendor's shop
            Shop shop = shopService.getShopByOwnerEmail(principal.getName());
            Page<ProductOrder> page = orderService.getOrdersByShopPagination(shop, pageNo, pageSize);

            // Add orders and pagination details to the model
            model.addAttribute("orders", page.getContent());
            model.addAttribute("srch", false);  // No search was performed
            model.addAttribute("pageNo", page.getNumber());
            model.addAttribute("pageSize", pageSize);
            model.addAttribute("totalElements", page.getTotalElements());
            model.addAttribute("totalPages", page.getTotalPages());
            model.addAttribute("isFirst", page.isFirst());
            model.addAttribute("isLast", page.isLast());
        }

        return "vendor/orders";  // Return the vendor orders page view
    }


    // Shop Settings
    @GetMapping("/shop-settings")
    public String shopSettings(Principal principal, Model model) {
        Shop shop = shopService.getShopByOwnerEmail(principal.getName());
        if (shop == null) {
            throw new RuntimeException("Shop not found for the logged-in user.");
        }
        model.addAttribute("shop", shop);
        return "vendor/shop_settings";
    }

    @PostMapping("/update-shop")
    public String updateShop(@ModelAttribute Shop shop, @RequestParam("file") MultipartFile logo,
                             HttpSession session, Model m, Principal principal) {

        // Retrieve the existing shop for the logged-in user
        Shop existingShop = shopService.getShopByOwnerEmail(principal.getName());

        // Validate the shop name (similar to product validation)
        if (shop.getName() == null || shop.getName().isEmpty()) {
            session.setAttribute("errorMsg", "Shop name cannot be empty.");
            return "redirect:/vendor/shop-settings";  // Redirect back to shop settings page if validation fails
        }

        // Retain the ID of the existing shop (update the existing record)
        shop.setId(existingShop.getId());

        // Ensure the owner_id is set explicitly
        User loggedInUser = userService.getUserByEmail(principal.getName());
        shop.setOwner(loggedInUser);  // Set the owner to the logged-in user

        // Handle logo file upload if available
        if (!logo.isEmpty()) {
            try {
                String logoName = logo.getOriginalFilename();
                shop.setLogo(logoName);

                // Save the logo file to the appropriate folder
                File saveFile = new ClassPathResource("static/img").getFile();
                Path path = Paths.get(saveFile.getAbsolutePath() + File.separator + "shop_img" + File.separator + logoName);

                if (!Files.exists(path.getParent())) {
                    Files.createDirectories(path.getParent());
                }

                Files.copy(logo.getInputStream(), path, StandardCopyOption.REPLACE_EXISTING);
            } catch (IOException e) {
                session.setAttribute("errorMsg", "Error uploading logo. Please try again.");
                e.printStackTrace();
                return "redirect:/vendor/shop-settings";  // Redirect if there is an error
            }
        }

        // Save the updated shop details
        Shop updatedShop = shopService.updateShopDetails(shop);

        // Set success or error message based on the outcome
        if (updatedShop != null) {
            session.setAttribute("succMsg", "Shop updated successfully.");
        } else {
            session.setAttribute("errorMsg", "Failed to update shop. Please try again.");
        }

        // Redirect to the shop settings page after update
        return "redirect:/vendor/shop-settings";
    }

    // Create Shop
    @GetMapping("/create-shop")
    public String createShopPage(Model model) {
        model.addAttribute("shop", new Shop());
        return "vendor/create_shop";
    }

    @PostMapping("/create-shop")
    public String createShop(@ModelAttribute Shop shop,
                             @RequestParam("file") MultipartFile logo,
                             Principal principal,
                             Model model) {
        try {
            User loggedInUser = userService.getUserByEmail(principal.getName());
            if (loggedInUser == null) {
                model.addAttribute("errorMsg", "Logged-in user not found. Please log in again.");
                return "vendor/create_shop";
            }
            shop.setOwner(loggedInUser);

            String logoName = logo.isEmpty() ? "default_logo.png" : logo.getOriginalFilename();
            shop.setLogo(logoName);

            Shop savedShop = shopService.saveShop(shop);

            if (!ObjectUtils.isEmpty(savedShop)) {
                if (!logo.isEmpty()) {
                    try {
                        File saveFile = new ClassPathResource("static/img").getFile();
                        Path path = Paths.get(saveFile.getAbsolutePath() + File.separator + "shop_img" + File.separator + logoName);

                        if (!Files.exists(path.getParent())) {
                            Files.createDirectories(path.getParent());
                        }

                        Files.copy(logo.getInputStream(), path, StandardCopyOption.REPLACE_EXISTING);
                    } catch (IOException e) {
                        model.addAttribute("errorMsg", "Error uploading logo. Please try again.");
                        e.printStackTrace();
                        return "vendor/create_shop";
                    }
                }

                model.addAttribute("succMsg", "Shop created and logo uploaded successfully.");
            } else {
                model.addAttribute("errorMsg", "Failed to save shop. Please try again.");
            }

        } catch (Exception e) {
            model.addAttribute("errorMsg", "Failed to create shop. Error: " + e.getMessage());
            e.printStackTrace();
            return "vendor/create_shop";
        }

        return "redirect:/vendor/";
    }
    @GetMapping("/profile")
    public String profile(Principal principal, Model model, HttpSession session) {
        // Get the logged-in user details based on the principal (authenticated vendor)
        User loggedInUserDetails = userService.getUserByEmail(principal.getName());
        model.addAttribute("user", loggedInUserDetails);
        return "vendor/profile";  // Vendor profile page
    }

    // Update Vendor Profile
    @PostMapping("/update-profile")
    public String updateProfile(@ModelAttribute User user, @RequestParam MultipartFile img, HttpSession session) {
        // Update the user's profile and handle the profile image upload
        User updatedUserProfile = userService.updateUserProfile(user, img);

        if (ObjectUtils.isEmpty(updatedUserProfile)) {
            session.setAttribute("errorMsg", "Profile not updated");
        } else {
            session.setAttribute("succMsg", "Profile Updated");
        }

        // Redirect back to the vendor's profile page after updating
        return "redirect:/vendor/profile";
    }

    // Change Vendor Password
    @PostMapping("/change-password")
    public String changePassword(@RequestParam String newPassword, @RequestParam String currentPassword, 
                                 Principal principal, HttpSession session) {
        // Get the logged-in user details based on the principal (authenticated vendor)
        User loggedInUserDetails = userService.getUserByEmail(principal.getName());

        // Check if the current password matches the stored password
        boolean matches = passwordEncoder.matches(currentPassword, loggedInUserDetails.getPassword());

        if (matches) {
            // If passwords match, encode the new password and update it
            String encodedPassword = passwordEncoder.encode(newPassword);
            loggedInUserDetails.setPassword(encodedPassword);
            User updatedUser = userService.updateUser(loggedInUserDetails);

            if (ObjectUtils.isEmpty(updatedUser)) {
                session.setAttribute("errorMsg", "Password not updated. Error in server");
            } else {
                session.setAttribute("succMsg", "Password updated successfully");
            }
        } else {
            session.setAttribute("errorMsg", "Current password is incorrect");
        }

        // Redirect back to the profile page after attempting to change the password
        return "redirect:/vendor/profile";
    }
    @GetMapping("/discounted-products")
    public String viewDiscountedProducts(Principal principal, Model model,
                                          @RequestParam(defaultValue = "0") Integer pageNo,
                                          @RequestParam(defaultValue = "10") Integer pageSize) {
        // Retrieve the shop associated with the logged-in vendor
        Shop shop = shopService.getShopByOwnerEmail(principal.getName());

        // Retrieve the products for the shop
        Page<Product> products = productService.getProductsByShop(shop, pageNo, pageSize);

        // Pass the products to the view
        model.addAttribute("products", products.getContent());
        model.addAttribute("pageNo", pageNo);
        model.addAttribute("pageSize", pageSize);
        model.addAttribute("totalPages", products.getTotalPages());

        return "vendor/discounted_products"; 
    }
    // Apply promotion to a specific product (set discount)
    @PostMapping("/product/{id}/apply-promotion")
    public String applyPromotionToProduct(@PathVariable Integer id, @RequestParam int discount, Principal principal) {
        Product product = productService.getProductById(id);
        if (product != null && discount > 0) {
            product.setDiscount(discount);  // Set the discount for the product
            productService.applyPromotion(product);  // Apply the promotion (calculate discount price)
            productService.saveProduct(product);  // Save the updated product
        }
        return "redirect:/vendor/discounted-products";  // Redirect to the page showing discounted products
    }
    
    @GetMapping("/revenue")
    public String viewRevenue(Model model, Principal principal, @RequestParam(required = false) String year) {
        // Retrieve the shop of the logged-in vendor
        Shop shop = shopService.getShopByOwnerEmail(principal.getName());
        if (shop == null) {
            model.addAttribute("errorMsg", "No shop found. Please create your shop first.");
            return "vendor/dashboard";
        }

        // Default to the current year if no year is provided
        if (year == null || year.isEmpty()) {
            year = String.valueOf(LocalDate.now().getYear());
        }

        // Retrieve revenue data
        double totalRevenue = orderService.getTotalRevenueForShop(shop);
        int totalProductsSold = orderService.getTotalProductsSoldForShop(shop);
        Map<String, Double> monthlyRevenueMap = null;
        
        try {
	        int yearInt = Integer.parseInt(year);
	        if (yearInt <= 0) { 
	        	model.addAttribute("year", null);
	        }
	        else {
	        	monthlyRevenueMap = orderService.getMonthlyRevenueForShop(shop, Integer.parseInt(year));

		        model.addAttribute("year", year);
	        }
		 } catch (NumberFormatException e) {
			 model.addAttribute("year", null);
		 }	  
               

        // Add attributes to the model for the view
        model.addAttribute("shop", shop);
        model.addAttribute("totalRevenue", totalRevenue);
        model.addAttribute("totalProductsSold", totalProductsSold);
        model.addAttribute("monthlyRevenueMap", monthlyRevenueMap);


        return "vendor/revenue"; // Render the vendor revenue view
    }

}
