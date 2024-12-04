package vn.iotstar.security.controllers;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;


import vn.iotstar.security.model.Category;
import vn.iotstar.security.model.Product;
import vn.iotstar.security.service.CategoryService;


@Controller
@RequestMapping("/admin")
public class AdminController {
	
	@Autowired
	private CategoryService categoryService;
	
    @GetMapping("/")
    public String index() {
        return "admin/index"; 
    }
    
    @GetMapping("/loadAddProduct")
	public String loadAddProduct(Model m) {
		List<Category> categories = categoryService.getAllCategory();
		m.addAttribute("categories", categories);
		return "admin/add_product";
	}
}