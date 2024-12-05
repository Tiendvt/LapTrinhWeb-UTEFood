package vn.iotstar.security.controllers;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.security.Principal;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.ObjectUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import vn.iotstar.security.model.Product;
import vn.iotstar.security.model.ProductOrder;
import vn.iotstar.security.model.Shop;
import vn.iotstar.security.service.OrderService;
import vn.iotstar.security.service.ProductService;
import vn.iotstar.security.service.ShopService;


@Controller
@RequestMapping("/vendor")
public class VendorController {

    @Autowired
    private ShopService shopService;

    @Autowired
    private ProductService productService;

    @Autowired
    private OrderService orderService;

    // Vendor dashboard
    @GetMapping("/")
    public String vendorDashboard(Principal principal, Model model) {
        Shop shop = shopService.getShopByOwnerEmail(principal.getName());
        model.addAttribute("shop", shop);
        return "vendor/dashboard";
    }

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


    @GetMapping("/add-product")
    public String addProductPage(Model model) {
        model.addAttribute("product", new Product());
        return "vendor/add_product";
    }

    @PostMapping("/save-product")
    public String saveProduct(@ModelAttribute Product product,
                              @RequestParam("file") MultipartFile image,
                              Principal principal,
                              Model model) {
        Shop shop = shopService.getShopByOwnerEmail(principal.getName());
        product.setShop(shop);

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

    @GetMapping("/edit-product/{id}")
    public String editProduct(@PathVariable("id") Integer id, Model model, Principal principal) {
        Product product = productService.getProductById(id);
        Shop shop = shopService.getShopByOwnerEmail(principal.getName());

        // Ensure the product belongs to the vendor's shop
        if (!product.getShop().getId().equals(shop.getId())) {
            model.addAttribute("errorMsg", "Unauthorized action. You cannot edit this product.");
            return "vendor/products";
        }

        model.addAttribute("product", product);
        return "vendor/edit_product";
    }

    @PostMapping("/update-product")
    public String updateProduct(@ModelAttribute Product product,
                                @RequestParam("file") MultipartFile image,
                                Model model,
                                Principal principal) {

        Shop shop = shopService.getShopByOwnerEmail(principal.getName());
        Product existingProduct = productService.getProductById(product.getId());

        // Ensure the product belongs to the vendor's shop
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

    @GetMapping("/delete-product/{id}")
    public String deleteProduct(@PathVariable("id") Integer id, Principal principal, Model model) {
        Product product = productService.getProductById(id);
        Shop shop = shopService.getShopByOwnerEmail(principal.getName());

        // Ensure the product belongs to the vendor's shop
        if (!product.getShop().getId().equals(shop.getId())) {
            model.addAttribute("errorMsg", "Unauthorized action. You cannot delete this product.");
            return "vendor/products";
        }

        productService.deleteProduct(id);
        return "redirect:/vendor/products";
    }

    // Manage orders
    @GetMapping("/orders")
    public String viewOrders(Principal principal, Model model,
                             @RequestParam(defaultValue = "0") Integer pageNo,
                             @RequestParam(defaultValue = "10") Integer pageSize,
                             @RequestParam(defaultValue = "") String status) {
        Shop shop = shopService.getShopByOwnerEmail(principal.getName());
        Page<ProductOrder> orders;

        if (!status.isEmpty()) {
            orders = orderService.getOrdersByStatusAndShop(status, shop, pageNo, pageSize);
        } else {
            orders = orderService.getOrdersByShopPagination(shop, pageNo, pageSize);
        }

        model.addAttribute("orders", orders.getContent());
        model.addAttribute("pageNo", pageNo);
        model.addAttribute("pageSize", pageSize);
        model.addAttribute("totalPages", orders.getTotalPages());
        model.addAttribute("status", status);
        return "vendor/orders";
    }

    @PostMapping("/update-order-status")
    public String updateOrderStatus(@RequestParam Integer id, @RequestParam String status) {
        orderService.updateOrderStatus(id, status);
        return "redirect:/vendor/orders";
    }

    // Shop settings
    @GetMapping("/shop-settings")
    public String shopSettings(Principal principal, Model model) {
        Shop shop = shopService.getShopByOwnerEmail(principal.getName());
        model.addAttribute("shop", shop);
        return "vendor/shop_settings";
    }

    @PostMapping("/update-shop")
    public String updateShop(@ModelAttribute Shop shop, Principal principal) {
        Shop existingShop = shopService.getShopByOwnerEmail(principal.getName());
        shop.setId(existingShop.getId());
        shopService.updateShopDetails(shop);
        return "redirect:/vendor/shop-settings";
    }
}
