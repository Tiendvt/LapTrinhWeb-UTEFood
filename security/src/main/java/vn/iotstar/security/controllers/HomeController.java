package vn.iotstar.security.controllers;

import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.security.Principal;
import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.ObjectUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;



import io.micrometer.common.util.StringUtils;
import jakarta.mail.MessagingException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import vn.iotstar.security.model.Category;
import vn.iotstar.security.model.Product;
import vn.iotstar.security.model.User;
import vn.iotstar.security.repository.FavoriteProductRepository;
import vn.iotstar.security.service.CartService;
import vn.iotstar.security.service.CategoryService;
import vn.iotstar.security.service.FavoriteService;
import vn.iotstar.security.service.ProductService;
import vn.iotstar.security.service.UserService;
import vn.iotstar.security.util.CommonUtil;

@Controller
public class HomeController {

	@Autowired
	private CategoryService categoryService;

	@Autowired
	private ProductService productService;


	@Autowired
	private UserService userService;

	@Autowired
	private CommonUtil commonUtil;

	@Autowired
	private BCryptPasswordEncoder passwordEncoder;

	@Autowired
	private CartService cartService;
	@Autowired
	private FavoriteProductRepository favoriteProductRepository;
	@Autowired
	private FavoriteService favoriteService;
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
	public String index(Model m) {
	    // Fetch all active categories
	    List<Category> allActiveCategory = categoryService.getAllActiveCategory().stream()
	            .sorted((c1, c2) -> c2.getId().compareTo(c1.getId())).limit(6).toList();

	    // Fetch all active products (limit to 8 products)
	    List<Product> allActiveProducts = productService.getAllActiveProducts("").stream()
	            .sorted((p1, p2) -> p2.getId().compareTo(p1.getId())).limit(8).toList();

	    // Fetch products that have sold more than 10 units
	    List<Product> productsSoldMoreThan10 = productService.getProductsSoldMoreThan10();

	    // Add these products to the model to display them in the view
	    m.addAttribute("productsSoldMoreThan10", productsSoldMoreThan10);
	    m.addAttribute("category", allActiveCategory);
	    m.addAttribute("products", allActiveProducts);

	    return "index";
	}


	@GetMapping("/signin")
	public String login(Principal principal) {
		// Kiểm tra nếu người dùng hiện tại đã đăng nhập
	    if (principal != null) {
	        // Chuyển hướng người dùng đã đăng nhập sang trang chính
	        return "redirect:/";
	    }
	    // Nếu chưa đăng nhập, hiển thị trang đăng nhập
	    return "login";
	}

	@GetMapping("/register")
	public String register() {
		return "register";
	}

	@GetMapping("/products")
	public String products(
	        Model m,
	        @RequestParam(value = "category", defaultValue = "") String category,
	        @RequestParam(name = "pageNo", defaultValue = "0") Integer pageNo,
	        @RequestParam(name = "pageSize", defaultValue = "12") Integer pageSize,
	        @RequestParam(defaultValue = "") String ch,
	        @RequestParam(name = "criteria", defaultValue = "DEFAULT") String criteria) {

	    // Nếu có tiêu chí lọc thì đổi pageSize thành 20
	    if (!"DEFAULT".equalsIgnoreCase(criteria)) {
	        pageSize = 20;
	    }

	    // Lấy danh sách danh mục
	    List<Category> categories = categoryService.getAllActiveCategory();
	    m.addAttribute("paramValue", category);
	    m.addAttribute("categories", categories);

	    Page<Product> page = null;

//	 Logic tìm kiếm

	    //Làm lại:
	    if(!StringUtils.isEmpty(ch))		//Nếu có nhập từ khóa vào tìm kiếm
	    {
	    	if(!"DEFAULT".equalsIgnoreCase(criteria)) {	//Nếu người dùng nhập từ khóa search, sau đó chọn tiêu chí lọc(Mới nhất, Nhiều đánh giá nhất, Bán chạy nhất,...) rồi search:
	    		page = productService.getProductsByKeywordAndCriteria(ch, criteria, PageRequest.of(pageNo, pageSize));
	    		System.out.print("keyword: "+ch+ "\n");
	    		
	    		
	    	}
	    	else if("DEFAULT".equalsIgnoreCase(criteria))//Nếu người dùng nhập từ khóa ko chọn tiêu chí gì cả mà search luôn:
	    	{
	    		System.out.print("keyword: "+ch+ "\n");
	    		System.out.print("Ko dung criteria\n");
	    		if(!StringUtils.isEmpty(category))
	    		{
	    			System.out.print("dung category" + category +"\n");

	    			page = productService.searchProductsByCategoryAndKeyword(category, ch, PageRequest.of(pageNo, pageSize));
	    		}
	    		else
	    			{
	    		page = productService.searchActiveProductPagination(pageNo, pageSize, "", ch);
	    		
	    		System.out.print("Criteria: " + criteria);
	    			}
	    	}
	    	
	    }
	    
	    else //trường hợp ko dùng keyword ch để search.(có thể là bấm chọn tiêu chí rồi nhấn search/ bấm chọn category rồi kết hợp với tiêu chí).
	    {
	    	System.out.print("Keyword la null: "+ch);
	    	if ("DEFAULT".equalsIgnoreCase(criteria)) //Nếu người dùng ko chọn tiêu chí gì cả (thì mặc định là DEFAULT).
	    	{
	    		System.out.print("\nCriteria là DEFAULT: "+criteria);
	    		if(!StringUtils.isEmpty(category))		//nếu người dùng ko chọn category.
	    		{
	    			
	    			//Lấy ra tất cả sản phẩm của category đó.
	    			page = productService.getAllActiveProductPagination(pageNo, pageSize, category);
	    			
	    		}
	    		else				//nếu người dùng ko chọn category.
	    		{
	    			//Lấy ra tất cả sản phẩm mà trang có.
	    			System.out.print("\nLấy ra tất cả các sản phẩm trên UTEFOOD ");
	    			page = productService.getAllActiveProductPagination(pageNo, pageSize, category);
	    			
	    		}
	    	}
	    	else		//Nếu criteria có thể là: NEWEST, MOST FAVORITE, BEST SELLING,...
	    	{
	    		System.out.print("\nCriteria không là DEFAULT: "+criteria);
	    		if(!StringUtils.isEmpty(category))		//nếu người dùng chọn category.
	    		{
	    			
	    			//Lấy ra tất cả sản phẩm của category có theo criteria.
	    			
	    			System.out.print("\nLấy ra tất cả các sản phẩm trong category của UTEFOOD theo criteria");
	    			page = productService.searchProductsByCategoryAndCriteria(category, criteria, PageRequest.of(pageNo, pageSize));
	    			
	    		}
	    		else				//nếu người dùng ko chọn category.
	    		{
	    			//Lấy ra tất cả sản phẩm mà trang có theo criteria.
	    			System.out.print("\nLấy ra tất cả các sản phẩm trên UTEFOOD theo criteria");
	    			page = productService.getProductsByCriteria(criteria, PageRequest.of(pageNo, pageSize));
	    			
	    		}
	    	}
	    }
	   
	    
	    // Gán các thông tin sản phẩm vào Model
	    List<Product> products = page.getContent();
	    m.addAttribute("products", products);
	    m.addAttribute("productsSize", products.size());
	    m.addAttribute("pageNo", page.getNumber());
	    m.addAttribute("pageSize", pageSize);
	    m.addAttribute("totalElements", page.getTotalElements());
	    m.addAttribute("totalPages", page.getTotalPages());
	    m.addAttribute("isFirst", page.isFirst());
	    m.addAttribute("isLast", page.isLast());
	    m.addAttribute("criteria", criteria);
	    m.addAttribute("ch", ch); // Thêm `ch` vào Model để giữ giá trị trong view

	    return "product";
	}




	@GetMapping("/product/{id}")
	public String product(@PathVariable int id, Model m, Principal principal) {
		Product product = productService.getProductById(id);
	    m.addAttribute("product", product);
	    boolean isFavorite = false;
	    if (principal != null) {
	        User user = userService.getUserByEmail(principal.getName());
	        
	        	isFavorite = favoriteProductRepository.existsByUserIdAndProductId(user.getId(), id);

	    } 
	 // Count the number of users who have favorited this product
	    int favoriteCount = favoriteService.countByProductId(product.getId());
	    
	        m.addAttribute("isFavorite", isFavorite);
	        m.addAttribute("favoriteCount", favoriteCount);
		return "view_product";
	}

	@PostMapping("/saveUser")
	public String saveUser(@ModelAttribute User user, @RequestParam("img") MultipartFile file, HttpSession session, HttpServletRequest request)
			throws IOException, UnsupportedEncodingException, MessagingException {

		Boolean existsEmail = userService.existsEmail(user.getEmail());

		if (existsEmail) {
			session.setAttribute("errorMsg", "Email already exist");
		} else {
			String imageName = file.isEmpty() ? "default.jpg" : file.getOriginalFilename();
			user.setProfileImage(imageName);
			User saveUser = userService.saveUser(user);

			if (!ObjectUtils.isEmpty(saveUser)) {
				if (!file.isEmpty()) {
					File saveFile = new ClassPathResource("static/img").getFile();

					Path path = Paths.get(saveFile.getAbsolutePath() + File.separator + "profile_img" + File.separator
							+ file.getOriginalFilename());

//					System.out.println(path);
					Files.copy(file.getInputStream(), path, StandardCopyOption.REPLACE_EXISTING);
				}
				
				String email = saveUser.getEmail();
				String activeToken = UUID.randomUUID().toString();
				userService.updateUserActiveToken(email, activeToken);
				String url = CommonUtil.generateUrl(request) + "/active-account?token=" + activeToken;
				Boolean sendMail = commonUtil.sendMailActive(url, email);
				
				if (!sendMail) {
					session.setAttribute("errorMsg", "Something wrong on server ! Email not send");
				} 
				
				session.setAttribute("succMsg", "Register successfully. Please check your email..Account active link sent");
			} else {
				session.setAttribute("errorMsg", "something wrong on server");
			}
		}

		return "redirect:/register";
	}
	
	@GetMapping("/active-account")
	public String showActiveAccount(@RequestParam String token, HttpSession session, Model m) {

		User userByActiveToken = userService.getUserByActiveToken(token);

		if (userByActiveToken == null) {
			m.addAttribute("errorMsg", "Your link is invalid or expired !!");
			return "message";
		} else {
			userByActiveToken.setActiveToken(null);
			userByActiveToken.setIsEnable(true);
			userService.updateUser(userByActiveToken);
			m.addAttribute("msg", "Account active successfully");

			return "message";
		}
	}


//	Forgot Password Code 

	@GetMapping("/forgot-password")
	public String showForgotPassword() {
		return "forgot_password.html";
	}

	@PostMapping("/forgot-password")
	public String processForgotPassword(@RequestParam String email, HttpSession session, HttpServletRequest request)
			throws UnsupportedEncodingException, MessagingException {

		User userByEmail = userService.getUserByEmail(email);

		if (ObjectUtils.isEmpty(userByEmail)) {
			session.setAttribute("errorMsg", "Invalid email");
		} else {

			String resetToken = UUID.randomUUID().toString();
			userService.updateUserResetToken(email, resetToken);

			String url = CommonUtil.generateUrl(request) + "/reset-password?token=" + resetToken;

			Boolean sendMail = commonUtil.sendMailReset(url, email);

			if (sendMail) {
				session.setAttribute("succMsg", "Please check your email..Password Reset link sent");
			} else {
				session.setAttribute("errorMsg", "Something wring on server ! Email not send");
			}
		}

		return "redirect:/forgot-password";
	}

	@GetMapping("/reset-password")
	public String showResetPassword(@RequestParam String token, HttpSession session, Model m) {

		User userByResetToken = userService.getUserByResetToken(token);

		if (userByResetToken == null) {
			m.addAttribute("msg", "Your link is invalid or expired !!");
			return "message";
		}
		m.addAttribute("token", token);
		return "reset_password";
	}

	@PostMapping("/reset-password")
	public String resetPassword(@RequestParam String token, @RequestParam String password, HttpSession session,Model m) {
		
		User userByToken = userService.getUserByResetToken(token);
		if (userByToken == null) {
			m.addAttribute("errorMsg", "Your link is invalid or expired !!");
			return "message";
		} else {
			userByToken.setPassword(passwordEncoder.encode(password));
			userByToken.setResetToken(null);
			userService.updateUser(userByToken);
			m.addAttribute("msg", "Password change successfully");

			return "message";
		}

	}

	@GetMapping("/search")
	public String searchProduct(@RequestParam String ch, Model m) {
		List<Product> searchProducts = productService.searchProduct(ch);
		m.addAttribute("products", searchProducts);
		List<Category> categories = categoryService.getAllActiveCategory();
		m.addAttribute("categories", categories);
		return "product";
	}
	@GetMapping("/profile")
	public String profile() {
		return "user/profile";
	}

}