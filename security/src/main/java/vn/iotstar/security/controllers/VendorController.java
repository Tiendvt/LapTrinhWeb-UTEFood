package vn.iotstar.security.controllers;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.security.Principal;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.domain.Page;
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
	private CommonUtil commonUtil;
    // Vendor Dashboard
    @GetMapping("/")
    public String vendorDashboard(Principal principal, Model model) {
        Shop shop = shopService.getShopByOwnerEmail(principal.getName());
        model.addAttribute("shop", shop);
        return "vendor/dashboard";
    }

    // View Products
    @GetMapping("/products")
    public String viewProducts(Principal principal, Model model,
                               @RequestParam(defaultValue = "0") Integer pageNo,
                               @RequestParam(defaultValue = "10") Integer pageSize) {
        Shop shop = shopService.getShopByOwnerEmail(principal.getName());
        Page<Product> products = productService.getProductsByShop(shop, pageNo, pageSize);
        model.addAttribute("products", products.getContent());
        model.addAttribute("pageNo", pageNo);
        model.addAttribute("pageSize", pageSize);
        model.addAttribute("totalPages", products.getTotalPages());
        return "vendor/products";
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
    public String updateOrderStatus(@RequestParam Integer id, @RequestParam Integer st, HttpSession session, Principal principal) {

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

        // Send a notification email for the updated order status (if needed)
        try {
            commonUtil.sendMailForProductOrder(updateOrder, status);
        } catch (Exception e) {
            e.printStackTrace();
        }

        // Check if the order was updated successfully
        if (!ObjectUtils.isEmpty(updateOrder)) {
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
    public String updateShop(@ModelAttribute Shop shop, Principal principal) {
        Shop existingShop = shopService.getShopByOwnerEmail(principal.getName());

        // Retain the ID of the existing shop (you want to update the existing record, not create a new one)
        shop.setId(existingShop.getId());

        // Ensure owner_id is set explicitly (in case it is missing)
        User loggedInUser = userService.getUserByEmail(principal.getName());
        shop.setOwner(loggedInUser);  // Set the owner to the logged-in user

        // Save the updated shop details
        shopService.updateShopDetails(shop);

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
}
